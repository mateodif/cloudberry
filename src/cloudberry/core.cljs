(ns cloudberry.core
  (:require [clojure.walk :as walk]
            [dumdom.core :as d])
  (:gen-class))

(defn execute-actions [store actions]
  (doseq [[action & args] actions]
    (println "Execute %s %s" action args)
    (case action
      :action/list-emails nil)))

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

(defn render [element state]
  (d/render [:span "test"] element))

(defn start [store element]
  (register-actions store)
  (add-watch store ::app
    (fn [_ _ _ state]
      (render element state)))
  (render element @store))
