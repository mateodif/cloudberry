(ns cloudberry.dev
  (:require [cloudberry.core :as app]))

(defonce store (atom {}))
(defonce element (js/document.getElementById "app"))

(app/start store element)
