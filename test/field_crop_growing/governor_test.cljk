(ns field-crop-growing.governor-test
  (:require [clojure.test :refer [deftest is testing]]
            [field-crop-growing.store :as store]
            [field-crop-growing.governor :as governor]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-plot! st {:plot-id "plot-1" :name "North Field"})
    st))

(deftest ok-on-clean-plant
  (let [st (fresh-store)
        proposal {:op :plant :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:plot-id "plot-1"} {} proposal st)]
    (is (:ok? v))
    (is (not (:hard? v)))
    (is (not (:escalate? v)))))

(deftest hard-on-unregistered-plot
  (let [st (fresh-store)
        proposal {:op :plant :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:plot-id "no-such-plot"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :no-plot (:rule %)) (:violations v)))))

(deftest hard-on-no-actuation-violation
  (let [st (fresh-store)
        proposal {:op :plant :effect :direct-write :confidence 0.9 :stake :low}
        v (governor/check {:plot-id "plot-1"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :no-actuation (:rule %)) (:violations v)))))

(deftest escalates-on-pesticide-application-near-water
  (let [st (fresh-store)
        proposal {:op :pesticide-application-near-water :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:plot-id "plot-1"} {} proposal st)]
    (is (:escalate? v))
    (is (not (:hard? v)))))

(deftest escalates-on-operate-near-heavy-machinery
  (let [st (fresh-store)
        proposal {:op :operate-near-heavy-machinery :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:plot-id "plot-1"} {} proposal st)]
    (is (:escalate? v))
    (is (not (:hard? v)))))

(deftest escalates-on-low-confidence
  (let [st (fresh-store)
        proposal {:op :plant :effect :propose :confidence 0.2 :stake :low}
        v (governor/check {:plot-id "plot-1"} {} proposal st)]
    (is (:escalate? v))
    (is (not (:hard? v)))))

(deftest store-records-and-ledger-append-only
  (let [st (fresh-store)]
    (store/commit-record! st {:plot-id "plot-1" :op :harvest})
    (store/append-ledger! st {:disposition :commit})
    (is (= 1 (count (store/records-of st "plot-1"))))
    (is (= 1 (count (store/ledger st))))))
