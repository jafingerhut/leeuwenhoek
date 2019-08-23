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

echo ""
JAVA_ARCH=`clojure -m leeuwenhoek.get-os-arch`
echo "JAVA_ARCH=${JAVA_ARCH}"
LINUX_ARCH=`uname -m`
echo "LINUX_ARCH=${LINUX_ARCH}"

# In my testing, for AdoptOpenJDK 8, the hsdis .so file needed to be copied
# into the arch-specific directory, but for AdoptOpenJDK 11, it only worked
# if it was in the ${JAVA_HOME}/lib directory.  Rather than trying to figure
# out which kind of JDK is installed at ${JAVA_HOME}, just copy it into both
# places and hopefully that will do no harm.

DST_DIR1="${JAVA_HOME}/lib"
DST_DIR2="${JAVA_HOME}/lib/${JAVA_ARCH}"
set -x
mkdir -p "${DST_DIR1}"
cp -p /usr/lib/${LINUX_ARCH}-linux-gnu/libhsdis.so.0.0.0 "${DST_DIR1}/hsdis-${JAVA_ARCH}.so"
mkdir -p "${DST_DIR2}"
cp -p /usr/lib/${LINUX_ARCH}-linux-gnu/libhsdis.so.0.0.0 "${DST_DIR2}/hsdis-${JAVA_ARCH}.so"
