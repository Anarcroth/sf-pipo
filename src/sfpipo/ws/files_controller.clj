(ns sfpipo.ws.files-controller
  (:require [compojure.core :refer [routes GET POST DELETE PUT]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.services.files-service :as files-service]
            [clojure.data.json :as json]
            [sfpipo.db :as db]))

(defn- auth-request
  [request]
  (if (authenticated? request)
    request
    (throw-unauthorized)))

(defn- extract-req-param
  ([request param]
   (get-in (auth-request request) [:route-params param]))
  ([request param param-string]
   (get-in (auth-request request) [:multipart-params param-string param])))

(defn format-data [f]
  (-> f
      (dissoc :file)
      (update :id str)))

(defn- get-file [request]
  (let [file-id (extract-req-param request :id)
        file (files-service/get-file-by-id file-id)]
    (json/write-str (format-data file))))

(defn- update-file [request]
  (let [file-id (extract-req-param request :id)])
  ; TODO implement me
  )

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
  (auth-request request)
  (json/write-str (map format-data (files-service/get-all-files))))

(defn file-routes []
  (routes
   (GET "/:id" [] get-file)
   (PUT "/:id" [] update-file)
   (PUT "/:id/rename/:name" [] rename-file)
   (DELETE "/:id" [] delete-file)
   (wrap-multipart-params (POST "/upload" [] upload-file))
   (GET "/all/" [] get-all-files)
   (GET "/all/:ids" []) ; TODO implement me
   (DELETE "/all/:ids" []))) ; TODO implement me
