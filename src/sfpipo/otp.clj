(ns sfpipo.otp
  (:import [java.util UUID]))

(defn gen-passwd
  "Generate a random password for per instance run use only."
  [n]
  (let [chars (map char (range 33 127))
        password (take n (repeatedly #(rand-nth chars)))]
    (reduce str password)))

(defn gen-otp
  "Get random one time user."
  []
  {:username (UUID/randomUUID) :passwd (gen-passwd 20)})
