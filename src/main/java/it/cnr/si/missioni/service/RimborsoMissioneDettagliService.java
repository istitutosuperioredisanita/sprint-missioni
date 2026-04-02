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
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.repository.OrdineMissioneRepository;
import it.cnr.si.missioni.repository.RimborsoImpegniRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneDettagliRepository;
import it.cnr.si.missioni.repository.RimborsoMissioneRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.TipoPasto;
import it.cnr.si.missioni.util.proxy.json.service.TipoPastoService;
import it.cnr.si.missioni.util.proxy.json.service.ValidaDettaglioRimborsoService;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private RimborsoMissioneRepository rimborsoMissioneRepository;

    @Autowired
    private OrdineMissioneRepository ordineMissioneRepository;

    @Autowired
    private RimborsoImpegniRepository rimborsoImpegniRepository;

    @Autowired
    @Lazy
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
    @Lazy
    private CMISRimborsoMissioneService cmisRimborsoMissioneService;

    @Autowired
    @Lazy
    private RimborsoImpegniService rimborsoImpegniService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TipoPastoService tipoPastoService;

    @Autowired
    private Environment env;

    @Autowired
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    @Autowired
    private OrdineMissioneTaxiService ordineMissioneTaxiService;

    @Autowired
    private OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService;

    @Transactional(readOnly = true)
    public CMISFileAttachment uploadAllegato(Long idRimborsoMissioneDettagli,
                                             InputStream inputStream, String name, MimeTypes mimeTypes) throws AwesomeException {
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
            throws AwesomeException {
        if (idRimborsoMissioneDettagli != null) {
            List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsDetail(idRimborsoMissioneDettagli);
            return lista;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoMissione)
            throws AwesomeException {

        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                .findById(idRimborsoMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        return rimborsoMissioneDettagliRepository.getRimborsoMissioneDettagli(rimborsoMissione);
    }

    @Transactional(readOnly = true)
    public List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoMissione, LocalDate data)
            throws AwesomeException {

        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                .findById(idRimborsoMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        return rimborsoMissioneDettagliRepository
                .getRimborsoMissioneDettagli(rimborsoMissione, data);
    }

    private void validaCRUD(RimborsoMissioneDettagli rimborsoMissioneDettagli) throws AwesomeException {

        RimborsoMissione rimborsoMissione = rimborsoMissioneDettagli.getRimborsoMissione();

        if (rimborsoMissioneDettagli.getKmPercorsi() != null) {

            if (rimborsoMissione.getOrdineMissione() != null) {

                Long idOrdine = (Long) rimborsoMissione.getOrdineMissione().getId();

                OrdineMissione ordineMissione = ordineMissioneRepository
                        .findById(idOrdine)
                        .orElseThrow(() -> new AwesomeException(
                                CodiciErrore.ERRGEN,
                                "Ordine missione non trovato."
                        ));

                OrdineMissioneAutoPropria autoPropria =
                        ordineMissioneService.getAutoPropria(ordineMissione);

                if (autoPropria != null &&
                        !Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(), "N").equals("S")) {

                    throw new AwesomeException(
                            CodiciErrore.ERRGEN,
                            "Non è possibile utilizzare il rimborso chilometrico perchè in fase d'ordine di missione non è stata scelta per la richiesta auto propria il motivo di ispezione, verifica e controlli."
                    );
                }
            }
        }

        if (!StringUtils.hasText(rimborsoMissioneDettagli.getDsSpesa())) {
            throw new AwesomeException(
                    CodiciErrore.ERRGEN,
                    "Indicare una descrizione per la spesa."
            );
        }

        if (!env.acceptsProfiles(Costanti.SPRING_PROFILE_SHOWCASE)) {
            validaDettaglioRimborsoService.valida(rimborsoMissioneDettagli);
        }
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

    private void controlliSpeseMezzi(RimborsoMissioneDettagli rimborsoMissioneDettagli, RimborsoMissione rimborsoMissione) {
        Long idMissione = Long.valueOf(rimborsoMissione.getOrdineMissione().getId().toString());

        boolean isTaxiUsed = ordineMissioneTaxiService.getTaxi(idMissione) != null;
        boolean isAutoNoleggioUsed = ordineMissioneAutoNoleggioService.getAutoNoleggio(idMissione) != null;
        boolean isAutoPropriaUsed = ordineMissioneAutoPropriaService.getAutoPropria(idMissione) != null;

        boolean tassaSoggiorno = rimborsoMissioneDettagliRepository
                .getRimborsoMissioneDettagli(rimborsoMissione)
                .stream()
                .anyMatch(dettaglio -> dettaglio.getImportoEuro().compareTo(BigDecimal.ZERO) != 0
                        && dettaglio.getCdTiSpesa().equalsIgnoreCase(Costanti.SPESA_PERNOTTAMENTO));

        String cdTiSpesa = rimborsoMissioneDettagli.getCdTiSpesa();
        String messaggioErrore = "ATTENZIONE! Voce non selezionabile in quanto NON preventivamente autorizzata";

        String utilizzoMotiviIspettivi = null;
        String utilizzoMotiviSediDisagiate = null;
        if (isAutoPropriaUsed) {
            OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(idMissione);
            utilizzoMotiviIspettivi = Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(), "N");
            utilizzoMotiviSediDisagiate = Utility.nvl(autoPropria.getUtilizzoMotiviSediDisagiate(), "N");
        }

        // Gestione delle spese in base al codice di spesa
        switch (cdTiSpesa) {
            case Costanti.SPESA_INDENNITA_KM:
                if (!isAutoPropriaUsed || utilizzoMotiviIspettivi.equals("N") && utilizzoMotiviSediDisagiate.equals("S")) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;

            case Costanti.SPESA_IND_AUTO_PROPRIA:
                if (!isAutoPropriaUsed || utilizzoMotiviIspettivi.equals("S") && utilizzoMotiviSediDisagiate.equals("N")) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;

            case Costanti.SPESA_NOLEGGIO_AUTO:
                if (!isAutoNoleggioUsed) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;

            case Costanti.SPESA_TAXI:
                if (!isTaxiUsed) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;

            case Costanti.SPESA_PEDAGGIO_AUTOSTRADA:
                if (!isAutoPropriaUsed && !isAutoNoleggioUsed) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;
            case Costanti.SPESA_PARCHEGGIO:
                if (!isAutoNoleggioUsed && !isAutoPropriaUsed) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;

            case Costanti.SPESA_ACC_DISABILE:
                if (!isTaxiUsed) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }

                OrdineMissioneTaxi taxi = ordineMissioneTaxiService.getTaxi(idMissione);
                if (StringUtils.isEmpty(taxi.getMotiviHandicap())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }
                break;


            case Costanti.SPESE_VISTO_VIAGGI_ESTERO:
                if (rimborsoMissione.getOrdineMissione().getTipoMissione().equals("I")) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, messaggioErrore);
                }

            default:
                break;
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
                                            RimborsoMissione rimborsoMissione,
                                            Integer livello) throws AwesomeException {

        long oreDifferenza = ChronoUnit.HOURS.between(
                rimborsoMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES),
                rimborsoMissione.getDataFineMissione().truncatedTo(ChronoUnit.MINUTES)
        );

        if (rimborsoMissione.getOrdineMissione() != null &&
                rimborsoMissione.getOrdineMissione().getId() != null) {

            // Recupero OrdineMissione in modo sicuro
            OrdineMissione ordineMissione = ordineMissioneRepository.findById(
                            (Long) rimborsoMissione.getOrdineMissione().getId())
                    .orElseThrow(() -> new AwesomeException(
                            CodiciErrore.ERRGEN,
                            "Ordine Missione non trovato per id " + rimborsoMissione.getOrdineMissione().getId()
                    ));

            if ((!rimborsoMissione.isAssociato() ||
                    (rimborsoMissione.getInquadramento() != null &&
                            rimborsoMissione.getInquadramento().compareTo(47L) < 0))) {
                livello = 3;
            }
        }

        // Controllo congruenza pasto
        if (livello < 4) {
            if (oreDifferenza < 4 ||
                    (oreDifferenza < 12 && rimborsoMissioneDettagli.getCdTiPasto().startsWith("G"))) {
                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Tipo pasto non spettante per la durata della missione"
                );
            }
        } else {
            if (oreDifferenza < 8 ||
                    (oreDifferenza < 12 && rimborsoMissioneDettagli.getCdTiPasto().startsWith("G"))) {
                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Tipo pasto non spettante per la durata della missione"
                );
            }
        }
    }

    @Transactional
    public RimborsoMissioneDettagli createRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli)
            throws AwesomeException {

        rimborsoMissioneDettagli.setUid(securityService.getCurrentUserLogin());
        rimborsoMissioneDettagli.setUser(securityService.getCurrentUserLogin());
        rimborsoMissioneDettagli.setStato(Costanti.STATO_INSERITO);

        if (rimborsoMissioneDettagli.getTiSpesaDiaria() == null) {
            rimborsoMissioneDettagli.setTiSpesaDiaria("S");
        }

        Long idMissione = (Long) rimborsoMissioneDettagli.getRimborsoMissione().getId();

        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                .findById(idMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
        rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);
        Long maxRiga = rimborsoMissioneDettagliRepository
                .getMaxRigaDettaglio(rimborsoMissione);

        if (maxRiga == null) {
            maxRiga = 0L;
        }

        rimborsoMissioneDettagli.setRiga(maxRiga + 1);
        rimborsoMissioneDettagli.setToBeCreated();
        controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);
        impostaImportoDivisa(rimborsoMissioneDettagli);
        validaCRUD(rimborsoMissioneDettagli);
        controlliPasto(rimborsoMissioneDettagli, rimborsoMissione);
        controlliSpeseMezzi(rimborsoMissioneDettagli, rimborsoMissione);
        aggiornaDatiImpegni(rimborsoMissioneDettagli);
        RimborsoMissioneDettagli saved =
                rimborsoMissioneDettagliRepository.save(rimborsoMissioneDettagli);

        log.debug("Created Information for RimborsoMissioneDettagli: {}", saved);

        return saved;
    }

    public void aggiornaDatiImpegni(RimborsoMissioneDettagli rimborsoMissioneDettagli) {

        if (rimborsoMissioneDettagli.getIdRimborsoImpegni() != null) {

            RimborsoImpegni rimborsoImpegni = rimborsoImpegniRepository
                    .findById(rimborsoMissioneDettagli.getIdRimborsoImpegni())
                    .orElse(null);

            if (rimborsoImpegni != null) {

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
                                                 Boolean deleteDocument) throws AwesomeException {
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
            throws AwesomeException, OptimisticLockException {
        RimborsoMissioneDettagli rimborsoMissioneDettagli = getRimborsoMissioneDettaglio(idRimborsoMissioneDettagli);

        // effettuo controlli di validazione operazione CRUD
        if (rimborsoMissioneDettagli != null) {
            rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissioneDettagli.getRimborsoMissione());
            cancellaRimborsoMissioneDettagli(rimborsoMissioneDettagli, true);
        }
    }

    public RimborsoMissioneDettagli getRimborsoMissioneDettaglio(Long idRimborsoMissioneDettagli)
            throws AwesomeException {

        return rimborsoMissioneDettagliRepository
                .findById(idRimborsoMissioneDettagli)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Dettaglio rimborso missione non trovato."
                ));
    }

    private void cancellaRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli,
            Boolean deleteDocument) throws AwesomeException {

        rimborsoMissioneDettagli.setToBeUpdated();
        rimborsoMissioneDettagli.setStato(Costanti.STATO_ANNULLATO);
        rimborsoMissioneDettagliRepository.save(rimborsoMissioneDettagli);

        if (Boolean.TRUE.equals(deleteDocument)) {
            List<CMISFileAttachment> lista =
                    getAttachments(Long.valueOf(rimborsoMissioneDettagli.getId().toString()));
            if (lista != null && !lista.isEmpty()) {
                for (CMISFileAttachment attach : lista) {
                    rimborsoMissioneService.gestioneCancellazioneAllegati(
                            attach.getId(),
                            Long.valueOf(rimborsoMissioneDettagli.getRimborsoMissione().getId().toString())
                    );
                }
            }
        }
    }

    private void impostaImportoDivisa(RimborsoMissioneDettagli rimborsoMissioneDettagli) {
        if (rimborsoMissioneDettagli.getCambio().compareTo(BigDecimal.ONE) == 0) {
            rimborsoMissioneDettagli.setImportoDivisa(rimborsoMissioneDettagli.getImportoEuro());
        }
    }

    @Transactional
    public RimborsoMissioneDettagli updateRimborsoMissioneDettagli(
            RimborsoMissioneDettagli rimborsoMissioneDettagli)
            throws AwesomeException {

        RimborsoMissioneDettagli dettaglioDB =
                rimborsoMissioneDettagliRepository
                        .findById((Long) rimborsoMissioneDettagli.getId())
                        .orElseThrow(() -> new AwesomeException(
                                CodiciErrore.ERRGEN,
                                "Dettaglio Rimborso Missione da aggiornare inesistente."
                        ));

        Long idMissione = (Long) rimborsoMissioneDettagli.getRimborsoMissione().getId();

        RimborsoMissione rimborsoMissione = rimborsoMissioneRepository
                .findById(idMissione)
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Rimborso missione non trovato."
                ));

        if (!rimborsoMissioneDettagli.isModificaSoloDatiFinanziari(dettaglioDB)) {
            rimborsoMissioneService.controlloOperazioniCRUDDaGui(rimborsoMissione);
        }

        rimborsoMissioneDettagli.setRimborsoMissione(rimborsoMissione);
        controlloDatiObbligatoriDaGui(rimborsoMissioneDettagli);

        // copia campi
        dettaglioDB.setCdTiPasto(rimborsoMissioneDettagli.getCdTiPasto());
        dettaglioDB.setCdTiSpesa(rimborsoMissioneDettagli.getCdTiSpesa());
        dettaglioDB.setDataSpesa(rimborsoMissioneDettagli.getDataSpesa());
        dettaglioDB.setDsSpesa(rimborsoMissioneDettagli.getDsSpesa());
        dettaglioDB.setImportoEuro(rimborsoMissioneDettagli.getImportoEuro());
        dettaglioDB.setKmPercorsi(rimborsoMissioneDettagli.getKmPercorsi());
        dettaglioDB.setCambio(rimborsoMissioneDettagli.getCambio());
        dettaglioDB.setCdDivisa(rimborsoMissioneDettagli.getCdDivisa());
        dettaglioDB.setNote(rimborsoMissioneDettagli.getNote());

        impostaImportoDivisa(dettaglioDB);
        dettaglioDB.setToBeUpdated();
        validaCRUD(dettaglioDB);
        controlliPasto(rimborsoMissioneDettagli, rimborsoMissione);
        controlliSpeseMezzi(rimborsoMissioneDettagli, rimborsoMissione);
        aggiornaDatiImpegni(dettaglioDB);
        RimborsoMissioneDettagli updated =
                rimborsoMissioneDettagliRepository.save(dettaglioDB);
        log.debug("Updated Information for Dettaglio Rimborso Missione: {}", updated);

        return updated;
    }

}
