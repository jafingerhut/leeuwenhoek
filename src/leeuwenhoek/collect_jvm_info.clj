(ns leeuwenhoek.collect-jvm-info)

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

(defn -main [& args]
  (shutdown-agents))
