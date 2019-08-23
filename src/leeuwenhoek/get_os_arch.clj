(ns leeuwenhoek.get-os-arch)

(defn -main [& args]
  (let [props (System/getProperties)]
    (println (get props "os.arch"))))
