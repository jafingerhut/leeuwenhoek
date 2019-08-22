(ns leeuwenhoek.collect-jvm-info
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str])
  (:import (java.io IOException)))

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

(defn -main [& args]
  (shutdown-agents))
