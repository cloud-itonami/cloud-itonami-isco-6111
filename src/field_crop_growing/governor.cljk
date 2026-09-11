(ns field-crop-growing.governor
  "FieldCropGrowingGovernor — the independent safety/traceability
  layer for the ISCO-08 6111 independent field-crop-and-vegetable-
  growing actor. Wired as its own `:govern` node in
  `field-crop-growing.actor`'s StateGraph, downstream of `:advise` —
  the Advisor has no notion of plot provenance or pesticide/machinery
  risk, so this MUST be a separate system able to reject a proposal
  (itonami actor pattern, per ADR-2607011000 / CLAUDE.md Actors
  section).

  `check` is a pure function of (request, context, proposal, store) ->
  verdict; it never mutates the store. The StateGraph's `:decide` node
  routes on the verdict:
    :hard? true                → :hold  (irreversible, no write)
    :escalate? true            → :request-approval (interrupt-before)
    otherwise                  → :commit

  HARD invariants (:hard? true, ALWAYS :hold, never overridable):
    1. plot provenance      — the request's plot must be registered.
    2. no-actuation         — proposal :effect must be :propose.
  ESCALATION invariants (:escalate? true, ALWAYS human sign-off, per
  the README robotics-premise: pesticide/herbicide application near
  water sources, or operating near heavy farm machinery, always
  require human sign-off):
    3. :op :pesticide-application-near-water.
    4. :op :operate-near-heavy-machinery.
    5. low confidence (< `confidence-floor`)."
  (:require [field-crop-growing.store :as store]))

(def confidence-floor 0.6)
(def ^:private escalating-ops #{:pesticide-application-near-water :operate-near-heavy-machinery})

(defn- hard-violations [{:keys [proposal]} plot-record]
  (cond-> []
    (nil? plot-record)
    (conj {:rule :no-plot :detail "未登録 plot"})

    (not= :propose (:effect proposal))
    (conj {:rule :no-actuation :detail "effect は :propose のみ許可（直接書込禁止）"})))

(defn check
  "Assess a proposal against `request`/`context`/`proposal` and a
  `store` implementing `field-crop-growing.store/Store`. Returns
  `{:ok? bool :violations [...] :confidence n :hard? bool :escalate? bool}`."
  [request context proposal store]
  (let [plot-record (store/plot store (:plot-id request))
        hard (hard-violations {:proposal proposal} plot-record)
        hard? (boolean (seq hard))
        conf (or (:confidence proposal) 0.0)
        low? (< conf confidence-floor)
        risky-op? (contains? escalating-ops (:op proposal))]
    {:ok? (and (not hard?) (not low?) (not risky-op?))
     :violations hard
     :confidence conf
     :hard? hard?
     :escalate? (and (not hard?) (or low? risky-op?))}))
