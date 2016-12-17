(ns mapping.ip-gen
  "Deterministic generator for IP addresses, written in order to
   a) guarantee that each IP generated in a particular run will be
      chosen only once, and b) that IPs selected sequentially will
   have minimal correlation in their geographic location (so trying
   1.1.1.1, 1.1.1.2, etc isn't sufficient)"
  (:require [clojure.string :as str]))

(defn ip->integer
  "Coerces an IP address string into a 32-bit integer."
  [ip]
  (let [ip-bytes (.getAddress (java.net.Inet4Address/getByName ip))]
    (-> (java.nio.ByteBuffer/wrap ip-bytes)
        .getInt)))

(defn mask-signature
  "Helper function for CIDR masks. The mask signature is
   essentially (take mask (bits ip)) except that (bits ip)
   isn't a thing, so we use bit-test and booleans instead.
   (Negative binary numbers mean doing this with bit strings
   is awkward, too.)"
  [ip mask]
  (map (partial bit-test (ip->integer ip))
       (map (partial - 31) (range mask))))

(defn ip-in-subnet?
  "Checks if an ip is in a subnet, e.g. 
      (ip-in-subnet? \"192.0.0.0/8\" \"192.168.1.1)
   ;=> true"
  [subnet ip]
  (let [[base mask] (str/split subnet #"/")]
    (apply = (map #(mask-signature % (Integer. mask)) [base ip]))))

(def special-subnets
  "Reserved blocks, per https://en.wikipedia.org/wiki/Reserved_IP_addresses"
  ["0.0.0.0/8"
   "10.0.0.0/8"
   "100.64.0.0/10"
   "127.0.0.0/8"
   "169.254.0.0/16"
   "172.16.0.0/12"
   "192.0.0.0/24"
   "192.0.2.0/24"
   "192.88.99.0/24"
   "192.168.0.0/16"
   "198.18.0.0/15"
   "198.51.100.0/24"
   "203.0.113.0/24"
   "224.0.0.0/4"
   "240.0.0.0/4"
   "255.255.255.255/32"])

(defn reserved-address? [ip]
  (some #(ip-in-subnet? % ip) special-subnets))

(def ip-addresses
  "LONG lazy sequence of all non-reserved IP addresses, created by
   incrementing first the highest-order byte, then the next-highest,
   and so on."
  (for [b4 (range 256)
        b3 (range 256)
        b2 (range 256)
        b1 (range 256)]
    (str/join "." [b1 b2 b3 b4])))

(def normal-ip-addresses
  "Lazy sequence of IP addresses with reserved addresses removed."
  (remove reserved-address? ip-addresses))
