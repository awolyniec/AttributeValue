/**
 * Created by tehredwun on 3/14/16.
 *
 *
 */
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ItemsetsToOutput {
    /*
        Given an array of itemsets, all with support of 0 and the same object, a count of transactions across
        the wider dataset, and the highest index of a non-null entry in the object group array,
        find the support and confidence for each, and set these fields for each itemset
     */
    public static void setSupportAndConfidenceForObjectGroup(Itemset[] objectGroup, int transactionsInAllGroups, int highestNonNull) {
        //assumes no duplicate itemsets within the same transaction

        //get the number of unique transactions in the group
        int transIDCounter = 0;
        int lastTransID = -1;
        for (int i = 0; i < objectGroup.length; i++) {
            if (objectGroup[i] == null) { break; }
            int currentTransID = objectGroup[i].getTransactionID();
            if (currentTransID != lastTransID) {
                transIDCounter++;
                lastTransID = currentTransID;
            }
        }

        String[][] output = new String[objectGroup.length][2];
        //scan through all itemsets
        for (int i = 0; i < highestNonNull; i++) {
            int[] featureMatches = new int[objectGroup.length];
            //Match all itemsets identical to this one, unless this itemset has already been matched to a previous one
            if (objectGroup[i].getSupport() == 0) {
                //get all itemsets identical to this one
                featureMatches[0] = i;
                String currentFeature = objectGroup[i].getFeat();
                int fullMatchCounter = 1;
                for (int j = i+1; j < highestNonNull; j++) {
                    if (objectGroup[j].getFeat().equals(currentFeature)) {
                        featureMatches[fullMatchCounter] = j;
                        fullMatchCounter++;
                    }
                }
                //get support and confidence
                double support = ((double)fullMatchCounter)/transactionsInAllGroups;
                double confidence = ((double)fullMatchCounter)/transIDCounter;
                //apply to all the feature matches
                for (int j = 0; j < fullMatchCounter; j++) {
                    objectGroup[featureMatches[j]].setSupport(support);
                    objectGroup[featureMatches[j]].setConfidence(confidence);
                }
            }
        }
    }

    //since everything is alphabetically ordered, we need to keep a running count of all transIDs or admit some inaccuracy
    //If there are two itemsets in one transaction, and one of them is identical to an itemset in another transaction,
    //alphabetical ordering may cause them to become separated
    //inaccuracy here
    public static int getTransactionCountFromFile(Scanner inputScanner, Pattern pattern) {
        int transactionCount = 0;
        int lastTransID = -1;
        Matcher matcher;
        //test
        //int highestTransID = 0;
        while (inputScanner.hasNextLine()) {
            String itemString = inputScanner.nextLine();
            matcher = pattern.matcher(itemString);
            if (matcher.find()) {
                int currentTransID = Integer.parseInt(matcher.group(3));
                //test
                //if (currentTransID > highestTransID) highestTransID = currentTransID;
                if (currentTransID != lastTransID) {
                    transactionCount++;
                    lastTransID = currentTransID;
                }
            }
        }
        //test
        
        //System.out.println("TRANSACTION COUNT: "+transactionCount);
        //System.out.println("HIGHEST TRANSID: "+highestTransID);
    
        return transactionCount;
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

        args: Input file, file to output all itemsets w/support, file to output the top 20k itemsets w/support
     */
    public static void main (String[] args) throws IOException {
        //initialize vars
        java.io.File inputFile = new java.io.File(args[0]);
        Scanner inputScanner = new Scanner(inputFile);
        String patternString = "(.*):(.*);([0-9]*)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher;

        //get transaction count
        int transactionCount = getTransactionCountFromFile(inputScanner, pattern);
        inputScanner = new Scanner(inputFile);
        FileWriter allOutput = new FileWriter(args[1]);
        FileWriter topOutput = new FileWriter(args[2]);
        MaxSizeHeap top20kItemsets = new MaxSizeHeap(20000);
        Itemset[] objectGroup = new Itemset[transactionCount/100 + 1];

        //write headers
        allOutput.write(generateOutput.fillIndent("Itemset", 100));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Support", 8));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Confidence", 10));
        allOutput.write("\n");
        topOutput.write(generateOutput.fillIndent("Itemset", 100));
        topOutput.write(generateOutput.fillIndent("", 10));
        topOutput.write(generateOutput.fillIndent("Support", 8));
        topOutput.write(generateOutput.fillIndent("", 10));
        topOutput.write(generateOutput.fillIndent("Confidence", 10));
        topOutput.write("\n");

        int objectGroupCounter = 0;
        //Get the support and confidence of each itemset in the file
        boolean lastTrigger = false;
        while (inputScanner.hasNextLine() || lastTrigger) {
            String itemString;
            //get the next line unless this is the last thing to be scanned
            if (lastTrigger) { itemString = ""; }
            else { itemString = inputScanner.nextLine(); }
            matcher = pattern.matcher(itemString);
            String currentObject;
            String currentFeature;
            int currentTransID;
            //got the itemset
            if (matcher.find() || lastTrigger) {
                if (!lastTrigger) {
                    currentObject = matcher.group(1);
                    currentFeature = matcher.group(2);
                    currentTransID = Integer.parseInt(matcher.group(3));
                }
                else {
                    currentObject = "";
                    currentFeature = "";
                    currentTransID = -1;
                }
                //If the current itemset is not part of the current group (see Line 118), process the support/confidence
                //of each item in the group, send them to output processing, and start a new group
                if ((objectGroup[0] != null && !currentObject.equals(objectGroup[0].getObj())) || lastTrigger) {
                    //set support and confidence strings
                    setSupportAndConfidenceForObjectGroup(objectGroup, transactionCount, objectGroupCounter);
                    //test
                    /*
                    for (int i = 0; i < objectGroupCounter; i++) {
                        System.out.println(objectGroup[i].getValue()+" "+objectGroup[i].getSupport()+" "+objectGroup[i].getConfidence());
                    }
                    */
                    //prepare the group for outputting
                    for (int i = 0; i < objectGroupCounter; i++) {
                        //directly print to allOutput
                        Itemset currItemset = objectGroup[i];
                        String supportStr = generateOutput.genDoubleString(currItemset.getSupport(), 6);
                        String confStr = generateOutput.genDoubleString(currItemset.getConfidence(), 6);
                        allOutput.write(generateOutput.fillIndent(currItemset.getValue(), 100));
                        allOutput.write(generateOutput.fillIndent("", 10));
                        allOutput.write(generateOutput.fillIndent(supportStr, 10));
                        allOutput.write(generateOutput.fillIndent("", 10));
                        allOutput.write(generateOutput.fillIndent(confStr, 8));
                        allOutput.write("\n");
                        //add to topOutput heap for later outputting
                        top20kItemsets.insertSet(currItemset);
                    }
                    //re-initialize
                    objectGroup = new Itemset[transactionCount/100 + 1];
                    objectGroupCounter = 0;
                    if (lastTrigger) {
                        break;
                    }
                }

                //Collect all itemsets with the same object into a group
                Itemset newIt = new Itemset(Itemset.valueFromObjAndFeat(currentObject, currentFeature), currentTransID);
                objectGroup[objectGroupCounter] = newIt;
                objectGroupCounter++;
                //double if necessary
                if (objectGroupCounter == objectGroup.length) {
                    Itemset[] newObjectGroup = new Itemset[objectGroupCounter*2];
                    for (int i = 0; i < objectGroupCounter; i++) {
                        newObjectGroup[i] = objectGroup[i];
                    }
                    objectGroup = newObjectGroup;
                }
                //if this is the last itemset, ensure one more loop iteration to print the group
                if (!inputScanner.hasNextLine()) {
                    lastTrigger = true;
                }
            }
        }

        //fill topOutput
        Itemset[] top20k = new Itemset[top20kItemsets.getSize()];
        for (int i = top20k.length - 1; i > -1; i--) {
            top20k[i] = top20kItemsets.delMin();
        }
        for (int i = 0; i < top20k.length; i++) {
            Itemset currItemset = top20k[i];
            String supportStr = generateOutput.genDoubleString(currItemset.getSupport(), 6);
            String confStr = generateOutput.genDoubleString(currItemset.getConfidence(), 6);
            topOutput.write(generateOutput.fillIndent(currItemset.getValue(), 100));
            topOutput.write(generateOutput.fillIndent("", 10));
            topOutput.write(generateOutput.fillIndent(supportStr, 10));
            topOutput.write(generateOutput.fillIndent("", 10));
            topOutput.write(generateOutput.fillIndent(confStr, 8));
            if (i < top20k.length) { topOutput.write("\n"); }
        }
        //terminate
        allOutput.close();
        topOutput.close();
    }
}
