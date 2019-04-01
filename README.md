
# Table of Contents

1.  [Installation](#org332b290)
2.  [Usage](#org05df283)
    1.  [Periods are just hash maps.](#orgdc89c04)
    2.  [The keys are unabbreviated and plural.](#org1fd9fbd)
    3.  [Normalize periods to be proper.](#orgb1a1cb6)
    4.  [365.25 days a year](#org6785047)

A small time period (e.g. 4 months, 3 hours, 2 minutes) library determined NOT
to work on date-times. I repeat, this is not a date-time library.


<a id="org332b290"></a>

# Installation

![img](https://img.shields.io/clojars/v/com.levitanong/periods.svg)

[Clojars](https://clojars.org/com.levitanong/periods)


<a id="org05df283"></a>

# Usage

From this point forward, it is assumed that your require statement looks like this

    (require '[periods.core :as periods])


<a id="orgdc89c04"></a>

## Periods are just hash maps.

    {:hours 25}


<a id="org1fd9fbd"></a>

## The keys are unabbreviated and plural.

    (def period-units
      [:years :months :days :hours :minutes :seconds :milliseconds])


<a id="orgb1a1cb6"></a>

## Normalize periods to be proper.

    (periods/normalize {:hours 25}) ;; {:days 1, :hours 1}


<a id="org6785047"></a>

## 365.25 days a year

That 0.25 refers to leap years. This means there are 30.4375 days in a month on
average.

    (periods/normalize-milliseconds {:milliseconds 66571200000})
    ;; {:years 2 :months 1 :days 9 :hours 13 :minutes 30}

