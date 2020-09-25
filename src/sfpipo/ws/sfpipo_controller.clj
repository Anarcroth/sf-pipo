(ns sfpipo.ws.sfpipo-controller
  (:require [compojure.core :refer [routes GET]]
            [sfpipo.services.generic-service :as generic-service]))

(defn greet
  [request]
  (slurp "resources/public/html/index.html"))

(defn ping
  [request]
  (generic-service/ping))

(defn generic-routes []
  (routes
   (GET "/" [] greet)
   (GET "/ping" [] ping)))
