#! /bin/bash

CLJ_CP="$HOME/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar"
CLJSPEC1_CP="$HOME/.m2/repository/org/clojure/spec.alpha/0.2.176/spec.alpha-0.2.176.jar"
CLJSPEC2_CP="$HOME/.m2/repository/org/clojure/core.specs.alpha/0.2.44/core.specs.alpha-0.2.44.jar"
CP="src/leeuwenhoek:$CLJ_CP:$CLJSPEC1_CP:$CLJSPEC2_CP"

set -x

for j in 1 2 3 4
do
    javac -Xlint -cp ${CP} src/leeuwenhoek/JavaVersion${j}.java
    COMMON_OPTS="-classpath $CP JavaVersion${j}"
    javap -c src/leeuwenhoek/JavaVersion${j}_other.class
    javap -c src/leeuwenhoek/JavaVersion${j}.class

    java -XX:TieredStopAtLevel=1 $COMMON_OPTS
    java -XX:TieredStopAtLevel=2 $COMMON_OPTS
    java -XX:TieredStopAtLevel=3 $COMMON_OPTS
    java -XX:TieredStopAtLevel=4 $COMMON_OPTS
done
