(ns mapping.ip-gen-test
  (:require [mapping.ip-gen :as sut]
            [clojure.test :refer [testing deftest is]]))

(deftest ip-masking
  (testing "IP masking works correctly"
    (is (sut/ip-in-subnet? "192.168.0.0/16" "192.168.32.1"))
    (is (not (sut/ip-in-subnet? "192.168.0.0/24" "192.168.32.1")))
    (is (sut/ip-in-subnet? "172.16.0.0/12" "172.16.0.0"))
    (is (sut/ip-in-subnet? "172.16.0.0/12" "172.31.255.255"))
    (is (not (sut/ip-in-subnet? "172.16.0.0/12" "172.32.255.255")))))

(deftest reserved-addresses
  (testing "detection of reserved addresses"
    (is (sut/reserved-address "192.168.0.1"))))
