(ns sfpipo.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [compojure.route :refer [not-found]]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [buddy.auth.backends :as backends]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [sfpipo.auth :as auth]
            [sfpipo.db :refer [setup-db] :as db]
            [sfpipo.view :as view])
  (:gen-class))

(def backend (backends/basic
              {:realm "sfpipo"
               :authfn auth/authenticate}))

(defroutes app
  (GET "/" [] view/greet)
  (GET "/ping" [] view/ping)
  (GET "/list-files" [] view/list-files)
  (GET "/file/:file-name" [] view/get-file)
  (DELETE "/file/:file-name" [] view/delete-file)
  (wrap-multipart-params (POST "/upload" [] view/upload-file))
  (GET "/usr/:user-name" [] view/get-user)
  (DELETE "/usr/:user-name" [] view/delete-user)
  (POST "/usr/:name&:pass" [] view/create-user)
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
