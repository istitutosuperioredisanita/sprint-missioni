# REST SERVICES SIGLA

Tutti i servizi REST di SIGLA elencati sono richiamabili con i seguenti metodi:

**GET:** Ritorna i nomi e i type degli attributi. Utilizzando questo metodo non sono necessari i "PARAMS".

**POST:** Ritorna i dati richiesti filtrati dai "PARAMS" indicati.

----
## UNITA' ORGANIZZATIVA

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsUnitaOrganizzativaAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"cd_proprio_unita","operator":"=","fieldValue":"000"}, {"condition": "AND","fieldName": "esercizio_fine", "operator": ">=","fieldValue": "2015"}], "orderBy" : [{"name": "cd_unita_organizzativa", "type": "ASC"}]}

Con questi parametri ritorna tutte le unità organizzative del CDS 000 valide nel 2015

----
## CDS

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsCdSAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition": "AND","fieldName": "esercizio_fine", "operator": ">=","fieldValue": "2015"}], "orderBy" : [{"name": "cd_proprio_unita", "type": "ASC"}]}

Con questi parametri ritorna tutte i CDS validi nel 2015

----
## NAZIONE

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsNazioneAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"ti_nazione","operator":"!=","fieldValue":"I"}], "orderBy" : [{"name": "ds_nazione", "type": "ASC"}]}

Con questi parametri ritorna tutte le nazioni esclusa l'Italia

----
## CDR

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsCdRAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"cd_unita_organizzativa","operator":"=","fieldValue":"000.411"}], "orderBy" : [{"name": "cd_centro_responsabilita", "type": "ASC"}]}

Con questi parametri ritorna tutti i CDR della UO 000.411

----
## VOCE DI BILANCIO

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsCapitoloAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"ti_gestione","operator":"=","fieldValue":"S"}, {"condition":"AND","fieldName":"ti_elemento_voce","operator":"=","fieldValue":"C"}, {"condition":"AND","fieldName":"ti_appartenenza","operator":"=","fieldValue":"D"}, {"condition": "AND","fieldName": "esercizio", "operator": "=","fieldValue": "2015"}], "orderBy" : [{"name": "cd_elemento_voce", "type": "ASC"}], "context":{"esercizio": 2015, "cd_cds": "000", "cd_unita_organizzativa": "000.411", "cd_cdr": "000.411.000"}}

Con questi parametri ritorna tutte le Voci di bilancio utilizzabili nel 2015

----
## GAE

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsGAEAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"ti_gestione","operator":"=","fieldValue":"S"}, {"condition":"AND","fieldName":"cd_centro_responsabilita","operator":"=","fieldValue":"000.411.000"}, {"condition":"AND","fieldName":"pg_progetto","operator":"=","fieldValue":"5233"}, {"condition": "AND","fieldName": "esercizio_fine", "operator": ">=","fieldValue": "2015"}, {"condition": "AND","fieldName": "esercizio_inizio", "operator": "<=","fieldValue": "2015"}], "orderBy" : [{"name": "cd_linea_attivita", "type": "ASC"}], "context":{"esercizio": 2015, "cd_cds": "000", "cd_unita_organizzativa": "000.411", "cd_cdr": "000.411.000"}}

Con questi parametri ritorna tutte le GAE di gestione del Centro di Responsabilità 000.411.000 e del progetto con identificativo 5233 utilizzabili nel 2015

----
## PROGETTO

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsProgettiAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"cd_unita_organizzativa","operator":"=","fieldValue":"000.411"}, {"condition":"AND","fieldName":"livello","operator":"=","fieldValue":"3"}, {"condition": "AND","fieldName": "esercizio", "operator": "=","fieldValue": "2015"}], "orderBy" : [{"name": "cd_progetto", "type": "ASC"}], "context":{"esercizio": 2015, "cd_cds": "000", "cd_unita_organizzativa": "000.411", "cd_cdr": "000.411.000"}}

Con questi parametri ritorna tutti i progetti della UO 000.411 utilizzabili nel 2015

----
## IMPEGNO

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsImpegnoAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"cdCds","operator":"=","fieldValue":"000"}, {"condition": "AND","fieldName": "esercizio_originale", "operator": "=","fieldValue": "2015"}, {"condition": "AND","fieldName": "esercizio", "operator": "=","fieldValue": "2015"}], "orderBy" : [{"name": "pgObbligazione", "type": "ASC"}], "context":{"esercizio": 2015, "cd_cds": "000", "cd_unita_organizzativa": "000.411", "cd_cdr": "000.411.000"}}

Con questi parametri ritorna tutti gli impegni di competenza del CDS 000 del 2015

----
## IMPEGNO CON GAE

**URL: ** 
    https://contab.cnr.it/SIGLA/ConsImpegnoGaeAction.json

**PARAMS: ** 

    {"activePage" : 0, "maxItemsPerPage" : 10, "clauses":[{"condition":"AND","fieldName":"cdCds","operator":"=","fieldValue":"000"}, {"condition": "AND","fieldName": "cdLineaAttivita", "operator": "=","fieldValue": "P0000004"}, {"condition": "AND","fieldName": "esercizio_originale", "operator": "=","fieldValue": "2015"}, {"condition": "AND","fieldName": "esercizio", "operator": "=","fieldValue": "2015"}], "orderBy" : [{"name": "pgObbligazione", "type": "ASC"}], "context":{"esercizio": 2015, "cd_cds": "000", "cd_unita_organizzativa": "000.411", "cd_cdr": "000.411.000"}}

Con questi parametri ritorna tutti gli impegni di competenza con GAE P0000004 del CDS 000 del 2015
