SET DEFINE OFF;

Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSPROGETTIREST', 'D', 'Servizio REST per i Progetti', TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);
Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSGAEREST', 'D', 'Servizio REST per le GAE', TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);
Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSIMPEGNIREST', 'D', 'Servizio REST per gli impegni', TO_DATE('01/28/2015 09:27:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('01/28/2015 09:27:39', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);
Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSIMPEGNIGAEREST', 'D', 'Servizio REST per gli impegni/gae', TO_DATE('01/28/2015 09:27:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('01/28/2015 09:27:39', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);
Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSCAPITOLIREST', 'D', 'Servizio REST per il piano dei conti finanziario', TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('12/23/2014 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);
COMMIT;

Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSPROGETTIREST', 'ConsProgettiBP', 'V', TO_DATE('12/23/2014 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('12/23/2014 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);
Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSGAEREST', 'ConsGAEBP', 'V', TO_DATE('12/23/2014 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('12/23/2014 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);
Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSIMPEGNIREST', 'ConsImpegnoBP', 'V', TO_DATE('01/28/2015 09:31:00', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('01/28/2015 09:31:05', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);
Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSIMPEGNIGAEREST', 'ConsImpegnoGAEBP', 'V', TO_DATE('01/28/2015 09:31:00', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('01/28/2015 09:31:05', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);
Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSCAPITOLIREST', 'ConsCapitoloBP', 'V', TO_DATE('01/28/2015 09:31:00', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('01/28/2015 09:31:05', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);
COMMIT;

Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSPROGETTIREST', 'CNRTUTTO', TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 1);
Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSGAEREST', 'CNRTUTTO', TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 1);
Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSIMPEGNIREST', 'CNRTUTTO', TO_DATE('01/28/2015 10:12:11', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('01/28/2015 10:12:15', 'MM/DD/YYYY HH24:MI:SS'), 1);
Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSIMPEGNIGAEREST', 'CNRTUTTO', TO_DATE('01/28/2015 10:12:11', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('01/28/2015 10:12:15', 'MM/DD/YYYY HH24:MI:SS'), 1);

Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSCAPITOLIREST', 'CNRTUTTO', TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('12/23/2014 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 1);

COMMIT;

UPDATE UTENTE SET PASSWORD = 'SDQwMDQ1SzQ=', FL_AUTENTICAZIONE_LDAP = 'N', DT_ULTIMA_VAR_PASSWORD = NULL WHERE CD_UTENTE = 'MISSIONI';
COMMIT;

DROP VIEW PCIR009.V_CONS_OBBLIGAZIONI;

/* Formatted on 2015/02/20 14:32 (Formatter Plus v4.8.8) */
CREATE OR REPLACE FORCE VIEW pcir009.v_cons_obbligazioni (cd_cds,
                                                          esercizio,
                                                          cd_unita_organizzativa,
                                                          cd_cds_origine,
                                                          cd_uo_origine,
                                                          cd_elemento_voce,
                                                          ti_appartenenza,
                                                          ti_gestione,
                                                          esercizio_originale,
                                                          pg_obbligazione,
                                                          ds_obbligazione,
                                                          fl_pgiro,
                                                          im_scadenza_comp,
                                                          im_scadenza_res,
                                                          im_associato_doc_amm_comp,
                                                          im_associato_doc_amm_res,
                                                          im_pagato_comp,
                                                          im_pagato_res
                                                         )
AS
   SELECT
--
-- Date: 14/05/2007
-- Version: 1.1
--
-- History
--
-- Date: 18/07/2006
-- Version: 1.0
-- Gestione Impegni/Accertamenti Residui:
-- gestito il nuovo campo ESERCIZIO_ORIGINALE
--
-- Date: 14/05/2007
-- Version: 1.1
-- Modificata logica per distinguere
-- la competenza dai residui
--
-- Body
--
          obbligazione.cd_cds, obbligazione.esercizio,
          obbligazione.cd_unita_organizzativa, obbligazione.cd_cds_origine,
          obbligazione.cd_uo_origine, obbligazione.cd_elemento_voce,
          obbligazione.ti_appartenenza, obbligazione.ti_gestione,
          obbligazione.esercizio_originale, obbligazione.pg_obbligazione,
          obbligazione.ds_obbligazione,
          obbligazione.fl_pgiro,
          NVL
             (DECODE
                 (obbligazione.esercizio,
                  obbligazione.esercizio_originale, (SELECT NVL
                                                               (SUM
                                                                   (obbligazione_scadenzario.im_scadenza
                                                                   ),
                                                                0
                                                               )
                                                       FROM obbligazione_scadenzario
                                                      WHERE obbligazione_scadenzario.cd_cds =
                                                               obbligazione.cd_cds
                                                        AND obbligazione_scadenzario.esercizio =
                                                               obbligazione.esercizio
                                                        AND obbligazione_scadenzario.esercizio_originale =
                                                               obbligazione.esercizio_originale
                                                        AND obbligazione_scadenzario.pg_obbligazione =
                                                               obbligazione.pg_obbligazione),
                  0
                 ),
              0
             ) im_scadenza_comp,
          NVL
             (DECODE
                  (obbligazione.esercizio,
                   obbligazione.esercizio_originale, 0,
                   (SELECT NVL (SUM (obbligazione_scadenzario.im_scadenza), 0)
                      FROM obbligazione_scadenzario
                     WHERE obbligazione_scadenzario.cd_cds =
                                                           obbligazione.cd_cds
                       AND obbligazione_scadenzario.esercizio =
                                                        obbligazione.esercizio
                       AND obbligazione_scadenzario.esercizio_originale =
                                              obbligazione.esercizio_originale
                       AND obbligazione_scadenzario.pg_obbligazione =
                                                  obbligazione.pg_obbligazione)
                  ),
              0
             ) im_scadenza_res,
          NVL
             (DECODE
                 (obbligazione.esercizio,
                  obbligazione.esercizio_originale, (SELECT NVL
                                                               (SUM
                                                                   (obbligazione_scadenzario.im_associato_doc_amm
                                                                   ),
                                                                0
                                                               )
                                                       FROM obbligazione_scadenzario
                                                      WHERE obbligazione_scadenzario.cd_cds =
                                                               obbligazione.cd_cds
                                                        AND obbligazione_scadenzario.esercizio =
                                                               obbligazione.esercizio
                                                        AND obbligazione_scadenzario.esercizio_originale =
                                                               obbligazione.esercizio_originale
                                                        AND obbligazione_scadenzario.pg_obbligazione =
                                                               obbligazione.pg_obbligazione),
                  0
                 ),
              0
             ) im_associato_doc_amm_comp,
          NVL
             (DECODE
                 (obbligazione.esercizio,
                  obbligazione.esercizio_originale, 0,
                  (SELECT NVL
                             (SUM
                                 (obbligazione_scadenzario.im_associato_doc_amm
                                 ),
                              0
                             )
                     FROM obbligazione_scadenzario
                    WHERE obbligazione_scadenzario.cd_cds =
                                                           obbligazione.cd_cds
                      AND obbligazione_scadenzario.esercizio =
                                                        obbligazione.esercizio
                      AND obbligazione_scadenzario.esercizio_originale =
                                              obbligazione.esercizio_originale
                      AND obbligazione_scadenzario.pg_obbligazione =
                                                  obbligazione.pg_obbligazione)
                 ),
              0
             ) im_associato_doc_amm_res,
          NVL
             (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, (SELECT NVL
                                                                 (SUM
                                                                     (DECODE
                                                                         (mandato_riga.stato,
                                                                          'A', 0,
                                                                          mandato_riga.im_mandato_riga
                                                                         )
                                                                     ),
                                                                  0
                                                                 )
                                                         FROM mandato_riga
                                                        WHERE mandato_riga.cd_cds =
                                                                 obbligazione.cd_cds
                                                          AND mandato_riga.esercizio_obbligazione =
                                                                 obbligazione.esercizio
                                                          AND mandato_riga.esercizio_ori_obbligazione =
                                                                 obbligazione.esercizio_originale
                                                          AND mandato_riga.pg_obbligazione =
                                                                 obbligazione.pg_obbligazione),
                    0
                   ),
              0
             ) im_pagato_comp,
          NVL
             (DECODE
                    (obbligazione.esercizio,
                     obbligazione.esercizio_originale, 0,
                     (SELECT NVL (SUM (DECODE (mandato_riga.stato,
                                               'A', 0,
                                               mandato_riga.im_mandato_riga
                                              )
                                      ),
                                  0
                                 )
                        FROM mandato_riga
                       WHERE mandato_riga.cd_cds = obbligazione.cd_cds
                         AND mandato_riga.esercizio_obbligazione =
                                                        obbligazione.esercizio
                         AND mandato_riga.esercizio_ori_obbligazione =
                                              obbligazione.esercizio_originale
                         AND mandato_riga.pg_obbligazione =
                                                  obbligazione.pg_obbligazione)
                    ),
              0
             ) im_pagato_res
     FROM obbligazione;


