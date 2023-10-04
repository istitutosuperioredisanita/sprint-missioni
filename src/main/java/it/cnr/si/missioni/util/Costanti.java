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

package it.cnr.si.missioni.util;

import java.math.BigDecimal;
import java.util.*;


/**
 *
 */
public class Costanti {
    public final static String REST_CDS = "ConsCdSAction.json";
    public final static String REST_CDR = "ConsCdRAction.json";
    public final static String REST_UO = "ConsUnitaOrganizzativaAction.json";
    public final static String REST_NAZIONE = "ConsNazioneAction.json";
    public final static String REST_MODULO = "ConsProgettiAction.json";
    public final static String REST_GAE = "ConsGAEAction.json";
    public final static String REST_VOCE = "ConsCapitoloAction.json";
    public final static String REST_TERZO = "ConsTerzoAction.json";
    public final static String REST_TERZO_PER_COMPENSO = "ConsTerzoPerCompensoAction.json";
    public final static String REST_INQUADRAMENTI = "ConsInquadramentoAction.json";
    public final static String REST_TIPO_PASTO = "ConsMissioneTipoPastoAction.json";
    public final static String REST_IMPEGNO = "ConsImpegnoAction.json";
    public final static String REST_IMPEGNO_GAE = "ConsImpegnoGaeAction.json";
    public final static String REST_TIPI_SPESA = "ConsTipiSpesaAction.json";
    public final static String REST_VALIDA_MASSIMALE_SPESA = "restapi/missioni/validaMassimaleSpesa";
    public final static String REST_COMUNICA_RIMBORSO_SIGLA = "restapi/missioni";

    public final static String REST_TERZO_INFO_SIGLA = "restapi/terzo/info/";
    public final static String REST_USERINFO_SIGLA = "restapi/terzo/info/";
    public final static String REST_ACCOUNT = "json/userinfo/";
    public final static String REST_UO_DIRECTOR = "json/sedi";
    public final static String REST_UO_TIT_CA = "titCa=";
    public final static String REST_UO_SEDE = "sedeId=";
    public final static String NOME_STAMPA_ORDINE = "ordine";
    public final static String NOME_STAMPA_RIMBORSO = "rimborso";
    public final static String NOME_STAMPA_AUTO_PROPRIA = "autoPropria";
    public final static String NOME_STAMPA_ANTICIPO = "anticipo";
    public final static String PARAMETER_DATA_SOURCE_FOR_PRINT = "REPORT_DATA_SOURCE";

    public final static String NOME_CACHE_MESSAGGIO = "cacheMessaggio";
    public final static String NOME_CACHE_PROXY = "cacheProxy";
    public final static String NOME_CACHE_TERZO_COMPENSO_SERVICE = "cacheTerzoCompensoService";

    public final static String NOME_CACHE_TERZO_INFO_SERVICE = "cacheTerzoInfoService";
    public final static String NOME_CACHE_TICKET_ALFRESCO = "cacheTicketAlfresco";
    public final static String NOME_CACHE_RUOLI = "cacheRuoli";
    public final static String NOME_CACHE_GRANT = "cacheGrant";
    public final static String NOME_CACHE_DATI_PERSONE = "cachePersone";
    public final static String NOME_CACHE_DATI_DIRETTORE = "cacheDirettore";
    public final static String NOME_CACHE_ID_SEDE = "cacheIdSede";
    public final static String NOME_CACHE_DATI_ACCOUNT = "cacheAccount";
    public final static String NOME_CACHE_DATI_UO = "cacheDatiUo";
    public final static String NOME_CACHE_DATI_UTENTI_PRESIDENTE_SPECIALI = "cacheDatiUtentiPresidenteSpeciali";
    public final static String NOME_CACHE_FAQ = "cacheFaq";
    public final static String NOME_CACHE_USER_SPECIAL = "cacheUserSpecial";
    public final static String NOME_CACHE_SERVICES_SIGLA = "cacheServicesSigla";
    public final static String APP_AA = "AA";
    public final static String TIPO_RAPPORTO_ASS = "ASS";
    public final static String TIPO_RAPPORTO_COLL = "COLL";
    public final static String TIPO_RAPPORTO_PROF = "PROF";
    public final static String TIPO_RAPPORTO_EXDIP = "EXDIP";
    public final static String TIPO_RAPPORTO_OCCA = "OCCA";
    public final static Long INQUADRAMENTO_ASSEGNISTA = Long.valueOf(43);
    public final static String TIPO_DOCUMENTO_OBBLIGAZIONI_SIGLA = "OBB";

