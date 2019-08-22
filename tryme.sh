#! /bin/bash

echo "----------------------------------------------------------------------"
echo "All JVM output from here until 'Start experiment now'"
echo "is probably irrelevant."
echo "----------------------------------------------------------------------"
GCLOG_ALIAS="gclog"
clojure -A:clj:1.10:${GCLOG_ALIAS} -m leeuwenhoek.hello-world
exit_status=$?
if [ ${exit_status} -ne 0 ]
then
    # One reason the command above can fail is if you try to run it
    # with JDK 8, which does not support the newer style JDK command
    # line options for enabling GC logging.  Try with command line
    # options that should work with older JDKs.
    echo "Failed to run JVM with newer style options to enable GC logging."
    echo "Will use older style options."
    GCLOG_ALIAS="gclogjdk8"
fi

clojure -A:clj:${GCLOG_ALIAS}:jitlog:1.10 -m leeuwenhoek.maybe-jit-slower | tee tryme-out.txt
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
