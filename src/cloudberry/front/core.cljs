(ns cloudberry.front.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cloudberry.front.ui.login-form :refer [AuthWrapper]]
            [cloudberry.front.ui.data :as ui]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.string :as str]
            [dumdom.core :as d]))

(defonce store (atom {}))
(def ^:constant api-base-url "http://localhost:3000")

(defn api-url [path]
  (str/join "/" [api-base-url (str/replace path #"^/" "")]))

(defn handle-action [action {:keys [field] :as payload} state]
  (case action
    :action/set-field (assoc state field (get-in payload [:event :value]))
    :api/make-request (assoc state :loading true :error nil)
    :api/success (-> state
                     (assoc :loading false :error nil)
                     (merge payload))
    :api/failure (assoc state :loading false :error (:error payload))
    :test (prn action payload state)
    state))

(defn handle-api-call! [{:keys [method route params]}]
  (let [req-url (api-url route)
        config {:with-credentials? false}]
    (prn method req-url params)
    (case method
      :get (http/get req-url (merge config {:query-params params}))
      :post (http/post req-url (merge config {:json-params params})))))

(defn process-api-response! [response]
  (let [res (js->clj response :keywordize-keys true)
        status (if (= (:status res) 200) :api/success :api/failure)]
    (swap! store #(handle-action status (:body res) %))))

(defn perform-api-call! [payload]
  (go
    (let [res (<! (handle-api-call! payload))]
      (js/setTimeout (process-api-response! res) 0))))

(defn prepare-api-payload [{:keys [fields] :as payload} store]
  (update payload :params #(merge % (update-keys (select-keys store fields) (comp keyword name)))))

(d/set-event-handler!
 (fn [event actions]
   (doseq [[action data] actions]
     (let [event-val (some-> event .-target .-value)
           event-key (some-> event .-keyCode)
           payload (cond-> (or data {})
                     event-val (assoc-in [:event :value] event-val)
                     event-key (assoc-in [:event :key-code] event-key))]
       (prn "Triggered action" action payload)
       (swap! store #(handle-action action payload %))
       (when (= (-> action namespace keyword) :api)
         (perform-api-call! (prepare-api-payload payload @store)))))))

(defn render! []
  (d/render
   (AuthWrapper (ui/prepare-auth-wrapper @store))
   (js/document.getElementById "app")))

(add-watch store ::app (fn [_ _ _ _]
                         (prn @store)
                         (render!)))

(defn ^:dev/after-load init! []
  (render!))
