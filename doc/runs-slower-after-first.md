# Introduction

I came across this odd behavior while testing the performance of
pretty simple Clojure code, and am curious how to explain what is
happening.

It is fairly common to define a Clojure function, do a performance
test on it several times, and see that the first time you run the code
it is relatively slow, and after a few runs the run time reduces, and
stays at that faster speed from then onwards.  This is due to JIT
compilation creating an optimized version of the code that runs faster
than the first version of the code you run.

```
$ clj -A:clj:jitlog:1.10

[ ... many lines of JIT log output deleted here, and later ... ]

user=> (defn foo2 [n]
         (let [n (int n)]
           (loop [i (int 0)]
             (if (< i n)
               (recur (inc i))
               i))))

user=> (time (foo2 100000000))

  83555 1568       3       java.lang.invoke.DirectMethodHandle::internalMemberName (8 bytes)
  83555 1569       3       java.lang.CharacterData::of (120 bytes)
  83556 1570       3       java.io.FilterReader::read (8 bytes)
  83557 1571       3       clojure.lang.Util::equiv (9 bytes)
  83559 1572       4       clojure.lang.Symbol::intern (52 bytes)
  83560 1573       3       java.lang.reflect.Method::getParameterTypes (11 bytes)
  83560 1574       3       clojure.lang.SeqIterator::next (31 bytes)
  83561 1533       4       clojure.asm.Type::appendDescriptor (234 bytes)   made not entrant
  83561 1575       3       clojure.asm.commons.GeneratorAdapter::checkCast (19 bytes)
  83562 1576       3       java.lang.String::isEmpty (14 bytes)
  83562 1577       3       clojure.lang.Numbers::inc (17 bytes)
  83565 1579 %     3       user$foo2::invokeStatic @ 13 (37 bytes)
  83566 1580       3       user$foo2::invokeStatic (37 bytes)
  83590  817       3       clojure.lang.Symbol::intern (52 bytes)   made not entrant
  83590 1581 %     4       user$foo2::invokeStatic @ 13 (37 bytes)
  83591 1579 %     3       user$foo2::invokeStatic @ 13 (37 bytes)   made not entrant
  83591 1578       4       clojure.lang.Numbers::inc (17 bytes)
  83592 1577       3       clojure.lang.Numbers::inc (17 bytes)   made not entrant
  83631 1581 %     4       user$foo2::invokeStatic @ 13 (37 bytes)   made not entrant
  83632 1582       3       jdk.internal.math.FDBigInteger::mult (64 bytes)
  83632 1583       3       jdk.internal.math.FDBigInteger::<init> (30 bytes)
  83632 1584       3 "Elapsed time: 68.722901 msecs"
      jdk.internal.math.FDBigInteger::trimLeadingZeros (57 bytes)
  83633 1585       3       java.lang.Character::equals (29 bytes)
  83634 1586       3       java.lang.Class::getInterfaces (49 bytes)
  83634 1587     n 0       java.lang.reflect.Array::getLength (native)   (static)
  83634 1588       3       java.lang.Class::getInterfaces (6 bytes)
  83634 1590       3       clojure.lang.Var::fn (8 bytes)
  83634 1591       3       clojure.lang.ArraySeq::createFromObject (212 bytes)
100000000
  83635 1589       3       clojure.core$bases::invokeStatic (65 bytes)
user=>   83636 1592       3       clojure.core$class_QMARK_::invokeStatic (19 bytes)
  83636 1593       1       clojure.lang.MultiFn::getPreferTable (5 bytes)

user=> (time (foo2 100000000))
 104147 1594       3       clojure.lang.LineNumberingPushbackReader::read (76 bytes)
 104148 1595   !   3       java.io.PushbackReader::read (54 bytes)
 104148 1599     n 0       jdk.internal.misc.Unsafe::putInt (native)   
 104148 1596       3       clojure.lang.RT::get (26 bytes)
 104149 1597       3       clojure.lang.KeywordLookupSite::fault (31 bytes)
 104150 1600       3       clojure.spec.alpha$reg_resolve_BANG_::invoke (7 bytes)
 104150 1601       3       clojure.spec.alpha$reg_resolve_BANG_::invokeStatic (100 bytes)
 104151 1602       4       java.lang.reflect.Method::copy (84 bytes)
 104151 1598       1       clojure.core$assoc__5416::getRequiredArity (2 bytes)
 104152 1603       3       clojure.asm.Type::getArgumentsAndReturnSizes (138 bytes)
 104154 1605       3       clojure.asm.Type::appendDescriptor (234 bytes)
 104155 1185       3       java.lang.reflect.Method::copy (84 bytes)   made not entrant
 104155 1606 %     4       user$foo2::invokeStatic @ 13 (37 bytes)
 104156 1604       3       clojure.lang.Compiler::maybePrimitiveType (46 bytes)
 104158 1607       4       user$foo2::invokeStatic (37 bytes)
 104160 1580       3       user$foo2::invokeStatic (37 bytes)   made not entrant
 104379 1608       3       sun.nio.cs.UTF_8$Encoder::encodeArrayLoop (489 bytes)
"Elapsed time: 226.024522 msecs"
100000000
user=>  104379 1610       4       jdk.internal.reflect.ReflectionFactory::copyMethod (10 bytes)
 104381 1609       3       java.nio.ByteBuffer::position (6 bytes)
 104383 1183       3       jdk.internal.reflect.ReflectionFactory::copyMethod (10 bytes)   made not entrant

```

Additional evaluations of the expression `(time (foo2 100000000))`
show a similar longer run time of around 225 msec.


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
