package de.jugh.kafka.producer.rest;

import org.aerogear.kafka.SimpleKafkaProducer;
import org.aerogear.kafka.cdi.annotation.KafkaConfig;
import org.aerogear.kafka.cdi.annotation.Producer;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/hello")
@KafkaConfig(bootstrapServers = "#{KAFKA_SERVICE_HOST}:#{KAFKA_SERVICE_PORT}")
public class HelloWorldEndpoint {

	@Producer
	private SimpleKafkaProducer<String, String> myproducer;

	private static final String TOPIC_SUCCESS = System.getenv("KAFKA_TOPIC_SUCCESS");
	private static final String TOPIC_FAIL = System.getenv("KAFKA_TOPIC_FAIL");

	@GET
	@Produces("text/plain")
	public Response doGet() {

		if (Math.random() > 0.2) {
			myproducer.send(TOPIC_SUCCESS, "SUCCESS: " + UUID.randomUUID().toString());
		} else {
			myproducer.send(TOPIC_FAIL, "FAILED: " + UUID.randomUUID().toString());
		}

		return Response.ok("Hello from WildFly Swarm!").build();
	}
}