package io.strimzi.streaming.app;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleConsumer implements Runnable {

    private static final String KAFKA_SERVICE_HOST = "KAFKA_SERVICE_HOST";
    private static final String KAFKA_SERVICE_PORT = "KAFKA_SERVICE_PORT";
    private static final String TEST_TOPIC = resolve("KAFKA_TOPIC_SUCCESS");
    private static final String KAFKA_CONSUMER_GROUP = "test-grp-ocp";

    private final AtomicBoolean running = new AtomicBoolean(Boolean.TRUE);
    private static final Logger LOGGER = Logger.getLogger(SimpleConsumer.class.getName());
    private final KafkaConsumer<String, String> consumer;

    public SimpleConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveKafkaService());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONSUMER_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumer = new KafkaConsumer(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singleton(TEST_TOPIC));
            while (isRunning()) {
                final ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);

                records.forEach((ConsumerRecord<String, String> record) -> {

                    final String theValue = record.value();

                    LOGGER.severe("Received Success msg: " + theValue);
                });
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (isRunning()) {
                LOGGER.log(Level.FINE, "Exception on close", e);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error", e);

        } finally {
            LOGGER.info("Close the consumer.");
            consumer.close();
        }
    }

    /**
     * True when a consumer is running; otherwise false
     */
    public boolean isRunning() {
        return running.get();
    }

    /*
     * Shutdown hook which can be called from a separate thread.
     */
    public void shutdown() {
        LOGGER.info("Shutting down the consumer.");
        running.set(Boolean.FALSE);
        consumer.wakeup();
    }

    private static String resolveKafkaService() {

        return new StringBuilder()
                .append(resolve(KAFKA_SERVICE_HOST))
                .append(":")
                .append(resolve(KAFKA_SERVICE_PORT)).toString();
    }

    private static String resolve(final String variable) {

        String value = System.getProperty(variable);
        if (value == null) {
            // than we try ENV ...
            value = System.getenv(variable);
        }
        return value;
    }

}
