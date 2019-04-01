
# Table of Contents

1.  [Installation](#org72b2cd0)
2.  [Usage](#org4407e1e)
    1.  [Periods are just hash maps.](#org820b002)
    2.  [The keys are unabbreviated and plural.](#orgfae67e6)
    3.  [Normalize periods to be proper.](#org1fd3b19)
    4.  [365.25 days a year](#org8a62f06)

A small time period (e.g. 4 months, 3 hours, 2 minutes) library determined NOT
to work on date-times. I repeat, this is not a date-time library.


<a id="org72b2cd0"></a>

# Installation

<a href="https://clojars.org/com.levitanong/periods">
<img src="https://img.shields.io/clojars/v/com.levitanong/periods.svg" />
</a>


<a id="org4407e1e"></a>

# Usage

From this point forward, it is assumed that your require statement looks like this

    (require '[periods.core :as periods])


<a id="org820b002"></a>

## Periods are just hash maps.

    {:hours 25}


<a id="orgfae67e6"></a>

## The keys are unabbreviated and plural.

    (def period-units
      [:years :months :days :hours :minutes :seconds :milliseconds])


<a id="org1fd3b19"></a>

## Normalize periods to be proper.

    (periods/normalize {:hours 25}) ;; {:days 1, :hours 1}


<a id="org8a62f06"></a>

## 365.25 days a year

That 0.25 refers to leap years. This means there are 30.4375 days in a month on
average.

    (periods/normalize-milliseconds {:milliseconds 66571200000})
    ;; {:years 2 :months 1 :days 9 :hours 13 :minutes 30}

