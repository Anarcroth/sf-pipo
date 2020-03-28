(ns sfpipo.files-controller
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [sfpipo.db :as db])
  (:gen-class))

(defn list-files
  [request]
  (if (authenticated? request)
    (let [files (db/get-file-names)]
      (log/info "Listing files.")
      (log/info (format "Found the following '%d' files:\n %s" (count files) (pr-str files)))
      {:status 200
       :body files})
    (throw-unauthorized)))

(defn get-file
  "Get file by filename, saved on the fs."
  [request]
  (if (authenticated? request)
    (let [filename (get-in request [:route-params :name])]
      (log/info (format "Getting file '%s'" filename))
      {:status 200
       :body (io/input-stream (db/get-file filename))})
    (throw-unauthorized)))

(defn delete-file
  "Delete file by filename, saved on the fs."
  [request]
  (if (authenticated? request)
    (let [filename (get-in request [:route-params :name])]
      (log/info (format "Deleting file '%s'" filename))
      (db/delete-file filename)
      {:status 200
       :body (format "Deleted '%s'\n" filename)})
    (throw-unauthorized)))

(defn upload-file
  "Save a passed file to the fs."
  [request]
  (if (authenticated? request)
    (let [tmpfile (get-in request [:multipart-params "file" :tempfile])
          filename (get-in request [:multipart-params "file" :filename])]
      (log/info (format "Uploading '%s'" filename))
      (db/insert-file filename tmpfile)
      {:status 200
       :body (format "Uploaded '%s'\n" filename)})
    (throw-unauthorized)))
