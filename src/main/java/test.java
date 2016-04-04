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
import java.util.regex.*;

//random things and tests
public class test {
    public static void main (String[] args) throws IOException {
        File file = new File("/Users/tehredwun/Documents/School/IRLab Research/data/data.xml");
        FileWriter writah = new FileWriter("src/main/development_txts/smallData.xml");
        Scanner scanner = new Scanner(file);
        int counter = 0;
        Pattern pattern = Pattern.compile("row Id=\"([0-9]+)\"");
        while (scanner.hasNextLine()) {
            if (counter == 1000) {
                break;
            }
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                counter++;
            }
            writah.write(line+"\n");
        }
        writah.close();
    }
}
