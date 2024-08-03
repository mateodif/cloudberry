(ns cloudberry.back.core
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [cloudberry.back.handler :as handler])
  (:gen-class))

(def config
  {:adapter/jetty {:handler (ig/ref :handler/run-app) :port 3000}
   :handler/run-app {}})

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts (dissoc handler) (assoc :join? false))))

(defmethod ig/init-key :handler/run-app [_ _]
  (handler/api))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defn -main []
  (ig/init config))
