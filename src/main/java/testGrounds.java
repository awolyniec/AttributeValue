import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
/*
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.semgraph.*;

//random things and tests
public class testGrounds {
    public static void main (String[] args) {
        double a = 4.000037283;
        long aRound6 = Math.round(a * 100000);
        double finA = ((double)aRound6)/100000;
        //System.out.println(util.roundDoubleToXSigFigs(a, 6));
    }
}
