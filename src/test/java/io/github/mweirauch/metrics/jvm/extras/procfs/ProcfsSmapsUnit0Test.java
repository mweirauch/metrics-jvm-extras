/*
 * Copyright © 2016-2019 Michael Weirauch (michael.weirauch@gmail.com)
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsSmaps.KEY;

public class ProcfsSmapsUnit0Test {

    private static Path BASE;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        BASE = Paths.get(ProcfsSmapsUnit0Test.class.getResource("/procfs/").toURI());
    }

    @Test
    public void testNullContract() {
        final ProcfsSmaps uut = new ProcfsSmaps(mock(ProcfsReader.class));

        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @SuppressWarnings("unused")
    @Test
    public void testInstantiation() {
        new ProcfsSmaps();
    }

    @Test
    public void testSimple() {
        final ProcfsSmaps uut = new ProcfsSmaps(new ProcfsReader(BASE, "smaps-001.txt"));

        assertEquals(Long.valueOf(4096), uut.get(KEY.VSS));
        assertEquals(Long.valueOf(4096), uut.get(KEY.RSS));
        assertEquals(Long.valueOf(2048), uut.get(KEY.PSS));
        assertEquals(Long.valueOf(0), uut.get(KEY.SWAP));
        assertEquals(Long.valueOf(-1), uut.get(KEY.SWAPPSS));
    }

    @Test
    public void testComplex() {
        final ProcfsSmaps uut = new ProcfsSmaps(new ProcfsReader(BASE, "smaps-002.txt"));

        assertEquals(Long.valueOf(4318720000L), uut.get(KEY.VSS));
        assertEquals(Long.valueOf(30535680), uut.get(KEY.RSS));
        assertEquals(Long.valueOf(20059136), uut.get(KEY.PSS));
        assertEquals(Long.valueOf(0), uut.get(KEY.SWAP));
        assertEquals(Long.valueOf(0), uut.get(KEY.SWAPPSS));
    }

    @Test
    public void testReturnDefaultValuesOnReaderFailure() throws IOException {
        final ProcfsReader reader = mock(ProcfsReader.class);
        when(reader.read()).thenThrow(new IOException("THROW"));

        final ProcfsSmaps uut = new ProcfsSmaps(reader);

        assertEquals(Long.valueOf(-1), uut.get(KEY.VSS));
        assertEquals(Long.valueOf(-1), uut.get(KEY.RSS));
        assertEquals(Long.valueOf(-1), uut.get(KEY.PSS));
        assertEquals(Long.valueOf(-1), uut.get(KEY.SWAP));
        assertEquals(Long.valueOf(-1), uut.get(KEY.SWAPPSS));
    }

}
