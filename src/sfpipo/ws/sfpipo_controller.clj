(ns sfpipo.ws.sfpipo-controller
  (:require [hiccup.page :as page]
            [sfpipo.services.generic-service :as generic-service]))

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
