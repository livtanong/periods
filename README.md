# Periods

A small time-period (e.g. 4 months, 3 hours, 2 minutes) library 
for easy conversion and normalization. Not meant to work on date-times.
I repeat, this is not a date-time library.

## Installation
[![clojars-image-shield]](https://clojars.org/com.levitanong/periods)

## Usage
From this point forward, it is assumed that your require statement looks like this
```clj
(require '[periods.core :as periods])
```

### Periods are just maps.
```clj
{:hours 25}
```

### The keys are unabbreviated and plural.
```clj
(def period-units
  [:years :months :days :hours :minutes :seconds :milliseconds])
```

### Normalize periods to be proper.
```clj
(periods/normalize {:hours 25}) ;; => {:days 1, :hours 1}
```

### Want to convert some period into a single period?
```clj
(periods/period->mono-period {:years 2 :days 40} :minutes) ;; => {:minutes 1109520}
```

### 365.25 days a year
```clj
(periods/normalize-milliseconds {:milliseconds 66571200000}) ;; => {:years 2, :months 1, :days 9, :hours 13, :minutes 30}
```

[clojars-image-shield]: https://img.shields.io/clojars/v/com.levitanong/periods.svg