(ns cloudberry.back.mail
  "FIXME: implement all"
  (:require [ring.util.response :as rr]))

(defn default [req & _]
  (rr/response req))

(def get-all default)

(def get-by-id default)

(def delete-by-id default)

(def update-by-id default)

(def send-mail default)

(def move default)

(def add-flag default)

(def remove-flag default)
