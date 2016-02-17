/**
 * Created by tehredwun on 1/29/16.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//Various methods to remove HTML tags from text data
public class removehtml {
    //Removes all characters between "<" and ">"; simple but may cause massive data loss in the worst case
    public static void maxSimpleRemove() throws IOException {
        File file = new File("src/main/dummy.txt");
        String text = "";
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            text += scanner.nextLine() + "\n";
        }
        FileWriter writah = new FileWriter("src/main/dummy.txt");

        boolean scan = true;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '<') {
                scan = false;
            } else if (scan == true) {
                writah.write(text.charAt(i));
            }
            if (text.charAt(i) == '>') {
                scan = true;
            }
        }
        writah.close();
    }

    /*
        Taking in the path to a given file, remove from the file all instances of the following HTML tags: p, li,
        ul, ol, a, strong, em, code, pre, h1, h2, h3, br, hr, img.
        Also incidentally removes any text contained in <> or </> that is a combination of any number of the tags
        in the order in which they are given above.
     */
    public static void smartRemove(String path) throws IOException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        FileWriter writah = new FileWriter("src/main/testParse.txt");
        //establish the regex to match in the text
        //if it's between two carrots and under 4 non-whitespace characters, it's probably html. Handle exceptions
        String patternString = "</?(p)??(li)??(ul)??(ol)??([aA]( href=\".*?)??)??(strong)??(em)??(code)??(pre)??(h1)??" +
                "(h2)??(h3)??(br( ??/)??)??(hr( ??/)??)??(img src=.*?)??(strike)??(blockquote)??(s)??(sup)??(sub)??(del)??" +
                "(b)??(i)??(kbd)??(dd)??(dt)??>";
        Pattern pattern = Pattern.compile(patternString);

        /*
            Scan the input text for the desired html tags, line-by-line, and extract all non-html-tag text around them.
            Write the resulting line to the file.
         */
        String text;
        String text2;
        Matcher matcher;
        //scan each line
        while(scanner.hasNextLine()) {
            text = scanner.nextLine() + "\n";
            text2 = "";
            matcher = pattern.matcher(text);
            int lastIndex = 0;

            //Match patterns to the line
            while (matcher.find()) {
                if (lastIndex != matcher.start()) {
                    text2 += text.substring(lastIndex, matcher.start());
                }
                lastIndex = matcher.end();
            }
            text2 += text.substring(lastIndex, text.length()); //collect remaining text, or all text if no tags were found
            //write the completed line to the output file
            writah.write(text2);
        }

        writah.close();
    }

    /*
        uses jsoup to remove HTML tags and pretty much any text within <>. Also has an option to omit all text before
         and including the first non-number character in each line. Writes parsed text to an output file.
     */
    public static void html2text(String path, boolean omitFirst) throws IOException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        FileWriter writah = new FileWriter("src/main/postsParsedNoNums.txt");

        //final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        while (scanner.hasNextLine()) {
            String text;
            if (omitFirst == false) {
                text = scanner.nextLine();
            }
            else {
                int i;
                String t = scanner.nextLine();
                for (i = 0; i < t.length(); i++) {
                    if (t.charAt(i)-'0' < 0 || t.charAt(i)-'0' > 9) {
                        break;
                    }
                }
                text = t.substring(++i, t.length());
            }
            writah.write(Jsoup.parse(text).text() + "\n");
        }
        writah.close();
    }

    public static void main (String[] args) throws IOException {
        //smartRemove("src/main/testParseDontRemove.txt");
        html2text("src/main/postsOriginal.txt", true);
    }
}
