(ns sfpipo.index
  (:require [reagent.core :as r :refer [atom]]))

(enable-console-print!)

(defn get-lists
  "Get the default 3 functionalities for the current time being."
  []
  [:div>li
   [:p [:a {:href "/ping"} "ping"]]
   [:p [:a {:href "/list-users"} "list-users"]]
   [:p [:a {:href "/list-files"} "list-files"]]])

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
   [butonclick]
   (.getElementById js/document "sfpipo-main")))
