import edu.stanford.nlp.ling.IndexedWord;

/*
    An object-feature itemset containing the object, the feature, and the transaction it's part of
 */
public class Itemset {
    //need to alter data structure representation
    public Itemset (String v, int id) {
        value = v;
        setUpObjAndFeat();
        transactionID = id;
        support = 0;
        confidence = 0;
    }
    private String value;
    private String obj;
    private String feat;
    private int transactionID;
    private double support;
    private double confidence;

    public String getValue() {return value;}

    //Also resets obj and feat
    public void setValue(String v) {
        value = v;
        setUpObjAndFeat();
    }

    public String getObj() {return obj;}

    public String getFeat() {return feat;}

    public int getTransactionID() {return transactionID;}

    public void setTransactionID(int t) {transactionID = t;}

    private void setUpObjAndFeat() {
        if (value.charAt(0) != '(') {
            System.err.println("Itemset is of the wrong format.");
            System.exit(1);
        }
        int parenthesisStack = 1;
        String obj = "";
        String feat = "";
        boolean scanObj = true;
        boolean scanFeat = false;
        for (int i = 1; i < value.length() - 1; i++) {
            if (value.charAt(i) == '(') parenthesisStack++;
            else if (value.charAt(i) == ')') parenthesisStack--;
            if (value.charAt(i) == ',' && parenthesisStack == 1) {
                i++;
                scanObj = false;
                scanFeat = true;
                continue;
            }
            if (scanObj) obj += value.charAt(i);
            else if (scanFeat) feat += value.charAt(i);
        }

        this.obj = obj;
        this.feat = feat;
    }

    public void setSupport(double s) {
        support = s;
    }

    public double getSupport(){
        return support;
    }

    public void setConfidence(double c) { confidence = c; }

    public double getConfidence() {
        return confidence;
    }

    public String toString() {
        String rep = "Itemset: "+value+"      Support: "+support+"      Confidence: "+confidence;
        //return value;
        return rep;
    }

    public String printFullTerm() {
        return null;
    }

}