    public final static String USER_CRON_MISSIONI = "app.missioni";
    public final static String ACE_SIGLA_CUG = "CUG";
    public final static String ACE_SIGLA_PRESIDENTE = "PRESIDENZA";

    public final static String STRING_FOR_SANITIZE_FILE_NAME = "([\\/:@()&\u20AC<>?\"])";
    public final static String REST_OIL_NEW_PROBLEM = "pest/HDSiper";
    public final static String APP_HELPDESK = "OIL";
    public final static String PROPERTY_RESULT_FLOW = "persistedObject";
    public final static String PATTERN_RESULT_FLOW = "id=(activiti\\$[0-9]+)";
    public final static String INITIAL_NAME_OLD_FLOWS = "activiti$";
    public final static String APP_SIGLA = "SIGLA";
    public final static String APP_SIPER = "SIPER";
    public final static String APP_STORAGE = "STORAGE";
    public final static String APP_FLOWS = "FLOWS";
    public final static String APP_VECCHIA_SCRIVANIA = "vecchiaScrivania";
    public final static String HEADER_FOR_PROXY_AUTHORIZATION = "x-proxy-authorization";
    public final static String STATO_ANNULLATO = "ANN";
    public final static String STATO_ANNULLATO_DOPO_APPROVAZIONE = "ANA";
    public final static String STATO_ANNULLATO_DOPO_APPROVAZIONE_CONSENTITO_RIMBORSO = "ANC";
    public final static String STATO_INSERITO = "INS";
    public final static String STATO_DEFINITIVO = "DEF";
    public final static String STATO_NON_INVIATO_FLUSSO = "INS";
    public final static String STATO_INVIATO_FLUSSO = "INV";
    public final static String STATO_FIRMATO_PRIMA_FIRMA_FLUSSO = "FPF";
    public final static String STATO_APPROVATO_FLUSSO = "APP";
    public final static String STATO_RESPINTO_UO_SPESA_FLUSSO = "RUS";
    public final static String STATO_RESPINTO_UO_FLUSSO = "RUO";
    public final static String STATO_CONFERMATO = "CON";
    public final static String STATO_INVIATO_RESPONSABILE = "INR";
    public final static String MISSIONE_ITALIANA = "I";
    public final static String MISSIONE_ESTERA = "E";
    public final static String PRIORITA_CRITICA = "5";
    public final static String PRIORITA_IMPORTANTE = "3";
    public final static String PRIORITA_MEDIA = "1";
    public final static String TAM = "T";
    public final static String RIMBORSO_DOCUMENTATO = "R";
    public final static String RESIDENZA_DOMICILIO = "R";
    public final static String SEDE_LAVORO = "S";
    public final static String ALTRO = "A";
    public final static String FONDI_DI_COMPETENZA = "C";
    public final static String FONDI_DI_RESIDUO = "R";
    public final static String STATO_FIRMATO_FROM_CMIS = "FIRMATO";
    public final static String STATO_ANNULLATO_FROM_CMIS = "ANNULLATO";
    public final static String STATO_RESPINTO_UO_FROM_CMIS = "RESPINTO UO";
    public final static String STATO_RESPINTO_SPESA_FROM_CMIS = "RESPINTO SPESA";
    public final static String STATO_RESPINTO_UO_REVOCA_FROM_CMIS = "RESPINTO UO REVOCA";
    public final static String STATO_RESPINTO_SPESA_REVOCA_FROM_CMIS = "RESPINTO SPESA REVOCA";
    public final static String STATO_FIRMA_UO_REVOCA_FROM_CMIS = "FIRMA UO REVOCA";
    public final static String STATO_FIRMA_SPESA_REVOCA_FROM_CMIS = "FIRMA SPESA REVOCA";
    public final static String STATO_FIRMA_UO_FROM_CMIS = "FIRMA UO";
    public final static String STATO_FIRMA_SPESA_FROM_CMIS = "FIRMA SPESA";
    public final static String STATO_RESPINTO_UO_RIMBORSO_FROM_CMIS = "RESPINTO UO RIMBORSO";
    public final static String STATO_RESPINTO_SPESA_RIMBORSO_FROM_CMIS = "RESPINTO SPESA RIMBORSO";
    public final static String STATO_FIRMA_UO_RIMBORSO_FROM_CMIS = "FIRMA UO RIMBORSO";
    public final static String STATO_FIRMA_SPESA_RIMBORSO_FROM_CMIS = "FIRMA SPESA RIMBORSO";
    public final static String STATO_INVIO_SIGLA_DA_COMUNICARE = "DAC";
    public final static String STATO_INVIO_DA_NON_COMUNICARE = "DNC";
    public final static String STATO_INVIO_SIGLA_COMUNICATA = "COM";
    public final static int DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_CACHE = 4999;
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_SHOWCASE = "showcase";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SYSTEM_ACCOUNT = "system";
    public final static String STATO_APPROVATO_PER_HOME = "A";
    public final static String STATO_RESPINTO_PER_HOME = "R";
    public final static String STATO_ANNULLATO_PER_HOME = "N";
    public final static String STATO_DA_AUTORIZZARE_PER_HOME = "D";
    public final static String STATO_DA_VALIDARE_PER_HOME = "V";
    public final static String STATO_DA_CONFERMARE_PER_HOME = "C";
    public final static String STATO_ALLA_VALIDAZIONE_AMM_PER_HOME = "VA";
    public final static String STATO_PER_RESPONSABILE_GRUPPO_PER_HOME = "M";
    public final static String CDS_SAC = "ASR";
    public final static String UO_SAC_PROGETTI = "ASR.666";
    public final static String TIPO_ORDINE_DI_MISSIONE = "O";
    public final static String TIPO_RIMBORSO_MISSIONE = "R";
    public final static String TIPO_ANNULLAMENTO_ORDINE_MISSIONE = "A";
    public final static String TIPO_ANNULLAMENTO_RIMBORSO_MISSIONE = "C";
    public final static String CODICE_DIVISA_DEFAULT_SIGLA = "EURO";
    public final static Long NAZIONE_ITALIA_SIGLA = Long.valueOf("1");
    public final static String SI = "Sì";
    public final static String NO = "No";
    public final static String TIPO_DOCUMENTO_MISSIONE = "missioni";
    public final static String TIPO_DOCUMENTO_ALLEGATO = "allegatoMissione";
    public final static String TIPO_DOCUMENTO_ANTICIPO = "anticipoMissione";
    public final static String TIPO_DOCUMENTO_AUTO_PROPRIA = "autoPropriaMissione";
    public final static String TIPO_DOCUMENTO_GIUSTIFICATIVO = "giustificativoMissione";
    public final static String TIPO_PAGAMENTO_BONIFICO = "BO";
    public final static String TIPO_PAGAMENTO_BONIFICO_ESTERO = "BOEST";
    public final static String TESTO_RIMBORSO_CONSENTITO_SU_ORDINE_ANNULLATO = "Annullamento Ordine di missione con Rimborso missione Consentito";
    public final static BigDecimal IMPORTO_SPESA_MAX_DEFAULT = new BigDecimal(999999999);
    public final static String NOME_PROCESSO_FLOWS_MISSIONI = "missioni";
    public final static String RUOLO_FIRMA = "firma-missioni";
    public final static String RUOLO_FIRMA_ESTERE = RUOLO_FIRMA + "-estere";
    public final static String RUOLO_FIRMA_PRESIDENTE = RUOLO_FIRMA + "-presidente";
    public final static String SIGLA_ACE_DIREZIONE_GENERALE = "DG";
    public final static String AMMINISTRATORE_MISSIONI = "supervisore@missioni";
    public static final String ROLE_FLOWS = "USER_flows#missioni";
    public static final String ROLE_ADMIN = "ADMIN#missioni";
    public static final String ROLE_USER = "USER#missioni";


