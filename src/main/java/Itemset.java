import edu.stanford.nlp.ling.IndexedWord;

/*
    An object-feature itemset containing the object, the feature, and the transaction it's part of
 */
public class Itemset {
    //need to alter data structure representation
    public Itemset (String v, int id) {
        value = v;
        obj = "";
        feat = "";
        transactionID = id;
        support = 0;
        confidence = 0;
    }
    String value;
    String obj;
    String feat;
    int transactionID;
    double support;
    double confidence;

    public void setSupport(double s) {
        support = s;
    }

    public double getSupport(){
        return support;
    }

    public void setConfidence(double c) {
        confidence = c;
    }

    public double getConfidence() {
        return confidence;
    }

    public String toString() {
        String rep = "Itemset: "+value+" Transaction ID: "+transactionID+" Support: "+support+" Confidence: "+confidence;
        //return value;
        return rep;
    }

    public String printFullTerm() {
        return null;
    }

}
