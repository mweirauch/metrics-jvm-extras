# metrics-jvm-extras

A set of additional metrics complementing Dropwizards [metrics-jvm](https://github.com/dropwizard/metrics/tree/3.2-development/metrics-jvm).

## Motivation

* get "real" memory usage of the JVM beyond its managed parts
* get ahold of that info from within the JVM in environments where you can't
  scrape from the outside (e.g. PaaS)

## Usage

There's no publicaly available artifacts, yet. Stay tuned.

```java
metrics.register("jvm.native", new NativeMemoryUsageGaugeSet());
```

## Available Metrics

### NativeMemoryUsageGaugeSet

The `NativeMemoryUsageGaugeSet` reads values from `/proc/self/smaps`.

* `vss`: Virtual set size. The amount of virtual memory the process can access.
   Mostly useles, but included for completenes sake.
* `rss`: Resident set size. The amount of process memory currently in RAM.
* `pss`: Proportional set size. The amount of process memory currently in RAM,
  accounting shared pages among processes. This metric is the most accurate in
  terms of "real" memory usage.
* `swap`: The amount of process memory paged out to swap.
* `swappss`: The amount of process memory paged out to swap accounting for
  shared memory among processes. Since Linux 4.3. Will return `-1` if your
  kernel is older. As with `pss`, the most accurate metric to watch.

## Notes
* procfs data is cached for `100ms` in order to relief the filesystem pressure
  when `Metric`s based on this data are queried by the registry one after
  another on collection run.
