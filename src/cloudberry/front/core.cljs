(ns cloudberry.front.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.walk :as walk]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cloudberry.front.ui.login-form :refer [AuthWrapper]]
            [cloudberry.front.ui.data :as ui]
            [dumdom.core :as d]))
(declare execute-actions)

(defonce host "http://localhost:3000")

(defn get-auth-credentials [store path]
  (zipmap [:host :user :password] (map #(get-in store [% :value]) path)))

(defn login! [store path]
  (go (let [params {:edn-params (get-auth-credentials @store path)}
            res (<! (http/post (str host "/login") params))
            auth? (-> res :body parse-boolean)]
        (js/setTimeout (swap! store assoc :authenticated? auth?) 0)
        (when auth?
          (execute-actions store [[:action/fetch-mails]])))))

(defn fetch-mails! [store]
  (go (let [response (<! (http/get (str host "/mail")))]
        (js/setTimeout (swap! store assoc :inbox (:body response)) 0))))

(defn execute-actions [store actions]
  (doseq [[action path data] actions]
    (println action path data)
    (case action
      :action/save (swap! store assoc-in path data)
      :action/login (login! store path)
      :action/fetch-mails (fetch-mails! store)
      nil)))

(defn get-actions [e actions]
  (walk/postwalk
   (fn [x]
     (if (= :event/target.value x)
       (some-> e .-target .-value)
       x))
   actions))

(defn register-actions [store]
  (d/set-event-handler!
   (fn [e actions]
     (->> actions
          (get-actions e)
          (execute-actions store)))))

(defn render [state element]
  (d/render (AuthWrapper (ui/prepare-auth-wrapper state)) element))

(defn start [store element]
  (register-actions store)
  (add-watch store ::app
    (fn [_ _ _ state]
      (println state)
      (render state element)))
  (render @store element))

(defonce store (atom {}))
(defonce element (js/document.getElementById "app"))

(defn ^:dev/after-load init! []
  (start store element))
