(ns mapping.geoloc
  (:require [cheshire.core :as json]
            [clj-http.client :as http]))

(defn geocode-ip [ip]
  (->> ip
       (str "http://freegeoip.net/json/")
       http/get
       :body
       json/parse-string
       (#(select-keys % ["latitude" "longitude"]))))

(geocode-ip "123.4.65.3")
