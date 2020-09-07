(ns sfpipo.files-controller
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [sfpipo.db :as db])
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

(defn get-file
  "Get file by filename, saved on the fs."
  [file-name]
  (log/info (format "Getting file '%s'" file-name))
  (let [file (db/get-file file-name)]
    (if (not-empty file)
      (io/input-stream (db/get-file file-name))
      (return-result "File '%s' was not found!" file-name))))

(defn delete-file
  "Delete file by filename, saved on the fs."
  [file-name]
  (log/info (format "Deleting file '%s'" file-name))
  (if (db/get-file-name file-name)
    (do
      (db/delete-file file-name)
      (return-result "Deleted '%s'" file-name))
    (return-result "No such file '%s' exists!" file-name)))

(defn upload-file
  "Save a passed file to the fs."
  [tmpfile file-name]
  (log/info (format "Uploading '%s' file" file-name))
  (if-not (db/get-file-name file-name)
    (do
      (db/insert-file file-name tmpfile)
      (return-result "Uploaded '%s'" file-name))
    (return-result "Such a file '%s' already exists!" file-name)))
