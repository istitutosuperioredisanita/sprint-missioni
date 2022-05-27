package it.cnr.si.missioni.amq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Profile("!showcase")
@RabbitListener(queues = "${spring.rabbitmq.consumer.name}")
@Configuration
public class ConsumerService {
	private static final Logger LOGGER  = LoggerFactory.getLogger(ConsumerService.class);

	
	@RabbitHandler
	public void process(@Payload String json, @Header("amqp_receivedRoutingKey") String rk) {

		LOGGER.info("JSON Verifica Coda {}", "Routing-key: "+rk+". Message:"+json);

	}


}
