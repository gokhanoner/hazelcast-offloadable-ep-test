package com.oner;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.Hazelcast;

public class HzServer {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: HzServer [in-memory-format]");
            return;
        }
        InMemoryFormat imf = InMemoryFormat.valueOf(args[0]);
        if(imf == InMemoryFormat.NATIVE) {
            System.out.println("Only BINARY and OBJECT formats supported.");
            return;
        }
        Config config = new Config();
        config.getGroupConfig().setName(TestConstants.GROUP_NAME).setPassword(TestConstants.GROUP_PASWD);
        config.getMapConfig(TestConstants.TEST_MAP).setInMemoryFormat(imf);
        Hazelcast.newHazelcastInstance(config);
    }

}
