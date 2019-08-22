#! /bin/bash

clojure -A:clj:1.10 -m leeuwenhoek.variations-on-a-loop
clojure -A:clj:1.10:decompile -m leeuwenhoek.variations-on-a-loop-disassemble

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
