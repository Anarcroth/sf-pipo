(ns sfpipo.services.files-service
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [sfpipo.db :as db])
  (:import [java.util UUID])
  (:gen-class))

(defn handle-list-files
  [files]
  (if (> (count files) 0)
    (format "Found the following '%d' files:\n %s" (count files) (pr-str files))
    (format "There are no files stored")))

(defn return-result
  [msg name]
  (let [result (format msg name)]
    (log/info result)
    result))

(defn list-files
  []
  (log/info "Listing files.")
  (let [files (db/get-file-names)
        result (handle-list-files files)]
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
  (let [file (db/get-file-name-by-id file-id)]
    (if (not-empty file)
      file
      (return-result "File with id [%s] doesn't exist!" file-id))))

(defn rename-file
  [file-id new-name]
  (log/info (format "Renaming file with id [%s]" file-id))
  (let [file (db/get-file-by-id file-id)]
    (if (not-empty file)
      (db/update-file file-id :name new-name))))

(defn get-file
  "Get file by filename, saved on the fs."
  [file-name]
  (log/info (format "Getting file '%s'" file-name))
  (let [file (db/get-file-by-name file-name)]
    (if (not-empty file)
      file
      (return-result "File '%s' was not found!" file-name))))

(defn download-file
  [filename]
  (log/info (format "Downloading file '%s'" filename))
  (let [file (db/get-file-by-name filename)]
    (if (not-empty file)
      (io/input-stream file)
      (return-result "File '%s' was not found!" filename))))

(defn delete-file-by-id
  "Delete file by `file-id` from the database."
  [file-id]
  (log/info (format "Deleting file '%s'" file-id))
  (if (db/get-file-by-id file-id)
    (do
      (db/delete-file-by-id file-id)
      (return-result "Deleted '%s'" file-id))
    (return-result "No such file '%s' exists!" file-id)))

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
