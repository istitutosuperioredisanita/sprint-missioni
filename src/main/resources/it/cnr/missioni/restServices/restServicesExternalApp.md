# REST SERVICES DI ALTRE APPLICAZIONI

----
## DATI UTENTE

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIPER
    Dove per SIPER si intende l'applicazione che gestisce i dati del personale
    
**PARAMS:(Query String Parameter) ** 
    proxyURL=json/userinfo/gianfranco.gasparro

**JSON DI RISPOSTA ** 

    {"codice_sede":"",codice_uo":"",livello_profilo":"",matricola":"",nome":"",cognome":"",codice_fiscale":"",sesso":"",data_nascita":"2100-00-00T00:00:00.000+01:00",comune_nascita":"",provincia_nascita":"",nazione_nascita":"",fl_cittadino_italiano":true,
    "indirizzo_residenza":"",num_civico_residenza":"",cap_residenza":"",comune_residenza":"",provincia_residenza":"",nazione_residenza":"",indirizzo_comunicazioni":"",num_civico_comunicazioni":"",cap_comunicazioni":"",comune_comunicazioni":"",provincia_comunicazioni":"",nazione_comunicazioni":"",telefono_comunicazioni":"",email_comunicazioni":"",profilo":"",struttura_appartenenza":"",uid":"",sigla_sede":"",citta_sede":""}

----
## NAZIONE

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsNazioneAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"ds_nazione","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"ti_nazione","operator":"!=","fieldValue":"I"}]}

Con questi parametri ritorna tutte le nazioni esclusa l'Italia

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"pg_nazione":63,"ds_nazione":"ABU DHABI (EMIRATI ARABI UNITI)","cd_area_estera":"E","ti_nazione":"E"}]}

----
## Centri di Spesa

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsCdSAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"cd_proprio_unita","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"esercizio_fine","operator":">=","fieldValue":2019}]}

Con questi parametri ritorna tutti i CDS di spesa attivi dal 2019

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_proprio_unita":"000","cd_tipo_unita":"SAC","ds_unita_organizzativa":"STRUTTURA AMMINISTRATIVA CENTRALE","cd_responsabile":"1"}]}

----
## UNITA' ORGANIZZATIVE

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsUnitaOrganizzativaAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"condition":"AND","fieldName":"cd_unita_padre","operator":"=","fieldValue":"000"},{"condition":"AND","fieldName":"esercizio_fine","operator":">=","fieldValue":2019}]}

Con questi parametri ritorna tutti LE UO del CDS di spesa 000 attive dal 2019

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_tipo_unita":"SAC","cd_unita_organizzativa":"000.000","ds_unita_organizzativa":"COMPLESSO DEGLI UFFICI DI DIRETTA COLLABORAZIONE","cd_responsabile":"1","cd_unita_padre":"000","fl_uo_cds":"true","esercizio_fine":2100}]}


----
## CDR

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsCdRAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"cd_centro_responsabilita","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"cd_unita_organizzativa","operator":"=","fieldValue":"000.006"}]}


Con questi parametri ritorna tutti i CDR della uo 000.006

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_centro_responsabilita":"000.006.000","cd_unita_organizzativa":"000.006","ds_cdr":"UDC - Ufficio per i rapporti istituzionali","cd_responsabile":"1"}]}

----
## VOCE DI BILANCIO

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsCapitoloAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"cd_elemento_voce","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"esercizio","operator":"=","fieldValue":2019},{"condition":"AND","fieldName":"ti_gestione","operator":"=","fieldValue":"S"},{"condition":"AND","fieldName":"ti_elemento_voce","operator":"=","fieldValue":"C"},{"condition":"AND","fieldName":"fl_solo_residuo","operator":"=","fieldValue":false},{"condition":"AND","fieldName":"fl_missioni","operator":"=","fieldValue":true},{"condition":"AND","fieldName":"ti_appartenenza","operator":"=","fieldValue":"D"}]}

Con questi parametri ritorna tutte le voci di spesa del 2019 che possono essere usate per le missioni

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_elemento_voce":"13030","esercizio":2019,"ds_elemento_voce":"Rimborso spese di missione e trasferta del personale dipendente","fl_solo_residuo":false}]}

