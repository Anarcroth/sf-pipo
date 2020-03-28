(ns sfpipo.db
  (:require [clojure.java.jdbc :as sql]
            [crypto.password.pbkdf2 :as passwd])
  (:import [java.nio.file Files Paths]
           [java.io File]))

(def sfpipo-db (or (System/getenv "DATABASE_URL")
                   {:dbtype "postgresql"
                    :dbname "sfpipoDb"
                    :user "sfpipo"
                    :password "changemepls"}))

(defn setup-enfile-table
  []
  (try
    (sql/db-do-commands sfpipo-db
                        (sql/create-table-ddl :enfile
                                              [[:name "text"] [:file :bytea]]
                                              {:conditional? true}))
    (catch Exception e
      "'enfile' table exists!")))

(defn setup-user-table
  []
  (try
    (sql/db-do-commands sfpipo-db
                        (sql/create-table-ddl :users
                                              [[:name "text"] [:password "text"]]
                                              {:conditional? true}))
    (catch Exception e
      "'user' table exists!")))

(defn setup-db
  []
  (setup-enfile-table)
  (setup-user-table))

(defn insert-file
  [name file]
  (sql/insert! sfpipo-db
               :enfile {:name name
                        :file (Files/readAllBytes (.toPath file))}))

(defn delete-file
  [name]
  (sql/delete! sfpipo-db :enfile ["name = ?" name]))

(defn get-files
  []
  (sql/query sfpipo-db ["select * from enfile"]))

(defn get-file-names
  []
  (map str (map :name (get-files)) "\n"))

(defn get-file
  [filename]
  (:file (sql/query sfpipo-db ["select * from enfile where name = ?" filename]
                    {:result-set-fn first})))

(defn delete-usr
  [username]
  (sql/delete! sfpipo-db :users ["name = ?" name]))

(defn insert-usr
  [username password]
  (sql/insert! sfpipo-db :users
               {:name username
                :password (passwd/encrypt password)}))

(defn get-usr
  [username]
  (sql/query sfpipo-db ["select * from users where name = ?" username]
             {:result-set-fn first}))
