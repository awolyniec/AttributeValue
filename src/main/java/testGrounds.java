import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
/*
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.semgraph.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.*;

//random things and tests
public class testGrounds {
    public static void main (String[] args) throws IOException {
        for (int i = 1; i < 6; i++) {
            String[] s = {"src/main/itemset_data/up"+i+".txt", "src/main/itemset_data/p"+i+".txt"};
            removehtml.html2text(s, true);
        }
    }
}
