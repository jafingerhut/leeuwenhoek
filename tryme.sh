#! /bin/bash

prog_name="$0"

usage() {
    1>&2 echo "usage:"
    1>&2 echo ""
    1>&2 echo "    $prog_name <tiered-compilation-option>"
    1>&2 echo ""
    1>&2 echo "<tiered-compilation-option> should be one of the values below:"
    1>&2 echo ""

    1>&2 echo "    none - use no options to modify Tiered compilation"
    1>&2 echo "    disable - use JVM option -XX:-TieredCompilation"
    1>&2 echo "    <level> in range 1 through 4 - use JVM option -XX:TieredStopAtLevel=<level>"
}

if [ $# -eq 1 ]
then
    case $1 in
    none) TIERED_ALIAS="" ;;
    disable) TIERED_ALIAS=":tiereddisable" ;;
    1|2|3|4) TIERED_ALIAS=":tieredmax$1" ;;
    *) usage ; exit 1 ;;
    esac
else
    usage
    exit 1
fi

TMPF=`mktemp tryme-XXXXXX-out.txt`

echo "----------------------------------------------------------------------"
echo "All JVM output from here until 'Start experiment now'"
echo "is probably irrelevant."
echo "----------------------------------------------------------------------"
GCLOG_ALIAS=":gclog"
clojure -A:clj:1.10${GCLOG_ALIAS} -m leeuwenhoek.hello-world
exit_status=$?
if [ ${exit_status} -ne 0 ]
then
    # One reason the command above can fail is if you try to run it
    # with JDK 8, which does not support the newer style JDK command
    # line options for enabling GC logging.  Try with command line
    # options that should work with older JDKs.
    echo "Failed to run JVM with newer style options to enable GC logging."
    echo "Will use older style options."
    GCLOG_ALIAS=":gclogjdk8"
fi

clojure -A:clj${GCLOG_ALIAS}${TIERED_ALIAS}:jitlog:1.10 -m leeuwenhoek.maybe-jit-slower | tee "${TMPF}"
echo ""
echo ""
echo "----------------------------------------------------------------------"
echo "Begin portion of JVM output relevant to JIT compilation of method foo2"
echo "----------------------------------------------------------------------"
egrep '(Elapsed time: |foo2::| defn of foo2 )' "${TMPF}"
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

/bin/rm -f "${TMPF}"
