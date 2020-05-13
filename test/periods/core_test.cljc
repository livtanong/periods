(ns periods.core-test
  (:require
   [periods.core :as periods]
   [clojure.test :as test :refer [deftest testing is]]))

(deftest period->milliseconds
  (testing "Using `==` for equality between different number types like scientifc notation and integers"
    (testing "empty map"
      (is (== (:milliseconds (periods/period->millisecond-period {})) 0)))
    (testing "one unit"
      (is (== (:milliseconds (periods/period->millisecond-period {:hours 1})) 3600000)))
    (testing "multiple units"
      (is (== (:milliseconds (periods/period->millisecond-period {:years 2 :days 40})) 66571200000)))))

(deftest period->mono-period
  (testing "empty map"
    (is (== (:seconds (periods/period->mono-period {} :seconds)) 0))
    (is (= (keys (periods/period->mono-period {} :minutes)) '(:minutes))))
  (testing "one unit"
    (is (== (:seconds (periods/period->mono-period {:hours 1} :seconds)) 3600)))
  (testing "multiple units"
    (is (== (:minutes (periods/period->mono-period {:years 2 :days 40} :minutes)) 1109520)))
  (testing "ignore smaller units"
    (is (== (:minutes (periods/period->mono-period {:years 2 :days 40 :seconds 59 :milliseconds 2} :minutes)) 1109520))))

(deftest normalize-milliseconds
  (testing "999 milliseconds"
    (is (= (periods/normalize-milliseconds {:milliseconds 999}) {:milliseconds 999})))
  (testing "1 hour"
    (is (= (periods/normalize-milliseconds {:milliseconds 3600000}) {:hours 1})))
  (testing "multiple units"
    (is (= (periods/normalize-milliseconds {:milliseconds 66571200000})
           {:years 2 :months 1 :days 9 :hours 13 :minutes 30}))))

(deftest normalize
  (is (= (periods/normalize {:hours 25}) {:days 1 :hours 1}))
  (is (= (periods/normalize {:days 0.5}) {:hours 12}))
  (is (= (periods/normalize {:hours 1.5 :minutes 30}) {:hours 2})))

(deftest format-period
  (is (= (periods/stringify-period {:hours 1 :minutes 5})
         "1 hour 5 minutes")))

