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

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsSmaps;
import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsSmaps.KEY;

public class NativeMemoryUsageGaugeSetUnit0Test {

    private final ProcfsSmaps smaps = mock(ProcfsSmaps.class);

    @Test
    public void testNullContract() {
        final NativeMemoryUsageGaugeSet uut = new NativeMemoryUsageGaugeSet(smaps);

        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @SuppressWarnings("unused")
    @Test
    public void testInstantiation() {
        new NativeMemoryUsageGaugeSet();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetMetrics() throws Exception {
        when(smaps.get(KEY.VSS)).thenReturn(1L);
        when(smaps.get(KEY.RSS)).thenReturn(2L);
        when(smaps.get(KEY.PSS)).thenReturn(3L);
        when(smaps.get(KEY.SWAP)).thenReturn(4L);
        when(smaps.get(KEY.SWAPPSS)).thenReturn(5L);

        final NativeMemoryUsageGaugeSet uut = new NativeMemoryUsageGaugeSet(smaps);

        final Map<String, Metric> metrics = uut.getMetrics();

        assertNotNull(metrics);
        assertEquals(5, metrics.keySet().size());
        assertTrue(metrics.keySet()
                .containsAll(Arrays.asList("vss", "rss", "pss", "swap", "swappss")));

        assertEquals(1L, ((Gauge) metrics.get("vss")).getValue());
        assertEquals(2L, ((Gauge) metrics.get("rss")).getValue());
        assertEquals(3L, ((Gauge) metrics.get("pss")).getValue());
        assertEquals(4L, ((Gauge) metrics.get("swap")).getValue());
        assertEquals(5L, ((Gauge) metrics.get("swappss")).getValue());
    }

}
