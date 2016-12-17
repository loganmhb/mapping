(ns mapping.db
  (:require [clojure.java.jdbc :as jdbc]))


(defn create-results-table-ddl
  "Create the table for ping results, if it doesn't exist."
  [conn]
  (jdbc/create-table-ddl
   "ping_results"
   [[:ip "varchar(32)"]
    [:time :double]]))

(def settings {:classname "org.h2.Driver"
               :subprotocol "h2"
               :subname "/tmp/mapping"
               :user "mapping"
               :password ""})
