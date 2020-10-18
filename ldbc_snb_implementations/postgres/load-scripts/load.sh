#!/bin/bash

# exit upon error
set -e

# 변수 설정, 수정할 필요 없다.
PG_DATA_DIR=${PG_DATA_DIR:-$(pwd)/../../../ldbc_snb_datagen/social_network/}
PG_DB_NAME=${PG_DB_NAME:-ldbcsf1}
PG_USER=${PG_USER:-$USER}
PG_FORCE_REGENERATE=${PG_FORCE_REGENERATE:-no}
PG_PORT=${PG_PORT:-5432}

# we regenerate PostgreSQL-specific CSV files for posts and comments, if either
#  - it doesn't exist
#  - the source CSV is newer
#  - we are forced to do so by environment variable PG_FORCE_REGENERATE=yes

if [ ! -f $PG_DATA_DIR/dynamic/post_0_0-postgres.csv -o $PG_DATA_DIR/dynamic/post_0_0.csv -nt $PG_DATA_DIR/dynamic/post_0_0-postgres.csv -o "${PG_FORCE_REGENERATE}x" = "yesx" ]; then
  cat $PG_DATA_DIR/dynamic/post_0_0.csv | \
    awk -F '|' '{ print $1"|"$2"|"$3"|"$4"|"$5"|"$6"|"$7"|"$8"|"$9"|"$11"|"$10"|"}' > \
    $PG_DATA_DIR/dynamic/post_0_0-postgres.csv
fi
if [ ! -f $PG_DATA_DIR/dynamic/comment_0_0-postgres.csv -o $PG_DATA_DIR/dynamic/comment_0_0.csv -nt $PG_DATA_DIR/dynamic/comment_0_0-postgres.csv -o "${PG_FORCE_REGENERATE}x" = "yesx" ]; then
  cat $PG_DATA_DIR/dynamic/comment_0_0.csv | \
    awk -F '|' '{print $1"||"$2"|"$3"|"$4"||"$5"|"$6"|"$7"|"$8"||"$9 $10}' > \
    $PG_DATA_DIR/dynamic/comment_0_0-postgres.csv
fi

# 기존 데이터베이스 삭제. postgres 설치 시 dropdb 및 createdb가 설치된다.
dropdb --if-exists $PG_DB_NAME -U $PG_USER -p $PG_PORT

# 데이터베이스 생성
createdb $PG_DB_NAME -U $PG_USER -p $PG_PORT --template template0 -l "C"

# schema.sql 파일로 명령어 실행. 스키마에 따라 테이블을 생성한다. 
psql -d $PG_DB_NAME -U $PG_USER -p $PG_PORT -a -f schema.sql -v "ON_ERROR_STOP=1"

# snb-load.sql 내에 실질적으로 엑셀 파일로부터 데이터를 로드하는 명령어가 있는데,
# 경로 설정을 위해서 cat 및 sed를 사용한다.
(cat snb-load.sql | sed "s|PATHVAR|$PG_DATA_DIR|g") | psql -d $PG_DB_NAME -U $PG_USER -p $PG_PORT -v "ON_ERROR_STOP=1"

# primary key 및 index 설정
psql -d $PG_DB_NAME -U $PG_USER -p $PG_PORT -a -f schema_constraints.sql -v "ON_ERROR_STOP=1"

# foreign key 설정
psql -d $PG_DB_NAME -U $PG_USER -p $PG_PORT -a -f schema_foreign_keys.sql -v "ON_ERROR_STOP=1"
