package it.cnr.si.missioni.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 
 * 
 */
public class Costanti {
	public final static String REST_CDS =  "ConsCdSAction.json";
	public final static String REST_CDR =  "ConsCdRAction.json";
	public final static String REST_UO =  "ConsUnitaOrganizzativaAction.json";
	public final static String REST_NAZIONE =  "ConsNazioneAction.json";
	public final static String REST_MODULO =  "ConsProgettiAction.json";
	public final static String REST_GAE =  "ConsGAEAction.json";
	public final static String REST_VOCE =  "ConsCapitoloAction.json";
	public final static String REST_TERZO =  "ConsTerzoAction.json";
	public final static String REST_INQUADRAMENTI =  "ConsInquadramentoAction.json";
	public final static String REST_IMPEGNO =  "ConsImpegnoAction.json";
	public final static String REST_IMPEGNO_GAE = "ConsImpegnoGaeAction.json";
    public final static String REST_TIPI_SPESA =  "ConsTipiSpesaAction.json";
	public final static String REST_VALIDA_MASSIMALE_SPESA =  "restapi/missioni/validaMassimaleSpesa";
	public final static String REST_COMUNICA_RIMBORSO_SIGLA =  "restapi/missioni";
	public final static String REST_ACCOUNT = "json/userinfo/";
	public final static String REST_UO_DIRECTOR = "json/sedi";
	public final static String REST_UO_TIT_CA = "titCa=";
	public final static String REST_UO_SEDE = "sedeId=";
	public final static String NOME_CACHE_PROXY = "cacheProxy";
	public final static String NOME_CACHE_DATI_UO = "cacheDatiUo";
	public final static String NOME_CACHE_USER_SPECIAL = "cacheUserSpecial";
	public final static String NOME_CACHE_SERVICES_SIGLA = "cacheServicesSigla";
	public final static String APP_AA = "AA";
	public final static String TIPO_DOCUMENTO_OBBLIGAZIONI_SIGLA = "OBB";
	
	public final static String APP_SIGLA = "SIGLA";
	public final static String APP_SIPER = "SIPER";
	public final static String HEADER_FOR_PROXY_AUTHORIZATION = "x-proxy-authorization";
	public final static String STATO_ANNULLATO = "ANN";
	public final static String STATO_ANNULLATO_DOPO_APPROVAZIONE = "ANA";
	public final static String STATO_INSERITO = "INS";
	public final static String STATO_DEFINITIVO = "DEF";
	public final static String STATO_NON_INVIATO_FLUSSO = "INS";
	public final static String STATO_INVIATO_FLUSSO = "INV";
	public final static String STATO_APPROVATO_FLUSSO = "APP";
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
	public final static int DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_CACHE = 1000000;
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SYSTEM_ACCOUNT = "system";
	public final static String STATO_APPROVATO_PER_HOME = "A";
	public final static String STATO_RESPINTO_PER_HOME = "R";
	public final static String STATO_ANNULLATO_PER_HOME = "N";
	public final static String STATO_DA_AUTORIZZARE_PER_HOME = "D";
	public final static String STATO_DA_VALIDARE_PER_HOME = "V";
	public final static String STATO_DA_CONFERMARE_PER_HOME = "C";
	public final static String STATO_PER_RESPONSABILE_GRUPPO_PER_HOME = "M";
	public final static String CDS_SAC = "000";
	public final static String UO_SAC_PROGETTI = "000.000";
    public final static String TIPO_ORDINE_DI_MISSIONE = "O";
    public final static String TIPO_RIMBORSO_MISSIONE = "R";
    public final static String TIPO_ANNULLAMENTO_ORDINE_MISSIONE = "A";
    public final static String CODICE_DIVISA_DEFAULT_SIGLA = "EURO";
	public final static Long NAZIONE_ITALIA_SIGLA = new Long ("1");
	public final static String SI = "Sì";
	public final static String NO = "No";
	public final static BigDecimal IMPORTO_SPESA_MAX_DEFAULT = new BigDecimal(999999999);
	
	public final static Map<String, String> PRIORITA;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(PRIORITA_CRITICA, "CRITICA");
        aMap.put(PRIORITA_IMPORTANTE, "IMPORTANTE");
        aMap.put(PRIORITA_MEDIA, "MEDIA");
        PRIORITA = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> TRATTAMENTO;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(TAM, "Trattamento Alternativo di Missione");
        aMap.put(RIMBORSO_DOCUMENTATO, "Rimborso Documentato");
        TRATTAMENTO = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> PARTENZA_DA;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(RESIDENZA_DOMICILIO, "Residenza/Domicilio Fiscale");
        aMap.put(SEDE_LAVORO, "Sede di Lavoro");
        aMap.put(ALTRO, "Altro");
        PARTENZA_DA = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> STATO;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_ANNULLATO, "Annullato");
        aMap.put(STATO_ANNULLATO_DOPO_APPROVAZIONE, "Annullato dopo l'approvazione");
        aMap.put(STATO_CONFERMATO, "Confermato");
        aMap.put(STATO_INVIATO_RESPONSABILE, "Inviato");
        aMap.put(STATO_INSERITO, "Inserito");
        aMap.put(STATO_DEFINITIVO, "Definitivo");
        STATO = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> TIPO_MISSIONE;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(MISSIONE_ITALIANA, "ITALIA");
        aMap.put(MISSIONE_ESTERA, "ESTERO");
        TIPO_MISSIONE = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> SI_NO;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("S", SI);
        aMap.put("N", NO);
        SI_NO = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> STATO_FLUSSO;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(STATO_INVIATO_FLUSSO, "Inviato");
        aMap.put(STATO_INSERITO, "Non Inviato");
        aMap.put(STATO_ANNULLATO, "Annullato");
        aMap.put(STATO_APPROVATO_FLUSSO, "Approvato");
        STATO_FLUSSO = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> STATO_FLUSSO_FROM_CMIS;
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

    public final static Map<String, String> STATO_FLUSSO_RIMBORSO_FROM_CMIS;
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

    public final static Map<String, String> STATO_INVIO_SIGLA;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(null, "Non Completa");
        aMap.put("", "Non Completa");
        aMap.put(STATO_INVIO_DA_NON_COMUNICARE, "Da Non Comunicare");
        aMap.put(STATO_INVIO_SIGLA_DA_COMUNICARE, "Da Comunicare");
        aMap.put(STATO_INVIO_SIGLA_COMUNICATA, "Comunicato");
        STATO_INVIO_SIGLA = Collections.unmodifiableMap(aMap);
    }

    public final static Map<String, String> FONDI;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(FONDI_DI_COMPETENZA, "Competenza");
        aMap.put(FONDI_DI_RESIDUO, "Residuo");
        FONDI = Collections.unmodifiableMap(aMap);
    }
}
