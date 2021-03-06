/*
 * Copyright © 2016 Michael Weirauch (michael.weirauch@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mweirauch.metrics.jvm.extras;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsSmaps;
import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsSmaps.KEY;

/**
 * @deprecated Use {@link ProcessMemoryUsageGaugeSet}
 *
 */
@Deprecated
public class NativeMemoryUsageGaugeSet implements MetricSet {

    private final ProcfsSmaps smaps;

    public NativeMemoryUsageGaugeSet() {
        this.smaps = new ProcfsSmaps();
    }

    /* default */ NativeMemoryUsageGaugeSet(ProcfsSmaps smaps) {
        this.smaps = Objects.requireNonNull(smaps);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();

        gauges.put("vss", new Gauge<Long>() {

            @Override
            public Long getValue() {
                return smaps.get(KEY.VSS);
            }
        });

        gauges.put("rss", new Gauge<Long>() {

            @Override
            public Long getValue() {
                return smaps.get(KEY.RSS);
            }
        });

        gauges.put("pss", new Gauge<Long>() {

            @Override
            public Long getValue() {
                return smaps.get(KEY.PSS);
            }
        });

        gauges.put("swap", new Gauge<Long>() {

            @Override
            public Long getValue() {
                return smaps.get(KEY.SWAP);
            }
        });

        gauges.put("swappss", new Gauge<Long>() {

            @Override
            public Long getValue() {
                return smaps.get(KEY.SWAPPSS);
            }
        });

        return Collections.unmodifiableMap(gauges);
    }

}
