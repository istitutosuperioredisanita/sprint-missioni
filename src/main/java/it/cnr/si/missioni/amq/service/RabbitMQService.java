package it.cnr.si.missioni.amq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.cnr.si.missioni.amq.domain.Missione;
import it.cnr.si.missioni.util.Utility;

@Component
public class RabbitMQService {
	private static final Logger LOGGER  = LoggerFactory.getLogger(RabbitMQService.class);

	private RabbitTemplate rabbitTemplate;

	@Value("${spring.rabbitmq.exchange}")
	private String exchange;

	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;

	private ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

	public RabbitMQService(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void send(Missione missione) {
		
		try {
			String json = om.writeValueAsString(missione);
			String routingKeySede = buildRoutingKey(missione);
			LOGGER.info("JSON invio coda "+ "Routing-key: "+routingKeySede +". |||{}", json);
			this.rabbitTemplate.convertAndSend(exchange, Utility.nvl(routingKeySede), json);
			LOGGER.info("Coda Inviata");
		} catch (JsonProcessingException e) {
			LOGGER.error("json error {}", missione, e);
		}
	}

	private String buildRoutingKey(Missione missione) {
		String routingKeySede = null;
		if (StringUtils.isEmpty(routingKey)){
			routingKeySede = missione.getCodice_sede();
		} else {
			routingKeySede = routingKey + "." + missione.getCodice_sede();
		}
		return routingKeySede;
	}

}
