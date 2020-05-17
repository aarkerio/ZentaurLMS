(ns zentaur.febe.pagination
  (:require #?(:clj [clojure.tools.logging :as log])
            [clojure.string :as cs]))

;;; PAGINATION

;Parameters
; :records        : Total number of records
; :items-per-page       : Items shown per page
; :max-links      : Maximum number of pagination links appear
; :current        : Current page number
; :biased         : :left or :right, if the number of pages shown is even,
;                   current page should either sit in left half of right half
; :link-tpl       : template to use for individual links
; :list-tpl       : tempate to use for entire list

(defn paginate
  "Returns data required to render paginator. Code from: https://github.com/manawardhana/paginator-clj"
  [{:keys [records items-per-page max-links current biased] :or {items-per-page 10 max-links 10 current 1 biased :left}}]
  (let [total-blocks    (int (Math/ceil (/ records items-per-page)))  ;; total pages adjusted to the next up integer, Math/ceil returns a float
        half            (Math/floor (/ max-links 2))      ;; round off the number passed as a parameter to its nearest integer in Downward direction. 2.0 constant weil immer vier?
        left-half       (int (if (= biased :left)  (- half (if (odd? max-links) 0 1)) half))
        right-half      (int (if (= biased :right) (- half (if (odd? max-links) 0 1)) half))
        virtual-start   (- current left-half)    ;; can be a minus. The starting point to the left considering "current" and "max-links"
        virtual-end     (+ current right-half)   ;; can be exceeding than available page limit. The ending point to the right considering "current" and "max-links"
        start           (max 1 (- virtual-start (if (> virtual-end total-blocks) (- virtual-end total-blocks) 0)))
        end             (inc (min total-blocks (+ current right-half (if (< virtual-start 1) (Math/abs (dec virtual-start)) 0))))
        dis-first-arrow (>= current max-links)              ;; display first arrow
        no-last-arrow   (- total-blocks max-links)          ;; defines no last arrow block
        dis-last-arrow  (< (- virtual-end no-last-arrow) max-links)  ;; display last arrow
       ]
    {:current current :pages (range start end) :dis-last-arrow dis-last-arrow :dis-first-arrow dis-first-arrow :total-blocks total-blocks}))

(defn format-link
  [located-tpl page current]
  (let  [link-1 (cs/replace located-tpl "{{page-number}}" (str page))
         class (if (= page current) "btn-outline-primary-orange" "btn-outline-primary-green")]
    (cs/replace link-1 "{{class}}" class)))

; todo: visible page number adjustment

; list template vars
; link template vars
; :link-tpl
; :list-tpl
; :show-first?
; :show-last?
; :first-text
; :last-text
(defn html-paginator [{:keys [location] :or {location "/"} :as all}]
  (let [{:keys [current pages dis-last-arrow dis-first-arrow total-blocks]} (paginate all)
        link-tpl        "<a class=\"btn {{class}}\" href=\"{{location}}/{{page-number}}\">{{page-number}}</a>"
        list-tpl        "<nav class=\"blog-pagination\">{{page-links}}</ul>"
        first-arrow     (str "<a class=\"btn btn-outline-primary-green\" title=\"First\" href=\"" location "/1\"> &lt; &lt;</a>")
        located-tpl     (cs/replace link-tpl "{{location}}" location)
        formatted-pages (reduce str (for [x pages] (format-link located-tpl x current)))
        first-links     (if dis-first-arrow (str first-arrow formatted-pages) formatted-pages)
        last-arrow      (if dis-last-arrow  (str first-links (str "<a class=\"btn btn-outline-primary-green\" title=\"Last\" href=\"" location "/" total-blocks "\"> &gt; &gt;</a>")) first-links)]
    (cs/replace list-tpl "{{page-links}}" last-arrow)))

