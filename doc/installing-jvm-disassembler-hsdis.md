# Enable printing of x86 assembly listings from Java JIT compiler

This tool looks like it may be interesting for analyzing the results
of the JVM JIT compiler, but I have not tried it yet.  The tool
probably supports Java fairly well, but would have to try it out on a
Clojure project to see if it did anything useful there.

JITwatch: https://github.com/AdoptOpenJDK/jitwatch

I believe it was the author of JITwatch who wrote [these
instructions](https://www.chrisnewland.com/building-hsdis-on-linux-amd64-on-debian-369)
for building and installing hsdis.

Below are my modifications to those instructions, tested on an Ubuntu
18.04.3 desktop Linux system, freshly installed starting with no other
packages than `git` installed.

Note that the `hg clone` command below downloads about 1.7 GBytes of
data, and can thus take a while to complete.

According to this page: https://jdk.java.net/archive/ `jd11u` is the
repository containing the code for JDK 11 update builds, I guess as
opposed to the original release of JDK 11?

This is just an example.  Replace `$HOME/clj/leeuwenhoek` with
whatever directory you created when you cloned your copy of the
leeuwenhoek repository.

```bash
LEE=$HOME/clj/leeuwenhoek
```


```bash
$ sudo apt-get install mercurial
$ cd $HOME
$ mkdir -p $HOME/jdks/hsdis
$ cd jdks
$ hg clone https://hg.openjdk.java.net/jdk-updates/jdk11u
$ cd jdk11u/src/utils/hsdis
$ patch -p1 < $LEE/doc/patches/jdk11u-hsdis-Makefile-for-amd64.patch
$ BINUTILS_VER=2.25
$ curl -O https://ftp.gnu.org/gnu/binutils/binutils-${BINUTILS_VER}.tar.bz2
$ bzcat binutils-${BINUTILS_VER}.tar.bz2 | tar xkf -
$ make BINUTILS=binutils-${BINUTILS_VER} clean
$ make BINUTILS=binutils-${BINUTILS_VER}
$ cp -p ./build/linux-x86_64/hsdis-x86_64.so $HOME/jdks/hsdis/
```

The above just needs to be done once for JDK 11.  It is possible that
the file hsdis-x86_64.so might be useful for multiple JDKs other than
OpenJDK 11, but that requires further testing to confirm.

Then change to the directory with your copy of the `leeuwenhoek`
repository:

```bash
$ cd $LEE
$ ./bin/ubuntu-hsdis-install2.sh
```


# Trying to analyze the weird performance seen with `tryme.sh`

Try to pick one JDK version and stick with it for a while, until
progress on understanding the Intel assembly code is made.  Only once
that is relatively solid, see if the results look similar, or
identical, with other JDKs.

I initially considered AdoptOpenJDK8 as the first one,
semi-arbitrarily, but I noticed the output of gonative.sh has 167
occurrences of lines like this while printing Intel disassembly
listings of JIT-compiled methods, matching "Disassembling failed".
Even after keeping only the part of the output in the "critical
section" of the `gonative.sh` output, there were still 29 such
messages.

```
  0x00007f7448ef1064: Fatal error: Disassembling failed with error code: 15   9588 1418       3       java.util.ArrayList::grow (45 bytes)
```

In particular, this version:

```bash
$ java -version
openjdk version "1.8.0_222"
OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_222-b10)
OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.222-b10, mixed mode)
```

When I instead used this version of OpenJDK, installed via `sudo
apt-get install openjdk-11-jdk-headless` on Ubuntu 18.04 desktop
Linux:

```bash
$ java -version
openjdk version "11.0.4" 2019-07-16
OpenJDK Runtime Environment (build 11.0.4+11-post-Ubuntu-1ubuntu218.04.3)
OpenJDK 64-Bit Server VM (build 11.0.4+11-post-Ubuntu-1ubuntu218.04.3, mixed mode, sharing)
```

I saw 5 such messages in the critical section of the output.  Not
perfect, but better.

I have run my `ubuntu-hsdis-install.sh` script to copy the
hsdis-amd64.so file into each of the JDKs I have used.

Below is sample output from near the end of the `tryme.sh` script.

Results for OpenJDK 11 first:

All "Trial 1" times I saw in 5 runs of `tryme.sh`, in msec, sorted in
ascending order:

53.1
59.5
61.4
64.7
65.6
80.9
85.0
96.4
96.7
98.6
108.7
110.2
118.9
134.8
153.0

All "Trial 2" through "Trial 10" times I saw in 5 runs of `tryme.sh`,
in msec, sorted in ascending order:

55.1
227.4
228.0
229.1
229.9
231.1
232.4
232.7
232.8
233.0
233.4
233.5
233.9
234.5
234.5
234.6
235.0
235.1
235.1
235.3
235.6
235.8
236.1
236.2
236.3
236.3
236.3
236.3
236.4
237.3
237.3
237.5
237.5
237.9
238.2
238.3
238.3
238.4
238.5
238.6
238.6
239.1
239.8
239.9
240.3
240.5
240.7
241.6
241.7
241.8
242.0
242.1
242.3
242.3
242.7
243.2
243.2
243.4
243.8
243.9
244.1
244.1
244.3
244.3
244.5
245.0
245.2
245.3
245.4
245.8
246.0
246.4
247.1
247.3
247.4
247.8
248.2
248.4
248.7
249.0
249.2
249.2
249.4
249.7
249.8
249.9
250.1
250.1
250.3
250.4
250.5
250.8
251.0
251.1
251.2
251.4
251.7
251.8
252.1
252.3
252.9
253.5
254.0
254.1
254.4
254.6
254.7
255.2
255.3
256.1
256.8
256.8
257.0
257.4
257.7
257.8
258.4
260.3
260.4
261.0
262.6
263.7
264.9
266.0
266.3
266.3
266.7
269.2
270.3
270.7
271.3
275.7
281.7
286.2
298.9


Results for AdoptOpenJDK 8 below:

Note: The "Trial  1" elapsed times are all in the range of 41.0 to 53.6
msec.

The "Trial  2" through "Trial 10" elapsed times are all in the range of
192.3 to 230.9 msec.

192.3 / 53.6 = 3.58 times slower.

```
Trial 1:              41.0 msec  up to   53.6 msec
Trial 2 through 10:  192.3 msec  up to  230.9 msec
```

After running tryme.sh 5 more times, the minimum and maximum values I
saw over all of them were a slightly wider range, but not much:

```
Trial 1:              38.8 msec  up to   56.7 msec  2 large outliers: 66.1 83.8
Trial 2 through 10:  191.3 msec  up to  235.4 msec

At least 80% of the Trial 2 through 10 elapsed times were in the range
[190, 210] msec
```

One I saw trial 1 45.0 msec then trial 2 32.7 msec, which I am not
including in the above ranges.

For 100 million iterations, N milliseconds of elapsed time for the
entire loop means N/100 nanoseconds per iteration of the loop, on
average.

So 41 msec total elapsed time means 0.41 nanoseconds per loop
iteration.  I am running this experiment on a 2.2 GHz Intel Core i7
processor that changes clock speeds as it runs, and during this
experiments I saw it go up to 3.16 GHz.  That is a clock period of
0.316 nanoseconds.

Is it actually possible that the compiled code could be executing
every iteration of the loop in an average of 0.41 / 0.316 = 1.3 clock
cycles?  I suppose perhaps with a sufficient amount of loop unrolling
it could?  Either that or perhaps it is optimizing to code that is
adding more than 1 to the loop variable in a single instruction?

```
----------------------------------------------------------------------
Begin portion of JVM output relevant to JIT compilation of method foo2
----------------------------------------------------------------------
Before defn of foo2 #1
After  defn of foo2 #1
   2262 1417 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   2263 1418       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   2263 1419 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   2264 1417 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
   2304 1419 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
Trial  1: "Elapsed time: 53.588248 msecs"
   2306 1421 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   2307 1422       4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   2309 1418       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)   made not entrant
Trial  2: "Elapsed time: 230.86465 msecs"
Trial  3: "Elapsed time: 197.492418 msecs"
Trial  4: "Elapsed time: 197.770999 msecs"
Trial  5: "Elapsed time: 200.488831 msecs"
Trial  6: "Elapsed time: 198.304581 msecs"
Trial  7: "Elapsed time: 199.131531 msecs"
Trial  8: "Elapsed time: 198.486465 msecs"
Trial  9: "Elapsed time: 196.958037 msecs"
Trial 10: "Elapsed time: 196.445735 msecs"
Before defn of foo2 #2
After  defn of foo2 #2
   4143 1470 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   4144 1471       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   4144 1472 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   4145 1470 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
   4182 1472 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
Trial  1: "Elapsed time: 41.015305 msecs"
   4183 1479 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   4186 1480       4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   4190 1471       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)   made not entrant
Trial  2: "Elapsed time: 201.300521 msecs"
Trial  3: "Elapsed time: 198.204295 msecs"
Trial  4: "Elapsed time: 195.378484 msecs"
Trial  5: "Elapsed time: 192.307544 msecs"
Trial  6: "Elapsed time: 195.233438 msecs"
Trial  7: "Elapsed time: 197.051333 msecs"
Trial  8: "Elapsed time: 208.976051 msecs"
Trial  9: "Elapsed time: 194.859076 msecs"
Trial 10: "Elapsed time: 195.730363 msecs"
Before defn of foo2 #3
After  defn of foo2 #3
   5983 1511 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   5987 1512       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   5988 1513 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   5989 1511 %     3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
   6023 1513 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ -2 (37 bytes)   made not entrant
Trial  1: "Elapsed time: 44.296907 msecs"
   6024 1514 %     4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic @ 13 (37 bytes)
   6026 1515       4       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)
   6029 1512       3       leeuwenhoek.maybe_jit_slower$foo2::invokeStatic (37 bytes)   made not entrant
Trial  2: "Elapsed time: 211.074576 msecs"
Trial  3: "Elapsed time: 199.025398 msecs"
Trial  4: "Elapsed time: 202.607324 msecs"
Trial  5: "Elapsed time: 201.866579 msecs"
Trial  6: "Elapsed time: 201.564501 msecs"
Trial  7: "Elapsed time: 201.316688 msecs"
Trial  8: "Elapsed time: 229.725067 msecs"
Trial  9: "Elapsed time: 197.646017 msecs"
Trial 10: "Elapsed time: 203.111797 msecs"
----------------------------------------------------------------------
End portion of JVM output relevant to JIT compilation of method foo2
----------------------------------------------------------------------
```
