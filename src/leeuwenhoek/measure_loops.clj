(ns leeuwenhoek.measure-loops
  (:require [criterium.core :as crit]
            [leeuwenhoek.clojure-loops :as cl])
  (:import (leeuwenhoek.java JavaLoops)))



(comment

(do
(require '[leeuwenhoek.measure-loops :as ml])
(require '[criterium.core :as crit]
         '[leeuwenhoek.clojure-loops :as cl])
(import '(leeuwenhoek.java JavaLoops))
)

(crit/bench (cl/loop-empty-nest1 100000))

;;Evaluation count : 1915500 in 60 samples of 31925 calls.
;;             Execution time mean : 30.306167 µs
;;    Execution time std-deviation : 710.383555 ns
;;   Execution time lower quantile : 29.708048 µs ( 2.5%)
;;   Execution time upper quantile : 32.110387 µs (97.5%)
;;                   Overhead used : 7.113167 ns
;;
;;Found 6 outliers in 60 samples (10.0000 %)
;;	low-severe	 6 (10.0000 %)
;; Variance from outliers : 11.0215 % Variance is moderately inflated by outliers

(JavaLoops/LoopEmptyNest1 5)
;; 5

(crit/bench (JavaLoops/LoopEmptyNest1 100000))

;;Evaluation count : 1995900 in 60 samples of 33265 calls.
;;             Execution time mean : 30.249840 µs
;;    Execution time std-deviation : 617.673777 ns
;;   Execution time lower quantile : 29.712546 µs ( 2.5%)
;;   Execution time upper quantile : 31.375717 µs (97.5%)
;;                   Overhead used : 7.089383 ns

(time (cl/loop-empty-nest2 1000))
;; 1000

(crit/bench (cl/loop-empty-nest2 1000))

;;Evaluation count : 197580 in 60 samples of 3293 calls.
;;             Execution time mean : 303.921176 µs
;;    Execution time std-deviation : 3.648412 µs
;;   Execution time lower quantile : 301.602527 µs ( 2.5%)
;;   Execution time upper quantile : 314.744894 µs (97.5%)
;;                   Overhead used : 9.792871 ns
;;
;;Found 6 outliers in 60 samples (10.0000 %)
;;	low-severe	 3 (5.0000 %)
;;	low-mild	 3 (5.0000 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers


(time (JavaLoops/LoopEmptyNest2 1000))
;; 1000

(crit/bench (JavaLoops/LoopEmptyNest2 1000))

;;Evaluation count : 192480 in 60 samples of 3208 calls.
;;             Execution time mean : 313.907944 µs
;;    Execution time std-deviation : 4.755445 µs
;;   Execution time lower quantile : 310.783246 µs ( 2.5%)
;;   Execution time upper quantile : 327.504131 µs (97.5%)
;;                   Overhead used : 9.792871 ns
;;
;;Found 7 outliers in 60 samples (11.6667 %)
;;	low-severe	 2 (3.3333 %)
;;	low-mild	 5 (8.3333 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

(time (JavaLoops/LoopSumNest1 100000))
;; 4999950000
(time (cl/loop-sum-nest1 100000))
;; 4999950000

(crit/bench (JavaLoops/LoopSumNest1 100000))
;;Evaluation count : 1606140 in 60 samples of 26769 calls.
;;             Execution time mean : 36.476201 µs
;;    Execution time std-deviation : 827.655249 ns
;;   Execution time lower quantile : 35.881213 µs ( 2.5%)
;;   Execution time upper quantile : 38.430369 µs (97.5%)
;;                   Overhead used : 9.852803 ns
;;
;;Found 9 outliers in 60 samples (15.0000 %)
;;	low-severe	 8 (13.3333 %)
;;	low-mild	 1 (1.6667 %)
;; Variance from outliers : 10.9782 % Variance is moderately inflated by outliers

(crit/bench (cl/loop-sum-nest1 100000))
;;Evaluation count : 1591500 in 60 samples of 26525 calls.
;;             Execution time mean : 38.012863 µs
;;    Execution time std-deviation : 517.211192 ns
;;   Execution time lower quantile : 37.705062 µs ( 2.5%)
;;   Execution time upper quantile : 39.325115 µs (97.5%)
;;                   Overhead used : 9.852803 ns
;;
;;Found 5 outliers in 60 samples (8.3333 %)
;;	low-severe	 1 (1.6667 %)
;;	low-mild	 4 (6.6667 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers


(time (JavaLoops/LoopSumNest2 1001))
;; 501501000
(time (cl/loop-sum-nest2 1001))
;; 501501000

(crit/bench (JavaLoops/LoopSumNest2 1001))
;;Evaluation count : 160140 in 60 samples of 2669 calls.
;;             Execution time mean : 376.322028 µs
;;    Execution time std-deviation : 2.512826 µs
;;   Execution time lower quantile : 374.243723 µs ( 2.5%)
;;   Execution time upper quantile : 383.192515 µs (97.5%)
;;                   Overhead used : 9.852803 ns
;;
;;Found 4 outliers in 60 samples (6.6667 %)
;;	low-severe	 3 (5.0000 %)
;;	low-mild	 1 (1.6667 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

(crit/bench (cl/loop-sum-nest2 1001))
;;Evaluation count : 156720 in 60 samples of 2612 calls.
;;             Execution time mean : 384.868161 µs
;;    Execution time std-deviation : 5.834706 µs
;;   Execution time lower quantile : 380.849326 µs ( 2.5%)
;;   Execution time upper quantile : 399.202663 µs (97.5%)
;;                   Overhead used : 9.852803 ns
;;
;;Found 9 outliers in 60 samples (15.0000 %)
;;	low-severe	 5 (8.3333 %)
;;	low-mild	 4 (6.6667 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

)
