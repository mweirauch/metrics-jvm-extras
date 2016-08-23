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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Objects;

import com.codahale.metrics.Gauge;

public class UptimeGauge implements Gauge<Long> {

    private final RuntimeMXBean runtimeBean;

    public UptimeGauge() {
        this(ManagementFactory.getRuntimeMXBean());
    }

    public UptimeGauge(RuntimeMXBean runtimeBean) {
        this.runtimeBean = Objects.requireNonNull(runtimeBean);
    }

    @Override
    public Long getValue() {
        return runtimeBean.getUptime();
    }

}