DROP VIEW V_CONS_OBBLIGAZIONI_GAE;

/* Formatted on 2015/01/29 16:07 (Formatter Plus v4.8.8) */
CREATE OR REPLACE FORCE VIEW v_cons_obbligazioni_gae (cd_cds,
                                                              esercizio,
                                                              cd_unita_organizzativa,
                                                              cd_cds_origine,
                                                              cd_uo_origine,
                                                              cd_elemento_voce,
                                                              ti_appartenenza,
                                                              ti_gestione,
                                                              esercizio_originale,
                                                              pg_obbligazione,
                                                              ds_obbligazione,
                                                              cd_linea_attivita,
                                                              fl_pgiro,
                                                              im_scadenza_comp,
                                                              im_scadenza_res,
                                                              im_associato_doc_amm_comp,
                                                              im_associato_doc_amm_res,
                                                              im_pagato_comp,
                                                              im_pagato_res
                                                             )
AS
   SELECT
--
-- Date: 22/01/2015
-- Version: 1.0
--
-- View che visualizza gli importi per impegno/gae..recupera solo impegni con cd_tipo_documento_cont != 'IMP'
--
-- Body
--
            obbligazione.cd_cds, obbligazione.esercizio,
            obbligazione.cd_unita_organizzativa, obbligazione.cd_cds_origine,
            obbligazione.cd_uo_origine, obbligazione.cd_elemento_voce,
            obbligazione.ti_appartenenza, obbligazione.ti_gestione,
            obbligazione.esercizio_originale, obbligazione.pg_obbligazione,
            obbligazione.ds_obbligazione,
            obbligazione_scad_voce.cd_linea_attivita, obbligazione.fl_pgiro,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, (SELECT NVL
                                                                 (SUM
                                                                     (b.im_voce
                                                                     ),
                                                                  0
                                                                 )
                                                         FROM obbligazione_scadenzario,
                                                              obbligazione_scad_voce b
                                                        WHERE obbligazione_scadenzario.cd_cds =
                                                                 obbligazione.cd_cds
                                                          AND obbligazione_scadenzario.esercizio =
                                                                 obbligazione.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 obbligazione.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 obbligazione.pg_obbligazione
                                                          AND obbligazione_scadenzario.cd_cds =
                                                                      b.cd_cds
                                                          AND obbligazione_scadenzario.esercizio =
                                                                   b.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 b.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 b.pg_obbligazione
                                                          AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                                 b.pg_obbligazione_scadenzario
                                                          AND obbligazione_scad_voce.cd_linea_attivita =
                                                                 b.cd_linea_attivita),
                    0
                   ),
                0
               ) im_scadenza_comp,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, 0,
                    (SELECT NVL (SUM (b.im_voce), 0)
                       FROM obbligazione_scadenzario,
                            obbligazione_scad_voce b
                      WHERE obbligazione_scadenzario.cd_cds =
                                                           obbligazione.cd_cds
                        AND obbligazione_scadenzario.esercizio =
                                                        obbligazione.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                              obbligazione.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                  obbligazione.pg_obbligazione
                        AND obbligazione_scadenzario.cd_cds = b.cd_cds
                        AND obbligazione_scadenzario.esercizio = b.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                                         b.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                             b.pg_obbligazione
                        AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                 b.pg_obbligazione_scadenzario
                        AND obbligazione_scad_voce.cd_linea_attivita =
                                                           b.cd_linea_attivita)
                   ),
                0
               ) im_scadenza_res,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, (SELECT NVL
                                                                 (SUM
                                                                     (b.im_voce
                                                                     ),
                                                                  0
                                                                 )
                                                         FROM obbligazione_scadenzario,
                                                              obbligazione_scad_voce b
                                                        WHERE obbligazione_scadenzario.cd_cds =
                                                                 obbligazione.cd_cds
                                                          AND obbligazione_scadenzario.esercizio =
                                                                 obbligazione.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 obbligazione.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 obbligazione.pg_obbligazione
                                                          AND obbligazione_scadenzario.cd_cds =
                                                                      b.cd_cds
                                                          AND obbligazione_scadenzario.im_associato_doc_amm =
                                                                 obbligazione_scadenzario.im_scadenza
                                                          AND obbligazione_scadenzario.esercizio =
                                                                   b.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 b.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 b.pg_obbligazione
                                                          AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                                 b.pg_obbligazione_scadenzario
                                                          AND obbligazione_scad_voce.cd_linea_attivita =
                                                                 b.cd_linea_attivita),
                    0
                   ),
                0
               ) im_associato_doc_amm_comp,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, 0,
                    (SELECT NVL (SUM (b.im_voce), 0)
                       FROM obbligazione_scadenzario,
                            obbligazione_scad_voce b
                      WHERE obbligazione_scadenzario.cd_cds =
                                                           obbligazione.cd_cds
                        AND obbligazione_scadenzario.esercizio =
                                                        obbligazione.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                              obbligazione.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                  obbligazione.pg_obbligazione
                        AND obbligazione_scadenzario.cd_cds = b.cd_cds
                        AND obbligazione_scadenzario.im_associato_doc_amm =
                                          obbligazione_scadenzario.im_scadenza
                        AND obbligazione_scadenzario.esercizio = b.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                                         b.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                             b.pg_obbligazione
                        AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                 b.pg_obbligazione_scadenzario
                        AND obbligazione_scad_voce.cd_linea_attivita =
                                                           b.cd_linea_attivita)
                   ),
                0
               ) im_associato_doc_amm_res,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, (SELECT NVL
                                                                 (SUM
                                                                     (b.im_voce
                                                                     ),
                                                                  0
                                                                 )
                                                         FROM obbligazione_scadenzario,
                                                              obbligazione_scad_voce b
                                                        WHERE obbligazione_scadenzario.cd_cds =
                                                                 obbligazione.cd_cds
                                                          AND obbligazione_scadenzario.esercizio =
                                                                 obbligazione.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 obbligazione.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 obbligazione.pg_obbligazione
                                                          AND obbligazione_scadenzario.cd_cds =
                                                                      b.cd_cds
                                                          AND obbligazione_scadenzario.im_associato_doc_contabile =
                                                                 obbligazione_scadenzario.im_scadenza
                                                          AND obbligazione_scadenzario.esercizio =
                                                                   b.esercizio
                                                          AND obbligazione_scadenzario.esercizio_originale =
                                                                 b.esercizio_originale
                                                          AND obbligazione_scadenzario.pg_obbligazione =
                                                                 b.pg_obbligazione
                                                          AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                                 b.pg_obbligazione_scadenzario
                                                          AND obbligazione_scad_voce.cd_linea_attivita =
                                                                 b.cd_linea_attivita),
                    0
                   ),
                0
               ) im_associato_doc_cont_comp,
            NVL
               (DECODE
                   (obbligazione.esercizio,
                    obbligazione.esercizio_originale, 0,
                    (SELECT NVL (SUM (b.im_voce), 0)
                       FROM obbligazione_scadenzario,
                            obbligazione_scad_voce b
                      WHERE obbligazione_scadenzario.cd_cds =
                                                           obbligazione.cd_cds
                        AND obbligazione_scadenzario.esercizio =
                                                        obbligazione.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                              obbligazione.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                  obbligazione.pg_obbligazione
                        AND obbligazione_scadenzario.cd_cds = b.cd_cds
                        AND obbligazione_scadenzario.im_associato_doc_contabile =
                                          obbligazione_scadenzario.im_scadenza
                        AND obbligazione_scadenzario.esercizio = b.esercizio
                        AND obbligazione_scadenzario.esercizio_originale =
                                                         b.esercizio_originale
                        AND obbligazione_scadenzario.pg_obbligazione =
                                                             b.pg_obbligazione
                        AND obbligazione_scadenzario.pg_obbligazione_scadenzario =
                                                 b.pg_obbligazione_scadenzario
                        AND obbligazione_scad_voce.cd_linea_attivita =
                                                           b.cd_linea_attivita)
                   ),
                0
               ) im_associato_doc_cont_res
       FROM obbligazione, obbligazione_scad_voce
      WHERE obbligazione.cd_cds = obbligazione_scad_voce.cd_cds
        AND obbligazione.esercizio = obbligazione_scad_voce.esercizio
        AND obbligazione.esercizio_originale =
                                    obbligazione_scad_voce.esercizio_originale
        AND obbligazione.pg_obbligazione =
                                        obbligazione_scad_voce.pg_obbligazione
        AND obbligazione.cd_tipo_documento_cont != 'IMP'
   GROUP BY obbligazione.cd_cds,
            obbligazione.esercizio,
            obbligazione.cd_unita_organizzativa,
            obbligazione.cd_cds_origine,
            obbligazione.cd_uo_origine,
            obbligazione.cd_elemento_voce,
            obbligazione.ti_appartenenza,
            obbligazione.ti_gestione,
            obbligazione.esercizio_originale,
            obbligazione.pg_obbligazione,
            obbligazione.ds_obbligazione,
            obbligazione.fl_pgiro,
            obbligazione_scad_voce.cd_linea_attivita;


