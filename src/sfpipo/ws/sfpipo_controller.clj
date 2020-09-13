(ns sfpipo.ws.sfpipo-controller
  (:require [hiccup.page :as page]
            [sfpipo.services.user-service :as user-service]
            [sfpipo.services.files-service :as files-service]
            [sfpipo.services.generic-service :as generic-service]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

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

(defn generate-response-page
  ([title msg]
   (generate-response-page title msg ()))
  ([title msg other]
   (page/html5
    (gen-page-head title)
    [:body
     [:h1 msg]]
    other)))

(defn greet
  [request]
  (slurp "resources/public/html/index.html"))

(defn ping
  [request]
  (generate-response-page "pong" (generic-service/ping)))

(defn get-user
  [request]
  (let [user-name (extract-req-param request :user-name)]
    (generate-response-page "get-usr" (user-service/get-user user-name))))

(defn delete-user
  [request]
  (let [user-name (extract-req-param request :user-name)]
    (generate-response-page "delete-usr" (user-service/delete-user user-name))))

(defn create-user
  [request]
  (let [name (extract-req-param request :name)
        password (extract-req-param request :pass)]
    (generate-response-page "create-usr" (user-service/create-user name password))))

(defn list-files
  [request]
  (auth-request request)
  (generate-response-page "list-files" (files-service/list-files)))

(defn list-users
  [request]
  (auth-request request)
  (generate-response-page "list-users" (user-service/list-users)))

(defn get-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (files-service/get-file file-name)))

(defn delete-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (generate-response-page "delete-files" (files-service/delete-file file-name))))

(defn upload-file
  [request]
  (let [tmpfile (extract-req-param request :tempfile "file")
        file-name (extract-req-param request :filename "file")]
    (files-service/upload-file tmpfile file-name)))
