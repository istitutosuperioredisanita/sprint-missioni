package it.cnr.si.missioni.service;

import feign.FeignException;
import it.cnr.si.flows.model.ProcessDefinitions;
import it.cnr.si.flows.model.StartWorkflowResponse;
import it.cnr.si.flows.model.TaskResponse;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISMissione;
import it.cnr.si.missioni.cmis.MessageForFlow;
import it.cnr.si.missioni.cmis.MessageForFlowRimborso;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.AceService;
import it.cnr.si.service.SiperService;
import it.cnr.si.service.application.FlowsService;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.letture.PersonaWebDto;
import it.cnr.si.service.dto.anagrafica.letture.RuoloPersonaWebDto;
import it.cnr.si.service.dto.anagrafica.scritture.BossDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MissioniAceService {
    private static final Log logger = LogFactory.getLog(MissioniAceService.class);

    @Autowired
    AceService aceService;

    @Autowired
    SiperService siperService;

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
    public SimpleRuoloWebDto recuperoRuolo(String tipoRuolo){
        return aceService.getRuoloBySigla(tipoRuolo);
    }
    public RuoloPersonaWebDto associaRuoloPersona(RuoloPersonaDto ruoloPersona){
        try{
            RuoloPersonaWebDto ruolo = aceService.associaRuoloPersona(ruoloPersona);
            return ruolo;
        } catch (FeignException e) {
            logger.info(e.getMessage() + " for Ruolo: idPersona: "  + ruoloPersona.getPersona() + " idRuolo: "+ ruoloPersona.getRuolo()+" Id Entita Organizzativa "+ruoloPersona.getEntitaOrganizzativa());
        }
        return null;
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_ACCOUNT)
    public UserInfoDto getAccountFromSiper(String currentLogin) {
        logger.info("getAccountFromSiper: "+ currentLogin);
        UserInfoDto userInfoDto = siperService.getUserInfoByUsername(currentLogin);
        return userInfoDto;
    }

    @Cacheable(value = Costanti.NOME_CACHE_RUOLI)
    public List<String> getRoles(String principal){
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            authorities = aceService.ruoliAttivi(principal).stream()
                    .filter(ruolo -> ruolo.getContesto().getSigla().equals("missioni-app"))
                    .map(a -> new SimpleGrantedAuthority(a.getSigla()))
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            logger.info(e.getMessage() + " for user: "+ "\"" +  principal + "\"");
        }

        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        return authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_DIRETTORE)
    public String getDirettore(String username) {
        BossDto direttore = aceService.findResponsabileStruttura(username);
        if (direttore != null){
            return direttore.getUtente().getUsername();
        }
        return "";
    }

    public SimplePersonaWebDto getPersona(String currentLogin) {
        logger.info("getAccountFromSiper: "+ currentLogin);
        return  aceService.getPersonaByUsername(currentLogin);
    }

}
