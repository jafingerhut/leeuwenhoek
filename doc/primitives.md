# Clojure/Java compiler use of primitive operations

Start with some example code from [this Clojure reference
documentation](https://clojure.org/reference/java_interop#primitives),
then try out some variations.

See "Hardware and software details" section near the end of this
document for version details.

```
$ clj -A:clj:decompile:socket
```

```
user=> (set! *warn-on-reflection* true)
user=> (require '[clj-java-decompiler.core :refer [decompile disassemble]])
user=> (defn foo [n]
         (loop [i 0]
           (if (< i n)
             (recur (inc i))
             i)))
```

Note that the clj-java-decompiler documentation points out late in the
README that it cannot be used to decompile an already-compiled Clojure
function, so a statement like the below does not produce results for
the internals of the function `asum`, but for simply the reference to
the Var `asum`, which is not terribly interesting.

```
(decompile foo2)
```

A straightforward way to use clj-java-decompiler on functions in your
source code is to make a copy of them, edit them to replace `defn`
with `fn`, and wrap them in a call to `decompile`, as demonstrated
below.

```
user=> (decompile
        (fn foo [n]
          (loop [i 0]
            (if (< i n)
              (recur (inc i))
              i))))

// Decompiling class: user$foo__219
import clojure.lang.*;

public final class user$foo__219 extends AFunction
{
    public static Object invokeStatic(final Object n) {
        long i;
        for (i = 0L; Numbers.lt(i, n); i = Numbers.inc(i)) {}
        return Numbers.num(i);
    }
    
    @Override
    public Object invoke(final Object n) {
        return invokeStatic(n);
    }
}

nil
user=> 
```

Note that while the Clojure compiler is using a primitive `long` for
the loop variable `i`, which is often better for performance, it is
using a call to `Numbers.lt` to compare it to the value `n` with class
`Object`, and it is using method `Number.inc` to calculate the current
value plus one, which is way more general than needed here, i.e. it
handles adding one to ratios, floating point values, BigDecimal, etc.

See function `foo2` below for a small change that produces faster
code.

```
user=> (decompile
        (fn foo2 [n]
          (let [n (int n)]
            (loop [i (int 0)]
              (if (< i n)
                (recur (inc i))
                i)))))

// Decompiling class: user$foo2__262
import clojure.lang.*;

public final class user$foo2__262 extends AFunction
{
    public static Object invokeStatic(final Object n) {
        int n2;
        long i;
        for (n2 = RT.intCast(n), i = RT.intCast(0L); i < n2; i = Numbers.inc(i)) {}
        return Numbers.num(i);
    }
    
    @Override
    public Object invoke(final Object n) {
        return invokeStatic(n);
    }
}

nil
user=> 
```


```
user=> (defn foo [n]
         (loop [i 0]
           (if (< i n)
             (recur (inc i))
             i)))

user=> (time (foo 100000000))
"Elapsed time: 427.553552 msecs"
100000000
user=> 

;; After running it 4 more times, after which the JIT is likely
;;  kicking in to optimize the code.

"Elapsed time: 372.222533 msecs"
100000000
user=> 

user=> (defn foo2 [n]
         (let [n (int n)]
           (loop [i (int 0)]
             (if (< i n)
               (recur (inc i))
               i))))

user=> (time (foo2 100000000))

;; TBD: Every time I redefine foo2 and time it the first time, it is
;; _faster_ than the later runs.  Very strange behavior that I do not
;; know how to explain yet.  Usually it starts out slower on the first
;; run, and then JIT compiles it to native machine code and it is then
;; faster from the onwards.

;; What is going on here?

"Elapsed time: 47.634541 msecs"
100000000

user=> (time (foo2 100000000))
"Elapsed time: 225.711922 msecs"
100000000
user=> (time (foo2 100000000))
"Elapsed time: 230.464443 msecs"
100000000
user=> (time (foo2 100000000))
"Elapsed time: 224.560563 msecs"
100000000
user=> (time (foo2 100000000))
"Elapsed time: 228.214549 msecs"
100000000
user=> 
```

Try things out with Criterium to see if it behaves any differently.

```
user=> (require '[criterium.core :as crit])
user=> (crit/bench (foo2 100000000))
(doc crit/bench)
```


# Hardware and software details

* Mid 2015 15-inch MacBook Pro, 2.2 GHz Intel Core i7
* macOS 10.13.6
* OpenJDK 11 downloaded from AdoptOpenJDK - https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.4%2B11.2/OpenJDK11U-jdk_x64_mac_hotspot_11.0.4_11.tar.gz
* Clojure 1.10.1

Version details:
```
$ uname -a
Darwin JAFINGER-M-W0SR 17.7.0 Darwin Kernel Version 17.7.0: Sun Jun  2 20:31:42 PDT 2019; root:xnu-4570.71.46~1/RELEASE_X86_64 x86_64

$ java -version
openjdk version "11.0.4" 2019-07-16
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.4+11)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.4+11, mixed mode)

$ clj -Sdescribe
{:version "1.10.1.466"
 :config-files ["/usr/local/Cellar/clojure/1.10.1.466/deps.edn" "/Users/jafinger/.clojure/deps.edn" "deps.edn" ]
 :install-dir "/usr/local/Cellar/clojure/1.10.1.466"
 :config-dir "/Users/jafinger/.clojure"
 :cache-dir ".cpcache"
 :force false
 :repro false
 :resolve-aliases ""
 :classpath-aliases ""
 :jvm-aliases ""
 :main-aliases ""
 :all-aliases ""}
```
