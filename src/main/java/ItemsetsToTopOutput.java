/**
 * Created by tehredwun on 3/14/16.
 */
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ItemsetsToTopOutput {

    private static java.io.File[] inputFiles;
    private static Scanner[] inputScanners;
    private static Pattern pattern;

    /*
        Returns the support of a given itemset in all of the data
     */
    public static double checkAgainstAllData (String currentItemset, String currentObject) {
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
                while (matcher.find()) {
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
        FileWriter allOutput = new FileWriter("src/main/finalOutput.txt");
        allOutput.write(generateOutput.fillIndent("Itemsets", 150));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Support", 8));

        FileWriter topOutput = new FileWriter("src/main/topOutput.txt");

        inputFiles = new java.io.File[args.length];
        inputScanners = new Scanner[args.length];
        for (int i = 0; i < args.length; i++) {
            inputFiles[i] = new java.io.File(args[i]);
            inputScanners[i] = new Scanner(inputFiles[i]);
        }
        String patternString = "(.*):(.*);([0-9]*)";
        pattern = Pattern.compile(patternString);

        //Check each input file
        for (int i = 0; i < inputFiles.length; i++) {
            java.io.File inputFile = inputFiles[i];
            Scanner inputScanner = inputScanners[i];

            Matcher matcher;
            //Check each itemset in the file
            while (inputScanner.hasNextLine()) {
                String itemString = inputScanner.nextLine();
                matcher = pattern.matcher(itemString);
                String currentItemset;
                String currentObject;
                int currentTransID;
                //got the itemset
                while (matcher.find()) {
                    currentItemset = matcher.group(1);
                    currentObject = matcher.group(2);
                    currentTransID = Integer.parseInt(matcher.group(3));

                    //check all other itemsets in the data
                    double support = checkAgainstAllData(currentItemset, currentObject);

                    //print each piece of output
                    allOutput.write(generateOutput.fillIndent(currentItemset, 150));
                    allOutput.write(generateOutput.fillIndent("", 10));
                    allOutput.write(generateOutput.fillIndent(Double.toString(support), 8));
                }

            }
        }

        allOutput.close();
        topOutput.close();
    }
}
