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

/**
 * @author ISED Interfaccia che dichiara gli errori e i rispettivi codici
 */
public interface CodiciErrore {
    int ATT_DEBUG_LEVEL = 1; // valori possibili 1 e 2
    int OK = 0;
    int ERRGEN = 1;
    int ERRSQL = 2;
    int ERR_RICERCA_DATI_ISTITUTO = 3;
    int SEDE_NON_TROVATA = 4;
    int DIPENDENTE_NON_TROVATO = 5;
    String ERR_AGGIORNAMENTO_DATI_ISTITUTO = "ERR_AGGIORNAMENTO_DATI_ISTITUTO";
    String INVALID_REQUEST = "INVALID_REQUEST";
    String TARGA_GIA_INSERITA = "TARGA_ALREADY_EXISTS";
    String CAMPO_OBBLIGATORIO = "Campo Obbligatorio";
    String DATI_GIA_INSERITI = "Dati già inseriti";
    String DATI_INCONGRUENTI = "DATI_INCONGRUENTI";
    String ERR_DATE_INCONGRUENTI = "ERR_DATE_INCONGRUENTI";
    String[] text = {
            "OK",
            "ERRGEN",
            "ERRSQL",
            "DATI_NON_TROVATI",
            "SEDE_NON_TROVATA",
            "DIPENDENTE_NON_TROVATO",
            "ERRDIPNABILASS",
            "ERRASSSOVRAPP",
            "ERRASSNONCONS31IV_X",
            "ERRINASSFUORILIMASSSEDE",
            "ERRFINEASSFUORILIMASSSEDE",
            "ERRASSNONCONS31",
            "ERRASSNONCONS37I_III",
            "ERRASSNONCONS37IV_X",
            "ERRDECORRENZA",
            "ERRDECORRENZANULLA",
            "ERRSTRAORFUORILIM",
            "ERRDIPNABILCOMPET",
            "ERRSTRAORCOMPLFUORILIM",
            "ERRSEDEDISAGIATA",
            "ERRDUPLICAZIONESTRAO",
            "ERRREPERIBILITAINDIVIDUALE",
            "ERRCODNONVAL",
            "ERRSUPERATOTETTOREPERMENS",
            "ERRSUPERATOTETTOSTRAOSEDE",
            "ERRDIPENDENTINONELABORATI",
            "ATTESTATONONVALIDATO",
            "ATTESTATOGIAVALIDATO",
            "ERRPASSWORDVALIDAZIONEMANCANTE",
            "ERRPASSWORDLOGINMANCANTE",
            "ERRPASSWORDVALIDAZIONESCORRETTA",
            "ERRINSTRALCIODAHOST",
            "ERRINREGISTRASUHOST",
            "WARNINSTRALCIODAHOST",
            "WARNINREGISTRASUHOST",
            "ATTESTATO GIA VALIDATO",
            "SUCCESSO",
            "DATI_SQL_INESISTENTI",
            "SEDE_SQL_INESISTENTE",
            "ORARIO_SEDE_INESISTENTE",
            "ERRREGISTRAZIONEDUPLICATA",
            "NONEXISTENTERRCODE",
            "ERROVERFLOWTABELLA",
            "ERRGENERICOSQL",
            "ERRSQLNONDISPONIBILE",
            "ERRTERMINAZANOMALA",
            "WARNING-TIMEOUT",
            "ERRASSNONCONS31CL9801",
            "ERRASSNONCONS37CL9801",
            "ERRASSCONSSOLOLIVIAIIICL9801",
            "ERRASSCONSSOLOLIVIVAIXCL9801",
            "ERRFAREPRIMALOSTRALCIO",
            "ATTENZIONESTRALCIOGIAESEGUITO",
            "ATTENZIONE_REGISTRAZIONE_GIA_ESEGUITA_IN_PRECEDENZA",
            "ERRORE_COMPETENZA_NULLA",
            "ATTENZIONE: GIORNI COMPETENZE SUPERIORI A GIORNI PRESENZE <BR> (assegnati giorni presenza effettivi)",
            "ATTENZIONE: ESEGUIRE CONTROLLO TETTO", "ERRSTRALCIOINCORSO",
            "ERRREGISTRAZIONEINCORSO",
            "ERRORESISTEMASOVRACCARICO_RIPROVARE_PIU_TARDI", "ERRORE_SQL_TCAS",
            "ERRORE_SQL_ORACLE", "DB_SOVRACCARICO",
            "ERR_SQLDS_CANNOT_ROLLBACK", "OPERAZIONE_DISABILITATA",
            "REGISTRAZIONE_INCOMPLETA", "LDAP_NON_DISPONIBILE",
            "ERRORE NOME UTENTE O PASSWORD ERRATI",
            "ERRORE UTENTE NON ABILITATO", "ERRASSNONCONS203",
            "ERRORE NEL REPORT", "ERRORE PASSWORD SCADUTA",
            "ERRORE QUANTITA' BUONI PASTO",
            "ATTESTATO BUONI PASTO NON MODIFICABILE"};
}
