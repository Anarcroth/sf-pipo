(ns sfpipo.services.files-service
  (:require [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:import [java.util UUID])
  (:gen-class))

(defn return-result
  [msg name]
  (let [result (format msg name)]
    (log/info result)
    result))

(defn get-all-files []
  (log/info "Getting all files")
  (let [files (db/get-all-files)]
    (log/info (format "Found [%d] files." (count files)))
    files))

(defn get-file-by-id
  "Get file by filename, saved on the fs."
  [file-id]
  (log/info (format "Getting file with id [%s]" file-id))
  (let [file (db/get-file-by-id file-id)]
    (if file
      file
      (return-result "File with id [%s] doesn't exist!\n" file-id))))

(defn rename-file
  [file-id new-name]
  (log/info (format "Renaming file with id [%s]" file-id))
  (let [file (db/get-file-by-id file-id)]
    (if file
      (db/update-file file-id :name new-name)
      (return-result "File with id [%s] doesn't exist!\n" file-id))))

(defn delete-file-by-id
  "Delete file by `file-id` from the database."
  [file-id]
  (log/info (format "Deleting file '%s'" file-id))
  (if (db/get-file-by-id file-id)
    (do
      (db/delete-file-by-id file-id)
      (return-result "Deleted '%s'\n" file-id))
    (return-result "File with id [%s] doesn't exists!\n" file-id)))

(defn upload-file
  "Save a passed file `tmpfile` with `file-name` to db."
  [tmpfile file-name]
  (log/info (format "Uploading '%s' file" file-name))
  (db/insert-file file-name tmpfile)
  (return-result "Uploaded '%s'" file-name))

(defn replace-file
  [file-id name file]
  (log/info (format "Replacing file with id [%s]" file-id))
  (if (db/get-file-by-id file-id)
    (do
      (delete-file-by-id file-id)
      (db/insert-file name file (UUID/fromString file-id))
      (return-result "File with id [%s] replaced!" file-id))
    (return-result "File with id [%s] doesn't exist!" file-id)))
