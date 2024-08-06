(ns cloudberry.front.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.walk :as walk]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cloudberry.front.ui.login-form :refer [AuthWrapper]]
            [cloudberry.front.ui.data :as ui]
            [dumdom.core :as d]))

(defonce host "http://localhost:3000")


(defn login! [store path]
  (swap! store assoc :authenticated?
         (go (let [credentials (zipmap [:host :user :password] (map #(get-in @store [% :value]) path))
                   response (<! (http/post (str host "/login") {:edn-params credentials}))]
               (parse-boolean (:body response))))))

(defn execute-actions [store actions]
  (doseq [[action path data] actions]
    (println action path data)
    (case action
      :action/save (swap! store assoc-in path data)
      :action/login (login! store path)
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

(defn init! []
  (start store element))
