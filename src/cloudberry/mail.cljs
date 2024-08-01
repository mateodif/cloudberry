(ns cloudberry.mail)

;; FIXME: Placeholder until I setup the backend properly
;; This will eventually be a clojure namespace, not cljs

(defn setup! [{:keys [server user pass]}]
  ;; (mail/store server user pass)
  nil)

(defn list-mails [store]
  ;; (map msg/read-message (mail/inbox store))
  [{:subject "Re: Presents for Dale's baby",
    :from {:address "<someone@aol.com>" :name "Someone"}
    :date-recieved "Tue Mar 11 12:54:41 GMT 2014",
    :to [{:address "owain@owainlewis.com" :name "Owain Lewis"}],
    :cc (),
    :bcc (),
    :multipart? true,
    :content-type "multipart/ALTERNATIVE",
    :sender {:address "<someone@aol.com>" :name "Someone"},
    :date-sent #inst "2015-10-23T12:19:33.838-00:00"
    :date-received #inst "2015-10-23T12:19:33.838-00:00"
    :body [{:content-type "text/plain" :body "..."}
           {:content-type "text/html" :body "..."}]
    :headers {"Subject" "Re: Presents for Dale's baby"}}
   ])
