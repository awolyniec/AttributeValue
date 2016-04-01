import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
/*
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.semgraph.*;


import java.util.Properties;

/**
 * Created by Alec Wolyniec on 1/14/16.
 */
public class inputTxtsToItemsets {
    /*
    Taking in a list of all the sentences in an Annotation, generates an array of all possible arrays containing an ID,
    a unique noun from the Annotation, and all of the words that depend on said noun (order doesn't matter)
     */
    static int numTransactions;
    public static IndexedWord[][] generateNounDependencies (List<CoreMap> sentences) {
        /*
            Estimate the number of words in the document by getting the average number of words in 3 randomly selected
            sentences and multiplying that by the number of sentences. The default length of the array to store
            dependencies will be 1/5 the size of that.
         */
        int wordCountEst = 0;
        for (int i = 0; i < 3; i++) {
            int randGuess = Math.abs((int) (Math.random() * sentences.size()));
            wordCountEst += (int)((sentences.get(randGuess).get(TokensAnnotation.class).size())/3.0);
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
        numTransactions = nounArrayCounter;
        //create a version of the dependency array with no null entries
        IndexedWord[][] newDeps = new IndexedWord[nounArrayCounter][];
        for (int i = 0; i < nounArrayCounter; i++) {
            newDeps[i] = nounDepArray[i];
        }
        return newDeps;
    }

    /*
        Given an array of nouns and their dependencies, prints each noun followed by its dependencies
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

    /*
        Args:
        0: A path to a txt file containing the input
        1: A path to a txt file to which outputs will be printed
     */
    public static void main (String[] args) throws IOException {

        //Get a version of the txt file without html tags
        String text = RemoveHTMLTagsFromTXTs.html2text(args[0], true);

        /* creates a StanfordCoreNLP object, with tokenization, sentence-splitting, POS-tagging, lemmatization,
        syntactic parsing, and dependency parsing
        */
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        //full: tokenize, ssplit, pos, lemma, parse, depparse
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        long annotBegin = System.currentTimeMillis();
        pipeline.annotate(document);
        long annotTime = ((System.currentTimeMillis() - annotBegin)/1000);
        System.out.println("Annotated...in "+annotTime+" seconds.");

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        //Generates and prints a list of noun dependencies in the document
        long depBegin = System.currentTimeMillis();
        IndexedWord[][] deps = generateNounDependencies(sentences);
        long depTime = ((System.currentTimeMillis() - depBegin)/1000);
        System.out.println("Generated noun dependencies...in "+depTime+" seconds.");
        //printNounDependencies(deps);

        long itemBegin = System.currentTimeMillis();
        Itemset[] itemsets = generateItemsets0_2.generateItemsets(deps, numTransactions);
        long itemTime = ((System.currentTimeMillis() - itemBegin)/1000);
        System.out.println("Generated itemsets...in "+itemTime+" seconds.");
        /*
            for (int i = 0; i < itemsets.length; i++) {
                System.out.println(itemsets[i].toString());
            }
         */
        //System.out.println("Number of itemsets: "+itemsets.length);

        /*
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
            for (CoreLabel token: tokens) {
                System.out.println();
                // this is the text of the token
                //String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                //String pos = token.get(PartOfSpeechAnnotation.class);
                //System.out.println(word + "/" + pos);
                //this is the NER tag of the token
                //String ne = token.get(NERAnnotation.class);
            }
            /*
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeAnnotation.class);
            System.out.println(tree);
            System.out.println();
        }
        */

        //prints the output to a file specified in args
        generateOutput.printItemsetsObjsTransIDsToFile(args[1], itemsets, false);
    }
}
