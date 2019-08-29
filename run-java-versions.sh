#! /bin/bash

prog_name="$0"

usage() {
    1>&2 echo "usage:"
    1>&2 echo ""
    1>&2 echo "    $prog_name <java-program-number>"
    1>&2 echo ""
    1>&2 echo "<java-program-number> is a number in the range 1 through 4"
}

if [ $# -eq 1 ]
then
    case $1 in
    1|2|3|4) prog_num=$1 ;;
    *) usage ; exit 1 ;;
    esac
else
    usage
    exit 1
fi

class_name="JavaVersion${prog_num}"

CLJ_CP="$HOME/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar"
CLJSPEC1_CP="$HOME/.m2/repository/org/clojure/spec.alpha/0.2.176/spec.alpha-0.2.176.jar"
CLJSPEC2_CP="$HOME/.m2/repository/org/clojure/core.specs.alpha/0.2.44/core.specs.alpha-0.2.44.jar"
CP="src/leeuwenhoek:${CLJ_CP}:${CLJSPEC1_CP}:${CLJSPEC2_CP}"

set -x
javac -Xlint -classpath ${CP} src/leeuwenhoek/${class_name}.java
javap -c src/leeuwenhoek/${class_name}_other.class
javap -c src/leeuwenhoek/${class_name}.class
set +x

for t in none disable 1 2 3 4
do
    case $t in
    none) tiered_opts="" ;;
    disable) tiered_opts="-XX:-TieredCompilation" ;;
    1|2|3|4) tiered_opts="-XX:TieredStopAtLevel=$t" ;;
    *) usage ; exit 1 ;;
    esac
    echo "----------------------------------------"
    echo "tiered_opts=${tiered_opts}"
    set -x
    java ${tiered_opts} -classpath ${CP} ${class_name}
    set +x
done
