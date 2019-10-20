(ns leeuwenhoek.clojure-loops)

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(defn loop-empty-nest1 [arg-n]
  (let [n (long arg-n)]
    (loop [i 0]
      (if (< i n)
        (recur (inc i))
        i))))

(defn loop-empty-nest2 [arg-n]
  (let [n (long arg-n)]
    (loop [i 0]
      (if (< i n)
        (let [inner-loop-return (loop [j 0]
                                  (if (< j n)
;;                                    (do
;;                                      (println "i=" i "j=" j)
                                    (recur (inc j))
;;                                      )
                                    j))]
          (recur (inc i)))
        i))))

(defn loop-sum-nest1 [arg-n]
  (let [n (long arg-n)]
    (loop [i 0
           sum 0]
      (if (< i n)
        (recur (inc i) (+ sum i))
        sum))))

(defn loop-sum-nest2 [arg-n]
  (let [n (long arg-n)]
    (loop [i 0
           outer-sum 0]
      (if (< i n)
        (let [inner-loop-sum (long (loop [j 0
                                          inner-sum 0]
                                     (if (< j n)
                                       (recur (inc j) (+ inner-sum j))
                                       inner-sum)))]
          (recur (inc i) (+ (+ outer-sum inner-loop-sum)
                            i)))
        outer-sum))))
