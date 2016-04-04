/**
 * Created by Alec Wolyniec on 2/11/16.
 */
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class generateAttrValPairs0_2 {

    //doubles the size of an attribute-value pair array
    static AttrValPair[] doubleArray(AttrValPair[] input, int inputCounter) {
        AttrValPair[] output = new AttrValPair[inputCounter * 2];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    //doubles the size of a 2-D IndexedWord array, if it's full
    static IndexedWord[][] doubleArrayIfFull(IndexedWord[][] input, int inputCounter) {
        if (inputCounter == input.length) {
            IndexedWord[][] output = new IndexedWord[inputCounter * 2][];
            for (int i = 0; i < input.length; i++) {
                output[i] = input[i];
            }
            return output;
        }
        return input;
    }

    public static AttrValPair[] generateAttrValPairs(IndexedWord[][] deps, int numTransactions) {
        //Count the total number of non-null dependent words, this will be the maximum size of the return array
        int depCounter = 0;
        for (int i = 0; i < deps.length; i++) {
            for (int j = 1; j < deps[i].length; j++) {
                if (deps[i][j] == null) {
                    break;
                }
                depCounter++;
            }
        }
        //The array of attribute-value pairs to be returned
        AttrValPair[] pairs = new AttrValPair[depCounter];
        int pairCounter = 0;
        //Gather all possible pair for each dependency; gather a list of all of them
        for (int i = 0; i < deps.length; i++) {
            //get the list for one dependency
            AttrValPair[] pairsOneDep = generateAttrValPairsOneDependencyList(deps[i], i);
            for (int j = 0; j < pairsOneDep.length; j++) {
                pairs[pairCounter] = pairsOneDep[j];
                pairCounter++;
                //double array size if full
                if (pairCounter == pairs.length) {
                    pairs = doubleArray(pairs, pairCounter);
                }
            }
        }

        //remove null entries
        AttrValPair[] newPairs = new AttrValPair[pairCounter];
        for (int i = 0; i < pairCounter; i++){
            newPairs[i] = pairs[i];
        }
        pairs = newPairs;

        return pairs;
    }

    private static AttrValPair[] generateAttrValPairsOneDependencyList(IndexedWord[] dep, int transId) {
        /*
            generate the chain of all eligible nouns, adjectives, and quantifiers that may form an attribute value pair with
            dep's root noun
         */
        AttrValPair[] output = new AttrValPair[0];
        IndexedWord[][] fullObject = defineEnvelope(dep);
        //if fullObject is empty
        if (fullObject[0].length == 1) {
            return output;
        }
        String[] generatedSets = generate(fullObject);
        output = new AttrValPair[generatedSets.length];
        //translate generated sets into attribute-value pair format
        for (int i = 0; i < generatedSets.length; i++) {
            output[i] = new AttrValPair(generatedSets[i], transId); //need to get transaction id
        }
        return output;
    }

    //generates strings to be the pair's value
    private static String[] generate (IndexedWord[][] base) {
        IndexedWord[] full = base[0];
        String[] output = new String[base[0].length];
        int outputCounter = 0;
        String object = full[0].lemma()+"/"+full[0].tag();
        String piece = "("+object+", "+")";
        for (int i = 1; i < full.length; i++) {
            piece = "("+object+", "+full[i].lemma()+"/"+full[i].tag()+")";
            if (full[i].tag().equals("NN") || full[i].tag().equals("NNS") || full[i].tag().equals("NNP") ||
                    full[i].tag().equals("NNPS")) {
                object = piece;
                if (i == full.length - 1) {
                    output[outputCounter] = piece;
                    outputCounter++;
                }
            }
            else {
                output[outputCounter] = piece;
                outputCounter++;
            }
        }
        if (outputCounter == 0) {
            output[0] = piece;
            outputCounter++;
        }

        //Eliminate null entries from output
        String[] newOutput = new String[outputCounter];
        for (int i = 0; i < outputCounter; i++) {
            newOutput[i] = output[i];
        }
        return newOutput;
    }

    /*
    Return an array of the maximal object and the maximal feature for a given
    root word
    */
    static IndexedWord[][] defineEnvelope(IndexedWord[] dep) {
         /*
            Define envelope of possible attribute-value pair elements for the root word
         */
        IndexedWord[] objectWords = new IndexedWord[1];
        //objectWords[0] = dep[0];
        int objectWordsCounter = 0;
        int rootIndex = dep[0].get(IndexAnnotation.class);
        /*
            For each position in the sentence that is left of the root's, starting at the root and moving leftward,
            collect every noun and adjective, stopping if a given position is occupied by a word not dependent on the
            root, a non-noun or non-adjective, or a noun that precedes an adjective between it and the root.
         */
        boolean adjFlag = false; //marks whether or not an adjective has been scanned already
        for (int i = rootIndex; i > -1; i--) {
            boolean found = false;
            //checks if there is a dependent that matches the criteria
            for (int k = 0; k < dep.length; k++) {
                String tag = dep[k].tag();
                if (dep[k].get(IndexAnnotation.class) == i) {
                    if ( ((tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS")) && !adjFlag) ||
                            tag.equals("JJ") || tag.equals("JJR") || tag.equals("CD")) {
                        //add
                        objectWords[objectWordsCounter] = dep[k];
                        objectWordsCounter++;

                        //if it's an adjective, activate the adjective flag
                        if (!(tag.equals("NN")) && !(tag.equals("NNS")) && !(tag.equals("NNP")) && !(tag.equals("NNPS"))) {
                            adjFlag = true;
                        }

                        //doubles the array if necessary
                        if (objectWordsCounter == objectWords.length) {
                            IndexedWord[] newThing = new IndexedWord[objectWords.length * 2];
                            for (int j = 0; j < objectWords.length; j++) {
                                newThing[j] = objectWords[j];
                            }
                            objectWords = newThing;
                        }

                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                break;
            }
        }
        //eliminates null entries from objectWords
        IndexedWord[] newObjectWords = new IndexedWord[objectWordsCounter];
        for (int i = 0; i < objectWordsCounter; i++) {
            newObjectWords[i] = objectWords[i];
        }

        //create the 2D array to start scanning
        IndexedWord[][] fullObject = {newObjectWords, {}};
        return fullObject;
    }

    /*
    Calculates the support and confidence of a pair within an array of pairs

    -Support of pair x: The percentage of transactions with a pair in the array that has the same object and
    feature as x
    -Confidence of pair x: The percentage of transactions with a pair in the array with the same object as x
    that have the same feature as x
    */
    public static void setSupportAndConfidence(AttrValPair[] pairs, AttrValPair pair, int numTransactions) {

        int lastObjectMatch = -1; //the last transaction id for which a pair had the same object as pair
        int lastFullMatch = -1;   // "" "" "" "" "" and feature as pair
        int objectMatchCounter = 0;
        int fullMatchCounter = 0;
        /*
            objectMatchCounter: Count the number of transactions with an pair in the array that has the same object
            as "pair"

            fullMatchCounter: Count the number of transactions with a pair in the array that has the same object
            and feature as "pair"
         */
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i].getObj().equals(pair.getObj())) {
                int id = pairs[i].getTransactionID();
                if (lastObjectMatch != id) {
                    objectMatchCounter++;
                    lastObjectMatch = id;
                }
                if (pairs[i].getFeat().equals(pair.getFeat()) && lastFullMatch != id) {
                    fullMatchCounter++;
                    lastFullMatch = id;
                }
            }
        }

        //Calculate support and confidence
        double support = (((double)fullMatchCounter)/numTransactions) * 100;
        double confidence = (((double)fullMatchCounter)/objectMatchCounter) * 100;
        pair.setSupport(support);
        pair.setConfidence(confidence);
    }

}
