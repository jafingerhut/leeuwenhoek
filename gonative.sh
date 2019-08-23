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

clojure -A:clj:1.10:${GCLOG_ALIAS}:jitlog:jitnative -m leeuwenhoek.maybe-jit-slower | tee tryme-out.txt
echo ""
echo ""
echo "----------------------------------------------------------------------"
echo "Begin portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"
egrep '(Elapsed time: |foo2::| defn of foo2 )' tryme-out.txt
echo "----------------------------------------------------------------------"
echo "End portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"

echo "If you see a line in the output like this ('hsdis' is a good"
echo "string to search for):"
echo ""
echo "    Could not load hsdis-amd64.so; library not loadable; PrintAssembly is disabled"
echo ""
echo "and if you are running on an Ubuntu Linux system, try running"
echo "the shell script below to attempt installing the hsdis shared"
echo "library, which in my testing has successfully enabled the JVM to"
echo "print native Intel assembly code for JIT-compiled methods when"
echo "using these JDKs on an Intel/AMD system:"
echo ""
echo "    * AdoptOpenJDK 8, 11, 12"
echo "    * Ubuntu OpenJDK 8, 11"
echo "    * Amazon Corretto JDK 8, 11"
echo "    * Azul Zulu JDK 8, 11"
echo ""
echo " ./bin/ubuntu-hsdis-install.sh
