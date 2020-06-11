(ns sfpipo.frontend
  (:require [sfpipo.user-controller :as db]
            [hiccup.page :as page]
            [hiccup.form :as form]
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

(defn user-view
  [request]
  (page/html5 {:lang "en"}
              [:body
               [:h1 "Add a Location"]
               [:form {:action "/usr/:user-name" :method "GET"}
                (util/anti-forgery-field) ; prevents cross-site scripting attacks
                [:p "user name: " [:input {:type "text" :name "user-name" :ng-model "user-name"}]]
                [:p [:input {:type "submit" :value "submit user name"}]]]]))

(defn get-user
  [request]
  (print request)
  (let [user (db/get-user request)]
    (page/html5
     {:lange "en"}
     [:body
      [:h1 "The user you are looking for is"]
      [:p user]])))
