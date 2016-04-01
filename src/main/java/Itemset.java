import edu.stanford.nlp.ling.IndexedWord;

/*
    An object-feature itemset containing the object, the feature, and the transaction it's part of
 */
public class Itemset {
    //need to alter data structure representation
    public Itemset (String v, int id) {
        setValue(v);
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
        String[] objAndFeat = ObjAndFeatFromValue(v);
        obj = objAndFeat[0];
        feat = objAndFeat[1];
    }

    public String getObj() {return obj;}

    //also resets value
    public void setObj(String o) {
        obj = o;
        value = valueFromObjAndFeat(o, feat);
    }

    public String getFeat() {return feat;}

    public void setFeat(String f) {
        feat = f;
        value = valueFromObjAndFeat(obj, f);
    }

    public int getTransactionID() {return transactionID;}

    public void setTransactionID(int t) {transactionID = t;}

    public static String[] ObjAndFeatFromValue(String value) {
        String[] output = new String[2];
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
        output[0] = obj;
        output[1] = feat;
        return output;
    }

    public static String valueFromObjAndFeat(String obj, String feat) {
        return("("+obj+", "+feat+")");
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
}
