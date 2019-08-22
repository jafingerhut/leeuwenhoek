(ns leeuwenhoek.maybe-jit-slower)

(println "Before defn of foo2 #1")
(defn foo2 [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
(println "After  defn of foo2 #1")

(println "
----------------------------------------------------------------------
Start experiment now - foo2 was recently defined above, but below is 1st call
----------------------------------------------------------------------")
(dotimes [i 10]
  (print (format "\nTrial %2d: " (inc i)))
  (time (foo2 100000000)))

(println "Before defn of foo2 #2")
(defn foo2 [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
(println "After  defn of foo2 #2")

(dotimes [i 10]
  (print (format "\nTrial %2d: " (inc i)))
  (time (foo2 100000000)))

(println "Before defn of foo2 #3")
(defn foo2 [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
(println "After  defn of foo2 #3")

(dotimes [i 10]
  (print (format "\nTrial %2d: " (inc i)))
  (time (foo2 100000000)))

(println "

----------------------------------------------------------------------
The 'critical path' part of the run is over.  You can ignore GC and
JIT tracing output from here onwards.
----------------------------------------------------------------------")

(require '[clojure.java.shell :as sh]
         '[clojure.string :as str])

(let [sh-ret (sh/sh "ps" "axguwww")]
  (println "
----------------------------------------------------------------------
Use ps to see all JVM command line options used.
----------------------------------------------------------------------
")
  (println (str "[exit status " (:exit sh-ret) "]"))
  (doseq [line (str/split-lines (:out sh-ret))]
    (if (or (and (re-find #"java" line)
                 (re-find #"leeuwenhoek.maybe-jit-slower" line))
            (and (re-find #"^USER " line)
                 (re-find #" PID " line)))
      (println line))))

(defn -main [& args]
  (shutdown-agents))
