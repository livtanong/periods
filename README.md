- [Installation](#org1456745)
- [Usage](#org5e48ea8)
  - [Periods are just hash maps.](#org12f49e6)
  - [The keys are unabbreviated and plural.](#org1ca08a6)
  - [Normalize periods to be proper.](#org83e6d71)
  - [365.25 days a year](#org7c32670)

A small time period (e.g. 4 months, 3 hours, 2 minutes) library determined NOT to work on date-times. I repeat, this is not a date-time library.


<a id="org1456745"></a>

# Installation

<a href="https://clojars.org/com.levitanong/periods">
<img src="https://img.shields.io/clojars/v/com.levitanong/periods.svg" />
</a>


<a id="org5e48ea8"></a>

# Usage

From this point forward, it is assumed that your require statement looks like this

```clojure
(require '[periods.core :as periods])
```


<a id="org12f49e6"></a>

## Periods are just hash maps.

```clojure
{:hours 25}
```


<a id="org1ca08a6"></a>

## The keys are unabbreviated and plural.

```clojure
(def period-units
  [:years :months :days :hours :minutes :seconds :milliseconds])
```


<a id="org83e6d71"></a>

## Normalize periods to be proper.

```clojure
(periods/normalize {:hours 25}) ;; {:days 1, :hours 1}
```


<a id="org7c32670"></a>

## 365.25 days a year

That 0.25 refers to leap years. This means there are 30.4375 days in a month on average.

```clojure
(periods/normalize-milliseconds {:milliseconds 66571200000})
;; {:years 2 :months 1 :days 9 :hours 13 :minutes 30}
```
