/*
 * Copyright 2015 Network New Technologies Inc.
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

package com.networknt.light.server;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

/**
 * Created by steve on 23/02/15.
 */
public class HzTopic {
    private static final int TOTAL = 1000000;
    private static final int LAP   = 100000;

    public static void main(String[] args) throws InterruptedException {
        final HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        final IQueue<Object> queue = hz.getQueue("test");

        final long start = System.currentTimeMillis();
        long lastLap = start;

        Thread t = new Thread() {
            long lastLap = start;

            @Override
            public void run() {
                System.out.println((System.currentTimeMillis() - lastLap) + " Start receiving msgs");
                for (int i = 1; i < TOTAL + 1; ++i) {
                    try {
                        Object msg = queue.take();

                        if (i % LAP == 0) {
                            final long lapTime = System.currentTimeMillis() - lastLap;
                            System.out.printf("<- messages %d/%d = %dms (%f msg/sec)\n", i, TOTAL, lapTime, ((float) LAP * 1000 / lapTime));
                            lastLap = System.currentTimeMillis();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        System.out.println((System.currentTimeMillis() - lastLap) + " Start sending msgs");
        for (int i = 1; i < TOTAL + 1; ++i) {
            queue.offer(i);

            if (i % LAP == 0) {
                final long lapTime = System.currentTimeMillis() - lastLap;
                System.out.printf("-> messages %d/%d = %dms (%f msg/sec)\n", i, TOTAL, lapTime, ((float) LAP * 1000 / lapTime));
                lastLap = System.currentTimeMillis();
            }
        }

        System.out.println((System.currentTimeMillis() - start) + " Finished sending msgs");

        t.join();

        System.out.println((System.currentTimeMillis() - start) + " Test finished");
    }
}
