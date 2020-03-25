(ns sfpipo.db
  (:require [clojure.java.jdbc :as sql])
  (:import [java.nio.file Files Paths]
           [java.io File]))

(def sfpipo-db (or (System/getenv "DATABASE_URL")
                   {:dbtype "postgresql"
                    :dbname "sfpipoDb"
                    :user "sfpipo"
                    :password "changemepls"}))

(defn setup-db
  []
  (try
    (sql/db-do-commands sfpipo-db (sql/create-table-ddl :enfile
                                                        [[:name "text"] [:file :bytea]]
                                                        {:conditional? true}))
    (catch Exception e
      "'enfile' table exists!")))

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
