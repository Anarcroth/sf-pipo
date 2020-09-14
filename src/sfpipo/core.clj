(ns sfpipo.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [compojure.route :refer [not-found resources]]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [sfpipo.auth :as auth]
            [sfpipo.db :as db]
            [sfpipo.ws.sfpipo-controller :as controller])
  (:gen-class))

(def backend
  (backends/basic
   {:realm "sfpipo" :authfn auth/authenticate}))

                                        ; there is a general problem if you pass an additional trailing '/' to each endpoint
(defroutes app
  (GET "/" [] controller/greet)
  (GET "/ping" [] controller/ping)
  (GET "/list-files" [] controller/list-files)
  (GET "/list-users" [] controller/list-users)
  (GET "/file/:file-name" [] controller/get-file)
  (DELETE "/file/:file-name" [] controller/delete-file)
  (wrap-multipart-params (POST "/upload" [] controller/upload-file))
  (GET "/usr/:user-name" [] controller/get-user)
  (DELETE "/usr/:user-name" [] controller/delete-user)
  (POST "/usr/:name&:pass" [] controller/create-user)
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
