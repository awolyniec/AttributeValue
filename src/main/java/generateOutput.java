/**
 * Created by tehredwun on 2/16/16.
 */
import java.io.*;

public class generateOutput {
    /*
       Returns an input string printed up to <length> characters long. if <length> is greater than
       the length of the input string, adds <input length> - <length> spaces to the output string.
     */
    public static String fillIndent(String input, int length) {
        String output = "";
        for (int i = 0; i < length; i++) {
            if (i >= input.length()) output += " ";
            else output += input.charAt(i);
        }
        return output;
    }

    /*
        Given a set of n itemsets and a path to an output file, print all itemsets and their transaction ids
        in the following format:
        "itemset1:obj1;transID1 itemset2:obj2;transID2 ........ itemsetn:objn;transIDn "
     */
    public static void printItemsetsObjsTransIDsToFile(String path, Itemset[] itemsets, boolean append) throws IOException {
        FileWriter writah = new FileWriter(path, append);
        for (int i = 0; i < itemsets.length; i++) {
            Itemset itemset = itemsets[i];
            writah.write(itemset.getValue()+":"+itemset.getObj()+";"+Integer.toString(itemset.getTransactionID())+"\n");
        }
        writah.close();
    }

    /*
        Given a set of n itemsets and a path to an output file, print all itemsets in the following format:
        Itemset                 Support     Confidence
        (obj1, feat1)           sup1        conf1
        (obj2, feat2)           sup2        conf1
        .                       .           .
        .                       .           .
        (objn, featn)           supn        confn
     */
    public static void printItemsetsSupAndConfToFile(String path, Itemset[] itemsets) throws IOException {
        FileWriter writah = new FileWriter(path);
        String header = fillIndent("Itemset", 100) +"     "+fillIndent("Support", 8)+"     "+fillIndent("Confidence", 10);
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