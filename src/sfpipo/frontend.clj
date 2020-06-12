(ns sfpipo.frontend
  (:require [sfpipo.user-controller :as db]
            [hiccup.page :as page]
            [hiccup.form :as form]
            [sfpipo.user-controller :as user-controller]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.anti-forgery :as util]))

(defn greet
  [request]
  (page/html5 {:lang "en"}
         [:body
          [:div [:h1 "Sfpipo"]]
          [:dev
           (form/form-to [:get "/ping"]
                         (form/submit-button "Ping"))]
          [:p]
          [:dev
           (form/form-to [:get "/list-files"]
                         (form/submit-button "List files"))]
          [:p]
          [:dev
           (form/text-field {:ng-model "fileName" :placeholder "Enter a file name here"} "file-name")
           (form/form-to [:get "/file/fileName"]
                         (form/submit-button "Get File"))]]))

(defn get-user
  [request]
  (if (authenticated? request)
    (let [user-name (get-in request [:route-params :user-name])
          user (user-controller/get-user user-name)]
      (page/html5
       [:body
        [:h1 "The user you are looking for is " user]]))
      (throw-unauthorized)))
