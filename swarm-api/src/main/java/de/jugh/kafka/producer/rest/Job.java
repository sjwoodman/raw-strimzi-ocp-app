package de.jugh.kafka.producer.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Job {

    private final String jobId = UUID.randomUUID().toString();
    private final List<String> deviceIDs = new ArrayList();


    public Job() {
        final long counterHelper = Math.round(Math.random()*200);

        for (int i = 0; i < counterHelper; i++) {
            deviceIDs.add(UUID.randomUUID().toString());
        }
    }

    public String getJobId() {
        return jobId;
    }

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }
}
