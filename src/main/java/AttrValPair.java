import edu.stanford.nlp.ling.IndexedWord;

/*
    Created by Alec Wolyniec
    An attribute-value pair containing the object, the feature, and the transaction it's part of. Represents
    attribute-value pairs as (attribute/POS, value/POS)
 */
public class AttrValPair {
    public AttrValPair(String p, int id) {
        setPair(p);
        transactionID = id;
        support = 0;
        confidence = 0;
    }
    private String pair;
    private String obj;
    private String feat;
    private int transactionID;
    private double support;
    private double confidence;

    public String getPair() {return pair;}

    //Also resets obj and feat
    public void setPair(String p) {
        pair = p;
        String[] objAndFeat = ObjAndFeatFromPair(p);
        obj = objAndFeat[0];
        feat = objAndFeat[1];
    }

    public String getObj() {return obj;}

    //also resets value
    public void setObj(String o) {
        obj = o;
        pair = pairFromObjAndFeat(o, feat);
    }

    public String getFeat() {return feat;}

    public void setFeat(String f) {
        feat = f;
        pair = pairFromObjAndFeat(obj, f);
    }

    public int getTransactionID() {return transactionID;}

    public void setTransactionID(int t) {transactionID = t;}

    //From a given pair string, gets a representation of the object and the feature
    public static String[] ObjAndFeatFromPair(String pair) {
        String[] output = new String[2];
        if (pair.charAt(0) != '(') {
            System.err.println("AttrValPair is of the wrong format.");
            System.exit(1);
        }
        int parenthesisStack = 1;
        String obj = "";
        String feat = "";
        boolean scanObj = true;
        boolean scanFeat = false;
        for (int i = 1; i < pair.length() - 1; i++) {
            if (pair.charAt(i) == '(') parenthesisStack++;
            else if (pair.charAt(i) == ')') parenthesisStack--;
            if (pair.charAt(i) == ',' && parenthesisStack == 1) {
                i++;
                scanObj = false;
                scanFeat = true;
                continue;
            }
            if (scanObj) obj += pair.charAt(i);
            else if (scanFeat) feat += pair.charAt(i);
        }
        output[0] = obj;
        output[1] = feat;
        return output;
    }

    //From a given object string and a given feature string, returns a string that represents a full attribute-value pair
    public static String pairFromObjAndFeat(String obj, String feat) {
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

    public String toString() { return pair; }

    public String printFull() {
        String rep = "AttrValPair: "+pair+"      Support: "+support+"      Confidence: "+confidence;
        return rep;
    }
}
