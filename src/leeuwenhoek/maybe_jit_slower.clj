(ns leeuwenhoek.maybe-jit-slower)

(println "Before defn of foo2 #1")
(defn foo2 [n]
  (let [n (int n)]
    (loop [i (int 0)]
      (if (< i n)
        (recur (inc i))
        i))))
(println "After  defn of foo2 #1")

(dotimes [i 10]
  (print (format "Trial %2d: " (inc i)))
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
  (print (format "Trial %2d: " (inc i)))
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
  (print (format "Trial %2d: " (inc i)))
  (time (foo2 100000000)))

(println "----------------------------------------")
(require '[clojure.java.shell :as sh]
         '[clojure.pprint :as pp]
         '[clojure.string :as str])
(import '(java.io IOException))
(defn run-sh [& sh-args]
  (println (str/join " " (cons "$" sh-args)))
  (let [ret (try
              (apply sh/sh sh-args)
              (catch IOException e
                {:exit 1, :out "",
                 :err (str/join " " (cons "IOException occurred during clojure.java.shell/sh, probably because command not found: " sh-args))}))]
    (println (str "[exit status " (:exit ret) "]"))
    (println (:out ret))
    (println (:err ret))))
(run-sh "uname" "-a")
(run-sh "lsb_release" "-a")
(println)
(let [props (System/getProperties)]
  (doseq [p ["os.name"
             "os.version"
             "os.arch"
             "java.runtime.name"
             "java.runtime.version"
             "java.vm.name"
             "java.vm.vendor"
             "java.vm.version"]]
    (println (str p ": " (get props p)))))
(shutdown-agents)
(System/exit 0)
