(ns cloudberry.front.ui.login-form
  (:require [dumdom.core :as d]
            [cloudberry.front.ui.common :refer [Input Button]]))

(d/defcomponent LoginForm [{:keys [server-field user-field pass-field button]}]
  [:div {:class [:container :my-6]}
   (Input server-field)
   (Input user-field)
   (Input (assoc pass-field :type "password"))
   (Button button)])

(d/defcomponent AuthWrapper [{:keys [authenticated? login-component main-component]}]
  (if authenticated?
    main-component
    login-component))
