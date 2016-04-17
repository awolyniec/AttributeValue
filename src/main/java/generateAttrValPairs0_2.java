/**
 * Created by Alec Wolyniec on 2/11/16.
 */
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class generateAttrValPairs0_2 {

    //double the size of an attribute-value pair array
    static AttrValPair[] doubleArray(AttrValPair[] input, int inputCounter) {
        AttrValPair[] output = new AttrValPair[inputCounter * 2];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    //double the size of a 2-D IndexedWord array, if it's full
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

    //keystone method, generates attribute value pairs for a dependency matrix
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

    //Get the attribute-value pairs for a single dependency (one noun and all of its dependents)
    private static AttrValPair[] generateAttrValPairsOneDependencyList(IndexedWord[] dep, int transId) {
        /*
            generate the chain of all eligible nouns, adjectives, and quantifiers that may form an attribute value pair with
            dep's root noun
         */
        AttrValPair[] output = new AttrValPair[0];
        IndexedWord[][] fullObject = defineEnvelope(dep);
        //if fullObject has length 1, don't generate itemsets
        if (fullObject[0].length < 2) {
            return output;
        }
        String[] generatedSets = generate(fullObject);
        output = new AttrValPair[generatedSets.length];
        //translate generated sets into attribute-value pair format
        for (int i = 0; i < generatedSets.length; i++) {
            output[i] = new AttrValPair(generatedSets[i], transId);
        }
        return output;
    }

    //generates attribute-value pairs from a maximal object
    private static String[] generate (IndexedWord[][] base) {
        IndexedWord[] full = base[0];
        String[] output = new String[base[0].length];
        int outputCounter = 0;
        //add the root word
        String object = "";
        String piece;
        /*
           Travel left from the root word, adding each word passed as the feature for a new attribute-value pair,
           and then using the new attribute-value pair as the object for an even larger pair (until an adjective is found,
           as an adjective is never part of the object)
         */
        for (int i = 0; i < full.length; i++) {
            //transform NNS tag to NN tag and NNPS tag to NNP tag (since words will be lemmatized, there will be no plurals)
            String tag = full[i].tag();
            if (tag.equals("NNS")) {
                tag = "NN";
            }
            else if (tag.equals("NNPS")) {
                tag = "NNP";
            }

            //initialize with the root word as the object
            if (i == 0) {
                object = full[i].lemma()+"/"+tag;
                continue;
            }
            piece = "("+object+", "+full[i].lemma()+"/"+tag+")";
            //If the current word is a noun, generate an attribute-value pair and use it as the object for a larger pair
            if (tag.equals("NN") || tag.equals("NNP")) {
                object = piece;
                if (i == full.length - 1) {
                    output[outputCounter] = piece;
                    outputCounter++;
                }
            }
            //If the current word is an adjective, generate an attribute-value pair
            else {
                output[outputCounter] = piece;
                outputCounter++;
            }
        }

        //Eliminate null entries from output
        String[] newOutput = new String[outputCounter];
        for (int i = 0; i < outputCounter; i++) {
            newOutput[i] = output[i];
        }
        return newOutput;
    }

    /*
    Return an array of the maximal object for a given
    root noun (the largest linear chain consisting of a. A root noun and b. nouns and adjectives to the left
    of the root noun, all depending on a., and with no nouns occurring to the left of adjectives)

    Also stipulates that itemsets must consist of relevant items, i.e. that all entries must have at least one
    alphanumeric capital (no itemsets like "(|/NN, |/NN)")
    */
    static IndexedWord[][] defineEnvelope(IndexedWord[] dep) {
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
                //if it is the right position in the linear chain
                if (dep[k].get(IndexAnnotation.class) == i) {
                    //if it has the right tag
                    if ( ((tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS")) && !adjFlag) ||
                            tag.equals("JJ") || tag.equals("JJR") || tag.equals("CD")) {
                        if (/*containsAlphanumeric(dep[k].word())*/ true) {
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

    //checks a string to see if it contains any alphanumeric characters
    public static boolean containsAlphanumeric (String input) {
        for (int i = 0; i < input.length(); i++) {
            char cha = input.charAt(i);
            //if it's a digit
            if (cha - '0' > -1 && cha - '0' < 10) {
                return true;
            }
            //if it's a capital letter
            if (cha - 'A' > -1 && cha - 'A' < 26) {
                return true;
            }
            //if it's a lowercase letter
            if (cha - 'a' > -1 && cha - 'a' < 26) {
                return true;
            }
        }
        return false;
    }
}
