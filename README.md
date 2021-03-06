# metrics-jvm-extras

A set of additional metrics complementing Dropwizards [metrics-jvm](https://github.com/dropwizard/metrics/tree/3.2-development/metrics-jvm).

[![Apache License 2](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/mweirauch/metrics-jvm-extras/master/LICENSE.txt)
[![Travis CI](https://img.shields.io/travis/mweirauch/metrics-jvm-extras.svg?maxAge=300)](https://travis-ci.org/mweirauch/metrics-jvm-extras)
[![Codacy grade](https://img.shields.io/codacy/grade/3ace40206b314f72a690a00be45c9a5a.svg?maxAge=300)](https://www.codacy.com/app/mweirauch/metrics-jvm-extras)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mweirauch/metrics-jvm-extras.svg?maxAge=300)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.mweirauch%22%20AND%20a%3A%22metrics-jvm-extras%22)

## Motivation

* get "real" memory usage of the JVM beyond its managed parts
* get ahold of that info from within the JVM in environments where you can't
  scrape from the outside (e.g. PaaS)

## Usage

```xml
<dependency>
    <groupId>io.github.mweirauch</groupId>
    <artifactId>metrics-jvm-extras</artifactId>
    <version>x.y.z</version>
</dependency>
```

```java
metrics.register("jvm.process.mem", new ProcessMemoryUsageGaugeSet());
metrics.register("jvm.process.fds.count", new FileDescriptorCountGauge());
metrics.register("jvm.process.uptime", new UptimeGauge());
```

## Available Metrics

### ProcessMemoryUsageGaugeSet

The `ProcessMemoryUsageGaugeSet` reads values from `/proc/self/smaps`.
Please note that `procfs` is only available on Linux-based systems.

* `vss`: Virtual set size. The amount of virtual memory the process can access.
   Mostly useles, but included for completeness sake.
* `rss`: Resident set size. The amount of process memory currently in RAM.
* `pss`: Proportional set size. The amount of process memory currently in RAM,
  accounting for shared pages among processes. This metric is the most accurate in
  terms of "real" memory usage.
* `swap`: The amount of process memory paged out to swap.
* `swappss`: The amount of process memory paged out to swap accounting for
  shared memory among processes. Since Linux 4.3. Will return `-1` if your
  kernel is older. As with `pss`, the most accurate metric to watch.

### NativeMemoryUsageGaugeSet (deprecated)

Deprecated in favour of `ProcessMemoryUsageGaugeSet`. Returns the very same
values. Will be removed.

### FileDescriptorCountGauge

Provides the count of open file descriptors in use by the JVM process.

### UptimeGauge

Provides uptime in `ms`.

## Notes
* procfs data is cached for `100ms` in order to relief the filesystem pressure
  when `Metric`s based on this data are queried by the registry one after
  another on collection run.

## What could be next?
* CPU usage details
* JVM Native Memory Tracking details
