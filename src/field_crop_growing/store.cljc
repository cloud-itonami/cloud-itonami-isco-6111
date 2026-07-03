(ns field-crop-growing.store
  "SSoT for the ISCO-08 6111 independent field-crop-and-vegetable-
  growing sole-proprietor actor. Store is a protocol injected into
  the `field-crop-growing.actor` StateGraph — `MemStore` is the
  default, deterministic, zero-dep backend; a Datomic/kotoba-server-
  backed implementation can be swapped in without touching the actor
  or governor (itonami actor pattern, per ADR-2607011000 / CLAUDE.md
  Actors section).

  Domain:

    plot     — a registered field plot (:plot-id, :name)
    record   — a committed operating record under a plot (plant
               step, harvest entry, pesticide/herbicide application
               near a water source, operation near heavy farm
               machinery) — written ONLY via commit-record!, never
               mutated in place
    ledger   — an append-only audit trail of every proposal/verdict/
               disposition, regardless of outcome (commit or hold)")

(defprotocol Store
  (plot [s plot-id])
  (records-of [s plot-id])
  (ledger [s])
  (register-plot! [s plot])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (plot [_ plot-id] (get-in @a [:plots plot-id]))
  (records-of [_ plot-id] (filter #(= plot-id (:plot-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-plot! [s plot]
    (swap! a assoc-in [:plots (:plot-id plot)] plot) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:plots {} :records [] :ledger []} seed)))))
