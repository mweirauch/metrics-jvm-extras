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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

public class UptimeGaugeUnit0Test {

    private final RuntimeMXBean runtimeBean = mock(RuntimeMXBean.class);

    @Test
    public void testNullContract() {
        final UptimeGauge uut = new UptimeGauge(runtimeBean);

        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @SuppressWarnings("unused")
    @Test
    public void testInstantiation() {
        new UptimeGauge();
    }

    @Test
    public void testGetValue() throws Exception {
        final UptimeGauge uut = new UptimeGauge(runtimeBean);
        when(runtimeBean.getUptime()).thenReturn(Long.valueOf(1337));

        final Long value = uut.getValue();

        assertNotNull(value);
        assertEquals(Long.valueOf(1337), value);
    }

    @Test
    public void testGetValueReal() throws Exception {
        final UptimeGauge uut = new UptimeGauge(ManagementFactory.getRuntimeMXBean());
        when(runtimeBean.getUptime()).thenReturn(Long.valueOf(1337));

        final Long value = uut.getValue();

        assertNotNull(value);
        assertTrue(value > 0);
    }

}
