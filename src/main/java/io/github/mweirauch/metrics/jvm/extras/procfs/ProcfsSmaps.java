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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

public class ProcfsSmaps extends ProcfsEntry {

    public enum KEY {
        /**
         * Virtual set size
         */
        VSS,
        /**
         * Resident set size
         */
        RSS,
        /**
         * Proportional set size
         */
        PSS,
        /**
         * Paged out memory
         */
        SWAP,
        /**
         * Paged out memory accounting shared pages. Since Linux 4.3.
         */
        SWAPPSS
    }

    private static final int KB = 1024;

    private final Map<KEY, AtomicLong> values = new HashMap<>();

    public ProcfsSmaps() {
        super(ProcfsReader.getInstance("smaps"));
    }

    // @VisibleForTesting
    ProcfsSmaps(ProcfsReader reader) {
        super(reader);
    }

    @Override
    protected void handle(Collection<String> lines) {
        Objects.requireNonNull(lines);

        EnumSet.allOf(KEY.class).forEach(e -> values.put(e, new AtomicLong(-1)));

        for (String l : lines) {
            if (l.startsWith("Size:")) {
                inc(KEY.VSS, parseKiloBytes(l) * KB);
            } else if (l.startsWith("Rss:")) {
                inc(KEY.RSS, parseKiloBytes(l) * KB);
            } else if (l.startsWith("Pss:")) {
                inc(KEY.PSS, parseKiloBytes(l) * KB);
            } else if (l.startsWith("Swap:")) {
                inc(KEY.SWAP, parseKiloBytes(l) * KB);
            } else if (l.startsWith("SwapPss:")) {
                inc(KEY.SWAPPSS, parseKiloBytes(l) * KB);
            }
        }
    }

    public Long get(KEY key) {
        Objects.requireNonNull(key);

        collect();
        return Long.valueOf(values.get(Objects.requireNonNull(key)).longValue());
    }

    private void inc(KEY key, long increment) {
        Objects.requireNonNull(key);

        values.get(key).getAndUpdate(new LongUnaryOperator() {

            @Override
            public long applyAsLong(long currentValue) {
                return currentValue + increment + (currentValue == -1 ? 1 : 0);
            }

        });
    }

    private static long parseKiloBytes(String line) {
        Objects.requireNonNull(line);

        return Long.parseLong(line.split("\\s+")[1]);
    }

}