----
## PROGETTO

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsProgettiAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"cd_progetto","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"livello","operator":"=","fieldValue":2},{"condition":"AND","fieldName":"fl_utilizzabile","operator":"=","fieldValue":true},{"condition":"AND","fieldName":"esercizio","operator":"=","fieldValue":2019},{"condition":"AND","fieldName":"cd_unita_organizzativa","operator":"=","fieldValue":"000.006"}]}

Con questi parametri ritorna tutti i progetti del 2019 che possono essere usati dalla uo 000.006

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_progetto":"DBA.GP001.001","pg_progetto":21111,"ds_progetto":"Gestione delle spese accentrate","dt_inizio":1451602800000,"stato":"A","esercizio":2019,"cd_unita_organizzativa":"000.006"}]}

----
## GAE

**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsGAEAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"orderBy":[{"name":"cd_linea_attivita","type":"ASC"}],"clauses":[{"condition":"AND","fieldName":"esercizio","operator":"=","fieldValue":2019},{"condition":"AND","fieldName":"pg_progetto","operator":"=","fieldValue":21111},{"condition":"AND","fieldName":"cd_centro_responsabilita","operator":"=","fieldValue":"000.006.000"},{"condition":"AND","fieldName":"ti_gestione","operator":"=","fieldValue":"S"},{"condition":"AND","fieldName":"cd_centro_responsabilita","operator":"LIKE","fieldValue":"000%"}]}


Con questi parametri ritorna tutte le GAE (sottoprogetti) del 2019 del progetto con id 21111

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_linea_attivita":"C0000020","cd_centro_responsabilita":"000.006.000","pg_progetto":21111,"esercizio_inizio":2003,"esercizio_fine":2100,"cd_responsabile_terzo":1}]}

----
## IMPEGNO


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsImpegnoAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":1000,"orderBy":[{"name":"esercizio","type":"DESC"}],"clauses":[{"condition":"AND","fieldName":"esercizio","operator":"=","fieldValue":2019},{"condition":"AND","fieldName":"cdCds","operator":"=","fieldValue":"000"},{"condition":"AND","fieldName":"cdUnitaOrganizzativa","operator":"=","fieldValue":"000.006"},{"condition":"AND","fieldName":"esercizioOriginale","operator":"=","fieldValue":2018},{"condition":"AND","fieldName":"pgObbligazione","operator":"=","fieldValue":940}]}

Con questi parametri ritorna i dati dell'impegno 2018/940

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":1000,"activePage":0,"elements":[{"cdCds":"000","esercizio":2019,"cdUnitaOrganizzativa":"000.006","esercizioOriginale":2018,"cdCdsOrigine":"000","pgObbligazione":940,"dsObbligazione":"Rimborso spese missione - personale dipendente ","cdUoOrigine":"000.006","cdElementoVoce":"13030","tiAppartenenza":"D","tiGestione":"S","flPgiro":false,"imScadenzaComp":0,"imScadenzaRes":182689.44,"imAssociatoDocAmmComp":0,"imAssociatoDocAmmRes":182684.51,"imPagatoComp":0,"imPagatoRes":182251.85}]}


----
## TERZO


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsTerzoAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"anagrafico.codice_fiscale","operator":"=","fieldValue":"GSPGFR76E31F839Z"},{"condition":"AND","fieldName":"dt_fine_rapporto","operator":"isnull"}]}

Con questi parametri ritorna i dati del terzo con il codice fiscale GSPGFR76E31F839Z

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_terzo":184076,"cd_anag":174071,"denominazione_sede":"GASPARRO GIANFRANCO","codice_fiscale_anagrafico":"GSPGFR76E31F839Z","partita_iva_anagrafico":null,"dt_fine_rapporto":null,"descrizioneAnagrafica":"GIANFRANCO GASPARRO  ","italianoEstero":"I"}]}


----
## DATI IBAN


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsBancaAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"cd_terzo","operator":"=","fieldValue":184076},{"condition":"AND","fieldName":"ti_pagamento","operator":"=","fieldValue":"B"},{"condition":"AND","fieldName":"fl_cancellato","operator":"=","fieldValue":false}]}


Con questi parametri ritorna i dati banca del terzo con id 184076

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"pg_banca":2,"ti_pagamento":"B","abi":"","cab":"","numero_conto":"","intestazione":"GASPARRO GIANFRANCO","codice_iban":"","codice_swift":null,"fl_cancellato":false,"fl_cc_cds":false,"cin":"B","quietanza":null,"cd_terzo_delegato":null,"ds_terzo_delegato":null,"ds_abicab":""}]}

