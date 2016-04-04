import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Scanner;

/**
 * Created by Alec Wolyniec on 4/1/16.
 *
 */
public class parseXML {
    static void parse (String pathToXMLFile, String pathToTxtFile) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        //documentbuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        //read in the file
        StringBuilder xmlStringBuilder = new StringBuilder();
        File file = new File(pathToXMLFile);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            xmlStringBuilder.append(scanner.nextLine());
        }
        //get document of file
        ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        Document doc = builder.parse(input);
        doc.getDocumentElement().normalize();

        //prepare to write to output file
        FileWriter fileWriter = new FileWriter(pathToTxtFile);

        //get rows and the information in them
        NodeList rows = doc.getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            Element elem = (Element)rows.item(i);
            fileWriter.write(elem.getAttribute("Id")+"\t"+elem.getAttribute("Title").replace("\n", " ")+"\t"+elem.getAttribute("Body").replace("\n", " ")+"\n");
        }
        fileWriter.close();
    }
}
