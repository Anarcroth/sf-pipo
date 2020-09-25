(ns sfpipo.ws.user-controller
  (:require [compojure.core :refer [routes GET POST DELETE PUT]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.services.user-service :as user-service]
            [clojure.data.json :as json]))

(defn auth-request
  [request]
  (if (authenticated? request)
    request
    (throw-unauthorized)))

(defn extract-req-param
  ([request param]
   (get-in (auth-request request) [:route-params param]))
  ([request param param-string]
   (get-in (auth-request request) [:multipart-params param-string param])))

(defn format-data [u]
  (-> u
      (dissoc :password)
      (update :id str)))

(defn get-user
  [request]
  (let [user-id (extract-req-param request :id)
        user (user-service/get-user user-id)]
    (json/write-str (format-data user))))

(defn delete-user
  [request]
  (let [user-id (extract-req-param request :id)]
    (user-service/delete-user user-id)))

(defn create-user
  [request]
  (let [name (extract-req-param request :name)
        password (extract-req-param request :pass)]
    (user-service/create-user name password)))

(defn user-routes []
  (routes
   (GET "/:id" [] get-user)
   (PUT "/:id" [] ) ; TODO implement me
   (DELETE "/:id" [] delete-user)
   (POST "/create/:name&:pass" [] create-user)))
