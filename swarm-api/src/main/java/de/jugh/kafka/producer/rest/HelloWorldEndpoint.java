package de.jugh.kafka.producer.rest;

import org.aerogear.kafka.SimpleKafkaProducer;
import org.aerogear.kafka.cdi.annotation.KafkaConfig;
import org.aerogear.kafka.cdi.annotation.Producer;

import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/hello")
@KafkaConfig(bootstrapServers = "#{KAFKA_SERVICE_HOST}:#{KAFKA_SERVICE_PORT}")
public class HelloWorldEndpoint {

	private final static Logger logger = Logger.getLogger(HelloWorldEndpoint.class.getName());

	@Producer
	private SimpleKafkaProducer<String, String> myproducer;

	private static final String TOPIC_SUCCESS = System.getenv("KAFKA_TOPIC_SUCCESS");
	private static final String TOPIC_FAIL = System.getenv("KAFKA_TOPIC_FAIL");

	@GET
	@Produces("text/plain")
	public Response doGet() {

		// some fake...
		final Job job = new Job();

		logger.info("Job processing for " + job.getDeviceIDs().size() + " devices");


		job.getDeviceIDs().forEach(deviceToken -> {

			if (Math.random() > 0.2) {
				myproducer.send(TOPIC_SUCCESS, job.getJobId(),"SUCCESS: " + deviceToken);
			} else {
				myproducer.send(TOPIC_FAIL,	job.getJobId(),"FAILED: " + deviceToken);
			}
		});

		return Response.ok("Hello from WildFly Swarm!").build();
	}
}