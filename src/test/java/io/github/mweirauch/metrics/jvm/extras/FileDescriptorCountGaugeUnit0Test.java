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
import static org.junit.Assume.assumeTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.management.ObjectName;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

public class FileDescriptorCountGaugeUnit0Test {

    private final OperatingSystemMXBean osBean = new DummyOperatingSystemMXBean();

    private static class DummyOperatingSystemMXBean implements OperatingSystemMXBean {

        @Override
        public ObjectName getObjectName() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getArch() {
            return null;
        }

        @Override
        public String getVersion() {
            return null;
        }

        @Override
        public int getAvailableProcessors() {
            return 0;
        }

        @Override
        public double getSystemLoadAverage() {
            return 0;
        }

        @SuppressWarnings("unused")
        public long getOpenFileDescriptorCount() {
            return 512L;
        }

        @SuppressWarnings("unused")
        public long getMaxFileDescriptorCount() {
            return 1024L;
        }

    }

    @Test
    public void testNullContract() {
        final FileDescriptorCountGauge uut = new FileDescriptorCountGauge(osBean);

        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @Test
    public void testInstantiation() {
        new FileDescriptorCountGauge();
    }

    @Test
    public void testGetValue() throws Exception {
        final FileDescriptorCountGauge uut = new FileDescriptorCountGauge(osBean);

        final Long value = uut.getValue();

        assertNotNull(value);
        assertEquals(Long.valueOf(512), value);
    }

    @SuppressWarnings("restriction")
    @Test
    public void testGetValueReal() throws Exception {
        final OperatingSystemMXBean realOsBean = ManagementFactory.getOperatingSystemMXBean();

        assumeTrue(realOsBean instanceof com.sun.management.UnixOperatingSystemMXBean);

        final FileDescriptorCountGauge uut = new FileDescriptorCountGauge(realOsBean);

        final Long value = uut.getValue();

        assertNotNull(value);
        assertTrue(value > 0L);
    }

}
