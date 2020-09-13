(ns sfpipo.services.generic-service
  (:require [clojure.tools.logging :as log]))

(defn greet "Handle greet request." []
  (log/info "Greeting page opened")
  "Simple File Ping Pong")

(defn ping "Handle ping request." []
  (log/info "Pong")
  "Pong")
