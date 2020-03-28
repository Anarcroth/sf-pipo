(ns sfpipo.generic-controller
  (:gen-class))

(defn ping
  "Handle ping request."
  [request]
  {:status 200
   :body "pong\n"})
