/**
 * Created by tehredwun on 3/14/16.
 */
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.PriorityQueue;

public class ItemsetsToTopOutput {

    private static java.io.File[] inputFiles;
    private static Pattern pattern;

    /*
        Returns the support of a given itemset in all of the data
     */
    public static double checkAgainstAllData (String currentItemset, String currentObject) throws IOException {
        //get a copy of all scanners
        Scanner[] inputScanners = new Scanner[inputFiles.length];
        for (int i = 0; i < inputFiles.length; i++) {
            inputScanners[i] = new Scanner(inputFiles[i]);
        }

        int lastTransID = -1;
        int lastMatchedTransID = -1;
        int matchCount = 0;
        int transIDCount = 0;
        //Check each file
        for (int i = 0; i < inputFiles.length; i++) {
            java.io.File inputFile = inputFiles[i];
            Scanner inputScanner = inputScanners[i];
            //Check each itemset in the file
            while (inputScanner.hasNextLine()) {
                String itemString = inputScanner.nextLine();
                Matcher matcher = pattern.matcher(itemString);
                String testItemset;
                String testObject;
                int testTransID;
                //got the itemset
                if (matcher.find()) {
                    testItemset = matcher.group(1);
                    testObject = matcher.group(2);
                    testTransID = Integer.parseInt(matcher.group(3));

                    //Check for a match
                    if (testTransID != lastTransID) { //may break down if multiple files with 1 transaction each exist
                        lastTransID = testTransID; //assumes each file starts at 0
                        testTransID = ++transIDCount;
                    }
                    //match found
                    if (currentItemset.equals(testItemset) && testTransID != lastMatchedTransID) {
                        matchCount++;
                        lastMatchedTransID = testTransID;
                    }
                }
            }
        }
        return ((double)matchCount)/transIDCount;
    }

    /*
        Take in a file of n itemsets in the following format:
        <object1>:<feature1>;transID1
        <object2>:<feature2>;transID2
        .
        .
        <objectn>:<featuren>;transIDn
        Generate a file containing the top 20,000 itemsets in terms of support, ordered by support, as well as
        their support and confidence, and generate a file containing all itemsets. Also keep a count of the
        number of itemsets and transactions in the first file

        args: Input files from which itemsets will be collected
     */
    public static void main (String[] args) throws IOException {
        FileWriter allOutput = new FileWriter("src/main/itemset_data_test/finalOutput10k.txt");
        allOutput.write(generateOutput.fillIndent("Itemsets", 100));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Support", 8));
        allOutput.write("\n");

        FileWriter topOutput = new FileWriter("src/main/itemset_data_test/topOutput10k.txt");
        topOutput.write(generateOutput.fillIndent("Itemsets", 100));
        topOutput.write(generateOutput.fillIndent("", 10));
        topOutput.write(generateOutput.fillIndent("Support", 8));
        topOutput.write("\n");
        Heap top20kItemsets = new Heap(20000);

        inputFiles = new java.io.File[args.length];
        Scanner[] inputScanners = new Scanner[args.length];
        for (int i = 0; i < args.length; i++) {
            inputFiles[i] = new java.io.File(args[i]);
            inputScanners[i] = new Scanner(inputFiles[i]);
        }
        String patternString = "(.*):(.*);([0-9]*)";
        pattern = Pattern.compile(patternString);

        //Check each input file
        for (int i = 0; i < inputScanners.length; i++) {
            Scanner inputScanner = inputScanners[i];

            Matcher matcher;
            //Check each itemset in the file
            while (inputScanner.hasNextLine()) {
                String itemString = inputScanner.nextLine();
                matcher = pattern.matcher(itemString);
                String currentItemset;
                String currentObject;
                //got the itemset
                if (matcher.find()) {
                    currentItemset = matcher.group(1);
                    currentObject = matcher.group(2);

                    //check all other itemsets in the data
                    double support = checkAgainstAllData(currentItemset, currentObject);

                    //print each piece of output
                    allOutput.write(generateOutput.fillIndent(currentItemset, 100));
                    allOutput.write(generateOutput.fillIndent("", 10));
                    //get support to 6 significant digits
                    String doubleString = util.genDoubleString(support, 6);
                    allOutput.write(generateOutput.fillIndent(doubleString, 10));
                    allOutput.write("\n");

                    //get output for the file of the top 20,000 itemsets
                    Itemset newIt = new Itemset(currentItemset, -1);
                    newIt.setSupport(support);
                    //inserts into the self-balancing heap
                    top20kItemsets.insertSet(newIt);
                }

            }
        }
        //fill topOutput
        Itemset[] top20k = new Itemset[top20kItemsets.getSize()];
        for (int i = top20k.length - 1; i > -1; i--) {
            top20k[i] = top20kItemsets.delMin();
        }
        for (int i = 0; i < top20k.length; i++) {
            Itemset currentItemset = top20k[i];
            topOutput.write(generateOutput.fillIndent(currentItemset.getValue(), 150));
            topOutput.write(generateOutput.fillIndent("", 10));
            String doubleString = util.genDoubleString(currentItemset.getSupport(), 6);
            topOutput.write(generateOutput.fillIndent(doubleString, 10));
            if (i < top20k.length) { topOutput.write("\n"); }
        }

        allOutput.close();
        topOutput.close();
    }
}
