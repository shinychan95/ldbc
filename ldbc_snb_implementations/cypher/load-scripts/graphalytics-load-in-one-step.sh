#!/bin/bash

./delete-neo4j-database.sh && ./convert-csvs.sh && ./graphalytics-import-to-neo4j.sh && ./restart-neo4j.sh
