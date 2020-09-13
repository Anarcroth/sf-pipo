(ns sfpipo.client.index
  (:require [reagent.core :as r :refer [atom]]))

(enable-console-print!)

(def controller-links
  [{:endpoint "/ping" :name "ping"}
   {:endpoint "/list-users" :name "list-users"}
   {:endpoint "/list-files" :name "list-files"}])

(defn create-controller-links
  [links]
  (map (fn [link] [:p>a {:href (:endpoint link)} (:name link)]) links))

(defn get-lists
  "Get the default 3 functionalities for the current time being."
  []
  [:ul (create-controller-links controller-links)])

(def click-count (r/atom 0))

(defn butonclick []
  [:div
   "The atom " [:code "click-count"] " has value: "
   @click-count ". "
   [:input {:type "button" :value "Click me!"
            :on-click #(swap! click-count inc)}]
   [get-lists]])

(defn init-lists []
  (r/render-component
   [get-lists]
   (.getElementById js/document "sfpipo-main")))
