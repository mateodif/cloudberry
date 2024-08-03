(ns cloudberry.front.core
  (:require [clojure.walk :as walk]
            [cloudberry.front.ui.login-form :refer [LoginForm]]
            [cloudberry.front.ui.data :as ui]
            [dumdom.core :as d])
  (:gen-class))

(defn execute-actions [store actions]
  (doseq [[action & args] actions]
    (println "Execute %s %s" action args)
    (case action
      :action/login (swap! store assoc :mail-store nil))))

(defn get-target-value [event el]
  (if (= :event/target.value el)
    (some-> event .-target .-value)
    el))

(defn register-actions [store]
  (d/set-event-handler!
   (fn [event actions]
     (->> actions
          (walk/postwalk #(get-target-value event %))
          (execute-actions store)))))

(defn render [state element]
  (d/render (LoginForm (ui/prepare state)) element))

(defn start [store element]
  (register-actions store)
  (add-watch store ::app
    (fn [_ _ _ state]
      (render element state)))
  (render @store element))
