(ns sfpipo.client.index
  (:require [reagent.core :as r :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(def controller-links
  [{:endpoint "/ping" :name "ping"}
   {:endpoint "/list-users" :name "list-users"}
   {:endpoint "/list-files" :name "list-files"}])

(defonce pong-res (r/atom 0))

(defn create-controller-links
  [links]
  (map (fn [link] [:p>a {:href (:endpoint link)} (:name link)]) links))

(defn show-pong []
  [:div "pong"
   [:p "this is a pong"]])

(defn rendre-pong-res []
  (r/render-component
   [show-pong]
   (.getElementById js/document "button-here")))

(defn static-ping
  []
  (go (let [response (<! (http/get "/ping"))]
        (if (= (:status response) 200)
          (do
            (prn "it works")
            #(swap! @pong-res inc))
          (prn "response is not 200")))))

(defn button-here []
  [:div {:id "button-here"}
   [:input {:type "button" :value "here"
            :on-click #(static-ping)}]])

(defn get-lists
  "Get the default 3 functionalities for the current time being."
  []
  [:div
   [:ul
    (create-controller-links controller-links)]
   [:p @pong-res]
   [button-here]])

(defn init-lists []
  (r/render-component
   [get-lists]
   (.getElementById js/document "sfpipo-main")))
