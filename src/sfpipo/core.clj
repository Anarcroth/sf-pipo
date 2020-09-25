(ns sfpipo.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer :all]
            [compojure.core :refer [context defroutes]]
            [compojure.route :refer [not-found resources]]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [sfpipo.auth :as auth]
            [sfpipo.db :as db]
            [sfpipo.ws.user-controller :as user-controller]
            [sfpipo.ws.files-controller :as files-controller]
            [sfpipo.ws.sfpipo-controller :as generic-controller])
  (:gen-class))

(def backend
  (backends/basic
   {:realm "sfpipo" :authfn auth/authenticate}))

(defroutes app
  (context "" [] (generic-controller/generic-routes))
  (context "/user" [] (user-controller/user-routes))
  (context "/file" [] (files-controller/file-routes))
  (resources "/")
  (not-found "<h1>This is not the page you are looking for</h1>"))

;; Prod main
(defn -main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [& [port]]
  (log/info auth/session-otp)
  (db/setup-db)
  (let [port (Integer. (or port (env :port) 8000))]
    (as-> app $
      (wrap-authorization $ backend)
      (wrap-authentication $ backend)
      (webserver/run-jetty $ {:port port :join? false}))))

;; Dev main
(defn -dev-main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [& [port]]
  (log/info auth/session-otp)
  (db/setup-db)
  (let [port (Integer. (or port (env :port) 8000))]
    (as-> (wrap-reload #'app) $
      (wrap-authorization $ backend)
      (wrap-authentication $ backend)
      (webserver/run-jetty $ {:port port :join? false}))))
