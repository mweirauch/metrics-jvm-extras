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

import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsReader.ReadResult;

import java.util.Collection;
import java.util.Objects;

abstract class ProcfsEntry {

    private final Object lock = new Object();

    private final ProcfsReader reader;

    protected ProcfsEntry(ProcfsReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    protected final void collect() {
        synchronized (lock) {
            final ReadResult result = reader.read();
            if (result.isUpdated()) {
                handle(result.getLines());
            }
        }
    }

    protected abstract void handle(Collection<String> lines);

}
