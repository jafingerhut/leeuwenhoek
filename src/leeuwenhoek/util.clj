(ns leeuwenhoek.util)


;; getUptime is in units of milliseconds since the JVM started, at
;; least with AdoptOpenJDK 11 on Ubuntu 18.04 Linux, and probably many
;; other JVM versions.  Many JVM log messages like those enabled by
;; command line options such -XX:+PrintCompilation and
;; -XX:+LogCompilation, include this value in their output, so
;; printing them as part of log messages from this code might help
;; relate the events to each other in time.

;; Note that this can be complicated somewhat in that (uptime) here is
;; called before the print or println calls occur, so by the time the
;; print messages appear on the output, it could be some time later
;; than the (uptime) return value.

(defn uptime []
  (.getUptime (java.lang.management.ManagementFactory/getRuntimeMXBean)))

;; Defining modified version of clojure.core/time, so that it includes
;; the value of (uptime) very shortly after the expression is
;; evaluated.

(defmacro my-time [expr msg-str]
  `(let [start# (. System (nanoTime))
         start-uptime# (uptime)
         ret# ~expr
         end# (. System (nanoTime))
         elapsed-msec# (/ (double (- end# start#)) 1000000.0)
         end-uptime# (uptime)]
     (println (str start-uptime# " - " end-uptime#
                   ~msg-str "Elapsed time: " elapsed-msec# " msecs"))
     ret#))
