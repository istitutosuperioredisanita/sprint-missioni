package it.cnr.si.missioni.amq.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.si.missioni.amq.domain.Missione;

@RabbitListener(queues = "${spring.rabbitmq.consumer.name}")
public class ConsumerService {
	private static final Logger LOGGER  = LoggerFactory.getLogger(ConsumerService.class);

	private ObjectMapper om = new ObjectMapper();


	@RabbitHandler
	public void process(@Payload String json) {

		LOGGER.info("JSON Verifica Coda {}", json);

		try {
			Missione m = om.readValue(json, Missione.class);

			LOGGER.info("Coda Ricevuta", m);

		} catch (IOException e) {
			LOGGER.error("error with json {}", json, e);
		}

	}


}
