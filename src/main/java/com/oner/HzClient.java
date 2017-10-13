package com.oner;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class HzClient {
    private static final Logger LOGGER = Logger.getLogger(HzClient.class.getSimpleName());
    private final int nOfEntries = 25_000;
    private final int nOfEpCalls = 100_000;
    private HazelcastInstance hz;

    public static void main(String[] args) throws Exception {
        new HzClient();
    }

    public HzClient() throws Exception {
        ClientConfig config = new ClientConfig();
        config.getGroupConfig().setName(TestConstants.GROUP_NAME).setPassword(TestConstants.GROUP_PASWD);

        hz = HazelcastClient.newHazelcastClient(config);
        LOGGER.info("put data");
        init();
        LOGGER.info("send EP");
        Thread.sleep(2000);
        test();
    }

    private void test() {
        IMap<Integer, MyData> testMap = hz.getMap(TestConstants.TEST_MAP);
        IntStream.rangeClosed(0, nOfEpCalls)
                .forEach(i -> testMap.submitToKey(i % nOfEntries, new MyEP()));
        LOGGER.info(nOfEpCalls + " EP sent to HzServer");
    }


    private void init() {
        Random random = new Random();
        IMap<Integer, MyData> testMap = hz.getMap(TestConstants.TEST_MAP);
        for (int i = 0; i < nOfEntries; i++) {
            MyData data = new MyData(
                    RandomStringUtils.randomAlphabetic(1024),
                    RandomStringUtils.randomAlphabetic(2048),
                    RandomStringUtils.randomAlphabetic(2048),
                    random.nextInt(100), new Date(random.nextLong()));
            testMap.put(i, data);
        }
    }

    static class MyExCallback implements ExecutionCallback<Object> {

        @Override
        public void onResponse(Object o) {

        }

        @Override
        public void onFailure(Throwable throwable) {

        }
    }

    static class MyEP implements EntryProcessor<Integer, MyData>, Offloadable, ReadOnly {

        @Override
        public Object process(Map.Entry<Integer, MyData> entry) {
            MyData value = entry.getValue();
            Integer key = entry.getKey();
            key++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        @Override
        public EntryBackupProcessor<Integer, MyData> getBackupProcessor() {
            return null;
        }

        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MyData implements Serializable {
        private String name;
        private String surname;
        private String abc;
        private int age;
        private Date birthdate;
    }
}
