(ns sf-pipo.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [clojure.java.io :as io]
            [clojure.contrib.duck-streams :as ds]))

(defn welcome
  "A warm welcome."
  [request]
  {:status 200
   :body "<h1>Hello and how did you find this page?</h1>"
   :headers {}})

(defn get-file
  "Get file by filename, saved on the fs."
  [request]
  (let [filename (get-in request [:route-params :filename])]
    {:status 200
     :body (io/input-stream "testfile")
     :headers {}}))

(defn save-file
  "Save a passed file to the fs."
  [request]
  (let [fi (get-in request [:multipart-params "file" :tempfile])]
    (with-open [w (io/writer fi)]
      (.write w "")))) ;; TODO save the file locally, not in `/tmp/`

(defroutes app
  (GET "/" [] welcome)
  (GET "/file/:filename" [] get-file)
  (GET "/request-info" [] handle-dump)
  (wrap-multipart-params
   (POST "/save/:filename" [] save-file))
  (not-found "<h1>This is not the page you are looking for</h1>
              <p>Sorry, the page you requested was not found!</p>"))

;; Prod main
(defn -main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [port-number]
  (webserver/run-jetty
   app
   {:port (Integer. port-number)}))

;; Dev main
(defn -dev-main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [port-number]
  (webserver/run-jetty (wrap-reload #'app)
     {:port (Integer. port-number)}))
