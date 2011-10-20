run various test with wagon http implementations:
* download small xml file (maven metadata file)
* huge zip file (maven distrib 3.0.3)

all tests use various setup:
* http https
* compressed response or not

Sys props available:
* wagon.benchmark.rq.parallel: number of parallel requests
* wagon.benchmark.rq.number: total number of requests to do

