(ns cloudberry.front.ui.data
  (:require [cloudberry.front.ui.login-form :refer [LoginForm]]
            [cloudberry.front.ui.mail :refer [MailViewer]]
            [clojure.string :as str]))

(defn prepare-input [state field]
  (let [{:keys [value]} (field state)]
    {:placeholder (name field)
     :value (or value "")
     :input-actions [[:action/set-field {:field field}]]}))

(defn prepare-login-button [state]
  (let [form-fields [:form/host :form/user :form/password]
        form (select-keys state form-fields)
        ready? (not-every? str/blank? form)]
    {:text "Login"
     :enabled? ready?
     :actions (when ready?
                [[:api/make-request {:method :post
                                     :route "/login"
                                     :fields form-fields}]])}))

(defn prepare-login [state]
  {:server-field (prepare-input state :form/host)
   :user-field (prepare-input state :form/user)
   :pass-field (prepare-input state :form/password)
   :button (prepare-login-button state)})

(defn prepare-mails [state]
  (select-keys state [:inbox]))

(defn prepare-auth-wrapper [state]
  {:authenticated? (:authenticated? state)
   :login-component (LoginForm (prepare-login state))
   :main-component (MailViewer (prepare-mails state))})
