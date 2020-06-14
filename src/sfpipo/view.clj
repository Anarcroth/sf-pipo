(ns sfpipo.view
  (:require [sfpipo.user-controller :as db]
            [hiccup.page :as page]
            [hiccup.form :as form]
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

(defn generate-response-page
  [header]
  (page/html5
   [:body
    [:h1 header]]))

(defn greet
  [request]
  (page/html5 {:lang "en"}
         [:body
          [:div [:h1 "Sfpipo"]]
          [:p]
          [:dev
           (form/form-to [:get "/list-files"]
                         (form/submit-button "List files"))]
          [:p]
          [:dev
           (form/text-field {:ng-model "fileName" :placeholder "Enter a file name here"} "file-name")
           (form/form-to [:get "/file/fileName"]
                         (form/submit-button "Get File"))]]))

(defn ping
  [request]
  (generate-response-page (generic-controller/ping)))

(defn get-user
  [request]
  (let [user-name (extract-req-param request :user-name)
        user (user-controller/get-user user-name)]
    (generate-response-page (str "The user you are looking for is " user))))

(defn delete-user
  [request]
  (let [user-name (extract-req-param request :user-name)]
    (generate-response-page (user-controller/delete-user user-name))))

(defn create-user
  [request]
  (let [name (extract-req-param request :name)
        password (extract-req-param request :pass)]
    (generate-response-page (user-controller/create-user name password))))

(defn list-files
  [request]
  (auth-request request)
  (generate-response-page (files-controller/list-files)))

(defn get-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (files-controller/get-file file-name)))

(defn delete-file
  [request]
  (let [file-name (extract-req-param request :file-name)]
    (files-controller/delete-file file-name)))

(defn upload-file
  [request]
  (let [tmpfile (extract-req-param request :tempfile "file")
        file-name (extract-req-param request :filename "file")]
    (files-controller/upload-file tmpfile file-name)))
