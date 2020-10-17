#!/bin/bash

echo "starting preprocessing"

# replace headers
while read line; do
  IFS=' ' read -r -a array <<< $line
  filename=${array[0]}
  header=${array[1]}
  sed -i.bkp "1s/.*/$header/" "${NEO4J_DATA_DIR}/${filename}${POSTFIX}"
done < headers.txt

# replace labels with one starting with an uppercase letter
sed -i.bkp "s/|city$/|City/" "${NEO4J_DATA_DIR}/static/place${POSTFIX}"
sed -i.bkp "s/|country$/|Country/" "${NEO4J_DATA_DIR}/static/place${POSTFIX}"
sed -i.bkp "s/|continent$/|Continent/" "${NEO4J_DATA_DIR}/static/place${POSTFIX}"
sed -i.bkp "s/|company|/|Company|/" "${NEO4J_DATA_DIR}/static/organisation${POSTFIX}"
sed -i.bkp "s/|university|/|University|/" "${NEO4J_DATA_DIR}/static/organisation${POSTFIX}"

# convert each date of format yyyy-mm-dd to a number of format yyyymmddd
sed -i.bkp "s#|\([0-9][0-9][0-9][0-9]\)-\([0-9][0-9]\)-\([0-9][0-9]\)|#|\1\2\3|#g" "${NEO4J_DATA_DIR}/dynamic/person${POSTFIX}"

# convert each datetime of format yyyy-mm-ddThh:mm:ss.mmm+0000
# to a number of format yyyymmddhhmmssmmm
sed -i.bkp "s#|\([0-9][0-9][0-9][0-9]\)-\([0-9][0-9]\)-\([0-9][0-9]\)T\([0-9][0-9]\):\([0-9][0-9]\):\([0-9][0-9]\)\.\([0-9][0-9][0-9]\)+0000#|\1\2\3\4\5\6\7#g" ${NEO4J_DATA_DIR}/dynamic/*${POSTFIX}

# removing .bkp files
rm ${NEO4J_DATA_DIR}/*/*.bkp

echo "preprocessing finished"
