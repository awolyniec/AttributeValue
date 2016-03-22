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
public class test {
    //bad method
    public static String removeHTMLTags (String s) {
        String newS = "";
        boolean scan = true;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '<') {
                scan = false;
            }
            else if (scan == true) {
                newS += s.charAt(i);
            }
            if (s.charAt(i) == '>') {
                scan = true;
            }
        }
        return newS;
    }

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
      Given an array of arrays, each array containing a noun followed by its dependencies, generate all possible
      2-item itemsets consisting of a noun and one of the following types of tokens depending on it: Adjective, noun,
      quantity marker. Return an array of each of these itemsets grouped by transaction.
    */
    public static Itemset[] generateItemsets0_1 (IndexedWord[][] deps) {
        Itemset[] itemsets;
        //Count the total number of non-null dependent words, this will be the maximum size of the return array
        int depCounter = 0;
        for (int i = 0; i < deps.length; i++) {
            if (deps[i] != null) {
                for (int j = 1; j < deps[i].length; j++) {
                    if (deps[i][j] == null) {
                        break;
                    }
                    depCounter++;
                }
            }
        }
        //The array of itemsets to be returned
        itemsets = new Itemset[depCounter];
        int placeCounter = 0;
        /*
            If an item in any dependency list is an adjective, a noun, or a quantity, make an itemset of the noun,
            it, and the transaction containing it
        */
        for (int i = 0; i < deps.length; i++) {
            if (deps[i] != null) {
                for (int j = 1; j < deps[i].length; j++) {
                    if (deps[i][j] == null) {
                        break;
                    }
                    String tag = deps[i][j].tag();
                    if (tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS") ||
                            tag.equals("JJ") || tag.equals("JJR") || tag.equals("JJS") || tag.equals("CD")) {
                        Itemset itemset = new Itemset("("+deps[i][0]+", "+deps[i][j]+")", i);
                        itemsets[placeCounter] = itemset;
                        placeCounter++;
                    }
                }
            }
        }
        return itemsets;
    }

    public static void printItemsets (Itemset[] itemsets) {
        System.out.println();
        System.out.println("Itemsets:");
        for (int i = 0; i < itemsets.length; i++) {
            if (itemsets[i] != null) {
                System.out.println(itemsets[i].toString());
            }
        }
    }

    /*
        Filters out infrequent itemsets from a list of itemsets based on given confidence and support thresholds
        (confidence: the % of transactions containing the itemset's object that also contain its feature)
        (support: the % of transactions containing the object and the feature)
     */
    public static Itemset[] filterByFrequency (Itemset[] itemsets, double support, double confidence) {
        Itemset[] filteredItemsets = new Itemset[itemsets.length];
        int filterCounter = 0;
        for (int i = 0; i < itemsets.length; i++) {
            if (itemsets[i] != null) {
                if (itemsets[i].getSupport() > support && itemsets[i].getConfidence() > confidence) {
                    filteredItemsets[filterCounter] = itemsets[i];
                    filterCounter++;
                }
            }
        }
        return filteredItemsets;
    }

    /*
        Args:
        -A path to a txt file; this is where the itemsets generated here will be printed
     */
    public static void main (String[] args) throws IOException {
        /* creates a StanfordCoreNLP object, with tokenization, sentence-splitting, POS-tagging, lemmatization,
        syntactic parsing, and dependency parsing
        */

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        //full: tokenize, ssplit, pos, lemma, parse, depparse
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // read some text in the text variable
        File posts = new File(args[0]);
        String text = "";
        try {
            Scanner scanner = new Scanner(posts);
            while (scanner.hasNextLine()) {
                text += scanner.nextLine() + "\n";
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        //Note that Stanford CoreNLP's POS-tagger is frequently inaccurate
        /*
        String text = "How do I repair a small leak in a steam radiator? Could this be a big problem?\t<p>One of my" +
                " steam radiators has a small leak under the bottom.  No hole is visible, but water drips out at a " +
                "rate of a few drops per minute.  Various websites suggest applying a 1 mm thick layer of " +
                "<a href=\"http://jbweld.net/products/uses.php\" rel=\"nofollow\">J-B Weld</a> after sanding and " +
                "cleaning with acetone.</p>  <p>Does the fact that the radiator leaks necessarily mean that it is " +
                "completely rusted from the inside and that leaks will soon appear elsewhere?  Or is it likely that " +
                "a slow leak is not due to rust, or that the rust is in only one place?</p>  <p>The radiator is " +
                "decades old (perhaps even a century), but has been moved recently in order to repair the floor.  " +
                "The leak is clearly away from the valve.</p>";
        */
        //String text = "The garden gnome society. The big old garden gnome society. My big fat stupid pig. My 1 inch blue hammer. A red water heater.";
        //text = removeHTMLTags(text);
        //System.out.println(text);
        //System.out.println();

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

        long itemBegin = System.currentTimeMillis();
        Itemset[] itemsets = generateItemsets0_2.generateItemsets(deps, numTransactions);
        long itemTime = ((System.currentTimeMillis() - itemBegin)/1000);
        System.out.println("Generated itemsets...in "+itemTime+" seconds.");

        //printNounDependencies(deps);

        //generateItemsets0_1.generateItemsets(deps);
        //generateItemsets1_1.generateItemsets(deps);
        //printItemsets(itemsets);

        //System.out.println();
        System.out.println("Filtered: ");
        itemsets = filterByFrequency(itemsets, 0, 0);
        printItemsets(itemsets);
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
