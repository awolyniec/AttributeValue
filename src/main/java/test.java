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
public class test {
    public static void main (String[] args) throws IOException {
        int[] HowToBasic = new int[4];
        FileWriter file = new FileWriter("src/main/development_txts/johncena.txt");
        for (int i = 0; i < HowToBasic.length; i++) {
            file.write("doo");
        }
        file.close();
    }
}
