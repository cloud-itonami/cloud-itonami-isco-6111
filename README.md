# cloud-itonami-isco-6111

Open Occupation Blueprint for **ISCO-08 6111**: Field Crop and Vegetable Growers.

This repository designs a forkable OSS business for an independent field crop and vegetable grower: a field-monitoring robot performs soil sensing and planting assist under a governor-gated actor, so the operator keeps their own field and input records instead of renting a closed farm-management SaaS.

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot performs
the physical domain work**. Here a field-monitoring robot performs soil sensing, planting assist and harvest support under an actor that proposes
actions and an independent **Field Crop Growing Governor** that gates them. The governor never
dispatches hardware itself; `:high`/`:safety-critical` actions (such as
pesticide/herbicide application near water sources, or operating near heavy farm machinery) require human sign-off.

A live sample of the operator console (robotics safety console, shared template) is rendered in [docs/samples/operator-console.html](docs/samples/operator-console.html) — pure-data HTML output of `kotoba.robotics.ui`.

## Core Contract

```text
planting plan + field map + input protocol
        |
        v
Field Crop Advisor -> Field Crop Growing Governor -> plant/harvest, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses, suppress
an operating record, or disclose sensitive data without governor approval and
audit evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `6111`). Required capabilities:

- :robotics
- :telemetry
- :optimization
- :dmn
- :bpmn
- :audit-ledger
- :forms

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.
