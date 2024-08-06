(ns cloudberry.front.ui.data
  (:require [cloudberry.front.ui.login-form :refer [LoginForm]]
            [cloudberry.front.ui.mail :refer [MailViewer]]))

(defn get-from-state [state fields]
  (map #(get-in state [(keyword :fields %) :value]) fields))

(defn prepare-input [state k]
  (let [{:keys [value]} (k state)]
    {:placeholder (str k)
     :value (or value "")
     :input-actions
     (->> [[:action/save [k :value] :event/target.value]]
          (remove nil?))}))

(defn prepare-button [state]
  (let [[server user pass] (get-from-state state [:server :user :pass])
        ready? (and server user pass)]
    {:text "Login"
     :enabled? ready?
     :actions (when ready?
                [[:action/login [:fields/server :fields/user :fields/pass] nil]])}))

(defn prepare-login [state]
  {:server-field (prepare-input state :fields/server)
   :user-field (prepare-input state :fields/user)
   :pass-field (prepare-input state :fields/pass)
   :button (prepare-button state)})

(defn prepare-mails [state]
  {:inbox (get-in state [:inbox])})

(defn prepare-auth-wrapper [state]
  {:authenticated? (:authenticated? state)
   :login-component (LoginForm (prepare-login state))
   :main-component (MailViewer (prepare-mails state))})
