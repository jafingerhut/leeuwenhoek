(ns leeuwenhoek.collect-jvm-info)

(defn -main [& args]
  (let [props (System/getProperties)]
    (doseq [p ["os.name"
               "os.version"
               "os.arch"
               "java.runtime.name"
               "java.runtime.version"
               "java.vm.name"
               "java.vm.vendor"
               "java.vm.version"]]
      (println (str p ": " (get props p "(unknown)")))))
  (shutdown-agents))
