(ns sfpipo.services.generic-service
  (:require [clojure.tools.logging :as log]))

(defn ping "Handle ping request." []
  (log/info "Pong")
  "Pong")
