'use strict';

missioniApp.factory('ProxyService', function($http, COSTANTI, APP_FOR_REST, SIGLA_REST, SIPER_REST, URL_REST, ui, Session, DirettoreUoService) {
    var today = new Date();
    var annoA = today.getFullYear();
    var calcoloAnnoDa = function(){
        annoDa = today.getFullYear();
        var meseAttuale = today.getMonth();
        if (meseAttuale < 4){
             annoDa = annoDa- 1;
        }
    }

    var annoDa = calcoloAnnoDa();

    var recuperoUo = function(anno, cds, uoRich){
        var urlRestProxy = URL_REST.STANDARD;
        var uos = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.UO;
        var objectPostUoOrderBy = [{name: 'cd_unita_organizzativa', type: 'ASC'}];
        var objectPostUoClauses = null;
        if (cds){
            objectPostUoClauses = [{condition: 'AND', fieldName: 'cd_unita_padre', operator: "=", fieldValue:cds},
                                    {condition: 'AND', fieldName: 'esercizio_fine', operator: ">=", fieldValue:anno}];
        } else {
            objectPostUoClauses = [{condition: 'AND', fieldName: 'esercizio_fine', operator: ">=", fieldValue:anno}];
        }
        var objectPostUo = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostUoOrderBy, clauses:objectPostUoClauses}
        return $http.post(urlRestProxy + app+'/', objectPostUo, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                if (data.elements){
                    var listaUo = data.elements;
                    if (uoRich){
                        uos = [];
                        var ind = -1;
                        for (var i=0; i<listaUo.length; i++) {
                            if (listaUo[i].cd_unita_organizzativa === uoRich){
                                ind ++;
                                uos[ind] = listaUo[i];
                            }
                        }

                        for (var i=0; i<listaUo.length; i++) {
                            if (listaUo[i].cd_unita_organizzativa != uoRich){
                                ind ++;
                                uos[ind] = listaUo[i];
                            }
                        }
                        return uos;
                    }
                } else {
                    return uos;
                }
            } else {
                return uos;
            }
        }).error(function (data) {
        });
    }

    var estraiUo = function(codice){
        return codice.substring(0,3)+'.'+codice.substring(3,6);
    }

    var estraiUoRichFromAccount = function(account){
        if (account.codice_uo){
            return estraiUo(account.codice_uo);
        }
        return "";
    }

    var isPersonaGiaPresente = function(persons, codice_fiscale){
        for (var i=0; i<persons.length; i++) {
            if (persons[i].codice_fiscale == codice_fiscale){
                return true;
            }
        }
        return false;
    }

    var recuperoDatiPerson = function(username){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.SIPER;
        var url = SIPER_REST.GET_PERSON;
        var x = $http.get('api/proxy/SIPER?proxyURL=json/userinfo/'+ username);
        var y = x.then(function (result) {
            if (result.data){
                return createPerson(result.data);
            } else {
                return [];
            }
        });
        x.error(function (data) {
        });
        return y;
    }

    var recuperoPersonsForUo = function(uo, soloDipendenti){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.SIPER;
        var url = SIPER_REST.PERSONS_FOR_UO;
        var persons = [];
        var uoSiper = uo.replace('.','');
        var x = $http.get(urlRestProxy + app +'?proxyURL=json/sedi/', {params: {titCa: uoSiper, userinfo:true, cessati:true}});
        var y = x.then(function (result) {
            if (result.data){
                var listaPersons = result.data;
                var ind = -1;

                var personPromise = DirettoreUoService.getDirettore(uoSiper);
                return personPromise.then(function(result){
                    if (result && result.data){
                        var direttore = result.data;
                        var trovatoDirettore = false;
                        for (var z=0; z<persons.length; z++) {
                            if (persons[z].uid == direttore.uid){
                                trovatoDirettore = true;
                            }
                        }
                        if (!trovatoDirettore){
                            listaPersons.push(direttore);
                        }
                    }
                    for (var k=0; k<listaPersons.length; k++) {

/*                        recuperoDatiInquadramentoCf(listaPersons[k].codice_fiscale, annoDa, annoA).then(function(ret){
                            if (ret && ret.data && ret.data.elements && ret.data.elements.length > 0){
                                $scope.inquadramento = ret.data.elements;
                            } else {
                                ui.error("Inquadramento non trovato");
                            }
                        }

*/
                        if ((soloDipendenti && listaPersons[k].matricola) || !soloDipendenti){
                            var person = null;
                            var cognome = null;
                            var cf = null;
                            var nome = null;
                            for (var i=0; i<listaPersons.length; i++) {
                                if ((soloDipendenti && listaPersons[i].matricola) || !soloDipendenti){
                                    if ((ind == -1 || !isPersonaGiaPresente(persons, listaPersons[i].codice_fiscale)) && (cognome == null || 
                                        (listaPersons[i].cognome < cognome && 
                                            (ind == -1 || cognome > persons[ind].cognome || (cognome == persons[ind].cognome && nome > persons[ind].nome) )) || 
                                        (listaPersons[i].cognome == cognome && listaPersons[i].nome < nome && 
                                            (ind == -1 || cognome > persons[ind].cognome || (cognome == persons[ind].cognome && nome > persons[ind].nome) )) || 
                                        (listaPersons[i].cognome == cognome && listaPersons[i].nome == nome && listaPersons[i].codice_fiscale < cf ) ) ) {
                                        person = listaPersons[i];
                                        cognome = person.cognome;
                                        nome = person.nome;
                                        cf = person.codice_fiscale;
                                    }
                                }
                            }
                            if (person != null){
                                ind ++;
                                persons[ind] = person;
                            }
                        }
                    }
                    return persons;
                });
            } else {
                return [];
            }
        });
        x.error(function (data) {
        });
        return y;
    }

    var createPerson = function(data){
        var userWork = {};
        userWork.login = data.uid;
        userWork.matricola = data.matricola;
        userWork.firstName = data.nome;
        userWork.lastName = data.cognome;
        userWork.email = data.email_comunicazioni;
        userWork.userRoles = ['ROLE_USER'];
        userWork.comune_nascita = data.comune_nascita; 
        userWork.data_nascita = data.data_nascita;
        userWork.comune_residenza = data.comune_residenza; 
        userWork.indirizzo_residenza = data.indirizzo_residenza; 
        userWork.num_civico_residenza = data.num_civico_residenza;
        if (userWork.num_civico_residenza){
            userWork.indirizzo_completo_residenza = data.indirizzo_residenza+" "+data.num_civico_residenza;
        } else {
            userWork.indirizzo_completo_residenza = data.indirizzo_residenza;
        }
        userWork.cap_residenza = data.cap_residenza;
        userWork.provincia_residenza = data.provincia_residenza;
        userWork.codice_fiscale = data.codice_fiscale;
        userWork.profilo = data.profilo;
        userWork.struttura_appartenenza = data.struttura_appartenenza;
        userWork.codice_sede = data.codice_sede;
        userWork.codice_uo = data.codice_uo;
        userWork.livello = data.livello_profilo;
        userWork.allUoForUsersSpecial = data.allUoForUsersSpecial;
        userWork.uoForUsersSpecial = data.uoForUsersSpecial;
        userWork.isAccountLDAP = true;
        return userWork;
    }


    var recuperoDatiInquadramento = function(cdAnag){
        var urlRestProxy = URL_REST.STANDARD;
        var inq = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.INQUADRAMENTO;
        var objectPostInqClauses = [{condition: 'AND', fieldName: 'cd_anag', operator: "=", fieldValue:cdAnag}];
        var objectPostInq = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostInqClauses}
        return $http.post(urlRestProxy + app+'/', objectPostInq, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                inq = data.elements;
            }
            return inq;
        }).error(function (data) {
        });
    }

    var recuperoDatiInquadramentoCf = function(cf, annoDa, annoA){
        var urlRestProxy = URL_REST.STANDARD;
        var inq = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.INQUADRAMENTO;
        var objectPostInqClauses = [{condition: 'AND', fieldName: 'cf', operator: "=", fieldValue:cf},
                                    {condition: 'AND', fieldName: 'annoDa', operator: "=", fieldValue:annoDa},
                                    {condition: 'AND', fieldName: 'annoA', operator: "=", fieldValue:annoA}];
        var objectPostInq = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostInqClauses}
        return $http.post(urlRestProxy + app+'/', objectPostInq, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                inq = data.elements;
            }
            return inq;
        }).error(function (data) {
        });
    }

    var recuperoModalitaPagamento = function(cdTerzo){
        var urlRestProxy = URL_REST.STANDARD;
        var inq = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.MOD_PAGAMENTO;
        var objectPostInqClauses = [{condition: 'AND', fieldName: 'cd_terzo', operator: "=", fieldValue:cdTerzo}];
        var objectPostInq = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostInqClauses}
        return $http.post(urlRestProxy + app+'/', objectPostInq, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                inq = data.elements;
            }
            return inq;
        }).error(function (data) {
        });
    }

    var recuperoMandato = function(cdTerzo, annoMandato, numeroMandato){
        var urlRestProxy = URL_REST.STANDARD;
        var man = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.MANDATO;
        var objectPostManClauses = [{condition: 'AND', fieldName: 'cd_tipo_documento_cont', operator: "=", fieldValue:"MAN"},
                                    {condition: 'AND', fieldName: 'soloAnticipi', operator: "=", fieldValue:"S"},
                                    {condition: 'AND', fieldName: 'stato', operator: "=", fieldValue:"P"},
                                    {condition: 'AND', fieldName: 'cd_terzo', operator: "=", fieldValue:cdTerzo},
                                    {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:annoMandato},
                                    {condition: 'AND', fieldName: 'pg_documento_cont', operator: "=", fieldValue:numeroMandato}];
        var objectPostMan = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostManClauses}
        return $http.post(urlRestProxy + app+'/', objectPostMan, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                man = data.elements;
            }
            return man;
        }).error(function (data) {
        });
    }

    var recuperoTipoSpesa = function(inquadramento, data, nazione, trattamento){
        var urlRestProxy = URL_REST.STANDARD;
        var tipiSpesa = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.TIPO_SPESA;
        var ammissibileRimborso = false;
        if (trattamento === 'T'){
            ammissibileRimborso = true;
        }
        var objectPostTipoSpesaClauses = [{condition: 'AND', fieldName: 'nazione', operator: "=", fieldValue:nazione},
                                    {condition: 'AND', fieldName: 'condizioneTipiSpesaMissione', operator: "=", fieldValue:"S"},
                                    {condition: 'AND', fieldName: 'inquadramento', operator: "=", fieldValue:inquadramento},
                                    {condition: 'AND', fieldName: 'data', operator: "=", fieldValue:data},
                                    {condition: 'AND', fieldName: 'ammissibileRimborso', operator: "=", fieldValue:ammissibileRimborso}];
        var objectPostTipoSpesa = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostTipoSpesaClauses}
        return $http.post(urlRestProxy + app+'/', objectPostTipoSpesa, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                tipiSpesa = data.elements;
            }
            return tipiSpesa;
        }).error(function (data) {
        });
    }

    var recuperoTipoPasto = function(inquadramento, data, nazione){
        var urlRestProxy = URL_REST.STANDARD;
        var tipiPasto = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.TIPO_PASTO;
        var objectPostTipoPastoClauses = [{condition: 'AND', fieldName: 'nazione', operator: "=", fieldValue:nazione},
                                    {condition: 'AND', fieldName: 'condizioneTipiPastoMissione', operator: "=", fieldValue:"S"},
                                    {condition: 'AND', fieldName: 'inquadramento', operator: "=", fieldValue:inquadramento},
                                    {condition: 'AND', fieldName: 'data', operator: "=", fieldValue:data}];
        var objectPostTipoPasto = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostTipoPastoClauses}
        return $http.post(urlRestProxy + app+'/', objectPostTipoPasto, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                tipiPasto = data.elements;
            }
            return tipiPasto;
        }).error(function (data) {
        });
    }

    var recuperoRimborsoKm = function(tipoAuto, data, nazione){
        var urlRestProxy = URL_REST.STANDARD;
        var rimborso = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.RIMBORSO_KM;
        var objectPostRimborsoClauses = [{condition: 'AND', fieldName: 'nazione', operator: "=", fieldValue:nazione},
                                    {condition: 'AND', fieldName: 'condizioneRimborsoKmMissione', operator: "=", fieldValue:"S"},
                                    {condition: 'AND', fieldName: 'tipoAuto', operator: "=", fieldValue:tipoAuto},
                                    {condition: 'AND', fieldName: 'data', operator: "=", fieldValue:data}];
        var objectPostRimborso = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostRimborsoClauses}
        return $http.post(urlRestProxy + app+'/', objectPostRimborso, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                rimborso = data.elements;
            }
            return rimborso;
        }).error(function (data) {
        });
    }

    var recuperoDivisa = function(inquadramento, data, nazione){
        var urlRestProxy = URL_REST.STANDARD;
        var divisa = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.DIVISA;
        var objectContext = {context:{"esercizio":2016,"cd_unita_organizzativa":"999.000","cd_cds":"999","cd_cdr":"999.000.000"}};
        return $http.post(urlRestProxy + app+'/?proxyURL='+url+'&data='+ data+'&nazione='+ nazione+'&inquadramento='+inquadramento, objectContext).success(function (data) {
            if (data){
                divisa = data.elements;
            }
            return divisa;
        }).error(function (data) {
        });
    }

    var recuperoDatiDivisa = function(divisa){
        var urlRestProxy = URL_REST.STANDARD;
        var datiDivisa = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.DATI_DIVISA;
        var objectPostDatiDivisaClauses = [{condition: 'AND', fieldName: 'cd_divisa', operator: "=", fieldValue:divisa}];
        var objectPostDatiDivisa = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostDatiDivisaClauses}
        return $http.post(urlRestProxy + app+'/', objectPostDatiDivisa, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                datiDivisa = data.elements;
            }
            return datiDivisa;
        }).error(function (data) {
        });
    }

    var validaMassimaleSpesa = function(inquadramento, data, nazione, divisa, cdTipoSpesa, cdTipoPasto, importoSpesa, km){
        var urlRestProxy = URL_REST.STANDARD;
        var valida = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.VALIDA_RIGA_RIMBORSO;
        var objectPost = {"data":data,"nazione":nazione, "inquadramento":inquadramento,"importoSpesa":importoSpesa,"divisa":divisa,"cdTipoSpesa":cdTipoSpesa, "cdTipoPasto":cdTipoPasto, "km":km};
        return $http.post(urlRestProxy + app+'/?proxyURL='+url, objectPost).success(function (data) {
            if (data){
                valida = data;
            }
            return valida;
        }).error(function (data) {
        });
    }

    var recuperoTerzoModalitaPagamento = function(cdTerzo, tipoPagamento){
        var urlRestProxy = URL_REST.STANDARD;
        var ele = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.BANCA;
        var objectPostClauses = [{condition: 'AND', fieldName: 'cd_terzo', operator: "=", fieldValue:cdTerzo},
                                    {condition: 'AND', fieldName: 'ti_pagamento', operator: "=", fieldValue:tipoPagamento},
                                    {condition: 'AND', fieldName: 'fl_cancellato', operator: "=", fieldValue:false}];
        var objectPost = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostClauses}
        return $http.post(urlRestProxy + app+'/', objectPost, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                ele = data.elements;
            }
            return ele;
        }).error(function (data) {
        });
    }

    var recuperoDatiTerzoSigla = function(codiceFiscale){
        var urlRestProxy = URL_REST.STANDARD;
        var terzo = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.TERZO;
        var objectPostTerzoClauses = [{condition: 'AND', fieldName: 'anagrafico.codice_fiscale', operator: "=", fieldValue:codiceFiscale},
                                    {condition: 'AND', fieldName: 'dt_fine_rapporto', operator: "isnull"}];
        var objectPostTerzo = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostTerzoClauses}
        return $http.post(urlRestProxy + app+'/', objectPostTerzo, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                terzo = data.elements;
            }
            return terzo;
        }).error(function (data) {
        });
    }

    return { getUos: recuperoUo,
             getPersons: recuperoPersonsForUo,
             getPerson: recuperoDatiPerson ,
             getTerzo: recuperoDatiTerzoSigla ,
             getInquadramento: recuperoDatiInquadramento,
             getInquadramentoCf: recuperoDatiInquadramentoCf,
             getModalitaPagamento: recuperoModalitaPagamento,
             getTipiSpesa: recuperoTipoSpesa,
             getRimborsoKm: recuperoRimborsoKm,
             getTipiPasto: recuperoTipoPasto,
             getMandato: recuperoMandato,
             getDivisa: recuperoDivisa,
             getDatiDivisa: recuperoDatiDivisa,
             validaRiga: validaMassimaleSpesa,
             getTerzoModalitaPagamento: recuperoTerzoModalitaPagamento,
             buildPerson: createPerson ,
             buildUoRichiedenteSiglaFromUoSiper: estraiUoRichFromAccount ,
             buildUoSiglaFromUoSiper: estraiUo };
});

