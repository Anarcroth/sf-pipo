(ns sfpipo.user-controller
  (:require [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:gen-class))

(def no-such-user (fn [name] (format "No such user '%s'!" name)))

(defn get-user
  [user-name]
  (log/info (format "Getting user '%s'" user-name))
  (let [name (:name (db/get-usr user-name))]
    (if name
      (format "The user you are looking for is '%s'" name)
      (no-such-user user-name))))

(defn delete-user
  [user-name]
  (log/info (format "Deleting user '%s'" user-name))
  (if (db/get-usr user-name)
    (do
      (db/delete-usr user-name)
      (format "Deleted user '%s'" user-name))
    (format "No such user '%s' exists!" user-name)))

(defn create-user
  [name pass]
  (log/info (format "Creating user '%s'" name))
  (if-not (:name (db/get-usr name))
    (do
      (db/insert-usr name pass)
      (format "Created user '%s'!" name))
    (format "User already exists with name '%s'!" name)))

(defn list-users
  []
  (let [users (db/get-all-usernames)]
    (log/info "Getting all users.")
    (if (> (count users) 0)
      (format "Found the following '%d' users:\n %s" (count users) (pr-str users))
      (format "There are no users stored"))))
