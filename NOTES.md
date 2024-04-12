# flight-tower/Flights

## Problem Notes

Functional requirements
1. Input
- Flight Events -> file -> read by program
    - Fields:
        - `Plane ID`
        - `Plane Model`
        - `Origin`
        - `Destination`
        - `Event Type`
        - `Timestamp`
        - `Fuel Delta`
- Query (what is the state @ time `t`?) -> CLI -> read by program
    - The form of this will be either:
        1. initialization arguments (if run-once)
        2. input to a CLI interface (if continuously running)
2. Output (state)
- Output a compound, aggregated 'state' (fields described below) for each Flight (i.e. each distinct `Plane ID`)
- Make sure the output reflects the state of all Flights _at the timestamp provided_ in the Query (see 'Input' above).
    - i.e. we only use information from Flight Events which are chronologically _before_ the timestamp in the query, and nothing from any Flight Events chronologically _after_ the timestamp in the query.
- Fields:
    - `Plane ID`
        - primary key
    - `Flight Status`
        - derived from `Event Type` of chronologically last Flight Event
    - `Last known fuel level`
        - aggregation of all `Fuel Delta` values for all Flight Events, in chronological order
3. Handles (out of order) corrections
- Corrections are effectively amendments to a previous event.
- Amendments must always amend a previously consumed event.
    - This means they must correspond to a previous event.
    - We need a key for this.
    - The key we are going to use for this is `Plane ID` + `Timestamp`

General solution forms
1. Background service running which probably reads events from a file (one per line, append only), can be interacted with via the command line. Think something like sshd in unix.
-  Would 'build up' an internal model, in memory, of the state of all Flights, as the Flight Events are consumed.
2. Run-once service which reads events from a file, is run from the command line with arguments which include the query itself (if temporal query). Think something like grep/sed/awk in unix.
- Would read the events in, at runtime, and then perform the 'query', and then exit

Solution Comparison
- You could actually use the same algorithm for both (see `Algorithm` section below) - just the background service would effectively keep the 'grouped, updated' Flight Events in memory ready to aggregate through (until a particular timestamp `t`) for the query. This would give us a bit more performance, and is how you probably would want to design a real service in an event-driven system: consume events from an authorative source, perform desired aggregation / filtering / transformation etc. locally, and then store the result locally, for optimised querying. In a distributed system which needs to be horiztonally scalable, you would partition replicas based on a key (so each replica is only storing 1 shard of data from the full dataset). In this exercise the key would be `Plane ID`.
    - However, for the purposes of this exercise I won't do this. I will just present the solution as a run-once application, which loads & queries all the data, for simplicity.

Algorithm
- Returning the aggregated state for a particular timestamp (`t`)
    - How?
        - Consume events
        - Group by Plane ID
            - Per Group:
            - Identify and apply amendments & cancellations
                - Create is a regular event
                    - Example: `F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -120`
                - Amendment is an event with the same `Plane ID` and `Timestamp` as a previous (i.e. previously _ingested_, not previously chronologically) event. The form of the record is exactly the same as a 'Create' event
                    - Example: `F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -300`
                - Cancel is an event with the same `Plane ID` and `Timestamp` as a previous event. Except this event has _no other fields_ except `Plane ID` and `Timestamp`, so the form is different to that of a 'Create' or 'Amend'
                    - Example: `F551 2021-03-29T12:00:00`
            - Order the events chronologically
            - Aggregate them in sequence
                - Terminate aggregation when timestamp > `t`

Decision
- For this specific use-case, I think the run-once service is adequate.
- Reasons:
    - When doing temporal / historical queries over a dataset (i.e. working out what the value of an aggregation was at time `t`), there is always a step of _rebuilding_ state from a sequence of historical events, up to a particular cut-off timestamp (`t`, here). This is what we are doing in `Algorithm` above.
    - We can optimise the steps _up to_ this rebuilding, but the rebuilding still needs to be done. This means: the data we need to work this out will be the same cardinality as the data we _already have_ (i.e. the Flight Event data), we can't really pre-aggregate any of this data further than it already is, as we would lose information required to perform these kinds of 'historical queries'. Really the Flight Event data is pretty close to the data format we would choose to store. For this reason I would argue that the Flight Event data _as is_, is pretty much the dataset you would store - so we can think of it as such (maybe in real life it would be implemented as a database, or perhaps as an append-only log e.g. Kafka).
    - This is why I will leave it as a flat file here in this exercise for simplicity. You could imagine replacing the file with some datastore, but remember that the data _we would want_ (from said datastore) in order to perform this query, would _look like_ the Flight Events in the file.

- What if the dataset gets really large?
    - We could imagine the history on this getting super really over time. `Plane ID`s will be constant over many different flights and routes (it is unique to the plane itself, and the lifetime of a plane is long...) So we might not want to have to retrieve and parse (aggregate) all events from the beginning of time (i.e. beginning of the plane's life) as this could become sub-optimal as the dataset increases in size.
    - Solutions
        - Snapshots
            - Taking regular snapshots allows you to rebuild state _from_ the last snapshot (prior to timestamp `t`) rather than needing to consume all events from the beginning of time
            - This implies some kind of _compaction_ occurs, which takes into account late-arriving events. This means we need to start thinking about time-windowing the late arrivals (whether or not they are included in the snapshot etc) and things like this, which can get quite nuanced.


- The events, which we are choosing to store in, and consume from, a file, are really the equivalent of a database.
    - If we consider this to be the case, then the fact that we are receiving data for _all_ flights and returning output for _all_ flights is really just a quirk of the use-case. If it were a database then we could easily return data for a subset of Flights and then only return output for these Flights too.
    - For argument's sake you could use sqlite here, but for simplicity I will leave it as a flat file.


Implicit requirements
- The events seem to be ordered _per flight_, but not overall (i.e. we have partial rather than total ordering)
    - This is common in event driven systems - and is why we would potentially partition on `Plane ID` in a real system

Sample Data

F222 747 DUBLIN LONDON Re-Fuel 2021-03-29T10:00:00 200
F551 747 PARIS LONDON Re-Fuel 2021-03-29T10:00:00 345
F324 313 LONDON NEWYORK Take-Off 2021-03-29T12:00:00 0
F123 747 LONDON CAIRO Re-Fuel 2021-03-29T10:00:00 428
F123 747 LONDON CAIRO Take-Off 2021-03-29T12:00:00 0
F551 747 PARIS LONDON Take-Off 2021-03-29T11:00:00 0
F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -120
F123 747 LONDON CAIRO Land 2021-03-29T14:00:00 -324

Filtered: Flight F551

F551 747 PARIS LONDON Re-Fuel 2021-03-29T10:00:00 345
F551 747 PARIS LONDON Take-Off 2021-03-29T11:00:00 0
F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -120

Filtered: Flight F123

F123 747 LONDON CAIRO Re-Fuel 2021-03-29T10:00:00 428
F123 747 LONDON CAIRO Take-Off 2021-03-29T12:00:00 0
F123 747 LONDON CAIRO Land 2021-03-29T14:00:00 -324
