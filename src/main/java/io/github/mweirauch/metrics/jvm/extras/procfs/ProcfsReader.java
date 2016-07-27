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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

class ProcfsReader {

    private static final Map<String, ProcfsReader> instances = new HashMap<>();

    private static final Object instancesLock = new Object();

    private static final Map<Path, List<String>> data = new HashMap<>();

    private static final Object dataLock = new Object();

    private static final Path BASE = Paths.get("/proc", "self");

    /* default */ static final long CACHE_DURATION_MS = 100;

    /* default */ long lastReadTime = -1;

    private final Path entryPath;

    private final boolean osSupport;

    private ProcfsReader(String entry) {
        this(BASE, entry, false);
    }

    /* default */ ProcfsReader(Path base, String entry) {
        this(base, entry, true);
    }

    private ProcfsReader(Path base, String entry, boolean forceOSSupport) {
        Objects.requireNonNull(base);
        Objects.requireNonNull(entry);

        this.entryPath = base.resolve(entry);

        this.osSupport = forceOSSupport
                || System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("linux");
    }

    /* default */ ReadResult read() {
        return read(System.currentTimeMillis());
    }

    /* default */ ReadResult read(long currentTimeMillis) {
        try {
            synchronized (dataLock) {
                final Path key = entryPath.getFileName();

                final ReadResult readResult;
                if (lastReadTime == -1 || lastReadTime + CACHE_DURATION_MS < currentTimeMillis) {
                    final List<String> lines = readPath(entryPath);
                    cacheResult(key, lines);
                    lastReadTime = System.currentTimeMillis();
                    readResult = new ReadResult(lines, true);
                } else {
                    readResult = new ReadResult(data.get(key), false);
                }
                return readResult;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed reading '" + entryPath + "'", e);
        }
    }

    /* default */ List<String> readPath(Path entryPath) throws IOException {
        Objects.requireNonNull(entryPath);

        if (!osSupport) {
            return Collections.emptyList();
        }
        return Files.readAllLines(entryPath);
    }

    /* default */ void cacheResult(Path key, List<String> lines) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(lines);

        data.put(key, lines);
    }

    /* default */ static ProcfsReader getInstance(String entry) {
        Objects.requireNonNull(entry);

        synchronized (instancesLock) {
            ProcfsReader reader = instances.get(entry);
            if (reader == null) {
                reader = new ProcfsReader(entry);
                instances.put(entry, reader);
            }
            return reader;
        }
    }

    /* default */ static class ReadResult {

        private final List<String> lines;

        private final boolean updated;

        /* default */ ReadResult(List<String> lines, boolean updated) {
            this.lines = Objects.requireNonNull(lines);
            this.updated = updated;
        }

        public boolean isUpdated() {
            return updated;
        }

        public List<String> getLines() {
            return lines;
        }

    }

}
