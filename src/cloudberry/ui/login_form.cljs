(ns cloudberry.ui.login-form
  (:require [dumdom.core :as d]))

(d/defcomponent Input [{:keys [class type message error? value placeholder input-actions blur-actions]}]
  [:div {:class (or class :my-3)}
   [:input {:class [:input (when error? :is-danger)]
            :type (or type "text")
            :value value
            :placeholder placeholder
            :on-input input-actions
            :on-blur blur-actions}]
   (when message
     [:p {:class [:help (when error? :is-danger)]}
      message])])

(d/defcomponent Button [{:keys [class text enabled? actions]}]
  [:button {:class (cond-> [:button :is-dark]
                     (keyword? class) (conj class)
                     (coll? class) (concat class))
            :disabled (false? enabled?)
            :on-click actions}
   text])

(d/defcomponent LoginForm [{:keys [server-field user-field pass-field button]}]
  [:div {:class [:container :my-6]}
   (Input server-field)
   (Input user-field)
   (Input (assoc pass-field :type "password"))
   (Button button)])
