(ns sfpipo.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [compojure.route :refer [not-found]]
            [environ.core :refer [env]]
            [clojure.java.io :as io])
  (:import [java.nio.file Files StandardCopyOption]
           [java.io File]))

(def proj-dir (str (System/getProperty "user.dir") "/"))
(def crypt-files "crypt-files/")
(def crypt-dir (str proj-dir crypt-files))

(defn welcome
  "A warm welcome."
  [request]
  {:status 200
   :body "<h1>Hello and how did you find this page?</h1>"
   :headers {}})

(defn list-files
  [request]
  {:status 200
   :body (for [file (.listFiles (io/file crypt-dir))]
           (str (.getName file) "\n"))
   :headers {}})

(defn get-file
  "Get file by filename, saved on the fs."
  [request]
  (let [filename (get-in request [:route-params :name])]
    {:status 200
     :body (io/input-stream (str crypt-files filename))
     :headers {}}))

(defn delete-file
  "Delete file by filename, saved on the fs."
  [request]
  (let [filename (get-in request [:route-params :name])]
    {:status 200
     :body (io/delete-file (str crypt-files filename))
     :headers {}}))

(defn move-file
  "Move temporary file from request to project folder.
  NOTE: Will replace if same file exists."
  [tmpfile filename]
  (.mkdir (io/file crypt-dir))
  (let [target-file (io/file (str crypt-dir filename))]
    (Files/move (.toPath tmpfile) (.toPath target-file)
                (into-array java.nio.file.CopyOption [(StandardCopyOption/REPLACE_EXISTING)]))))

(defn upload-file
  "Save a passed file to the fs."
  [request]
  (let [tmpfile (get-in request [:multipart-params "file" :tempfile])
        filename (get-in request [:multipart-params "file" :filename])]
    (move-file tmpfile filename)
    {:status 200
     :headers {}}))

(defroutes app
  (GET "/" [] welcome)
  (GET "/list-files" [] list-files)
  (GET "/file/:name" [] get-file)
  (DELETE "/file/:name" [] delete-file)
  (wrap-multipart-params
   (POST "/upload/" [] upload-file))
  (not-found "<h1>This is not the page you are looking for</h1>"))

;; Prod main
(defn -main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [& [port]]
  (let [port (Integer. (or port (env :port) 8000))]
    (webserver/run-jetty
     app
     {:port port
      :join? false})))

;; Dev main
(defn -dev-main
  "A very simple web server on Jetty that ping-pongs a couple of files."
  [& [port]]
  (let [port (Integer. (or port (env :port) 8000))]
    (webserver/run-jetty (wrap-reload #'app)
                         {:port port :join? false})))
