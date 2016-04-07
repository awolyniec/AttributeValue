# Taking in an argument of the following form:
# -An XML input file
# Returns the attribute-value pairs specified in this project's framework
echo ''
if [[ ! $1 ]] ; then
   echo '#############################################'
   echo 'ERROR. Program takes 1 arguments. '$#' given.'
   echo '#############################################'
   echo ''
   exit 1
fi

mvn compile

# parse XML input, get text, get pairs from text
mvn exec:java -Dexec.mainClass="InputXMLsToAttrValPairs" -Dexec.args="$1 src/main/I-O_data/attrValPairs.txt"

# sort pairs
export LC_ALL="C"
touch src/main/I-O_data/sortedAttrValPairs.txt
sort --ignore-case src/main/I-O_data/attrValPairs.txt > src/main/I-O_data/sortedAttrValPairs.txt

# bash run2.sh src/main/I-O_data/finalOutput.txt src/main/I-O_data/topOutput.txt