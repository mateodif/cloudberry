(ns cloudberry.front.ui.mail
  (:require [dumdom.core :refer [defcomponent]]))

(defcomponent Sidebar []
   [:div {:class [:column :is-1]}
    [:aside {:class [:menu :p-4]}
     [:p {:class :menu-label}
      "Folders"]
     [:ul {:class :menu-list}
      [:li [:a {:class :is-active}
            "Inbox"]]
      [:li [:a "Sent"]]
      [:li [:a "Spam"]]]]])

(defcomponent MailViewer [{:keys [inbox]}]
  [:div {:class [:columns :is-gapless] :style {:height "100vh"}}
   (Sidebar)
   [:div {:class :column}
    [:div {:class :section}
     [:h1 {:class :title} "Inbox"]
     [:table {:class [:table :is-fullwidth :is-hoverable]}
      [:thead
       [:tr
        [:th "Subject"]
        [:th "From"]
        [:th "Date"]]]
      [:tbody
       (for [mail inbox]
         [:tr {:class :is-clickable}
          [:td (:subject mail)]
          [:td (:from mail)]
          [:td (:sent-date mail)]])]]]]])
