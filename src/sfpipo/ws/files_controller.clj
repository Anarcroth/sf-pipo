(ns sfpipo.ws.files-controller
  (:require [compojure.core :refer [routes GET POST DELETE PUT]]
            [sfpipo.auth :as auth]
            [sfpipo.services.files-service :as files-service]
            [clojure.data.json :as json]))

(defn- extract-req-param
  ([request param]
   (get-in (auth/auth-request request) [:route-params param]))
  ([request param param-string]
   (get-in (auth/auth-request request) [:multipart-params param-string param])))

(defn format-data [f]
  (-> f
      (dissoc :file)
      (update :id str)))

(defn- get-file [request]
  (let [file-id (extract-req-param request :id)
        file (files-service/get-file-by-id file-id)]
    (json/write-str (format-data file))))

(defn- replace-file [request]
  (let [file-id (extract-req-param request :id)
        file (extract-req-param request :tempfile "file")
        file-name (extract-req-param request :filename "file")]
    (files-service/replace-file file-id file-name file)))

(defn- rename-file [request]
  (let [file-id (extract-req-param request :id)
        new-name (extract-req-param request :name)]
    (files-service/rename-file file-id new-name)))

(defn- delete-file [request]
  (let [file-id (extract-req-param request :id)]
    (files-service/delete-file-by-id file-id)))

(defn- upload-file [request]
  (let [tmpfile (extract-req-param request :tempfile "file")
        file-name (extract-req-param request :filename "file")]
    (files-service/upload-file tmpfile file-name)))

(defn- get-all-files [request]
  (auth/auth-request request)
  (json/write-str (map format-data (files-service/get-all-files))))

(defn- get-all-files-by-ids [request]
  (let [ids (:ids (:params request))]
    (map files-service/get-file-by-id ids)))

(defn- delete-all-file-by-ids [request]
  (let [ids (:ids (:params request))]
    (map files-service/delete-file-by-id ids)))

(defn file-routes []
  (routes
   (GET "/:id" [] get-file) ; TODO add option to specify if only a specific parameter is wanted, ex only whole file or size or link
   (DELETE "/:id" [] delete-file)
   (PUT "/:id/replace" [] replace-file)
   (PUT "/:id/rename/:name" [] rename-file)
   (POST "/upload" [] upload-file)
   (GET "/all/" [] get-all-files)
   (GET "/all/ids" [] get-all-files-by-ids)
   (DELETE "/all/ids" [] delete-all-file-by-ids)))
