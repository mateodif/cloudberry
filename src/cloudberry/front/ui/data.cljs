(ns cloudberry.front.ui.data)

(defn get-from-state [state fields]
  (map #(get-in state [(keyword :fields %) :value]) fields))

(defn prepare-input [state k]
  (let [{:keys [value]} (k state)]
    {:placeholder (str k)
     :value (or value "")
     :input-actions
     [[:action/save [k :value] :event/target.value]]}))

(defn prepare-button [state]
  (let [[server user pass] (get-from-state state [:server :user :pass])
        ready? (and server user pass)]
    {:text "Login"
     :enabled? ready?
     :actions (when ready?
                [[:action/login {:server server :user user :pass pass}]])}))

(defn prepare [state]
  {:server-field (prepare-input state :fields/server)
   :user-field (prepare-input state :fields/user)
   :pass-field (prepare-input state :fields/pass)
   :button-field (prepare-button state)})
