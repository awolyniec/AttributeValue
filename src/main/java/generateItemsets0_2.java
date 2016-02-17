/**
 * Created by tehredwun on 2/11/16.
 */
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class generateItemsets0_2 {
    public static Itemset[] generateItemsets(IndexedWord[][] deps, int numTransactions) {
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
                    itemsets = generateItemsets1_1.doubleArray(itemsets, itemsetCounter);
                }
            }
        }

        //get support and confidence for each itemset
        for (int i = 0; i < itemsets.length; i++) {
            setSupportAndConfidence(itemsets, itemsets[i], numTransactions);
        }
        return itemsets;
    }

    private static Itemset[] generateItemsetsOneDependencyList(IndexedWord[] dep, int transId) {
        /*
            generate the chain of all eligible nouns, adjectives, and quantifiers that may form an itemset with
            dep's root noun
         */
        IndexedWord[][] fullObject = generateItemsets1_1.defineEnvelope(dep);

        String[] generatedSets = generate(fullObject);
        Itemset[] output = new Itemset[generatedSets.length];
        //translate generated sets into itemset format
        for (int i = 0; i < generatedSets.length; i++) {
            output[i] = new Itemset(generatedSets[i], transId); //need to get transaction id
        }
        return output;
    }

    //generates strings to be the itemset value
    //somewhat longer than necessary, perhaps
    private static String[] generate (IndexedWord[][] base) {
        IndexedWord[] full = base[0];
        String[] output = new String[base[0].length];
        int outputCounter = 0;
        String object = full[0].toString();
        String piece = "("+object+", "+")";
        for (int i = 1; i < full.length; i++) {
            piece = "("+object+", "+full[i].toString()+")";
            if (full[i].tag().equals("NN") || full[i].tag().equals("NNS") || full[i].tag().equals("NNP")) {
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
    Calculates the support and confidence of an itemset within an array of itemsets
    */
    public static void setSupportAndConfidence(Itemset[] itemsets, Itemset itemset, int numTransactions) {

        int lastObjectMatch = -1; //the last transaction id for which an itemset had the same object as itemset
        int lastFullMatch = -1;   // "" "" "" "" "" and feature as itemset
        int objectMatchCounter = 0;
        int fullMatchCounter = 0;
        /*
            objectMatchCounter: Count the number of transactions with an itemset in the array that has the same object
            as "itemset"

            fullMatchCounter: Count the number of transactions with an itemset in the array that has the same object
            and feature as "itemset"
         */
        for (int i = 0; i < itemsets.length; i++) {
            if (itemsets[i] != itemset) {
                if (itemsets[i].obj == itemset.obj) {
                    int id = itemsets[i].transactionID;
                    if (lastObjectMatch != id) {
                        objectMatchCounter++;
                        lastObjectMatch = id;
                    }
                    if (itemsets[i].feat == itemset.feat && lastFullMatch != id) {
                        fullMatchCounter++;
                        lastFullMatch = id;
                    }
                }
            }
        }

        //Calculate support and confidence
        double support = (((double)fullMatchCounter)/numTransactions) * 100;
        double confidence = (((double)fullMatchCounter)/objectMatchCounter) * 100;
        itemset.setSupport(support);
        itemset.setConfidence(confidence);
    }

}
