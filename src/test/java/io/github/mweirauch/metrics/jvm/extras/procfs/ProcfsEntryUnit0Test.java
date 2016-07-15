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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsReader.ReadResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

public class ProcfsEntryUnit0Test {

    private static class TestProcfsEntry extends ProcfsEntry {

        protected TestProcfsEntry(ProcfsReader reader) {
            super(reader);
        }

        @Override
        protected void handle(Collection<String> lines) {
            Objects.requireNonNull(lines);
        }

    }

    private final ProcfsReader reader = mock(ProcfsReader.class);

    private final ProcfsEntry uut = new TestProcfsEntry(reader);

    @Test
    public void testNullContract() {
        final NullPointerTester npt = new NullPointerTester();

        npt.testConstructors(uut.getClass(), Visibility.PACKAGE);
        npt.testStaticMethods(uut.getClass(), Visibility.PACKAGE);
        npt.testInstanceMethods(uut, Visibility.PACKAGE);
    }

    @Test
    public void testCollectUpdated() throws Exception {
        final ReadResult readResult = mock(ReadResult.class);
        when(readResult.isUpdated()).thenReturn(true);
        final List<String> lines = Arrays.asList("foo");
        when(readResult.getLines()).thenReturn(lines);
        when(reader.read()).thenReturn(readResult);
        final ProcfsEntry spy = Mockito.spy(uut);

        spy.collect();

        verify(reader).read();
        verify(readResult).isUpdated();
        verify(spy).handle(lines);
    }

    @Test
    public void testCollectCached() throws Exception {
        final ReadResult readResult = mock(ReadResult.class);
        when(readResult.isUpdated()).thenReturn(false);
        final List<String> lines = Arrays.asList("foo");
        when(readResult.getLines()).thenReturn(lines);
        when(reader.read()).thenReturn(readResult);
        final ProcfsEntry spy = Mockito.spy(uut);

        spy.collect();

        verify(reader).read();
        verify(readResult).isUpdated();
        verify(spy, never()).handle(lines);
    }

}
