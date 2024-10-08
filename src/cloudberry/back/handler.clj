(ns cloudberry.back.handler
  (:require [cloudberry.back.mail :as mail]
            [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :as rr]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(defn auth-middleware [handler]
  ;; FIXME: not yet implemented
  (fn [req]
    (if (mail/authenticated? req)
      (handler req)
      (rr/not-found req))))

(def store-middleware
  {:name ::store
   :compile (fn [{:keys [store]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :store store)))))})

(defn api [store]
  (ring/ring-handler
   (ring/router
    [["/mail"
      ["" {:get {:handler #'mail/get-all}
           :post {:handler #'mail/send-mail}}]
      ["/:id" {:get {:parameters {:path {:id string?}}}
               :handler #'mail/get-by-id
               :delete {:handler #'mail/delete-by-id}
               :put {:handler #'mail/update-by-id}}]
      ["/:id/move" {:post {:parameters {:path {:id string?}}}
                    :handler #'mail/move}]
      ["/:id/flag" {:post {:parameters {:path {:id string?}}}
                    :handler #'mail/add-flag}]
      ["/:id/flag/:flag" {:delete {:parameters {:path {:id string?
                                                       :flag string?}}}
                          :handler #'mail/remove-flag}]]
     ["/login" {:post {:parameters {:body {:host string? :user string? :password string?}}
                       :handler #'mail/login!}}]
     ["/health" {:get {:handler (constantly {:status 200 :body "OK"})}}]]
    {:data {:store store
            :muuntaja m/instance
            :middleware [muuntaja/format-middleware
                         store-middleware
                         parameters/parameters-middleware
                         wrap-keyword-params
                         wrap-json-response]}})
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))
