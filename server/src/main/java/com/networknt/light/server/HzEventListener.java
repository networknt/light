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
import javax.annotation.PostConstruct;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import java.util.Map;

/**
 * This is Hazelcast implementation for clusters.
 */

public class HzEventListener implements MessageListener< Map<String, Object>> {
	private final HazelcastInstance hInstance;

	public HzEventListener(HazelcastInstance hInstance) {
		this.hInstance = hInstance;
	}
	
    @PostConstruct
    public void register() {
		ITopic<Map<String, Object>> eventBus = hInstance.getTopic("eventBus");
		eventBus.addMessageListener(this);
    }
 
    @Override
    public void onMessage(Message<Map<String, Object>> message) {
        System.out.println(message.getMessageObject().get("host"));
        System.out.println(message.getMessageObject().get("app"));
        System.out.println(message.getMessageObject().get("category"));
        System.out.println(message.getMessageObject().get("name"));
        System.out.println(message.getMessageObject().get("data"));
    }
}
