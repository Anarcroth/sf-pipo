(ns sfpipo.user-controller
  (:require [clojure.tools.logging :as log]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.db :as db])
  (:gen-class))

(defn get-user
  [user-name]
  (log/info (format "Getting user '%s'" user-name))
  (let [name (:name (db/get-usr user-name))]
    (or name (format "No such user '%s'!" name))))

(defn delete-user
  [user-name]
  (log/info (format "Deleting user '%s'" user-name))
  (db/delete-usr user-name))

(defn create-user
  [name pass]
  (log/info (format "Creating user '%s'" name))
  (db/insert-usr name pass))
