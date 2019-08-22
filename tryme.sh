#! /bin/bash

echo "----------------------------------------------------------------------"
echo "All JVM output from here until 'Start experiment now'"
echo "is probably irrelevant."
echo "----------------------------------------------------------------------"
#clojure -A:clj:gclogjdk8:jitlog:1.10 -m leeuwenhoek.maybe-jit-slower | tee tryme-out.txt
clojure -A:clj:gclog:jitlog:1.10 -m leeuwenhoek.maybe-jit-slower | tee tryme-out.txt
echo ""
echo ""
echo "----------------------------------------------------------------------"
echo "Begin portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"
egrep '(Elapsed time: |foo2::| defn of foo2 )' tryme-out.txt
echo "----------------------------------------------------------------------"
echo "End portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"

echo ""
echo ""
echo "Info about system:"
echo ""
echo "$ uname -a"
uname -a
echo "$ lsb_release -a"
lsb_release -a
echo ""
echo ""
echo "Info about JVM:"
echo ""
clojure -A:clj:1.10 -m leeuwenhoek.collect-jvm-info
