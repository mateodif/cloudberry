(ns cloudberry.back.handler
  (:require [cloudberry.back.mail :as mail]
            [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(defn auth-middleware [handler]
  ;; FIXME: not yet implemented
  (fn [req] (handler req)))

(defn api []
  (ring/ring-handler
   (ring/router
    [["/mail"
      ["" {:get {:handler mail/get-all}
           :post {:handler mail/send-mail}}]
      ["/:id" {:get {:parameters {:path {:id string?}}}
                       :handler mail/get-by-id
                       :delete {:handler mail/delete-by-id}
                       :put {:handler mail/update-by-id}}]
      ["/:id/move" {:post {:parameters {:path {:id string?}}}
                            :handler mail/move}]
      ["/:id/flag" {:post {:parameters {:path {:id string?}}}
                            :handler mail/add-flag}]
      ["/:id/flag/:flag" {:delete {:parameters {:path {:id string?
                                                       :flag string?}}}
                                       :handler mail/remove-flag}]]]
    {:data {:middleware [auth-middleware
                         parameters/parameters-middleware
                         wrap-keyword-params]}})
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))
