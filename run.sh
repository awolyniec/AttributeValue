# Taking in 3 arguments of the following form:
# -An XML input file
# -A .txt output file for all attribute-value pairs
# -A .txt output file for the most frequent attribute-value pairs, up to 20,000 of them
# Returns the attribute-value pairs specified in this project's framework
echo ''
if [[ ! $3 ]] ; then
   echo '#############################################'
   echo 'ERROR. Program takes 3 arguments. '$#' given.'
   echo '#############################################'
   echo ''
   exit 1
fi

mvn compile

# parse XML input, get text, get pairs from text
mvn exec:java -Dexec.mainClass="InputXMLsToAttrValPairs" -Dexec.args="$1 src/main/I-O_data/attrValPairs.txt"

# sort pairs
export LC_ALL="C"
touch src/main/I-O_data/attrValPairsSorted.txt
sort --ignore-case src/main/I-O_data/attrValPairs.txt src/main/I-O_data/attrValPairsSorted.txt

# output itemsets
mvn exec:java -Dexec.mainClass="AttrValPairsToOutput" -Dexec.args="src/main/I-O_data/attrValPairsSorted.txt $2 $3"

# delete intermediate files
rm src/main/I-O_data/input.txt
# rm src/main/I-O_data/parsedInput.txt
rm src/main/I-O_data/attrValPairs.txt
rm src/main/I-O_data/attrValPairsSorted.txt
