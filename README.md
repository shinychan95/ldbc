# ldbc-project

## 연구 목적
LDBC SNB(Linked Data Benchmark Council Social Network Benchmark)를 활용하여 Database를 benchmark test하는 환경을 구성하고, Postgres database와 연구실에서 개발한 Graph and RDF database를 각각 benchmark test한 뒤 비교 검증하여, 예상 밖 성능 하락이 일어나는 부분을 찾고 이를 개선하기 위한 방안을 모색한다.

## 연구 배경
Social Network 데이터들을 저장하고 처리하는데 있어, 기존 RDB보다 Graph and RDF databases을 활용하면 성능이 뛰어나다는 연구 결과가 있다. 이러한 가설에 대한 검증에 있어서 테스트는 필수이다. 또한 테스트를 통해 database가 가지는 특징, 문제 그리고 한계점을 파악할 수 있다.

## 연구 방법
1. LDBC benchmark test를 위한 개발 환경 설정
LDBC benchmark test는 총 3가지(ldbc_snb_datagen, ldbc_snb_driver, ldbc_snb_implementations) 구성 요소로 이루어진다. Github 내에 각각에 대한 환경 설정 및 실행 방법이 설명되어 있다. 이를 참고하여 코드를 다운받고, 환경 설정 후 실행한다. 각각에 대해 소개하면, 첫번째 ldbc_snb_datagen는 데이터 생산기로 Social Network 데이터를 생산하는 요소이다. 두 번째 ldbc_snb_driver는 데이터 및 사용자 입력을 바탕으로 workload를 생산하여 데이터베이스가 수행하도록 한다. 마지막 ldbc_snb_implementations 은 Postgres 등 기존 데이터베이스에 대해서 benchmark test를 할 수 있도록 instruction을 제공한다.

2. PostgreSQL benchmark test 및 workload 분석
개발 중인 database 테스트를 하기 이전에 Postgres에 대해 선행 테스트를 진행하면서 각 workload가 가지는 의미를 이해하고, 전체적인 benchmark test 과정을 이해한다.

3. 연구실에서 개발 중인 database benchmark test
개발 중인 database에 대해서 benchmark test를 진행하기 위한 환경을 구성하고 test를 진행한다. 후에 각각 성능을 비교해보고, 결과를 분석한다.

## 기대 효과
각 workload는 데이터 처리 및 저장에 있어 여러 측면에서 검증하기 위한 것이다. 즉, 각 workload에 대해 benchmark test의 결과를 비교하면서, database가 어떤 측면에서 우수한 지 혹은 부족한 지 결과를 알 수 있다. 그리고 이는 앞으로 개발하는데 있어 수행해야 할 방향을 명시해줄 것이다.


## 참고 문헌
http://www.ldbcouncil.org/benchmarks/snb
https://github.com/ldbc/ldbc_snb_datagen
https://github.com/ldbc/ldbc_snb_driver
https://github.com/ldbc/ldbc_snb_implementations