    public final static Map<String, String> TIPO_DOCUMENTO_FLOWS;
    public final static List<String> TIPI_DOCUMENTO_FLOWS_DA_FIRMARE;
    public final static Map<String, String> PRIORITA;
    public final static Map<String, String> TRATTAMENTO;
    public final static Map<String, String> TRATTAMENTO_SHORT;
    public final static Map<String, String> PARTENZA_DA;
    public final static Map<String, String> STATO;
    public final static Map<String, String> TIPO_MISSIONE;
    public final static Map<String, String> SI_NO;
    public final static Map<String, String> STATO_FLUSSO;
    public final static Map<String, String> STATO_FLUSSO_FROM_CMIS;
    public final static Map<String, String> STATO_FLUSSO_RIMBORSO_FROM_CMIS;
    public final static Map<String, String> STATO_INVIO_SIGLA;
    public final static Map<String, String> FONDI;

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(TIPO_DOCUMENTO_ALLEGATO, "Allegato");
        aMap.put(TIPO_DOCUMENTO_ANTICIPO, "Anticipo");
        aMap.put(TIPO_DOCUMENTO_AUTO_PROPRIA, "Auto Propria");
        aMap.put(TIPO_DOCUMENTO_MISSIONE, "Missione");
        aMap.put(TIPO_DOCUMENTO_GIUSTIFICATIVO, "Giustificativo");
        TIPO_DOCUMENTO_FLOWS = Collections.unmodifiableMap(aMap);
    }

    static {
        List<String> list = new ArrayList<String>();
        list.add(TIPO_DOCUMENTO_MISSIONE);
        list.add(TIPO_DOCUMENTO_ANTICIPO);
        TIPI_DOCUMENTO_FLOWS_DA_FIRMARE = Collections.unmodifiableList(list);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(PRIORITA_CRITICA, "CRITICA");
        aMap.put(PRIORITA_IMPORTANTE, "IMPORTANTE");
        aMap.put(PRIORITA_MEDIA, "MEDIA");
        PRIORITA = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(TAM, "Trattamento Alternativo di Missione");
        aMap.put(RIMBORSO_DOCUMENTATO, "Rimborso Documentato");
        TRATTAMENTO = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(TAM, "TAM");
        aMap.put(RIMBORSO_DOCUMENTATO, "Rimborso");
        TRATTAMENTO_SHORT = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(RESIDENZA_DOMICILIO, "Residenza/Domicilio Fiscale");
        aMap.put(SEDE_LAVORO, "Sede di Lavoro");
        aMap.put(ALTRO, "Altro");
        PARTENZA_DA = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_ANNULLATO, "Annullato");
        aMap.put(STATO_ANNULLATO_DOPO_APPROVAZIONE, "Annullato dopo l'approvazione");
        aMap.put(STATO_ANNULLATO_DOPO_APPROVAZIONE_CONSENTITO_RIMBORSO, "Annullato dopo l'approvazione.Permesso il Rimborso");
        aMap.put(STATO_CONFERMATO, "Confermato");
        aMap.put(STATO_INVIATO_RESPONSABILE, "Inviato al responsabile");
        aMap.put(STATO_INSERITO, "Inserito");
        aMap.put(STATO_DEFINITIVO, "Definitivo");
        STATO = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(MISSIONE_ITALIANA, "ITALIA");
        aMap.put(MISSIONE_ESTERA, "ESTERO");
        TIPO_MISSIONE = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("S", SI);
        aMap.put("N", NO);
        SI_NO = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_INVIATO_FLUSSO, "Inviato");
        aMap.put(STATO_FIRMATO_PRIMA_FIRMA_FLUSSO, "Prima Firma Effettuata");
        aMap.put(STATO_INSERITO, "Non Inviato");
        aMap.put(STATO_ANNULLATO, "Annullato");
        aMap.put(STATO_APPROVATO_FLUSSO, "Approvato");
        aMap.put(STATO_RESPINTO_UO_SPESA_FLUSSO, "Respinto da Uo Spesa");
        aMap.put(STATO_RESPINTO_UO_FLUSSO, "Respinto da Uo");
        STATO_FLUSSO = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_ANNULLATO_FROM_CMIS, "Annullato");
        aMap.put(STATO_FIRMA_SPESA_FROM_CMIS, "Alla Firma Uo Spesa");
        aMap.put(STATO_FIRMA_UO_FROM_CMIS, "Alla Firma Uo");
        aMap.put(STATO_FIRMATO_FROM_CMIS, "Approvato");
        aMap.put(STATO_RESPINTO_SPESA_FROM_CMIS, "Respinto da Uo Spesa");
        aMap.put(STATO_RESPINTO_UO_FROM_CMIS, "Respinto da Uo");
        STATO_FLUSSO_FROM_CMIS = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_ANNULLATO_FROM_CMIS, "Annullato");
        aMap.put(STATO_FIRMA_SPESA_RIMBORSO_FROM_CMIS, "Alla Firma Uo Spesa");
        aMap.put(STATO_FIRMA_UO_RIMBORSO_FROM_CMIS, "Alla Firma Uo");
        aMap.put(STATO_FIRMATO_FROM_CMIS, "Approvato");
        aMap.put(STATO_RESPINTO_SPESA_RIMBORSO_FROM_CMIS, "Respinto da Uo Spesa");
        aMap.put(STATO_RESPINTO_UO_RIMBORSO_FROM_CMIS, "Respinto da Uo");
        STATO_FLUSSO_RIMBORSO_FROM_CMIS = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(null, "Non Completa");
        aMap.put("", "Non Completa");
        aMap.put(STATO_INVIO_DA_NON_COMUNICARE, "Da Non Comunicare");
        aMap.put(STATO_INVIO_SIGLA_DA_COMUNICARE, "Da Comunicare");
        aMap.put(STATO_INVIO_SIGLA_COMUNICATA, "Comunicato");
        STATO_INVIO_SIGLA = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(FONDI_DI_COMPETENZA, "Competenza");
        aMap.put(FONDI_DI_RESIDUO, "Residuo");
        FONDI = Collections.unmodifiableMap(aMap);
    }
}
