(ns cloudberry.back.mail
  (:require [ring.util.response :as rr])
  (:import [jakarta.mail Folder FetchProfile FetchProfile$Item]))

(defn default [req & _]
  (rr/response "Success."))

(def get-by-id default)

(def delete-by-id default)

(def update-by-id default)

(def send-mail default)

(def move default)

(def add-flag default)

(def remove-flag default)

(defn get-last-n-messages [folder limit]
  (let [end (.getMessageCount folder)
        start (max 1 (- end limit -1))]
    (.getMessages folder start end)))

(defn message->map [message]
  {:id (.getMessageID message)
   :subject (.getSubject message)
   :from (-> message .getFrom first .toString)
   :sent-date (.getSentDate message)})

(defn get-all [{:keys [session]}]
  (with-open [inbox (doto (.getFolder session "INBOX")
                      (.open Folder/READ_ONLY))]
    (let [messages (get-last-n-messages inbox 20)
          fp (doto (FetchProfile.)
               (.add FetchProfile$Item/ENVELOPE))]
      (.fetch inbox messages fp)
      (->> messages
           (mapv message->map)
           (assoc {} :inbox)
           rr/response))))

(defn login! [{:keys [body-params session]}]
  (let [{:keys [host user password]} body-params
        store session]
    (try
      (.connect store host user password)
      (rr/response {:authenticated? true})
      (catch IllegalStateException _
        ;; Already connected
        (rr/response {:authenticated? true}))
      (catch Exception _
        (rr/response {:authenticated? false})))))

(defn authenticated? [{:keys [session]}]
  (try
    (let [connected? (.isConnected session)]
      (rr/response (str connected?)))
    (catch Exception _
      (rr/response (str false)))))
