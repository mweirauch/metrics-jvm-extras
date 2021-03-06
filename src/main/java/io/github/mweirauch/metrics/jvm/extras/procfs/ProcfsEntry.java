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

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mweirauch.metrics.jvm.extras.procfs.ProcfsReader.ReadResult;

abstract class ProcfsEntry {

    private static final Logger log = LoggerFactory.getLogger(ProcfsEntry.class);

    private final Object lock = new Object();

    private final ProcfsReader reader;

    private long lastHandle = -1;

    protected ProcfsEntry(ProcfsReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    protected final void collect() {
        synchronized (lock) {
            try {
                final ReadResult result = reader.read();
                if (result != null && (lastHandle == -1 || lastHandle != result.getReadTime())) {
                    reset();
                    handle(result.getLines());
                    lastHandle = result.getReadTime();
                }
            } catch (IOException e) {
                reset();
                log.warn("Failed reading '" + reader.getEntryPath() + "'!", e);
            }
        }
    }

    protected abstract void reset();

    protected abstract void handle(Collection<String> lines);

}
