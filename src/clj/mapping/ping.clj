(ns mapping.ping
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn ping-ip [ip]
  (sh "ping" "-qc" "5" ip))

(defn parse-avg-time [ping-output]
  (try
    (->> ping-output
         (re-find #"round-trip min/avg/max/stddev = [\d\.]+/([\d\.]+)/")
         second ;; first group
         Double/parseDouble)
    (catch Exception e
      (log/error e (str "Unparsable output from ping:\n" ping-output)))))

(defn get-ping-time [ip]
  (let [ping-res (ping-ip ip)]
    (if (not (zero? (:exit ping-res)))
      (log/error "Ping exited with nonzero status: " (:err ping-res))
      (parse-avg-time (:out ping-res)))))


