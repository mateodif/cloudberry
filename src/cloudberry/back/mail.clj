(ns cloudberry.back.mail
  (:require [ring.util.response :as rr])
  (:import [jakarta.mail Folder FetchProfile FetchProfile$Item]
           [jakarta.mail.search MessageIDTerm]))

(defn default [req & _]
  (rr/response "Success."))

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

(defn get-by-id [{:keys [store]} msg-id]
  (with-open [inbox (doto (.getFolder store "INBOX")
                      (.open Folder/READ_ONLY))]
    (let [messages (.search inbox (MessageIDTerm. msg-id))
          fp (doto (FetchProfile.)
               (.add FetchProfile$Item/ENVELOPE))]
      (.fetch inbox messages fp)
      (if-let [msg (first messages)]
        (rr/response (merge (message->map msg) {:content (.getContent msg)}))
        (rr/not-found (str "Message with id " msg-id " can't be found."))))))

(defn get-all [{:keys [store]}]
  (with-open [inbox (doto (.getFolder store "INBOX")
                      (.open Folder/READ_ONLY))]
    (let [messages (get-last-n-messages inbox 20)
          fp (doto (FetchProfile.)
               (.add FetchProfile$Item/ENVELOPE))]
      (.fetch inbox messages fp)
      (->> messages
           (mapv message->map)
           (assoc {} :inbox)
           rr/response))))

(defn login! [{:keys [body-params store]}]
  (println body-params)
  (let [{:keys [host user password]} body-params]
    (try
      (.connect store host user password)
      (rr/response {:authenticated? true})
      (catch IllegalStateException _
        ;; Already connected
        (rr/response {:authenticated? true}))
      (catch Exception _
        (rr/response {:authenticated? false})))))

(defn authenticated? [{:keys [store]}]
  (try
    (let [connected? (.isConnected store)]
      (rr/response (str connected?)))
    (catch Exception _
      (rr/response (str false)))))
