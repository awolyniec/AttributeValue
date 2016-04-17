import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * Created by Alec Wolyniec on 4/3/16.
 */
public class getNounDependencies {
    /*
        Taking in a list of all the sentences in an Annotation, generates an array of all possible arrays containing an ID,
        a unique noun from the Annotation, and all of the words that depend on said noun (order doesn't matter)
    */
    public static IndexedWord[][] generateNounDependencies (List<CoreMap> sentences) {
        /*
            Estimate the number of words in the document by getting the average number of words in 3 randomly selected
            sentences and multiplying that by the number of sentences. The default length of the array to store
            dependencies will be 1/5 the size of that.
         */
        int wordCountEst = 0;
        for (int i = 0; i < 3; i++) {
            int randGuess = Math.abs((int) (Math.random() * sentences.size()));
            wordCountEst += (int)((sentences.get(randGuess).get(CoreAnnotations.TokensAnnotation.class).size())/3.0);
        }
        wordCountEst *= sentences.size();
        int sizeEst = (int)(wordCountEst * 0.2);
        if (sizeEst == 0) {
            sizeEst = 1;
        }

        /*
            A 2-D array to store all nouns and all their dependencies
         */
        IndexedWord[][] nounDepArray = new IndexedWord[sizeEst][];
        int nounArrayCounter = 0;
        int subArrayCounter;

        //all sentences in the document
        for(CoreMap sentence: sentences) {
            SemanticGraph dependencies = sentence.get
                    (SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            //Create arrays of each noun in the sentence and all words depending on it
            for (IndexedWord rootNoun: dependencies.vertexListSorted()) {
                //String lemma = rootNoun.lemma();
                String tag = rootNoun.tag();
                //collect nouns and plural nouns
                if (tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS")) {
                    subArrayCounter = 1;
                    IndexedWord[] subArray = new IndexedWord[2]; //the array of the given noun's dependencies
                    subArray[0] = rootNoun;
                    //scan all the noun's dependencies, and add each to subArray
                    for (IndexedWord descendant: dependencies.descendants(rootNoun)) {
                        if (descendant != rootNoun) {
                            subArray[subArrayCounter] = descendant;
                            subArrayCounter++;
                            //double size of subArray if it's full
                            if (subArrayCounter == subArray.length) {
                                IndexedWord[] newSub = new IndexedWord[subArrayCounter * 2];
                                for (int i = 0; i < subArray.length; i++) {
                                    newSub[i] = subArray[i];
                                }
                                subArray = newSub;
                            }
                        }
                    }
                    //create a version of subArray without null entries
                    IndexedWord[] newSubArray = new IndexedWord[subArrayCounter];
                    for (int j = 0; j < subArrayCounter; j++) {
                        newSubArray[j] = subArray[j];
                    }
                    nounDepArray[nounArrayCounter] = newSubArray; //Add the array of completed dependencies to the array
                    nounArrayCounter++;
                    //double the size of the dependency array if it's full
                    if (nounArrayCounter == nounDepArray.length) {
                        IndexedWord[][] newDeps = new IndexedWord[nounArrayCounter * 2][];
                        for (int i = 0; i < nounDepArray.length; i++) {
                            newDeps[i] = nounDepArray[i];
                        }
                        nounDepArray = newDeps;
                    }
                }
            }
        }
        //create a version of the dependency array with no null entries
        IndexedWord[][] newDeps = new IndexedWord[nounArrayCounter][];
        for (int i = 0; i < nounArrayCounter; i++) {
            newDeps[i] = nounDepArray[i];
        }
        return newDeps;
    }

    /*
        Given an array of nouns and their dependencies, print each noun followed by its dependencies
    */
    public static void printNounDependencies(IndexedWord[][] nounDepArray) {
        System.out.println();
        System.out.println("All nouns in the document, and all words depending on them:");
        for (int i = 0; i < nounDepArray.length; i++) {
            System.out.print("Dependencies of "+ nounDepArray[i][0] +": ");
            for (int j = 1; j < nounDepArray[i].length; j++) {
                if (nounDepArray[i][j] != null) {
                    if (j > 1) {
                        System.out.print(", ");
                    }
                    System.out.print(nounDepArray[i][j].toString());
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
