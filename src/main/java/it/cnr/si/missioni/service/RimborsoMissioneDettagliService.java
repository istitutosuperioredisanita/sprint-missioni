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

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.TipoPasto;
import it.cnr.si.missioni.util.proxy.json.service.TipoPastoService;
import it.cnr.si.missioni.util.proxy.json.service.ValidaDettaglioRimborsoService;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.OptimisticLockException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

/**
 * Service class for managing users.
 */
@Service
public class RimborsoMissioneDettagliService {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneDettagliService.class);

    @Autowired
    private RimborsoMissioneDettagliRepository rimborsoMissioneDettagliRepository;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private ValidaDettaglioRimborsoService validaDettaglioRimborsoService;

    @Autowired
    private CMISRimborsoMissioneService cmisRimborsoMissioneService;

    @Autowired
    private RimborsoImpegniService rimborsoImpegniService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TipoPastoService tipoPastoService;

    @Autowired
    private CRUDComponentSession crudServiceBean;
    @Autowired
    private Environment env;

    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idRimborsoMissioneDettagli,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
        RimborsoMissioneDettagli dettaglio = getRimborsoMissioneDettaglio(idRimborsoMissioneDettagli);
        if (dettaglio != null) {
            rimborsoMissioneService.controlloAllegatoDettaglioModificabile(dettaglio.getRimborsoMissione());
            CMISFileAttachment attachment = cmisRimborsoMissioneService.uploadAttachmentDetail(dettaglio,
                    inputStream, name, mimeTypes);
            return attachment;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<CMISFileAttachment> getAttachments(Long idRimborsoMissioneDettagli)
            throws ComponentException {
        if (idRimborsoMissioneDettagli != null) {
            List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsDetail(idRimborsoMissioneDettagli);
            return lista;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoMissione)
            throws ComponentException {
        RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(
                RimborsoMissione.class, idRimborsoMissione);

        if (rimborsoMissione != null) {
            List<RimborsoMissioneDettagli> lista = rimborsoMissioneDettagliRepository
                    .getRimborsoMissioneDettagli(rimborsoMissione);
            return lista;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoMissione, LocalDate data)
            throws ComponentException {
        RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(
                RimborsoMissione.class, idRimborsoMissione);

        if (rimborsoMissione != null) {
            List<RimborsoMissioneDettagli> lista = rimborsoMissioneDettagliRepository
                    .getRimborsoMissioneDettagli(rimborsoMissione, data);
            return lista;
        }
        return null;
    }

    private void validaCRUD(RimborsoMissioneDettagli rimborsoMissioneDettagli) throws ComponentException {
        RimborsoMissione rimborsoMissione = rimborsoMissioneDettagli.getRimborsoMissione();
        if (rimborsoMissioneDettagli.getKmPercorsi() != null) {
            if (rimborsoMissione.getOrdineMissione() != null) {
                OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
                if (ordineMissione != null) {
                    OrdineMissioneAutoPropria autoPropria = ordineMissioneService.getAutoPropria(ordineMissione);
                    if (autoPropria != null && !Utility.nvl(autoPropria.utilizzoMotiviIspettivi, "N").equals("S")) {
                        throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile utilizzare il rimborso kilometrico perchè in fase d'ordine di missione non è stata scelta per la richiesta auto propria il motivo di ispezione, verifica e controlli.");
                    }
                }
            }
        }
        if (StringUtils.isEmpty(rimborsoMissioneDettagli.getDsSpesa())) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Indicare una descrizione per la spesa.");
        }
        if (!env.acceptsProfiles(Costanti.SPRING_PROFILE_SHOWCASE))
            validaDettaglioRimborsoService.valida(rimborsoMissioneDettagli);
    }

    private void controlliPasto(RimborsoMissioneDettagli rimborsoMissioneDettagli,
                                RimborsoMissione rimborsoMissione) {
        if (rimborsoMissioneDettagli.isDettaglioPasto()) {
            List<RimborsoMissioneDettagli> listaDettagliGiorno = getRimborsoMissioneDettagli(Long.valueOf(rimborsoMissione.getId().toString()), rimborsoMissioneDettagli.getDataSpesa());
            for (RimborsoMissioneDettagli dett : listaDettagliGiorno) {
                if (dett.isDettaglioPasto() && dett.getRiga().compareTo(rimborsoMissioneDettagli.getRiga()) != 0) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile indicare più spese per il pasto nello stesso giorno");
                }
            }
            Integer livelloRich = null;
            if (rimborsoMissione.getLivelloRich() != null) {
                try {
                    livelloRich = Integer.valueOf(rimborsoMissione.getLivelloRich());
                } catch (NumberFormatException e) {
                    livelloRich = recuperoLivelloEquivalente(rimborsoMissioneDettagli, rimborsoMissione, livelloRich);
                }
            } else {
                livelloRich = recuperoLivelloEquivalente(rimborsoMissioneDettagli, rimborsoMissione, livelloRich);
            }
            controlloCongruenzaPasto(rimborsoMissioneDettagli, rimborsoMissione, livelloRich);
        }
    }

    protected Integer recuperoLivelloEquivalente(RimborsoMissioneDettagli rimborsoMissioneDettagli,
                                                 RimborsoMissione rimborsoMissione, Integer livelloRich) {
        List<TipoPasto> lista = tipoPastoService.loadTipoPasto(rimborsoMissioneDettagli.getCdTiPasto(), rimborsoMissione.getNazione(), rimborsoMissione.getInquadramento(), rimborsoMissioneDettagli.getDataSpesa());
        if (lista != null && !lista.isEmpty()) {
            TipoPasto tipoPasto = lista.get(0);
            if (rimborsoMissioneDettagli.getCdTiPasto().startsWith("G")) {
                if (tipoPasto.getLimiteMaxPasto().compareTo(Double.valueOf(50)) > 0) {
                    livelloRich = 1;
                } else {
                    livelloRich = 4;
                }
            } else {
                if (tipoPasto.getLimiteMaxPasto().compareTo(Double.valueOf(30)) > 0) {
                    livelloRich = 1;
                } else {
                    livelloRich = 4;
                }
            }
        }
        return livelloRich;
    }

    protected void controlloCongruenzaPasto(RimborsoMissioneDettagli rimborsoMissioneDettagli,
                                            RimborsoMissione rimborsoMissione, Integer livello) {
        long oreDifferenza = ChronoUnit.HOURS.between(rimborsoMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES), rimborsoMissione.getDataFineMissione().truncatedTo(ChronoUnit.MINUTES));
        if (rimborsoMissione.getOrdineMissione().getId() != null) {
            OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById(
                    OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
            if (ordineMissione != null /*&& Utility.nvl(ordineMissione.getPersonaleAlSeguito()).equals("S")*/ && (!rimborsoMissione.isAssociato() || (rimborsoMissione.getInquadramento() != null && rimborsoMissione.getInquadramento().compareTo(Long.valueOf(47)) < 0))) {
                livello = 3;
            }
        }
        if (livello < 4) {
            if (oreDifferenza < 4 || (oreDifferenza < 12 && rimborsoMissioneDettagli.getCdTiPasto().startsWith("G"))) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Tipo pasto non spettante per la durata della missione");
            }
        } else {
            if (oreDifferenza < 8 || (oreDifferenza < 12 && rimborsoMissioneDettagli.getCdTiPasto().startsWith("G"))) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Tipo pasto non spettante per la durata della missione");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissioneDettagli createRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli) throws AwesomeException, ComponentException,
            OptimisticLockException, PersistencyException, BusyResourceException {
        rimborsoMissioneDettagli.setUid(securityService.getCurrentUserLogin());
        rimborsoMissioneDettagli.setUser(securityService.getCurrentUserLogin());
        rimborsoMissioneDettagli.setStato(Costanti.STATO_INSERITO);
        if (rimborsoMissioneDettagli.getTiSpesaDiaria() == null) {
            rimborsoMissioneDettagli.setTiSpesaDiaria("S");
        }
        RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(
                RimborsoMissione.class, rimborsoMissioneDettagli.getRimborsoMissione().getId());
        if (rimborsoMissione != null) {
            rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
        }
        rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);
        Long maxRiga = rimborsoMissioneDettagliRepository.getMaxRigaDettaglio(rimborsoMissione);
        if (maxRiga == null) {
            maxRiga = Long.valueOf(0);
        }
        maxRiga = maxRiga + 1;
        rimborsoMissioneDettagli.setRiga(maxRiga);
        rimborsoMissioneDettagli.setToBeCreated();
        controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);
        impostaImportoDivisa(rimborsoMissioneDettagli);
        validaCRUD(rimborsoMissioneDettagli);
        controlliPasto(rimborsoMissioneDettagli, rimborsoMissione);

        aggiornaDatiImpegni(rimborsoMissioneDettagli);

        rimborsoMissioneDettagli = (RimborsoMissioneDettagli) crudServiceBean.creaConBulk(rimborsoMissioneDettagli);
        log.debug("Created Information for RimborsoMissioneDettagli: {}", rimborsoMissioneDettagli);
        return rimborsoMissioneDettagli;
    }

    public void aggiornaDatiImpegni(RimborsoMissioneDettagli rimborsoMissioneDettagli) {
        if (rimborsoMissioneDettagli.getIdRimborsoImpegni() != null) {
            RimborsoImpegni rimborsoImpegni = (RimborsoImpegni) crudServiceBean.findById(RimborsoImpegni.class, rimborsoMissioneDettagli.getIdRimborsoImpegni());
            if (rimborsoMissioneDettagli.getIdRimborsoImpegni() != null) {
                rimborsoMissioneDettagli.setCdCdsObbligazione(rimborsoImpegni.getCdCdsObbligazione());
                rimborsoMissioneDettagli.setEsercizioObbligazione(rimborsoImpegni.getEsercizioObbligazione());
                rimborsoMissioneDettagli.setEsercizioOriginaleObbligazione(rimborsoImpegni.getEsercizioOriginaleObbligazione());
                rimborsoMissioneDettagli.setPgObbligazione(rimborsoImpegni.getPgObbligazione());
                rimborsoMissioneDettagli.setVoce(rimborsoImpegni.getVoce());
                rimborsoMissioneDettagli.setDsVoce(rimborsoImpegni.getDsVoce());
            }
        } else {
            rimborsoMissioneDettagli.setCdCdsObbligazione(null);
            rimborsoMissioneDettagli.setEsercizioObbligazione(null);
            rimborsoMissioneDettagli.setEsercizioOriginaleObbligazione(null);
            rimborsoMissioneDettagli.setPgObbligazione(null);
            rimborsoMissioneDettagli.setVoce(null);
            rimborsoMissioneDettagli.setDsVoce(null);
        }
    }

    private void controlloDatiObbligatoriDaGui(RimborsoMissioneDettagli dettaglio) {
        if (dettaglio != null) {
            if (StringUtils.isEmpty(dettaglio.getDataSpesa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Spesa");
            } else if (StringUtils.isEmpty(dettaglio.getImportoEuro())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Importo Euro");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cancellaRimborsoMissioneDettagli(RimborsoMissione rimborsoMissione,
                                                 Boolean deleteDocument) throws ComponentException {
        List<RimborsoMissioneDettagli> listaRimborsoMissioneDettagli = rimborsoMissioneDettagliRepository
                .getRimborsoMissioneDettagli(rimborsoMissione);
        if (listaRimborsoMissioneDettagli != null && !listaRimborsoMissioneDettagli.isEmpty()) {
            for (Iterator<RimborsoMissioneDettagli> iterator = listaRimborsoMissioneDettagli.iterator(); iterator
                    .hasNext(); ) {
                RimborsoMissioneDettagli dettaglio = iterator.next();
                cancellaRimborsoMissioneDettagli(dettaglio, deleteDocument);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRimborsoMissioneDettagli(Long idRimborsoMissioneDettagli)
            throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException,
            BusyResourceException {
        RimborsoMissioneDettagli rimborsoMissioneDettagli = getRimborsoMissioneDettaglio(idRimborsoMissioneDettagli);

        // effettuo controlli di validazione operazione CRUD
        if (rimborsoMissioneDettagli != null) {
            rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissioneDettagli.getRimborsoMissione());
            cancellaRimborsoMissioneDettagli(rimborsoMissioneDettagli, true);
        }
    }

    public RimborsoMissioneDettagli getRimborsoMissioneDettaglio(Long idRimborsoMissioneDettagli) {
        RimborsoMissioneDettagli rimborsoMissioneDettagli = (RimborsoMissioneDettagli) crudServiceBean
                .findById(RimborsoMissioneDettagli.class, idRimborsoMissioneDettagli);
        return rimborsoMissioneDettagli;
    }

    private void cancellaRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli, Boolean deleteDocument) throws ComponentException {
        rimborsoMissioneDettagli.setToBeUpdated();
        rimborsoMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);
        crudServiceBean.modificaConBulk(rimborsoMissioneDettagli);
        if (deleteDocument) {
            List<CMISFileAttachment> lista = getAttachments(Long.valueOf(rimborsoMissioneDettagli.getId().toString()));
            if (lista != null && !lista.isEmpty()) {
                for (CMISFileAttachment attach : lista) {
                    rimborsoMissioneService.gestioneCancellazioneAllegati(attach.getId(), Long.valueOf(rimborsoMissioneDettagli.getRimborsoMissione().getId().toString()));
                }
            }
        }
    }

    private void impostaImportoDivisa(RimborsoMissioneDettagli rimborsoMissioneDettagli) {
        if (rimborsoMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0) {
            rimborsoMissioneDettagli.setImportoDivisa(rimborsoMissioneDettagli.getImportoEuro());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissioneDettagli updateRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli) throws AwesomeException, ComponentException,
            OptimisticLockException, PersistencyException, BusyResourceException {

        RimborsoMissioneDettagli rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli) crudServiceBean
                .findById(RimborsoMissioneDettagli.class, rimborsoMissioneDettagli.getId());
        if (rimborsoMissioneDettagliDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dettaglio Rimborso Missione da aggiornare inesistente.");
        RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(
                RimborsoMissione.class, rimborsoMissioneDettagli.getRimborsoMissione().getId());
        if (rimborsoMissione != null && !rimborsoMissioneDettagli.isModificaSoloDatiFinanziari(rimborsoMissioneDettagliDB)) {
            rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
        }
        rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);

        controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);

        rimborsoMissioneDettagliDB.setCdTiPasto(rimborsoMissioneDettagli.getCdTiPasto());
        rimborsoMissioneDettagliDB.setCdTiSpesa(rimborsoMissioneDettagli.getCdTiSpesa());
        rimborsoMissioneDettagliDB.setTiCdTiSpesa(rimborsoMissioneDettagli.getTiCdTiSpesa());
        rimborsoMissioneDettagliDB.setDataSpesa(rimborsoMissioneDettagli.getDataSpesa());
        rimborsoMissioneDettagliDB.setDsSpesa(rimborsoMissioneDettagli.getDsSpesa());
        rimborsoMissioneDettagliDB.setTiSpesaDiaria(rimborsoMissioneDettagli.getTiSpesaDiaria());
        rimborsoMissioneDettagliDB.setDsTiSpesa(rimborsoMissioneDettagli.getDsTiSpesa());
        rimborsoMissioneDettagliDB.setNote(rimborsoMissioneDettagli.getNote());
        rimborsoMissioneDettagliDB.setFlSpesaAnticipata(rimborsoMissioneDettagli.getFlSpesaAnticipata());
        rimborsoMissioneDettagliDB.setKmPercorsi(rimborsoMissioneDettagli.getKmPercorsi());
        rimborsoMissioneDettagliDB.setCambio(rimborsoMissioneDettagli.getCambio());
        rimborsoMissioneDettagliDB.setCdDivisa(rimborsoMissioneDettagli.getCdDivisa());
        rimborsoMissioneDettagliDB.setImportoEuro(rimborsoMissioneDettagli.getImportoEuro());
        rimborsoMissioneDettagliDB.setDsNoGiustificativo(rimborsoMissioneDettagli.getDsNoGiustificativo());
        rimborsoMissioneDettagliDB.setLocalitaSpostamento(rimborsoMissioneDettagli.getLocalitaSpostamento());
        rimborsoMissioneDettagliDB.setIdRimborsoImpegni(rimborsoMissioneDettagli.getIdRimborsoImpegni());
        impostaImportoDivisa(rimborsoMissioneDettagliDB);

        rimborsoMissioneDettagliDB.setToBeUpdated();

        validaCRUD(rimborsoMissioneDettagliDB);
        controlliPasto(rimborsoMissioneDettagli, rimborsoMissione);

        aggiornaDatiImpegni(rimborsoMissioneDettagliDB);

        rimborsoMissioneDettagliDB = (RimborsoMissioneDettagli) crudServiceBean.modificaConBulk(rimborsoMissioneDettagliDB);

        log.debug("Updated Information for Dettaglio Rimborso Missione: {}", rimborsoMissioneDettagliDB);
        return rimborsoMissioneDettagliDB;
    }

}
