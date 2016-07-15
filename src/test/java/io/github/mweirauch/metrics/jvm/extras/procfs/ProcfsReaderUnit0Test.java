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
package io.github.mweirauch.metrics.jvm.extras.procfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsReader.ReadResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

public class ProcfsReaderUnit0Test {

    private static Path BASE;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        BASE = Paths.get(Class.class.getResource("/procfs/").toURI());
    }

    @Test
    public void testNullContract() throws Exception {
        final ProcfsReader uut = new ProcfsReader(BASE, "smaps-001.txt");

        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @Test(expected = RuntimeException.class)
    public void testReadProcSelfNonExistant() throws Exception {
        final ProcfsReader uut = mock(ProcfsReader.class);

        when(uut.readPath(any())).thenThrow(new IOException("THROW"));
        when(uut.read()).thenCallRealMethod();
        when(uut.read(anyLong())).thenCallRealMethod();

        uut.read();
    }

    @Test
    public void testRead() throws Exception {
        final ProcfsReader uut = new ProcfsReader(BASE, "smaps-001.txt");

        final ReadResult result = uut.read();

        assertNotNull(result);
        assertEquals(17, result.getLines().size());
        assertEquals("Size:                  4 kB", result.getLines().get(1));
        assertEquals("Locked:                0 kB", result.getLines().get(16));
    }

    @Test
    public void testCacheResultMissInitialAndSubsequent() throws IOException {
        final ProcfsReader uut = new ProcfsReader(BASE, "smaps-001.txt");
        final ProcfsReader spy = spy(uut);

        ReadResult result = spy.read();

        assertTrue(result.isUpdated());

        spy.lastReadTime = 0;
        result = spy.read(ProcfsReader.CACHE_DURATION_MS + 10);

        // updated data and lastReadTime was updated?
        assertTrue(result.isUpdated());
        assertNotEquals(0, spy.lastReadTime);

        verify(spy, times(2)).readPath(any());
        verify(spy, times(2)).cacheResult(any(), any());
    }

    @Test
    public void testCacheResultHit() throws IOException {
        final ProcfsReader uut = new ProcfsReader(BASE, "smaps-001.txt");
        final ProcfsReader spy = spy(uut);

        ReadResult result = spy.read();

        assertTrue(result.isUpdated());

        spy.lastReadTime = 0;
        result = spy.read(ProcfsReader.CACHE_DURATION_MS - 10);

        // cached data and lastReadTime was not touched?
        assertFalse(result.isUpdated());
        assertEquals(0, spy.lastReadTime);

        verify(spy).readPath(any());
        verify(spy).cacheResult(any(), any());
    }

    @Test
    public void testGetInstance() throws Exception {
        final ProcfsReader instance1 = ProcfsReader.getInstance("foo");
        final ProcfsReader instance2 = ProcfsReader.getInstance("foo");

        assertTrue(instance1 == instance2);

        final ProcfsReader instance3 = ProcfsReader.getInstance("bar");

        assertFalse(instance3 == instance1);
        assertFalse(instance3 == instance2);
    }

}
