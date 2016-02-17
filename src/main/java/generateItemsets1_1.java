import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
/*
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.semgraph.*;


//NOTE: This has not been altered to conform with the new layout of itemsets, and will thus generate a list of
//null ones.
public class generateItemsets1_1 {
    /*
        generate all itemsets for a given set of dependencies. Deps is an array of arrays; each array inside of it
        consists of a word followed by all words that depend on it

        If there is a root word without eligible dependencies for itemsets, return a null entry for it
     */
    public static Itemset[] generateItemsets (IndexedWord[][] deps) {
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
        Itemset[] itemsets = new Itemset[depCounter];
        int itemsetCounter = 0;
        //Gather all possible itemsets for each dependency; gather a list of all of them
        for (int i = 0; i < deps.length; i++) {
            //get the list for one dependency
            Itemset[] itemsetsOneDep = generateItemsetsOneDependencyList(deps[i], i);

            for (int j = 0; j < itemsetsOneDep.length; j++) {
                itemsets[itemsetCounter] = itemsetsOneDep[j];
                itemsetCounter++;
                //double array size if full
                if (itemsetCounter == itemsets.length) {
                    itemsets = doubleArray(itemsets, itemsetCounter);
                }
            }
        }
        return itemsets;
    }

    /*
        generate all itemsets for one dependency
     */
    // NN/NNS/JJ/JJR/CD??
    private static Itemset[] generateItemsetsOneDependencyList(IndexedWord[] dep, int transId) {
        IndexedWord[][] fullObject = defineEnvelope(dep);
        IndexedWord[][] generatedSets = generate(fullObject);
        Itemset[] output = new Itemset[generatedSets.length];
        //translate generated sets into itemset format
        for (int i = 0; i < generatedSets.length; i++) {
            //output[i] = new Itemset(generatedSets[i], null, null, transId); //need to get transaction id
        }
        return output;
    }

    /*
        Return an array of the maximal object and the maximal feature for a given
        root word
     */
    static IndexedWord[][] defineEnvelope(IndexedWord[] dep) {
         /*
            Define envelope of possible itemset elements for the root word
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
                    if ( ((tag.equals("NN") || tag.equals("NNS")) || tag.equals("NNP") && !adjFlag) ||
                            tag.equals("JJ") || tag.equals("JJR") || tag.equals("CD")) {
                        //add
                        objectWords[objectWordsCounter] = dep[k];
                        objectWordsCounter++;

                        //if it's an adjective, activate the adjective flag
                        if (!(tag.equals("NN")) && !(tag.equals("NNS")) && !(tag.equals("NNP"))) {
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
        Generate all possible itemsets for a given dependency envelope, including
        all possible interpretations of a compound word (or potential compound word)
     */
    private static IndexedWord[][] generate (IndexedWord[][] base) {
        //Check to see if the object and the feature each consist of just one word
        if (base[0].length == 1 && base[1].length == 1) {
            IndexedWord[][] labeled = {{null, base[0][0], base[1][0], null}};
            return labeled;
        }
        else {
            //Itemset for all the permutations of the object
            IndexedWord[][][] objPerms = getPermutations(base[0]);
            IndexedWord[][] objInterps = new IndexedWord[1][]; //make sure to double this if needed
            int objInterpCounter = 0;
            for (int i = 0; i < objPerms.length; i++) {
                IndexedWord[][] permInterps = generate(objPerms[i]);
                for (int j = 0; j < permInterps.length; j++) {
                    objInterps[objInterpCounter] = permInterps[j];
                    objInterpCounter++;
                    objInterps = doubleArrayIfFull(objInterps, objInterpCounter);
                }
            }

            //Itemset for all the permutations of the feature
            IndexedWord[][][] featPerms = getPermutations(base[1]);
            IndexedWord[][] featInterps = new IndexedWord[1][]; //make sure to double this if needed
            int featInterpCounter = 0;
            for (int i = 0; i < featPerms.length; i++) {
                IndexedWord[][] featPermInterps = generate(featPerms[i]);
                for (int j = 0; j < featPermInterps.length; j++) {
                    featInterps[featInterpCounter] = featPermInterps[j];
                    featInterpCounter++;
                    featInterps = doubleArrayIfFull(featInterps, featInterpCounter);
                }
            }

            //Cross objInterps with featInterps and return
            IndexedWord[][] combinedInterps = new IndexedWord[objInterpCounter * featInterpCounter][];
            int combinedInterpsCounter = 0;
            for (int i = 0; i < objInterps.length; i++) {
                for (int j = 0; j < featInterps.length; j++) {
                    //Combines an object interpretation with a feature interpretation
                    IndexedWord[] entry = new IndexedWord[objInterps[i].length + featInterps[j].length + 2];
                    entry[0] = null;
                    int entryCounter = 1;
                    for (int k = 0; k < objInterps[i].length; k++) {
                        entry[entryCounter] = objInterps[i][k];
                        entryCounter++;
                    }
                    for (int k = 0; k < featInterps[j].length; j++) {
                        entry[entryCounter] = featInterps[j][k];
                        entryCounter++;
                    }
                    entry[entryCounter] = null;

                    //Adds the combined interpretation to the final array
                    combinedInterps[combinedInterpsCounter] = entry;
                    combinedInterpsCounter++;
                }
            }
            return combinedInterps;
        }
    }

    /*

     */
    static IndexedWord[][][] getPermutations (IndexedWord[] objOrFeat) {
        IndexedWord[][][] fin = new IndexedWord[objOrFeat.length-1][][];
        int finCounter = 0;
        for (int i = 0; i < objOrFeat.length - 1; i++) {
            IndexedWord[][] entry = new IndexedWord[2][];
            IndexedWord[] objEntry = new IndexedWord[i+1];
            IndexedWord[] featEntry = new IndexedWord[objOrFeat.length-i-1];
            for (int j = 0; j <= i; j++) {
                objEntry[j] = objOrFeat[j];
            }
            for (int j = i+1; j < objOrFeat.length; j++) {
                featEntry[j-i-1] = objOrFeat[j];
            }
            entry[0] = objEntry;
            entry[1] = featEntry;
            fin[finCounter] = entry;
            finCounter++;
        }
        return fin;
    }

    //doubles the size of an itemset array
    static Itemset[] doubleArray(Itemset[] input, int inputCounter) {
        Itemset[] output = new Itemset[inputCounter * 2];
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
}

