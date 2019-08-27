#! /bin/bash

TMPF=`mktemp gonative-XXXXXX-out.txt`
TMP2=`mktemp gonative-XXXXXX-hsdis-out.txt`

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

clojure -A:clj:1.10:${GCLOG_ALIAS}:jitlog:jitnative -m leeuwenhoek.maybe-jit-slower | tee "${TMPF}"
echo ""
echo ""
echo "----------------------------------------------------------------------"
echo "Begin portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"
egrep '(Elapsed time: |foo2::| defn of foo2 )' "${TMPF}"
echo "----------------------------------------------------------------------"
echo "End portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"

grep "PrintAssembly is disabled" "${TMPF}" > "${TMP2}"
if [ -s "${TMP2}" ]
then
    echo "The following line in the JVM output indicates that your JDK does not"
    echo "have an hsdis library.  See the README for instructions on installing"
    echo "one."
    echo ""
    cat "${TMP2}"
fi

/bin/rm -f "${TMPF}" "${TMP2}"
