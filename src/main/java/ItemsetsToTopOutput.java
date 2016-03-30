/**
 * Created by tehredwun on 3/14/16.
 *
 *
 */
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.PriorityQueue;

public class ItemsetsToTopOutput {

    /*
        Given an array objsAndFeats containing items of the following format: {String object, String feature}, and a
        count of itemsets, return an array for which array[i] =
        {String support for the itemset in objsAndFeats[i], String confidence for objsAndFeats[i]}
     */
    public static String[][] getSupportAndConfidenceStrings(Itemset[] objectGroup, int itemsetsInAllGroups) {
        //assuming no duplicate itemsets within the same transaction

        //get the number of unique transactions in the group
        int transIDCounter = 0;
        int lastTransID = -1;
        for (int i = 0; i < objectGroup.length; i++) {
            int currentTransID = objectGroup[i].getTransactionID();
            if (currentTransID != lastTransID) {
                transIDCounter++;
                lastTransID = currentTransID;
            }
        }

        String[][] output = new String[objectGroup.length][2];
        //scan through all itemsets
        for (int i = 0; i < objectGroup.length; i++) {
            int[] featureMatches = new int[objectGroup.length];
            //Match all itemsets identical to this one, unless this itemset has already been matched to a previous one
            if (output[i] == null) {
                //get all itemsets identical to this one
                featureMatches[0] = i;
                String currentFeature = objectGroup[i].getFeat();
                int fullMatchCounter = 1;
                for (int j = i+1; j < objectGroup.length; j++) {
                    if (objectGroup[j].getFeat().equals(currentFeature)) {
                        featureMatches[fullMatchCounter] = j;
                        fullMatchCounter++;
                    }
                }
                //get support and confidence
                double support = ((double)fullMatchCounter)/itemsetsInAllGroups;
                String supportString = util.genDoubleString(support, 6);
                double confidence = ((double)fullMatchCounter)/transIDCounter;
                String confidenceString = util.genDoubleString(confidence, 6);
                String[] outputSegment = {supportString, confidenceString};
                //apply to all the feature matches
                for (int j = 0; j < featureMatches.length; j++) {
                    if (j == 0 || featureMatches[j] == 0) {
                        output[j] = outputSegment;
                    }
                }
            }
        }
        return output;
    }

    public static int getItemsetCountFromFile(Scanner inputScanner, Pattern pattern) {
        int itemsetCount = 0;
        int lastTransID = -1;
        Matcher matcher;
        while (inputScanner.hasNextLine()) {
            String itemString = inputScanner.nextLine();
            matcher = pattern.matcher(itemString);
            int currentTransID = Integer.parseInt(matcher.group(3));
            if (currentTransID != lastTransID) {
                itemsetCount++;
                lastTransID = currentTransID;
            }
        }
        return itemsetCount;
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
        //get itemset count
        int itemsetCount = getItemsetCountFromFile(inputScanner, pattern);
        inputScanner = new Scanner(inputFile);
        FileWriter allOutput = new FileWriter(args[1]);
        FileWriter topOutput = new FileWriter(args[2]);
        Heap top20kItemsets = new Heap(20000);
        Itemset[] objectGroup = new Itemset[itemsetCount/100 + 1];

        //write headers
        allOutput.write(generateOutput.fillIndent("Itemset", 100));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Support", 8));
        allOutput.write(generateOutput.fillIndent("", 10));
        allOutput.write(generateOutput.fillIndent("Confidence", 8));
        allOutput.write("\n");
        topOutput.write(generateOutput.fillIndent("Itemset", 100));
        topOutput.write(generateOutput.fillIndent("", 10));
        topOutput.write(generateOutput.fillIndent("Support", 8));
        topOutput.write(generateOutput.fillIndent("", 10));
        topOutput.write(generateOutput.fillIndent("Confidence", 8));
        topOutput.write("\n");

        int objectGroupCounter = 0;
        //Get the support and confidence of each itemset in the file
        while (inputScanner.hasNextLine()) {
            String itemString = inputScanner.nextLine();
            matcher = pattern.matcher(itemString);
            String currentObject;
            String currentFeature;
            int currentTransID;
            //got the itemset
            if (matcher.find()) {
                currentObject = matcher.group(1);
                currentFeature = matcher.group(2);
                currentTransID = Integer.parseInt(matcher.group(3));

                //If the current itemset is not part of the current group (see Line 118), process the support/confidence
                //of each item in the group, send them to output processing, and start a new group
                if (objectGroup[0] != null && !currentObject.equals(objectGroup[0].getObj())) {
                    //get support and confidence strings
                    String[][] supsAndConfs = getSupportAndConfidenceStrings(objectGroup, itemsetCount);

                    //output to allOutput

                    //add to topOutput heap

                    //re-initialize
                    objectGroup = new Itemset[itemsetCount/100 + 1];
                    objectGroupCounter = 0;
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

                //check all other itemsets in the data
                //double support = checkAgainstAllData(currentObject, currentFeature);

                /*
                //print each piece of output
                allOutput.write(generateOutput.fillIndent(Itemset.valueFromObjAndFeat(currentObject, currentFeature), 100));
                allOutput.write(generateOutput.fillIndent("", 10));
                //get support to 6 significant digits
                String doubleString = util.genDoubleString(support, 6);
                allOutput.write(generateOutput.fillIndent(doubleString, 10));
                allOutput.write("\n");

                //get output for the file of the top 20,000 itemsets
                Itemset newIt = new Itemset(Itemset.valueFromObjAndFeat(currentObject, currentFeature), -1);
                newIt.setSupport(support);
                //inserts into the self-balancing heap
                top20kItemsets.insertSet(newIt);
                */
            }
        }
        //fill topOutput
        Itemset[] top20k = new Itemset[top20kItemsets.getSize()];
        for (int i = top20k.length - 1; i > -1; i--) {
            top20k[i] = top20kItemsets.delMin();
        }
        for (int i = 0; i < top20k.length; i++) {
            Itemset currentItemset = top20k[i];
            topOutput.write(generateOutput.fillIndent(currentItemset.getValue(), 100));
            topOutput.write(generateOutput.fillIndent("", 10));
            String doubleString = util.genDoubleString(currentItemset.getSupport(), 6);
            topOutput.write(generateOutput.fillIndent(doubleString, 10));
            if (i < top20k.length) { topOutput.write("\n"); }
        }

        allOutput.close();
        topOutput.close();
    }
}
