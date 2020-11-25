package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISMissione;
import it.cnr.si.missioni.cmis.MessageForFlow;
import it.cnr.si.missioni.cmis.MessageForFlowRimborso;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.service.AceService;
import it.cnr.si.service.dto.anagrafica.scritture.BossDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageForFlowsService {
    @Autowired
    UoService uoService;

    @Autowired
    AceService aceService;

    @Autowired
    DatiIstitutoService datiIstitutoService;


    public MessageForFlow impostaGruppiFirmatari(CMISMissione cmisMissione, MessageForFlow messageForFlows){
        Uo uoDatiSpesa = uoService.recuperoUo(cmisMissione.getUoSpesa());
        String uoRichSigla = cmisMissione.getUoRichSigla();
        String uoSpesaSigla = null;
        String uoRich = cmisMissione.getUoRich();
        String uoSpesa = null;
        if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
            if (StringUtils.hasLength(cmisMissione.getUoCompetenza())){
                uoSpesa = cmisMissione.getUoCompetenza();
                uoSpesaSigla = cmisMissione.getUoCompetenzaSigla();
            } else {
                uoSpesa = cmisMissione.getUoRich();
                uoSpesaSigla = cmisMissione.getUoRichSigla();
            }
        } else {
            uoSpesa = cmisMissione.getUoSpesa();
            uoSpesaSigla = cmisMissione.getUoSpesaSigla();
        }

        String gruppoPrimoFirmatario = null;
        String gruppoSecondoFirmatario = null;

        DatiIstituto datiIstitutoUoSpesa = datiIstitutoService.getDatiIstituto(uoSpesaSigla, new Integer(cmisMissione.getAnno()));
        DatiIstituto datiIstitutoUoRich = datiIstitutoService.getDatiIstituto(uoRichSigla, new Integer(cmisMissione.getAnno()));

        SimplePersonaWebDto persona = aceService.getPersonaByUsername(cmisMissione.getUsernameUtenteOrdine());
        Integer idSede = persona.getSede().getId();
        String ruolo = "firma-missioni";
        if (cmisMissione.isMissioneEstera() && !cmisMissione.isMissionePresidente()){
            ruolo = ruolo+"-estere";
            if (uoRich.startsWith(Costanti.CDS_SAC)){
                Account direttore = uoService.getDirettore(uoRich);
                if (direttore.getUid().equals(cmisMissione.getUsernameUtenteOrdine())){
                    BossDto boss = aceService.findResponsabileUtente(direttore.getUid());
                    idSede = boss.getEntitaOrganizzativa().getId();
                }
            }
        }

        if (cmisMissione.isMissioneGratuita()){
            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
        } else {
            if (cmisMissione.isMissioneCug()){
                gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
//TODO					Gestione recupero CUG
//					gruppoSecondoFirmatario = ;
            } else if (cmisMissione.isMissionePresidente()){
//TODO Verificare se è corretto passare la sede per il presidente.
                if (messageForFlows instanceof MessageForFlowRimborso){
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo+"-presidente", idSede);
                } else {
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo+"-presidente", idSede);
                    gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                }
            } else {
                List<SimpleEntitaOrganizzativaWebDto> listaSediSpesa = recuperoSediDaUo(uoSpesa);
                if (cmisMissione.getCdsRich().equals(cmisMissione.getCdsSpesa()) ){
                    if (Utility.nvl(datiIstitutoUoSpesa.getSaltaFirmaUosUoCds(),"N").equals("S") ){
                        SimpleEntitaOrganizzativaWebDto sedePrincipale = recuperoSedePrincipale(listaSediSpesa);
                        if (sedePrincipale != null){
                            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, sedePrincipale.getId());
                            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
                        }
                    } else if (Utility.nvl(datiIstitutoUoRich.getSaltaFirmaUosUoCds(),"N").equals("S")){
                        List<SimpleEntitaOrganizzativaWebDto> listaSediRich = recuperoSediDaUo(uoRich);
                        SimpleEntitaOrganizzativaWebDto sedePrincipale = recuperoSedePrincipale(listaSediRich);
                        if (sedePrincipale != null){
                            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, sedePrincipale.getId());
                            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
                        }
                    } else {
                        gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                        gruppoSecondoFirmatario = recuperoGruppoSecondoFirmatarioStandard(uoSpesa, ruolo, idSede);
                    }
                } else {
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    if (uoSpesa.equals(uoRich)){
                        gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    } else {
                        gruppoSecondoFirmatario = recuperoGruppoSecondoFirmatarioStandard(uoSpesa, ruolo, idSede);
                    }
                }
            }
        }
        if (gruppoPrimoFirmatario == null){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è state recuperate un gruppo per il primo firmatario.");
        }

        if (gruppoSecondoFirmatario == null){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è state recuperate un gruppo per il secondo firmatario.");
        }

        messageForFlows.setGruppoFirmatarioUo(gruppoPrimoFirmatario);
        messageForFlows.setGruppoFirmatarioSpesa(gruppoSecondoFirmatario);

        return messageForFlows;
    }
    public String recuperoGruppoSecondoFirmatarioStandard(String uo, String ruolo, Integer idSedePrimoGruppoFirmatario){
        List<SimpleEntitaOrganizzativaWebDto> lista = recuperoSediDaUo(uo);
        if (lista.size() == 0){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non sono state recuperate sedi per la uo "+uo);
        }
        SimpleEntitaOrganizzativaWebDto sede = recuperoSedePrincipale(lista);
        if (sede != null){
            return costruisciGruppoFirmatario(ruolo, sede.getId());
        } else {
            for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : lista){
                if (entitaOrganizzativaWebDto.getId().compareTo(idSedePrimoGruppoFirmatario) == 0){
                    return costruisciGruppoFirmatario(ruolo, entitaOrganizzativaWebDto.getId());
                }
            }
            return costruisciGruppoFirmatario(ruolo, lista.get(0).getId());
        }
    }

    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediDaUo(String uo){
        List<SimpleEntitaOrganizzativaWebDto> lista = aceService.entitaOrganizzativaFindByTerm(uo);
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return entita.getCdsuo().equals(uo) ;
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        return listaEntitaUo;
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePrincipale(List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo ){
        for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : listaEntitaUo){
            String tipoEntitaOrganizzativa = entitaOrganizzativaWebDto.getTipo().getSigla();
            if (tipoEntitaOrganizzativa.equals("UFF") ||
                    tipoEntitaOrganizzativa.equals("SPRINC")||
                    tipoEntitaOrganizzativa.equals("AREA")||
                    tipoEntitaOrganizzativa.equals("DIP")){
                return entitaOrganizzativaWebDto;
            }
        }
        return null;
    }
    private String costruisciGruppoFirmatario(String ruolo, Integer idSede){
        return ruolo+"@"+idSede;
    }

}
