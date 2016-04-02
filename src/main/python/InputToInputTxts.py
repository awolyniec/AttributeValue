# reads posts from from xml

import xml.etree.ElementTree as ET
import sys
import io

fout = io.open(sys.argv[2], 'w', encoding="utf8")
fin = open(sys.argv[1], encoding="utf8")

tree = ET.parse(fin)
root = tree.getroot()

def clean_string(s):
    s = s.replace(u'\n', ' ')
    ##    s = s.translate(table)
    ##    s = s.lower()
    ##    words = s.split()
    ##    s = " ".join(set(words) - stopword_set)
    return s

for row in root:
    QuestionID = row.get('Id')
    ##if QuestionID in QIDs:
    if row.get('Title'):
        title = clean_string(row.get('Title'))
    body = clean_string(row.get('Body'))
    s = QuestionID + '\t' + title + '\t' + body + '\n'
    fout.write(s)
fin.close()
fout.close()

