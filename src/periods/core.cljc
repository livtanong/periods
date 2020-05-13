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
(s/def ::unit #{:years :months :days :hours :minutes :seconds :milliseconds})

(def shrinking-multiplier
  {:milliseconds 1
   :seconds      1000
   :minutes      60
   :hours        60
   :days         24
   :months       30.4375 ;; accounts for leap years
   :years        12})

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

(defn period->mono-period
  "Convert any period to a period with one specific unit.
If source period has data in smaller periods than `target-unit`,
that data will be dropped."
  [period target-unit]
  {:pre  [(s/valid? ::period period) (s/valid? ::unit target-unit)]
   :post [(contains? % target-unit)]}
  ;; Make it so that the target unit is not shrinkable to a smaller unit.
  (let [limited-shrink (dissoc shrink target-unit)]
    (reduce (fn [acc-period period-unit]
              (if-let [unit-value (get acc-period period-unit)]
                (let [multiplier  (get shrinking-multiplier period-unit 1)
                      shrunk-unit (limited-shrink period-unit)]
                ;; If a value already exists for the shrunken unit, just add them together.
                ;; e.g. {:hours 1 :minutes 30} => {:minutes 90}
                  (-> acc-period
                      ;; remove the bigger unit
                      (dissoc period-unit)
                      ;; apply new data.
                      (update shrunk-unit
                              (fn [existing-value]
                                (+ (or existing-value 0)
                                   (* multiplier unit-value))))))
              ;; if the value doesn't exist, skip.
                acc-period))
          ;; initialize period to always at least have 0 of target unit.
            (update period target-unit
                    (fn [value] (or value 0)))
          ;; we want to go from big units first, milliseconds last.
          ;; only take up until target-unit.
            (let [target-unit-index (.indexOf period-units target-unit)]
              (take target-unit-index period-units)))))

(defn period->millisecond-period
  "Convert any period to a period with milliseconds"
  [period]
  {:pre  [(s/valid? ::period period)]
   :post [(s/valid? ::millisecond-period %)]}
  (period->mono-period period :milliseconds))

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
