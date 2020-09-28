(ns sfpipo.auth
  (:require [crypto.password.pbkdf2 :as passwd]
            [clojure.tools.logging :as log]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.db :as db])
  (:import [java.util UUID]))

(defn gen-passwd
  "Generate a random password for per instance run use only."
  [n]
  (let [chars-between #(map char (range (int %1) (inc (int %2))))
        chars (concat (chars-between \0 \9)
                      (chars-between \a \z)
                      (chars-between \A \Z)
                      [\_])
        password (repeatedly n #(rand-nth chars))]
    (reduce str password)))

(defn gen-otp
  "Get random one time user."
  []
  {:username (UUID/randomUUID) :passwd (gen-passwd 20)})

(def session-otp (gen-otp))

(defn is-otp-valid?
  [username password]
  (log/info "Validating OTP")
  (and
   (= username (str (:username session-otp)))
   (= password (:passwd session-otp))))

(defn user-exists?
  [username password]
  (log/info "Validating user '%s'" username)
  (let [usrname (:name (db/get-usr username))]
    (if (and (not-empty usrname)
             (= username usrname))
      (passwd/check password (:password (db/get-usr username)))
      false)))

(defn authenticate
  [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (or (is-otp-valid? username password)
        (user-exists? username password))))

(defn auth-request
  [request]
  (if (authenticated? request)
    request
    (throw-unauthorized)))
