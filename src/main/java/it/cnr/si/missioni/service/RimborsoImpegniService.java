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

package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.RimborsoImpegniRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Voce;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.VoceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import jakarta.persistence.OptimisticLockException;
import java.util.Iterator;
import java.util.List;

/**
 * Service class for managing users.
 */
@Service
public class RimborsoImpegniService {

    private final Logger log = LoggerFactory.getLogger(RimborsoImpegniService.class);

    @Autowired
    private RimborsoImpegniRepository rimborsoImpegniRepository;

    @Autowired
    private RimborsoMissioneRepository rimborsoMissioneRepository;

    @Autowired
    private ImpegnoGaeService impegnoGaeService;

    @Autowired
    private ImpegnoService impegnoService;

    @Autowired
    private VoceService voceService;

    @Autowired
    
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private RimborsoMissioneDettagliRepository rimborsoMissioneDettagliRepository;


    @Autowired
    private SecurityService securityService;


    @Transactional(readOnly = true)
    public List<RimborsoImpegni> getRimborsoImpegni(Long idRimborso)
            throws AwesomeException {

        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                .findById(idRimborso)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato con ID: " + idRimborso
                ));

        return rimborsoImpegniRepository.getRimborsoImpegni(rimborsoMissione);
    }

    private void validaCRUD(RimborsoImpegni rimborsoImpegni) {

        RimborsoMissione rimborsoMissione = rimborsoImpegni.getRimborsoMissione();

        if (!StringUtils.hasText(String.valueOf(rimborsoImpegni.getEsercizioOriginaleObbligazione()))
                || !StringUtils.hasText(String.valueOf(rimborsoImpegni.getPgObbligazione()))) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati degli impegni incompleti.");
        }

        if (StringUtils.hasText(rimborsoMissione.getGae())) {

            ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(
                    rimborsoMissione.getCdsSpesa(),
                    rimborsoMissione.getUoSpesa(),
                    rimborsoImpegni.getEsercizioOriginaleObbligazione(),
                    rimborsoImpegni.getPgObbligazione(),
                    rimborsoMissione.getGae()
            );

            if (impegnoGae == null) {

                rimborsoMissione.setGae(null);
                rimborsoMissione.setToBeUpdated();

            } else {

                if (StringUtils.hasText(rimborsoMissione.getVoce())) {

                    if (!impegnoGae.getCdElementoVoce().equals(rimborsoMissione.getVoce())) {
                        rimborsoMissione.setVoce(null);
                        rimborsoMissione.setToBeUpdated();
                    }
                }

                rimborsoImpegni.setCdCdsObbligazione(impegnoGae.getCdCds());
                rimborsoImpegni.setEsercizioObbligazione(impegnoGae.getEsercizio());
                rimborsoImpegni.setVoce(impegnoGae.getCdElementoVoce());
            }

        } else {

            Impegno impegno = impegnoService.loadImpegno(
                    rimborsoMissione.getCdsSpesa(),
                    rimborsoMissione.getUoSpesa(),
                    rimborsoImpegni.getEsercizioOriginaleObbligazione(),
                    rimborsoImpegni.getPgObbligazione()
            );

            if (impegno == null) {

                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "L'impegno indicato "
                                + rimborsoImpegni.getEsercizioOriginaleObbligazione()
                                + "-"
                                + rimborsoImpegni.getPgObbligazione()
                                + " non esiste"
                );

            } else {

                if (StringUtils.hasText(rimborsoMissione.getVoce())) {

                    if (!impegno.getCdElementoVoce().equals(rimborsoMissione.getVoce())) {
                        rimborsoMissione.setVoce(null);
                        rimborsoMissione.setToBeUpdated();
                    }

                } else {
                    rimborsoImpegni.setVoce(impegno.getCdElementoVoce());
                }

                rimborsoImpegni.setVoce(impegno.getCdElementoVoce());
                rimborsoImpegni.setCdCdsObbligazione(impegno.getCdCds());
                rimborsoImpegni.setEsercizioObbligazione(impegno.getEsercizio());
            }
        }

        Voce voce = voceService.loadVoce(
                rimborsoImpegni.getEsercizioObbligazione(),
                rimborsoImpegni.getVoce()
        );

        if (voce != null) {

            rimborsoImpegni.setDsVoce(voce.getDs_elemento_voce());

        } else {

            throw new AwesomeException(
                    CodiciErrore.ERRGEN,
                    CodiciErrore.DATI_INCONGRUENTI
                            + ": L'impegno indicato "
                            + rimborsoImpegni.getEsercizioOriginaleObbligazione()
                            + "-"
                            + rimborsoImpegni.getPgObbligazione()
                            + " è collegato ad una voce di Bilancio "
                            + rimborsoImpegni.getVoce()
                            + " per la quale non è previsto l'utilizzo per le missioni"
            );
        }

        List<RimborsoImpegni> lista = rimborsoImpegniRepository.getRimborsoImpegni(
                rimborsoImpegni.getRimborsoMissione(),
                rimborsoImpegni.getEsercizioOriginaleObbligazione(),
                rimborsoImpegni.getPgObbligazione()
        );

        if (lista != null && !lista.isEmpty()) {

            if (rimborsoImpegni.getId() != null) {

                for (RimborsoImpegni rimbImp : lista) {

                    if (!rimborsoImpegni.getId().equals(rimbImp.getId())) {
                        throw new AwesomeException(CodiciErrore.ERRGEN, "Impegno già esistente.");
                    }
                }

            } else {

                throw new AwesomeException(CodiciErrore.ERRGEN, "Impegno già esistente.");
            }
        }

        if (rimborsoMissione.isToBeUpdated()) {

            rimborsoMissione = rimborsoMissioneRepository.save(rimborsoMissione);

            rimborsoImpegni.setRimborsoMissione(rimborsoMissione);
        }
    }

    @Transactional
    public RimborsoImpegni createRimborsoImpegni(RimborsoImpegni rimborsoImpegni)
            throws AwesomeException {

        rimborsoImpegni.setUser(securityService.getCurrentUserLogin());
        rimborsoImpegni.setStato(Costanti.STATO_INSERITO);

        RimborsoMissione rimborso = rimborsoMissioneRepository
                .findById((Long) rimborsoImpegni.getRimborsoMissione().getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        controlloOperazione(rimborso);

        rimborsoImpegni.setRimborsoMissione(rimborso);
        rimborsoImpegni.setToBeCreated();

        validaCRUD(rimborsoImpegni);

        rimborsoImpegni = rimborsoImpegniRepository.save(rimborsoImpegni);

        log.debug("Created Information for rimborsoImpegni: {}", rimborsoImpegni);

        return rimborsoImpegni;
    }

    protected void controlloOperazione(RimborsoMissione rimborso) {

        RimborsoMissione rimborsoDB = rimborsoMissioneRepository
                .findById((Long) rimborso.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        if (!rimborsoDB.isMissioneDaValidare() && !rimborsoDB.isMissioneInserita()) {
            throw new AwesomeException(
                    CodiciErrore.ERRGEN,
                    "La missione si trova in uno stato in cui non è possibile effettuare l'operazione."
            );
        }
    }

    public void cancellaRimborsoImpegni(
            RimborsoMissione rimborsoMissione)
            throws AwesomeException {
        List<RimborsoImpegni> listaRimborsoImpegni = rimborsoImpegniRepository.getRimborsoImpegni(rimborsoMissione);
        if (listaRimborsoImpegni != null && !listaRimborsoImpegni.isEmpty()) {
            for (Iterator<RimborsoImpegni> iterator = listaRimborsoImpegni.iterator(); iterator.hasNext(); ) {
                RimborsoImpegni rimborsoImpegni = iterator.next();
                cancellaRimborsoImpegni(rimborsoImpegni);
            }
        }
    }

    @Transactional
    public void deleteRimborsoImpegni(Long idRimborsoImpegni)
            throws AwesomeException, OptimisticLockException {

        RimborsoImpegni rimborsoImpegni = rimborsoImpegniRepository
                .findById(idRimborsoImpegni)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso Impegni non trovato con ID: " + idRimborsoImpegni
                ));

        if (rimborsoImpegni.getRimborsoMissione() != null) {
            controlloOperazione(rimborsoImpegni.getRimborsoMissione());
        }

        List<RimborsoMissioneDettagli> lista =
                rimborsoMissioneDettagliRepository.getRimborsoMissioneDettagli((RimborsoMissione) rimborsoImpegni.getId());

        if (lista != null && !lista.isEmpty()) {
            throw new AwesomeException(
                    CodiciErrore.ERRGEN,
                    "Operazione non possibile. Esistono dettagli con l'impegno "
                            + rimborsoImpegni.getEsercizioOriginaleObbligazione()
                            + "-"
                            + rimborsoImpegni.getPgObbligazione()
                            + " valorizzato."
            );
        }

        cancellaRimborsoImpegni(rimborsoImpegni);
    }

    private void cancellaRimborsoImpegni(RimborsoImpegni rimborsoImpegni) {
        rimborsoImpegni.setToBeUpdated();
        rimborsoImpegni.setStato(Costanti.STATO_ANNULLATO);
        rimborsoImpegniRepository.save(rimborsoImpegni);
    }

    @Transactional
    public RimborsoImpegni updateRimborsoImpegni(RimborsoImpegni rimborsoImpegni)
            throws AwesomeException, OptimisticLockException {

        RimborsoImpegni rimborsoImpegniDB = rimborsoImpegniRepository
                .findById((Long) rimborsoImpegni.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso Impegni da aggiornare inesistente."
                ));

        if (rimborsoImpegniDB.getRimborsoMissione() != null) {
            controlloOperazione(rimborsoImpegniDB.getRimborsoMissione());
        }

        rimborsoImpegniDB.setCdCdsObbligazione(rimborsoImpegni.getCdCdsObbligazione());
        rimborsoImpegniDB.setEsercizioObbligazione(rimborsoImpegni.getEsercizioObbligazione());
        rimborsoImpegniDB.setEsercizioOriginaleObbligazione(rimborsoImpegni.getEsercizioOriginaleObbligazione());
        rimborsoImpegniDB.setPgObbligazione(rimborsoImpegni.getPgObbligazione());

        validaCRUD(rimborsoImpegniDB);

        rimborsoImpegniDB.setToBeUpdated();

        rimborsoImpegniDB = rimborsoImpegniRepository.save(rimborsoImpegniDB);

        log.debug("Updated Information for RimborsoImpegni: {}", rimborsoImpegniDB);

        return rimborsoImpegniDB;
    }


}
