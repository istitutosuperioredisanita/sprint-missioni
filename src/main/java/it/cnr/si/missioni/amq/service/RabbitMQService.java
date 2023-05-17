/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.amq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.cnr.si.missioni.amq.domain.Missione;
import it.cnr.si.missioni.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Profile("!showcase")
@Component
public class RabbitMQService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQService.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routing-key}")
    private String routingKey;

    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Missione missione) {

        try {
            String json = om.writeValueAsString(missione);
            String routingKeySede = buildRoutingKey(missione);
            LOGGER.info("JSON invio coda " + "Routing-key: " + routingKeySede + ". |||{}", json);
            this.rabbitTemplate.convertAndSend(exchange, Utility.nvl(routingKeySede), json);
            LOGGER.info("Coda Inviata");
        } catch (JsonProcessingException e) {
            LOGGER.error("json error {}", missione, e);
        }
    }

    private String buildRoutingKey(Missione missione) {
        String routingKeySede = null;
        if (StringUtils.isEmpty(routingKey)) {
            routingKeySede = missione.getCodice_sede();
        } else {
            routingKeySede = routingKey + "." + missione.getCodice_sede();
        }
        return routingKeySede;
    }

}
