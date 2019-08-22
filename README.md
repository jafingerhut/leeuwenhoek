# leeuwenhoek

Putting some Clojure, compiled to JVM byte codes, under the
microscope, to see if we can learn anything therefrom.

Named in honor of [Antonie van
Leeuwenhoek](https://ucmp.berkeley.edu/history/leeuwenhoek.html)
[[Wikipedia article]](https://en.wikipedia.org/wiki/Antonie_van_Leeuwenhoek).


## Usage

Prerequisites:
* Tested on macOS and Linux systems -- no attempt to test on Windows yet
* JVM installed as `java` somewhere in your command path
* `clojure` command installed via instructions [here](https://clojure.org/guides/getting_started)

To run some experiments with logging of JIT compilation and GC
enabled, which I have seen demonstrates an effect where it appears
that a Clojure function run the first time, optimized by the JVM at
tier 3, runs about 3 to 4 times faster than when it is later optimized
by the JVM at tier 4.

```bash
$ ./tryme.sh
```

To run several variations of that Clojure function, one which is
identical to the one exercised by the `tryme.sh` command, and several
which do not exhibit the slower execution on the second and later
calls, and thus are probably optimized just as fast at tier 4 JIT
compilation as they were at tier 3.

```bash
$ ./variations.sh
```


## License

Copyright © 2019 Andy Fingerhut

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
