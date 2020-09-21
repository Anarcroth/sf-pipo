(ns sfpipo.db
  (:require [clojure.java.jdbc :as sql]
            [crypto.password.pbkdf2 :as passwd]
            [clojure.tools.logging :as log])
  (:import [java.nio.file Files]))

(def sfpipo-db (or (System/getenv "DATABASE_URL")
                   {:dbtype "postgresql"
                    :dbname "sfpipoDb"
                    :user "sfpipo"
                    :password "changemepls"}))

(defn setup-enfile-table
  []
  (try
    (log/info "Trying to create 'enfile' database table")
    (sql/db-do-commands sfpipo-db
                        (sql/create-table-ddl :enfile
                                              [[:name "text"] [:file :bytea]]
                                              {:conditional? true}))
    (catch Exception e
      (log/warn (format "'enfile' table already exists! %s" (.getMessage e))))))

(defn setup-user-table
  []
  (try
    (log/info "Trying to create 'users' database table")
    (sql/db-do-commands sfpipo-db
                        (sql/create-table-ddl :users
                                              [[:name "text"] [:password "text"]]
                                              {:conditional? true}))
    (catch Exception e
      (log/warn (format "'user' table already exists! %s" (.getMessage e))))))

(defn setup-db
  []
  (log/info "Setting up database")
  (setup-enfile-table)
  (setup-user-table))

(defn insert-file
  [name file]
  (sql/insert! sfpipo-db :enfile
               {:name name
                :file (Files/readAllBytes (.toPath file))}))

(defn delete-file
  [name]
  (sql/delete! sfpipo-db :enfile ["name = ?" name]))

(defn get-file-objects []
  (sql/query sfpipo-db ["select * from enfile"]))

(defn get-whole-files []
  (map :file (sql/query sfpipo-db ["select file from enfile"])))

(defn get-file-names []
  (map :name (sql/query sfpipo-db ["select name from enfile"])))

(defn get-file-name
  [filename]
  (:name (sql/query sfpipo-db ["select * from enfile where name = ?" filename]
                    {:result-set-fn first})))

(defn get-file
  [filename]
  (:file (sql/query sfpipo-db ["select * from enfile where name = ?" filename]
                    {:result-set-fn first})))

(defn delete-usr
  [username]
  (sql/delete! sfpipo-db :users ["name = ?" username]))

(defn insert-usr
  [username password]
  (sql/insert! sfpipo-db :users
               {:name username
                :password (passwd/encrypt password)}))

(defn get-all-users
  []
  (sql/query sfpipo-db ["select * from users"]))

(defn get-all-usernames
  []
  (map str (map :name (get-all-users)) "\n"))

(defn get-usr
  [username]
  (sql/query sfpipo-db ["select * from users where name = ?" username]
             {:result-set-fn first}))
