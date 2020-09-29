(ns sfpipo.db
  (:require [clojure.java.jdbc :as sql]
            [crypto.password.pbkdf2 :as passwd]
            [clojure.tools.logging :as log])
  (:import [java.nio.file Files]
           [java.util UUID]))

(def sfpipo-db (or (System/getenv "DATABASE_URL")
                   {:dbtype "postgresql"
                    :dbname "sfpipoDb"
                    :user "sfpipo"
                    :password "changemepls"}))

(defn setup-enfile-table
  []
  (try
    (log/info "Trying to create 'enfile' database table")
    (sql/db-do-commands
     sfpipo-db
     (sql/create-table-ddl :enfile
                           [[:name "text"]
                            [:file :bytea]
                            [:id :uuid]
                            [:size :bigint]
                            [:downloadable_link "text"]
                            [:owner :uuid]]
                           {:conditional? true}))
    (catch Exception e
      (log/warn (format "'enfile' table already exists! %s" (.getMessage e))))))

(defn setup-user-table
  []
  (try
    (log/info "Trying to create 'users' database table")
    (sql/db-do-commands
     sfpipo-db
     (sql/create-table-ddl :users
                           [[:name "text"]
                            [:password "text"]
                            [:id :uuid]]
                           {:conditional? true}))
    (catch Exception e
      (log/warn (format "'user' table already exists! %s" (.getMessage e))))))

(defn setup-db
  []
  (log/info "Setting up database")
  (setup-enfile-table)
  (setup-user-table))

(defn get-from-file-by-id
  [param name id]
  (param
   (sql/query sfpipo-db
              [(format "select %s from enfile where id = ?" name) (UUID/fromString id)]
              {:result-set-fn first})))

(defn insert-file
  ([name file]
   (insert-file name file (UUID/randomUUID)))
  ([name file id]
   (sql/insert! sfpipo-db :enfile
                {:id id
                 :name name
                 :file (Files/readAllBytes (.toPath file))
                 :size (Files/size (.toPath file))})))

(defn delete-file
  "`Deprecated`. Kept for backwards compatibility.
  Delete a file object from the `enfile` table with the given `name`."
  [name]
  (sql/delete! sfpipo-db :enfile ["name = ?" name]))

(defn delete-file-by-id
  "Delete a file by `file-id` from the `enfile` table."
  [file-id]
  (sql/delete! sfpipo-db :enfile ["id = ?" (UUID/fromString file-id)]))

(defn get-all-files
  "Gather all `enfile` file objects from the `enfile` table."
  []
  (sql/query sfpipo-db ["select * from enfile"]))

(defn get-file-by-id
  "Get file object by `file-id` from `enfile` table."
  [file-id]
  (sql/query sfpipo-db ["select * from enfile where id = ?" (UUID/fromString file-id)] {:result-set-fn first}))

(defn get-whole-files
  "Get only the files as bytes from the `enfile` table."
  []
  (map :file (sql/query sfpipo-db ["select file from enfile"])))

(defn get-whole-file-by-id
  "Get only the file as bytes by `file-id` from the `enfile` table."
  [file-id]
  (get-from-file-by-id :file "file" file-id))

(defn get-file-names
  "Get only the file names from the `enfile` table."
  []
  (map :name (sql/query sfpipo-db ["select name from enfile"])))

(defn get-file-name-by-id
  "Get only the file name by `file-id` from the `enfile` table."
  [file-id]
  (get-from-file-by-id :name "name" file-id))

(defn get-file-sizes
  "Get only the file sizes from the `enfile` table."
  []
  (map :size (sql/query sfpipo-db ["select size from enfile"])))

(defn get-file-size-by-id
  "Get only the file size by `file-id` from the `enfile` table."
  [file-id]
  (get-from-file-by-id :size "size" file-id))

(defn get-file-downloadable-links
  "Get only the downloadable links from the `enfile` table."
  []
  (map :downloadable_link (sql/query sfpipo-db ["select downloadable_link from enfile"])))

(defn get-file-downloadable-link
  "Get only the downloadable link for a file."
  [file-id]
  (get-from-file-by-id :downloadable_link "downloadable_link" file-id))

(defn get-file-name
  "`Deprecated`. Kept for backwards compatibility."
  [filename]
  (:name (sql/query sfpipo-db
                    ["select * from enfile where name = ?" filename]
                    {:result-set-fn first})))

(defn get-files-by-name
  "Get same named files by `filename` from the `enfile` table."
  [filename]
  (map :file (sql/query sfpipo-db
                    ["select * from enfile where name = ?" filename])))

(defn get-file-by-name
  "Get first matched file object by `filename` from `enfile` table."
  [filename]
  (sql/query sfpipo-db ["select * from enfile where name = ?" filename] {:result-set-fn first}))

(defn get-whole-file-by-name
  [filename]
  (:file (sql/query sfpipo-db
                    ["select * from enfile where name = ?" filename]
                    {:result-set-fn first})))

(defn update-file
  [file-id key new-name]
  (sql/update! sfpipo-db :enfile {key new-name} ["id = ?" (UUID/fromString file-id)]))

(defn delete-usr
  "Delete user by `user-name` from the `users` table."
  [user-name]
  (sql/delete! sfpipo-db :users ["name = ?" user-name]))

(defn insert-usr
  [username password]
  (sql/insert! sfpipo-db :users
               {:name username
                :password (passwd/encrypt password)}))

(defn get-usr
  "Get user by `user-name` from the `users` table."
  [user-name]
  (sql/query sfpipo-db
             ["select * from users where name = ?" user-name]
             {:result-set-fn first}))
