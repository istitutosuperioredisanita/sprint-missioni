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

import it.cnr.si.missioni.service.AnnullamentoOrdineMissioneService;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.service.RimborsoMissioneService;
import it.cnr.si.missioni.util.data.DataQueue;
import it.cnr.si.missioni.util.data.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!showcase")
@Component
public class ResendQueueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResendQueueService.class);

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    public void resendQueue(DataQueue dataQueue) {
        if (dataQueue != null) {
            LOGGER.info("Esiste Coda");
            for (Queue queue : dataQueue.getQueues()) {
                switch (queue.getTipo()) {
                    case "ORDINE":
                        ordineMissioneService.popolaCoda(queue.getId());
                        break;
                    case "ANNULLAMENTO":
                        annullamentoOrdineMissioneService.popolaCoda(queue.getId());
                        break;
                    case "RIMBORSO":
                        rimborsoMissioneService.popolaCoda(queue.getId());
                        break;
                }
            }
        }
    }
}