Insert into ACCESSO
   (CD_ACCESSO, TI_ACCESSO, DS_ACCESSO, DUVA, UTUV, 
    DACR, UTCR, PG_VER_REC)
 Values
   ('CONSTERZOREST', 'D', 'Servizio REST per i Terzi', TO_DATE('04/13/2016 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('04/13/2016 16:02:32', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1);

Insert into ASS_BP_ACCESSO
   (CD_ACCESSO, BUSINESS_PROCESS, TI_FUNZIONE, DACR, UTCR, 
    DUVA, UTUV, PG_VER_REC, ESERCIZIO_INIZIO_VALIDITA, ESERCIZIO_FINE_VALIDITA)
 Values
   ('CONSTERZOREST', 'ConsTerzoRestBP', 'V', TO_DATE('04/13/2016 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 
    TO_DATE('04/13/2016 16:02:41', 'MM/DD/YYYY HH24:MI:SS'), '$$$$$MIGRAZIONE$$$$$', 1, NULL, NULL);

Insert into RUOLO_ACCESSO
   (CD_RUOLO, CD_ACCESSO, UTUV, DACR, UTCR, 
    DUVA, PG_VER_REC)
 Values
   ('MISSIONI', 'CONSTERZOREST', 'CNRTUTTO', TO_DATE('04/13/2016 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 'CNRTUTTO', 
    TO_DATE('04/13/2016 16:03:38', 'MM/DD/YYYY HH24:MI:SS'), 1);

COMMIT;
