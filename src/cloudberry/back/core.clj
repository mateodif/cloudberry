(ns cloudberry.back.core
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [cloudberry.back.handler :as handler])
  (:import [jakarta.mail Session Store Folder Message]
           [jakarta.mail.internet InternetAddress]
           [java.util Properties])
  (:gen-class))

(def config
  {:adapter/jetty {:handler (ig/ref :handler/run-app) :port 3000}
   :handler/run-app {:store (ig/ref :mail/store)}
   :mail/store {}})

;; jetty
(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts (dissoc handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

;; app handler
(defmethod ig/init-key :handler/run-app [_ {:keys [store]}]
  (handler/api store))

(defmethod ig/halt-key! :handler/run-app [_ _]
  nil)

;; imap store
(defmethod ig/init-key :mail/store [_ _]
  (let [props (doto (Properties.)
                (.put "mail.store.protocol" "imaps"))
        session (Session/getDefaultInstance props nil)]
    (.getStore session "imaps")))

(defmethod ig/halt-key! :mail/store [_ store]
  (when (.isConnected store)
    (.close store)))

(defn start! []
  (ig/init config))

(defn stop! []
  (ig/halt! config))
