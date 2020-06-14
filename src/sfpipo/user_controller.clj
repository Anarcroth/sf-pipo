(ns sfpipo.user-controller
  (:require [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:gen-class))

(def no-such-user (fn [name] (format "No such user '%s'!" name)))

(defn get-user
  [user-name]
  (log/info (format "Getting user '%s'" user-name))
  (let [name (:name (db/get-usr user-name))]
    (or name (no-such-user name))))

(defn delete-user
  [user-name]
  (log/info (format "Deleting user '%s'" user-name))
  (if (= (db/delete-usr user-name) 1)
    (format "Deleted user '%s'!" user-name)
    (no-such-user user-name)))

(defn create-user
  [name pass]
  (log/info (format "Creating user '%s'" name))
  (if-not (:name (db/get-usr name))
    (do
      (db/insert-usr name pass)
      (format "Created user '%s'!" name))
    (format "User already exists with name '%s'!" name)))
