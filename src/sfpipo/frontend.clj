(ns sfpipo.frontend
  (:require [hiccup.page :as page]
            [hiccup.form :as form]))

(defn greet
  [request]
  (page/html5 {:lang "en"}
         [:body
          [:div [:h1 "Sfpipo"]
           (form/label "File Input" "File Input")
           (form/file-upload "Upload")]]))
