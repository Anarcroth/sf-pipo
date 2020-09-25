(ns sfpipo.services.user-service
  (:require [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:gen-class))

(def no-such-user
  (fn [name]
    (let [msg (format "No such user '%s'!" name)]
      (log/info msg)
      msg)))

(defn handle-list-users
  [users]
  (if (> (count users) 0)
    (format "Found the following '%d' users:\n %s" (count users) (pr-str users))
    (format "There are no users stored")))

(defn return-result
  [msg name]
  (let [result (format msg name)]
    (log/info result)
    result))

(defn get-user
  [user-id]
  (log/info (format "Getting user with id [%s]" user-id))
  (let [user (:name (db/get-usr-by-id user-id))]
    (if user
      user
      (no-such-user user-id))))

(defn delete-user
  [user-id]
  (log/info (format "Deleting user with id [%s]" user-id))
  (if (db/get-usr user-id)
    (do
      (db/delete-usr-by-id user-id)
      (return-result "Deleted user [%s]" user-id))
    (no-such-user user-id)))

(defn create-user
  [name pass]
  (log/info (format "Creating user '%s'" name))
  (if-not (:name (db/get-usr name))
    (do
      (db/insert-usr name pass)
      (return-result "Created user '%s'!" name))
    (format "User already exists with name '%s'!" name)))

(defn list-users
  []
  (log/info "Getting all users.")
  (let [users (db/get-all-usernames)
        result (handle-list-users users)]
    (log/info result)
    result))
