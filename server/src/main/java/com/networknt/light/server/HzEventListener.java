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
