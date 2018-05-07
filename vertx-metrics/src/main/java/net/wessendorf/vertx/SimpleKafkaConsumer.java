/*
 * Copyright (C) 2018 Matthias Weßendorf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wessendorf.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.kafka.client.consumer.KafkaReadStream;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class SimpleKafkaConsumer extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(SimpleKafkaConsumer.class);

    private KafkaReadStream<String, String> consumer;

    @Override
    public void start(Future<Void> fut) {

        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, resolveKafkaService());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "my_vertx_group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        consumer = KafkaReadStream.create(vertx, config);

        consumer.handler(record -> {
            logger.warn("received failed message: " + record.value());
        });

        consumer.subscribe(Collections.singleton(System.getenv("KAFKA_TOPIC_FAIL")));

        // we are ready w/ deployment
        fut.complete();
    }


    private static String resolveKafkaService() {

        return new StringBuilder()
                .append(resolve("KAFKA_SERVICE_HOST"))
                .append(":")
                .append(resolve("KAFKA_SERVICE_PORT")).toString();
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

