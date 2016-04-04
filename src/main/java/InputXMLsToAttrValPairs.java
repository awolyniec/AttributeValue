import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
/*
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
import edu.stanford.nlp.util.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Properties;

/**
 * Created by Alec Wolyniec on 1/14/16.
 */
public class InputXMLsToAttrValPairs {
    /*
        Args:
        0: A path to an xml file containing the input
        1: A path to a txt file to which outputs will be printed
     */
    public static void main (String[] args) throws IOException, ParserConfigurationException, SAXException {
        //parse the xml into a txt file
        parseXML.parse(args[0], "src/main/I-O_data/input.txt");

        //Get a version of the txt file without html tags
        String text = RemoveHTMLTagsFromTXTs.html2text("src/main/I-O_data/input.txt", "src/main/I-O_data/parsedInput.txt", true);

        //get text
        /*
        String text = "";
        File parsedInput = new File("src/main/I-O_data/parsedInput.txt");
        Scanner scanner = new Scanner(parsedInput);
        while (scanner.hasNextLine()) {
            text += scanner.nextLine();
        }
        */

        /* creates a StanfordCoreNLP object, with tokenization, sentence-splitting, POS-tagging, lemmatization,
        syntactic parsing, and dependency parsing
        */
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        //full: tokenize, ssplit, pos, lemma, parse, depparse
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        long annotBegin = System.currentTimeMillis();
        pipeline.annotate(document);
        long annotTime = ((System.currentTimeMillis() - annotBegin)/1000);
        System.out.println("Annotated...in "+annotTime+" seconds.");

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        //Generates and prints a list of noun dependencies in the document
        long depBegin = System.currentTimeMillis();
        IndexedWord[][] deps = getDependencies.generateNounDependencies(sentences);
        long depTime = ((System.currentTimeMillis() - depBegin)/1000);
        System.out.println("Generated noun dependencies...in "+depTime+" seconds.");
        //printNounDependencies(deps);

        long itemBegin = System.currentTimeMillis();
        AttrValPair[] pairs = generateAttrValPairs0_2.generateAttrValPairs(deps, deps.length);
        long itemTime = ((System.currentTimeMillis() - itemBegin)/1000);
        System.out.println("Generated attribute value pairs...in "+itemTime+" seconds.");
        /*
            for (int i = 0; i < pairs.length; i++) {
                System.out.println(pairs[i].toString());
            }
         */
        //System.out.println("Number of pairs: "+pairs.length);

        /*
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
            for (CoreLabel token: tokens) {
                System.out.println();
                // this is the text of the token
                //String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                //String pos = token.get(PartOfSpeechAnnotation.class);
                //System.out.println(word + "/" + pos);
                //this is the NER tag of the token
                //String ne = token.get(NERAnnotation.class);
            }
            /*
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeAnnotation.class);
            System.out.println(tree);
            System.out.println();
        }
        */
        //
        //prints the output to a file specified in args
        generateOutput.printPairFieldsToFile(args[1], pairs, false);
    }
}
