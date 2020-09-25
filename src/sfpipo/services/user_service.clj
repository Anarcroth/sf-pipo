(ns sfpipo.services.user-service
  (:require [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:gen-class))

(def no-such-user
  (fn [name]
    (let [msg (format "No such user '%s'!" name)]
      (log/info msg)
      msg)))

(defn return-result
  [msg name]
  (let [result (format msg name)]
    (log/info result)
    result))

(defn get-user
  [user-name]
  (log/info (format "Getting user [%s]" user-name))
  (let [user (db/get-usr user-name)]
    (if user
      user
      (no-such-user user-name))))

(defn delete-user
  [user-name]
  (log/info (format "Deleting user [%s]" user-name))
  (if (db/get-usr user-name)
    (do
      (db/delete-usr user-name)
      (return-result "Deleted user [%s]" user-name))
    (no-such-user user-name)))

(defn create-user
  [name pass]
  (log/info (format "Creating user '%s'" name))
  (db/insert-usr name pass)
  (return-result "Created user '%s'!" name))
