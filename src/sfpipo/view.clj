(ns sfpipo.view
  (:require [sfpipo.user-controller :as db]
            [hiccup.page :as page]
            [sfpipo.user-controller :as user-controller]
            [sfpipo.files-controller :as files-controller]
            [sfpipo.generic-controller :as generic-controller]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.anti-forgery :as util]))

(defn auth-request
  [request]
  (if (authenticated? request)
    request
    (throw-unauthorized)))

(defn extract-req-param
  ([request param]
   (get-in (auth-request request) [:route-params param]))
  ([request param param-string]
   (get-in (auth-request request) [:multipart-params param-string param])))

(defn gen-page-head
  [title]
  [:head
   {:lang "en"}
   [:title title]
   (page/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ " [:a {:href "/"} "Simple File Ping Pong"]
   " | " [:a {:href "/ping"} "Ping"]
   " | " [:a {:href "/list-files"} "List files"] " ]"])

(defn generate-response-page
  [title msg]
  (page/html5
   (gen-page-head title)
   [:body
    [:h1 msg]]))

(defn greet
  [request]
  (page/html5
   (gen-page-head "Simple File Ping Pong")
   header-links
   [:h1 "Simple File Ping Pong"]))

(defn ping
  [request]
  (generate-response-page "pong" (generic-controller/ping)))

(defn get-user
  [request]
  (let [user-name (extract-req-param request :user-name)]
    (generate-response-page "get-usr" (user-controller/get-user user-name))))

(defn delete-user
  [request]
  (let [user-name (extract-req-param request :user-name)]
    (generate-response-page "delete-usr" (user-controller/delete-user user-name))))

(defn create-user
  [request]
  (let [name (extract-req-param request :name)
        password (extract-req-param request :pass)]
    (generate-response-page "create-usr" (user-controller/create-user name password))))

(defn list-files
  [request]
  (auth-request request)
  (generate-response-page "list-files" (files-controller/list-files)))

(defn list-users
  [request]
  (auth-request request)
  (generate-response-page "list-users" (user-controller/list-users)))

(defn get-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (files-controller/get-file file-name)))

(defn delete-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (generate-response-page "delete-files" (files-controller/delete-file file-name))))

(defn upload-file
  [request]
  (let [tmpfile (extract-req-param request :tempfile "file")
        file-name (extract-req-param request :filename "file")]
    (files-controller/upload-file tmpfile file-name)))
