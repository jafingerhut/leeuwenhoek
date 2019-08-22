(ns leeuwenhoek.variations-on-a-loop
  (:import (java.io PushbackReader StringReader)))

;; Pointed out by Christophe Grand in Clojurians Slack #clojure-dev
;; channel on 2019-Aug-22.

(defmacro print-and-eval [s]
  `(let [expr# (read (PushbackReader. (StringReader. ~s)))]
     (println "Clojure code:")
     (println ~s)
     (eval expr#)))

(println "
cgrand: The loop is in return position and returns a
primitive, the function return type is object, this return type
mismatch is the root of the issue:
1: Repro of your case, where the fn returns Object, loop returns primitive")

(def fn-name "foo2-fn-ret-object-loop-ret-prim")
(print-and-eval
"(defn foo2-fn-ret-object-loop-ret-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
")
(dotimes [i 10]
  (print (format "Trial %2d %s: " (inc i) fn-name))
  (time (foo2-fn-ret-object-loop-ret-prim 100000000)))

(println "
cgrand:
2: fn returns Object, loop returns Object")
(def fn-name "foo2-fn-ret-object-loop-ret-object")
(print-and-eval
"(defn foo2-fn-ret-object-loop-ret-object [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        :foo))))
")
(dotimes [i 10]
  (print (format "Trial %2d %s: " (inc i) fn-name))
  (time (foo2-fn-ret-object-loop-ret-object 100000000)))

(println "
cgrand:
3: fn returns primitive, loop returns primitive")
(def fn-name "foo2-fn-ret-prim-loop-ret-prim")
(print-and-eval
"(defn ^long foo2-fn-ret-prim-loop-ret-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
")
(dotimes [i 10]
  (print (format "Trial %2d %s: " (inc i) fn-name))
  (time (foo2-fn-ret-prim-loop-ret-prim 100000000)))

(println "
cgrand:
More experiments:
* explicit boxing on exit of the loop works great")
(def fn-name "foo2-fn-ret-object-loop-ret-boxed-prim")
(print-and-eval
"(defn foo2-fn-ret-object-loop-ret-boxed-prim [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        (Integer/valueOf i)))))
")
(dotimes [i 10]
  (print (format "Trial %2d %s: " (inc i) fn-name))
  (time (foo2-fn-ret-object-loop-ret-boxed-prim 100000000)))

(println "
cgrand:
More experiments:
* explicit boxing on the loop expression doesn't")
(def fn-name "foo2-fn-ret-object-loop-ret-prim-then-boxed")
(print-and-eval
"(defn foo2-fn-ret-object-loop-ret-prim-then-boxed [n]
  (let [n (int n)]
    (Integer/valueOf
     (loop [i (int 0)]
       (if (< i n)
         (recur (inc i))
         i)))))
")
(dotimes [i 10]
  (print (format "Trial %2d %s: " (inc i) fn-name))
  (time (foo2-fn-ret-object-loop-ret-prim-then-boxed 100000000)))

(defn -main [& args]
  (shutdown-agents))
