(ns cloudberry.back.mail
  (:require [ring.util.response :as rr])
  (:import [jakarta.mail Folder]))

(defn default [req & _]
  (rr/response "Success."))

(def get-by-id default)

(def delete-by-id default)

(def update-by-id default)

(def send-mail default)

(def move default)

(def add-flag default)

(def remove-flag default)

(defn get-last-n-messages [folder n]
  (let [message-count (.getMessageCount folder)
        start (max 1 (- message-count (dec n)))]
    (.getMessages folder start message-count)))

(defn message->map [message]
  {:subject (.getSubject message)
   :from (-> message .getFrom first .toString)
   :sent-date (.getSentDate message)
   :content (.toString (.getContent message))})

(defn get-all [{:keys [session]}]
  (println session)
  (with-open [inbox (doto (.getFolder session "INBOX")
                      (.open Folder/READ_ONLY))]
    (->> (get-last-n-messages inbox 1)
         (mapv #(.getSubject %))
         rr/response)))

(defn login! [{:keys [body-params session]}]
  (println body-params)
  (let [{:keys [host user password]} body-params
        store session]
    (try
      (.connect store host user password)
      (rr/response (str true))
      (catch Exception _
        (rr/response (str false))))))

(defn authenticated? [{:keys [session]}]
  (try
    (let [connected? (.isConnected session)]
      (rr/response (str connected?)))
    (catch Exception _
      (rr/response (str false)))))
