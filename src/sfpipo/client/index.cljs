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

(defonce pong-res (r/atom ""))

(defonce file-upload-res (r/atom ""))

(defn create-controller-links
  [links]
  (map (fn [link] [:p>a {:href (:endpoint link)} (:name link)]) links))

(defn static-ping
  []
  (go (let [response (<! (http/get "/ping"))]
        (= (:status response) 200))))

(defn get-file [name]
  (go (let [response (<! (http/get (str "/file/" name)))]
        (prn response))))

(defn handle-ping-press []
  (if (static-ping)
      #(swap! pong-res (fn [] (str "Pong! Service is up :)")))))

(defn ping-button []
  [:div {:id "ping-button"}
   [:input {:type "button" :value "Ping"
            :on-click (handle-ping-press)}]])

(defn download-button []
  [:div {:id "download-button"}
   [:form
    [:input {:type "text"}]
    [:input {:type "submit" :value "Download file"}]]])

(defn handle-file-upload [f]
  (if (save-file-to-db f)
    #(swap! file-upload-res (fn [] (str "File uploaded!")))))

(defn save-file-to-db [f]
  (go (let [response (<! (http/post "/upload" {:multipart-params [["file" f]]}))]
        (= (:status response) 200))))

(defn file-input [on-result]
  [:input {:type "file"
           :on-change
           (fn [this]
             (if (not (= "" (-> this .-target .-value)))
               (let [^js/File file (-> this .-target .-files (aget 0))]
                 (save-file-to-db file)
                 (set! (-> this .-target .-value) ""))))}])

(defn get-lists
  "Get the default 3 functionalities for the current time being."
  []
  [:div
   [:ul
    (create-controller-links controller-links)]
   [:p @pong-res]
   [ping-button]
   [file-input]])

(defn init-lists []
  (r/render-component
   [get-lists]
   (.getElementById js/document "sfpipo-main")))
