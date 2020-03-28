(ns sfpipo.user-controller
  (:require [clojure.tools.logging :as log]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.db :as db])
  (:gen-class))

(defn delete-user
  [request]
  (if (authenticated? request)
    (let [username (get-in request [:route-params :name])]
      (log/info (format "Deleteing user '%s'" username))
      (db/delete-usr username)
      {:status 200
       :body (format "Deleted user '%s'" username)})
    (throw-unauthorized)))

(defn create-user
  [request]
  (if (authenticated? request)
    (let [username (get-in request [:route-params :name])
          password (get-in request [:route-params :pass])]
      (log/info (format "Creating user '%s'" username))
      (db/insert-usr username password)
      {:status 200
       :body (format "Created user '%s'" username)})
    (throw-unauthorized)))
