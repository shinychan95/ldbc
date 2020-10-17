// Q3. Tag evolution
/*
  :param {
    year: 2010,
    month: 10
  }
*/
WITH
  $year AS year1,
  $month AS month1,
  $year + toInteger($month / 12.0) AS year2,
  $month % 12 + 1 AS month2
// year-month 1
MATCH (tag:Tag)
OPTIONAL MATCH (message1:Message)-[:HAS_TAG]->(tag)
  WHERE message1.creationDate/10000000000000   = year1
    AND message1.creationDate/100000000000%100 = month1
WITH year2, month2, tag, count(message1) AS countMonth1
// year-month 2
OPTIONAL MATCH (message2:Message)-[:HAS_TAG]->(tag)
  WHERE message2.creationDate/10000000000000   = year2
    AND message2.creationDate/100000000000%100 = month2
WITH
  tag,
  countMonth1,
  count(message2) AS countMonth2
RETURN
  tag.name,
  countMonth1,
  countMonth2,
  abs(countMonth1-countMonth2) AS diff
ORDER BY
  diff DESC,
  tag.name ASC
LIMIT 100
