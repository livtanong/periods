(ns periods.core
  "This namespace focuses entirely on periods.
  This is not a date/time library.

  All period units are plural."
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(s/def ::milliseconds number?)
(s/def ::seconds number?)
(s/def ::minutes number?)
(s/def ::hours number?)
(s/def ::days number?)
(s/def ::months number?)
(s/def ::years number?)
(s/def ::period
  (s/keys :opt-un [::years ::months ::days ::hours ::minutes ::seconds ::milliseconds]))
(s/def ::millisecond-period
  (s/keys :req-un [::milliseconds]))

(def shrinking-multiplier
  {:milliseconds 1
   :seconds      1000
   :minutes      60
   :hours        60
   :days         24
   :months       30.4375 ;; accounts for leap years
   :years        12
   })

(def shrink
  {:milliseconds nil
   :seconds      :milliseconds
   :minutes      :seconds
   :hours        :minutes
   :days         :hours
   :months       :days
   :years        :months})

(def period-units
  [:years :months :days :hours :minutes :seconds :milliseconds])

(defn period->millisecond-period
  "Convert any period to a period with milliseconds"
  [period]
  {:pre  [(s/valid? ::period period)]
   :post [(s/valid? ::millisecond-period %)]}
  (reduce (fn [acc-period period-unit]
            ;; if the value doesn't exist, skip.
            (if-let [unit-value (get acc-period period-unit)]
              (let [multiplier   (get shrinking-multiplier period-unit 1)
                    shrunk-unit  (shrink period-unit)
                    shrunk-value (* multiplier unit-value)]
                ;; If a value already exists for the shrunken unit, just add them together.
                ;; e.g. {:hours 1 :minutes 30} => {:minutes 90}
                (if-not shrunk-unit
                  acc-period
                  (merge-with +
                              (dissoc acc-period period-unit)
                              {shrunk-unit shrunk-value})))
              acc-period))
          ;; initialize period to always at least have 0 milliseconds
          (if (:milliseconds period)
            period
            (assoc period :milliseconds 0))
          ;; we want to go from big units first, milliseconds last.
          period-units))

(defn normalize-milliseconds
  [millisecond-period]
  {:pre  [(s/valid? ::millisecond-period millisecond-period)]
   :post [(s/valid? ::period %)]}
  ;; While tempting to start from small to big (first converting from mills to seconds)
  ;; We need to go from big to small since we can't guarantee multiples.
  (letfn [(period-units->factor [p]
            (apply * (map shrinking-multiplier p)))]
    (loop [milliseconds       (:milliseconds millisecond-period)
           out-period         {}
           [next-unit & next-units
            :as period-units] period-units]
      (if (empty? period-units)
        out-period
        (let [factor      (period-units->factor period-units)
              quotient    (int (quot milliseconds factor))
              remainder   (rem milliseconds factor)
              next-period (if (zero? quotient)
                            out-period
                            (assoc out-period next-unit quotient))]
          (recur remainder
                 next-period
                 next-units))))))

(defn normalize
  "If a value is outside the normal bounds of a unit, spill over to next unit.
  e.g.
  {:hours 25} => {:days 1 :hours 1}
  {:days 0.5} => {:hours 12}
  {:hours 1.5} => {:hours 1 :minutes 30}
  {:hours 1.5 :minutes 30} => {:hours 2}"
  [period]
  {:pre  [(s/valid? ::period period)]
   :post [(s/valid? ::period %)]}
  (normalize-milliseconds (period->millisecond-period period)))

(defn stringify-period
  "Not too happy with this API yet. Do not use unless you're willing to deal with breaking changes.
  reserve the symbol `format-period` for a future api that makes use of format strings to make usage easier"
  ([period]
   (stringify-period period (fn [value unit]
                           (let [unit-str (name unit)]
                             (if (> value 1)
                               unit-str
                               (subs unit-str 0 (dec (count unit-str))))))))
  ([period format-unit]
   {:pre [(s/valid? ::period period)]}
   (let [normalized   (normalize period)
         present-keys (set (keys normalized))
         units        (filter present-keys period-units)]
     (string/join " "
                  (map (fn [unit]
                         (let [value (get normalized unit)]
                           (str value " " (format-unit value unit))))
                       units)))))
