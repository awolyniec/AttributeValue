import edu.stanford.nlp.ling.IndexedWord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by tehredwun on 3/31/16.
 *
 * Some code created during this project that is no longer in use
 *
 */
public class defunctCode {
    //Removes all characters between "<" and ">"; simple but may cause massive data loss in the worst case
    public static void maxSimpleRemove() throws IOException {
        File file = new File("src/main/dummy.txt");
        String text = "";
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            text += scanner.nextLine() + "\n";
        }
        FileWriter writah = new FileWriter("src/main/dummy.txt");

        boolean scan = true;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '<') {
                scan = false;
            } else if (scan == true) {
                writah.write(text.charAt(i));
            }
            if (text.charAt(i) == '>') {
                scan = true;
            }
        }
        writah.close();
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
                System.out.println(itemsets[i].getValue());
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
    Given a set of n itemsets and a path to an output file, print all itemsets in the following format:
    AttrValPair                 Support     Confidence
    (obj1, feat1)           sup1        conf1
    (obj2, feat2)           sup2        conf1
    .                       .           .
    .                       .           .
    (objn, featn)           supn        confn
    */
    public static void printItemsetsSupAndConfToFile(String path, Itemset[] itemsets) throws IOException {
        FileWriter writah = new FileWriter(path);
        String header = fillIndent("AttrValPair", 100) +"     "+fillIndent("Support", 8)+"     "+fillIndent("Confidence", 10);
        writah.write(header+"\n");

        for (int i = 0; i < itemsets.length; i++) {
            Itemset itemset = itemsets[i];
            String text = "";
            //Make a 150-character column of the first 150 chars in value, and placeholder spaces if needed
            text += fillIndent(itemset.getValue(), 100);
            //get support and confidence
            text += "     "+fillIndent(Double.toString(itemset.getSupport()), 8);
            text += "     "+fillIndent(Double.toString(itemset.getConfidence()), 8);
            writah.write(text+"\n");
        }
        writah.close();
    }
}
