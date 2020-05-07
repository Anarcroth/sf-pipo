(ns sfpipo.frontend
  (:require [hiccup.page :as page]
            [hiccup.form :as form]))

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
                         (form/submit-button "Get File"))]
          [:p]
          [:div
           (form/text-field {:ng-model "userName" :placeholder "Enter a user name here"} "user-name")
           (form/form-to [:get "/usr/userName"]
                         (form/submit-button "Get User"))]]))