----
## DATI INQUADRAMENTO


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsInquadramentoAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"cd_anag","operator":"=","fieldValue":174071}]}

Con questi parametri ritorna i dati dell'inquadramento con id anagrafico 174071

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"dt_ini_validita":1391036400000,"cd_tipo_rapporto":"DIP","ds_inquadramento":null,"pg_rif_inquadramento":14,"cd_anag":174071,"dt_fin_validita":1451516400000}]}


----
## DATI MODALITA PAGAMENTO


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsModalitaPagamentoAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"cd_terzo","operator":"=","fieldValue":184076}]}

Con questi parametri ritorna i dati delle modalità di pagamento con id anagrafico 184076

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_modalita_pag":"BO","ti_pagamento":"B","ds_modalita_pag":"Bonifico su conto corrente bancario o postale"}]}


----
## DATI DIVISA


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsDivisaAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"cd_divisa","operator":"=","fieldValue":"EURO"}]}

Con questi parametri ritorna i dati della divisa euro

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_divisa":"EURO","ds_divisa":"EURO","precisione":2,"fl_calcola_con_diviso":false}]}


----
## DATI DIVISA


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsDivisaAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"cd_divisa","operator":"=","fieldValue":"EURO"}]}

Con questi parametri ritorna i dati della divisa euro

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_divisa":"EURO","ds_divisa":"EURO","precisione":2,"fl_calcola_con_diviso":false}]}


----
## DATI TIPO SPESA


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsMissioneTipoSpesaAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"nazione","operator":"=","fieldValue":1},{"condition":"AND","fieldName":"condizioneTipiSpesaMissione","operator":"=","fieldValue":"S"},{"condition":"AND","fieldName":"inquadramento","operator":"=","fieldValue":14},{"condition":"AND","fieldName":"data","operator":"=","fieldValue":"11/09/2019"},{"condition":"AND","fieldName":"ammissibileRimborso","operator":"=","fieldValue":false}]}

Con questi parametri ritorna i dati dei tipi spesa ammissibili alla data per l'inquadramento del terzo

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"pg_rif_inquadramento":0,"cd_ti_spesa":"PASTO","ds_ti_spesa":"RIMBORSO SPESE PASTO","ti_area_geografica":"I","pg_nazione":1,"ds_nazione":"ITALIA","dt_inizio_validita":915145200000,"dataFineValidita":null,"fl_giustificativo_richiesto":true,"fl_pasto":true,"fl_trasporto":false,"fl_rimborso_km":false,"fl_alloggio":false,"cd_divisa":"EURO","ds_divisa":"EURO","percentuale_maggiorazione":0,"limite_max_spesa":999999999,"dt_cancellazione":null,"fl_ammissibile_con_rimborso":false}]}


----
## DATI TIPO PASTO


**URL: ** 
    https://missioni.cnr.it/api/proxy/SIGLA/
    Dove per SIGLA si intende l'applicazione che gestisce i dati della contabilità

**PARAMS: ** 
    Query String Parameters: proxyURL=ConsMissioneTipoPastoAction.json
    Request payload: {"activePage":0,"maxItemsPerPage":4999,"clauses":[{"condition":"AND","fieldName":"nazione","operator":"=","fieldValue":1},{"condition":"AND","fieldName":"condizioneTipiPastoMissione","operator":"=","fieldValue":"S"},{"condition":"AND","fieldName":"inquadramento","operator":"=","fieldValue":14},{"condition":"AND","fieldName":"data","operator":"=","fieldValue":"11/09/2019"}]}

Con questi parametri ritorna i dati dei tipi pasto ammissibili alla data per l'inquadramento del terzo

**JSON DI RISPOSTA ** 

{"totalNumItems":1,"maxItemsPerPage":4999,"activePage":0,"elements":[{"cd_ti_pasto":"GIORNALIERO","limite_max_pasto":44.26,"ti_area_geografica":"I","pg_nazione":1,"ds_nazione":"ITALIA","cd_divisa":"EURO","ds_divisa":"EURO","inquadramento":14,"dt_inizio_validita":1009839600000,"dataFineValidita":2556054000000,"dt_cancellazione":null,"cd_area_estera":"*","ds_area_estera":"Area Estera non definita"}]}

