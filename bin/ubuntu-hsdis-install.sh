#! /bin/bash

if [ "x"${JAVA_HOME} != "x" ]
then
    if [ -d "${JAVA_HOME}" ]
    then
	echo "JAVA_HOME=${JAVA_HOME} environment variable set and names an existing directory"
    else
	echo "JAVA_HOME=${JAVA_HOME} environment variable set, but there is no such directory."
	echo "Ensure it names an existing directory of a Java installation on your system."
	exit 1
    fi
else
    echo "JAVA_HOME environment variable is not set."
    echo "Ensure it names an existing directory of a Java installation on your system."
    exit 1
fi

sudo apt-get install libhsdis0-fcml

JAVA_ARCH=`clojure -m leeuwenhoek.get-os-arch`
LINUX_ARCH=`uname -m`

DST_DIR="${JAVA_HOME}/lib/${JAVA_ARCH}"
set -x
cp -p /usr/lib/${LINUX_ARCH}-linux-gnu/libhsdis.so.0.0.0 "${DST_DIR}/hsdis-${JAVA_ARCH}.so"
