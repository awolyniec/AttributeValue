# Taking in two arguments of the following form:
# -A .txt output file for all attribute-value pairs
# -A .txt output file for the most frequent attribute-value pairs, up to 20,000 of them
# Returns the attribute-value pairs specified in this project's framework
echo ''
if [[ ! $2 ]] ; then
   echo '#############################################'
   echo 'ERROR. Program takes 1 arguments. '$#' given.'
   echo '#############################################'
   echo ''
   exit 1
fi

# output itemsets
mvn exec:java -Dexec.mainClass="AttrValPairsToOutput" -Dexec.args="src/main/I-O_data/sortedAttrValPairs.txt $1 $2"

# delete intermediate files
rm src/main/I-O_data/input.txt
# rm src/main/I-O_data/parsedInput.txt
rm src/main/I-O_data/attrValPairs.txt
rm src/main/I-O_data/sortedAttrValPairs.txt
