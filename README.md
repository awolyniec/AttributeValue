# AttributeValue

Project description
--------------------------------------



Methodology/Procedure
--------------------------------------


How to use
--------------------------------------
1 - Ensure that the JAVA_HOME variable is set, preferably to version 1.8.0_73 or later of the JDK
2 - Ensure that Maven, preferably version 3.3.9 or later, is installed and included in the classpath
3 - Run the "run.sh" file in the root project directory (ex: enter "bash run.sh" into Bash when in the root directory).
    Include 3 arguments as follows:
    1: A path to input data (an XML file containing discrete entries denoted with the tag "row", and the text as the
       value of the "body" attribute)
    2: A path to a file that will contain all of the outputted attribute-value pairs, plus the support and confidence
       of each
    3: A path to a file that will contain up to the top 20,000 most frequent outputted attribute-value pairs, plus the
       support and confidence of each

Directory structure and relevant file descriptions
--------------------------------------
1 - .idea
2 - .src/main
    i. defunctJava
    ii. development_txts
    iii. I-O_data
    iv. java
        - Package containing all Java classes needed and used in the execution of the project (all are .java classes)
         a. AttrValPair
            -A representation of an attribute-value pair, with fields for the attribute, value, transaction ID (if the
             pair is part of a collection of attribute-value pairs from various transactions), and frequency metrics
             such as support and confidence
         b. AttrValPairsToOutput
         c. combineAttrValPairs
         d. generateAttrValPairs0_2
         e. generateOutput
         f. getDependencies
         g. InputXMLsToAttrValPairs
         h. MaxSizeHeap
         i. parseXML
         j. RemoveHTMLTagsFromTXTs
         k. splitInputs
         l. test
    v. resources
3 - target
4 - AttributeValue.iml
5 - pom.xml
    -Project object model
6 - README.md
    -This file
7 - run.sh
    -Bash shell script that controls the way in which the project is run