# Taking in 3 arguments of the following form:
# -An XML input file
# -A .txt output file for all itemsets
# -A .txt output file for the most frequent itemsets, up to 20,000 of them
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

# parse XML input, get text
python src/main/python/InputToInputTxts.py $1 src/main/I-O_data/input.txt

# get itemsets from text
mvn exec:java -Dexec.mainClass="InputTxtsToItemsets" -Dexec.args="src/main/I-O_data/input.txt src/main/I-O_data/itemsets.txt"

# sort itemsets
export LC_ALL="C"
touch src/main/I-O_data/itemsetsSorted.txt
sort --ignore-case src/main/I-O_data/itemsets.txt src/main/I-O_data/itemsetsSorted.txt

# output itemsets
mvn exec:java -Dexec.mainClass="ItemsetsToTopOutput" -Dexec.args="src/main/I-O_data/itemsetsSorted.txt $2 $3"

# delete intermediate files
rm src/main/I-O_data/input.txt
rm src/main/I-O_data/itemsets.txt
rm src/main/I-O_data/itemsetsSorted.txt
