(ns cloudberry.front.ui.mail
  (:require [dumdom.core :refer [defcomponent]]))

(defcomponent Sidebar []
  [:div {:class [:column :is-1]}
   [:aside {:class [:menu :p-4]}
    [:button {:class [:button :is-small]}
     "New message"]
    [:p {:class :menu-label} "Folders"]
    [:ul {:class :menu-list}
     [:li [:a {:class :is-active} "Inbox"]]
     [:li [:a "Sent"]]
     [:li [:a "Spam"]]]]])

(defcomponent MailViewer [{:keys [inbox]}]
  [:div {:class [:columns :is-gapless] :style {:height "100vh"}}
   (Sidebar)
   [:div {:class :column}
    [:div {:class :section}
     [:h1 {:class :title} "Inbox"]
     [:p {:class [:control :has-icons-left :mb-2]}
      [:input {:class :input :type :text :placeholder "Search"}]
      [:span {:class [:icon :is-left]}
       [:i {:class [:bx :bx-search]}]]]
     [:table {:class [:table :is-fullwidth :is-hoverable]}
      [:thead
       [:tr
        [:th ]
        [:th "Subject"]
        [:th "From"]
        [:th "Date"]]]
      [:tbody
       (for [[mail index] (zipmap inbox (range))]
         [:tr {:class :is-clickable
               :data-id (:id mail)}
          [:td index]
          [:td (:subject mail)]
          [:td (:from mail)]
          [:td (:sent-date mail)]])]]]]])
