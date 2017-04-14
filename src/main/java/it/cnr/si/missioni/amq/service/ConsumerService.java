package it.cnr.si.missioni.amq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RabbitListener(queues = "${spring.rabbitmq.consumer.name}")
@Service
public class ConsumerService {
	private static final Logger LOGGER  = LoggerFactory.getLogger(ConsumerService.class);

	@RabbitHandler
	public void process(@Payload String json) {

		LOGGER.info("JSON Verifica Coda {}", json);

	}


}
