package io.strimzi.streaming.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Processor{

    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());

    public static void main(String... args) {

        LOGGER.info("Starting Kafka Consumers");
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final SimpleConsumer consumer = new SimpleConsumer();
        executor.submit(consumer);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.shutdown();
            executor.shutdown();
            try {
                executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                LOGGER.log(Level.SEVERE, "Error on close", ie);
            }
        }));

    }
}
