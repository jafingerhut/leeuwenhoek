(ns leeuwenhoek.variations-on-a-loop-disassemble
  (:require [clojure.pprint :as pp]
            [clj-java-decompiler.core :as d])
  (:import (java.io PushbackReader StringReader)))

;; Pointed out by Christophe Grand in Clojurians Slack #clojure-dev
;; channel on 2019-Aug-22.

(defmacro scrutinize [s]
  `(let [expr# (read (PushbackReader. (StringReader. ~s)))]
     (println "Clojure code:")
     (println ~s)
     (println "clj-java-decompiler/disassemble output:")
     (d/decompile-form {:decompiler :bytecode} expr#)
     (println "clj-java-decompiler/decompile output:")
     (d/decompile-form {:decompiler :java}  expr#)))


(println "
cgrand: The loop is in return position and returns a
primitive, the function return type is object, this return type
mismatch is the root of the issue:
1: Repro of your case, where the fn returns Object, loop returns primitive")

(def fn-name "foo2-fn-ret-object-loop-ret-prim")
(println (str "JVM byte code for: " fn-name))
(scrutinize
"(fn foo2-fn-ret-object-loop-ret-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
")

(println "
cgrand:
2: fn returns Object, loop returns Object")
(def fn-name "foo2-fn-ret-object-loop-ret-object")
(println (str "JVM byte code for: " fn-name))
(scrutinize
"(fn foo2-fn-ret-object-loop-ret-object [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        :foo))))
")

(println "
cgrand:
3: fn returns primitive, loop returns primitive")
(def fn-name "foo2-fn-ret-prim-loop-ret-prim")
(println (str "JVM byte code for: " fn-name))
(scrutinize
"(fn ^long foo2-fn-ret-prim-loop-ret-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
")

(println "
cgrand:
More experiments:
* explicit boxing on exit of the loop works great")
(def fn-name "foo2-fn-ret-object-loop-ret-boxed-prim")
(println (str "JVM byte code for: " fn-name))
(scrutinize
"(fn foo2-fn-ret-object-loop-ret-boxed-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        (Integer/valueOf i)))))
")

(println "
cgrand:
More experiments:
* explicit boxing on the loop expression doesn't")
(def fn-name "foo2-fn-ret-object-loop-ret-prim-then-boxed")
(println (str "JVM byte code for: " fn-name))
(scrutinize
"(fn foo2-fn-ret-object-loop-ret-prim-then-boxed [n]
  (let [n (int n)]
    (Integer/valueOf
     (loop [i (int 0)]
       (if (< i n)
         (recur (inc i))
         i)))))
")

(defn -main [& args]
  (shutdown-agents))
