'use strict';

missioniApp.factory('ProxyService', function($http, COSTANTI, APP_FOR_REST, SIGLA_REST, SIPER_REST, URL_REST, ui, Session, DirettoreUoService) {
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
            ui.error(data);
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
        var x = $http.get('app/proxy/SIPER?proxyURL=json/userinfo/'+ username);
        var y = x.then(function (result) {
            if (result.data){
                return createPerson(result.data);
            } else {
                return [];
            }
        });
        x.error(function (data) {
            ui.error(data);
        });
        return y;
    }

    var recuperoPersonsForUo = function(uo){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.SIPER;
        var url = SIPER_REST.PERSONS_FOR_UO;
        var persons = [];
        var uoSiper = uo.replace('.','');
        var x = $http.get(urlRestProxy + app +'?proxyURL=json/sedi/', {params: {titCa: uoSiper, userinfo:true}});
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
                        var person = null;
                        var cognome = null;
                        var cf = null;
                        var nome = null;
                        for (var i=0; i<listaPersons.length; i++) {
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
                        if (person != null){
                            ind ++;
                            persons[ind] = person;
                        }
                    }
                    return persons;
                });
            } else {
                return [];
            }
        });
        x.error(function (data) {
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
        });
    }

    var recuperoMandato = function(cdTerzo, annoMandato, numeroMandato){
        var urlRestProxy = URL_REST.STANDARD;
        var man = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.MANDATO;
        var objectPostManClauses = [{condition: 'AND', fieldName: 'cd_tipo_documento_cont', operator: "=", fieldValue:"MAN"},
                                    {condition: 'AND', fieldName: 'soloAnticipi', operator: "=", fieldValue:"S"},
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
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
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
            ui.error(data);
        });
    }

    var recuperoTerzoModalitaPagamento = function(cdTerzo, cdModPag){
        var urlRestProxy = URL_REST.STANDARD;
        var ele = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.BANCA;
        var objectPostClauses = [{condition: 'AND', fieldName: 'cd_terzo', operator: "=", fieldValue:cdTerzo},
                                    {condition: 'AND', fieldName: 'cd_modalita_pag', operator: "=", fieldValue:cdModPag},
                                    {condition: 'AND', fieldName: 'fl_cancellato', operator: "=", fieldValue:false}];
        var objectPost = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostClauses}
        return $http.post(urlRestProxy + app+'/', objectPost, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                ele = data.elements;
            }
            return ele;
        }).error(function (data) {
            ui.error(data);
        });
    }

    var recuperoDatiTerzoSigla = function(codiceFiscale){
        var urlRestProxy = URL_REST.STANDARD;
        var terzo = [];
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.TERZO;
        var objectPostTerzoClauses = [{condition: 'AND', fieldName: 'anagrafico.codice_fiscale', operator: "=", fieldValue:codiceFiscale}];
        var objectPostTerzo = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, clauses:objectPostTerzoClauses}
        return $http.post(urlRestProxy + app+'/', objectPostTerzo, {params: {proxyURL: url}}).success(function (data) {
            if (data){
                terzo = data.elements;
            }
            return terzo;
        }).error(function (data) {
            ui.error(data);
        });
    }

    return { getUos: recuperoUo,
             getPersons: recuperoPersonsForUo,
             getPerson: recuperoDatiPerson ,
             getTerzo: recuperoDatiTerzoSigla ,
             getInquadramento: recuperoDatiInquadramento ,
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

missioniApp.factory('OrdineMissioneService', function ($resource, DateUtils) {
        return $resource('app/rest/ordineMissione/:ids', {}, {
            'get': { method: 'GET', isArray: true},
            'add':  { method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'modify':  { method: 'PUT', 
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'delete':  { method: 'DELETE'},
            'confirm':  { method: 'PUT', params:{confirm:true, daValidazione:"N"}, 
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'confirm_validate':  { method: 'PUT', params:{confirm:true, daValidazione:"S"}, 
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'return_sender':  { method: 'PUT', params:{confirm:false, daValidazione:"R"}, 
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'finalize':  { method: 'PUT', params:{confirm:false, daValidazione:"D"}, 
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            }
        });
    });

missioniApp.controller('OrdineMissioneController', function ($rootScope, $scope, $routeParams, $sessionStorage, OrdineMissioneService, ProxyService, ElencoOrdiniMissioneService, AccessToken,
            ui, $location, $filter, $http, COSTANTI, APP_FOR_REST, SIGLA_REST, URL_REST, Session) {

    var urlRestProxy = URL_REST.STANDARD;
    $scope.today = function() {
        // Today + 1 day - needed if the current day must be included
        var today = new Date();
        today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date
        return today;
    }

    var isInQuery = function(){
        if ($scope.idMissione === undefined || $scope.idMissione === "" ) {
            return false;
        } else {
            return true;
        }
    }
    
    var controlliPrimaDelSalvataggio = function(){
        if ($scope.impegnoSelected){
            if ($scope.uoSpesaSelected.cd_unita_organizzativa) {
                $scope.ordineMissioneModel.uoSpesa = $scope.uoSpesaSelected.cd_unita_organizzativa;
                $scope.ordineMissioneModel.objUoSpesa = $scope.uoSpesaSelected;
            }
        }
    }

    $scope.formatResultCdr = function(item) {
      return item.cd_centro_responsabilita+' '+item.ds_cdr;
    }

    $scope.undoCds = function(){
        $scope.ordineMissioneModel.cdsSpesa = null;
    };

    $scope.undoVoce = function(){
        $scope.ordineMissioneModel.voce = null;
    };

    var caricaCds = function(cds, listaCds){
        if (listaCds){
            if (listaCds.length === 1){
                $scope.ordineMissioneModel.cdsSpesa = $scope.formatResultCds(listaCds[0]);
            } else {
                if (cds){
                    $scope.elencoCds = [];
                    var ind = 0;
                    for (var i=0; i<listaCds.length; i++) {
                        if (listaCds[i].cd_proprio_unita === cds){
                            $scope.elencoCds[0] = $scope.formatResultCds(listaCds[i]);
//                            $scope.elencoCds[0].selected = true;
//                            $scope.elencoCds[0] = listaCds[i];
                        } else {
                            ind ++;
                            $scope.elencoCds[ind] = $scope.formatResultCds(listaCds[i]);
                        }
                    }
                    if ($scope.ordineMissioneModel){
                        $scope.ordineMissioneModel.cdsSpesa = cds;
                    }
                }
            }
        } else {
            $scope.elencoCds = [];
        }
    
    };

    var caricaCdsCompetenza = function(cds, listaCds){
        if (listaCds){
            if (listaCds.length === 1){
                $scope.ordineMissioneModel.cdsCompetenza = $scope.formatResultCds(listaCds[0]);
            } else {
                if (cds){
                    $scope.elencoCdsCompetenza = [];
                    var ind = 0;
                    for (var i=0; i<listaCds.length; i++) {
                        if (listaCds[i].cd_proprio_unita === cds){
                            $scope.elencoCdsCompetenza[0] = $scope.formatResultCds(listaCds[i]);
//                            $scope.elencoCds[0].selected = true;
//                            $scope.elencoCds[0] = listaCds[i];
                        } else {
                            ind ++;
                            $scope.elencoCdsCompetenza[ind] = $scope.formatResultCds(listaCds[i]);
                        }
                    }
                    if ($scope.ordineMissioneModel){
                        $scope.ordineMissioneModel.cdsCompetenza = cds;
                    }
                } else {
                    $scope.elencoCdsCompetenza = listaCds;
                }
            }
        } else {
            $scope.elencoCdsCompetenza = [];
        }
    
    };

    $scope.restCds = function(anno, cdsRich){
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.CDS;
        var objectPostCdsOrderBy = [{name: 'cd_proprio_unita', type: 'ASC'}];
        var objectPostCdsClauses = [{condition: 'AND', fieldName: 'esercizio_fine', operator: ">=", fieldValue:anno}];
        var objectPostCds = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostCdsOrderBy, clauses:objectPostCdsClauses}
        $http.post(urlRestProxy + app+'/', objectPostCds, {params: {proxyURL: url}}).success(function (data) {
            caricaCds(cdsRich, data.elements);
        }).error(function (data) {
                ui.error(data);
        });
        var a = 1;
    }

    $scope.restCdsCompetenza = function(anno, cds){
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.CDS;
        var objectPostCdsOrderBy = [{name: 'cd_proprio_unita', type: 'ASC'}];
        var objectPostCdsClauses = [{condition: 'AND', fieldName: 'esercizio_fine', operator: ">=", fieldValue:anno}];
        var objectPostCds = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostCdsOrderBy, clauses:objectPostCdsClauses}
        $http.post(urlRestProxy + app+'/', objectPostCds, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        $scope.elencoCdsCompetenza = data.elements;
                    } else {
                        $scope.elencoCdsCompetenza = [];
                    }
                }
//            caricaCdsCompetenza(cds, data.elements);
        }).error(function (data) {
                ui.error(data);
        });
        var a = 1;
    }

    $scope.restNazioni = function(){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.NAZIONE;
        var objectPostNazioneOrderBy = [{name: 'ds_nazione', type: 'ASC'}];
        var objectPostNazioneClauses = [{condition: 'AND', fieldName: 'ti_nazione', operator: "!=", fieldValue:'I'}];
        var objectPostNazione = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostNazioneOrderBy, clauses:objectPostNazioneClauses}
        $http.post(urlRestProxy + app+'/', objectPostNazione, {params: {proxyURL: url}}).success(function (data) {
            if (data)
                $scope.nazioni = data.elements;
        });
    }        

    $scope.restUo = function(anno, cds, uoRich){
        var uos = ProxyService.getUos(anno, cds, uoRich).then(function(result){
        	if (result && result.data){
		        $scope.elencoUo = result.data.elements;
		        if ($scope.elencoUo){
		            if ($scope.elencoUo.length === 1){
		                $scope.ordineMissioneModel.uoSpesa = $scope.elencoUo[0];
		            }
		        }
        	} else {
		        $scope.elencoUo = [];
        	}
        });
    }
    
    $scope.restUoCompetenza = function(anno, cds, uo){
        $scope.elencoUoCompetenza = [];
        if (cds){
            var uos = ProxyService.getUos(anno, cds, uo).then(function(result){
                if (result && result.data){
                    $scope.elencoUoCompetenza = result.data.elements;
                    if ($scope.elencoUoCompetenza){
                        if ($scope.elencoUoCompetenza.length === 1){
                            $scope.ordineMissioneModel.uoCompetenza = $scope.elencoUoCompetenza[0];
                        }
                    }
                } else {
                    $scope.elencoUoCompetenza = [];
                }
            });
        }
    }
    
    $scope.restCdr = function(uo, daQuery){
        if (uo){
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.CDR;
            var objectPostCdrOrderBy = [{name: 'cd_centro_responsabilita', type: 'ASC'}];
            var objectPostCdrClauses = [{condition: 'AND', fieldName: 'cd_unita_organizzativa', operator: "=", fieldValue:uo}];
            var objectPostCdr = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostCdrOrderBy, clauses:objectPostCdrClauses}
            $http.post(urlRestProxy + app+'/', objectPostCdr, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        $scope.elencoCdr = data.elements;
                        if (data.elements.length === 1){
                            $scope.ordineMissioneModel.cdrSpesa = data.elements[0].cd_centro_responsabilita;
                            if (daQuery != 'S'){
                                $scope.restModuli($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.uoSpesa);
                                $scope.restGae($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.pgProgetto, $scope.ordineMissioneModel.cdrSpesa, $scope.ordineMissioneModel.uoSpesa);
                            }
                        }
                    } else {
                        $scope.elencoCdr = [];
                    }
                }
            }).error(function (data) {
                ui.error(data);
            });
        } else {
            $scope.elencoCdr = [];
        }
    }
    
    $scope.restModuli = function(anno, uo){
        if (uo){
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.MODULO;
            var varOrderBy = [{name: 'cd_progetto', type: 'ASC'}];
            if (uo.substring(0,3) == COSTANTI.CDS_SAC){
                uo = COSTANTI.UO_STANDARD_SAC;
            }
            var varClauses = [{condition: 'AND', fieldName: 'livello', operator: "=", fieldValue:2},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'cd_unita_organizzativa', operator: "=", fieldValue:uo}];
            var postModuli = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $http.post(urlRestProxy + app+'/', postModuli, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        $scope.elencoModuli = data.elements;
                        if (data.elements.length === 1){
                            $scope.ordineMissioneModel.modulo = data.elements[0].pg_progetto;
                        }
                    } else {
                        $scope.elencoModuli = [];
                    }
                }
            }).error(function (data) {
                ui.error(data);
            });
        } else {
            $scope.elencoModuli = [];
        }
    }
    
    $scope.restImpegno = function(){
        if ($scope.ordineMissioneModel.esercizioOriginaleObbligazione && $scope.ordineMissioneModel.pgObbligazione){
            var app = APP_FOR_REST.SIGLA;
            var url = null;
            var varClauses = [];
            if ($scope.gaeSelected){
                url = SIGLA_REST.IMPEGNO_GAE;
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.ordineMissioneModel.anno},
                              {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.ordineMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.ordineMissioneModel.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.ordineMissioneModel.pgObbligazione},
                              {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:$scope.ordineMissioneModel.gaeSelected}];
            } else {
                url = SIGLA_REST.IMPEGNO;
                varClauses = [{condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.ordineMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.ordineMissioneModel.anno},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.ordineMissioneModel.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.ordineMissioneModel. pgObbligazione}];
            }
            var varOrderBy = [{name: 'esercizio', type: 'DESC'}];
            var postImpegno = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $http.post(urlRestProxy + app+'/', postImpegno, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        $scope.impegnoSelected = data.elements[0];
                    } else {
                        $scope.impegnoSelected = [];
                    }
                }
            }).error(function (data) {
                ui.error(data);
            });
        } else {
            $scope.impegnoSelected = [];
        }
    }
    
    $scope.restGae = function(anno, modulo, cdr, uo){
        if (cdr || modulo || uo){
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.GAE;
            var varOrderBy = [{name: 'cd_linea_attivita', type: 'ASC'}];
            var varClauses = [];
            if (modulo){
                if (cdr){
                     varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'pg_progetto', operator: "=", fieldValue:modulo},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "=", fieldValue:cdr},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
                } else if (uo) {
                     varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'pg_progetto', operator: "=", fieldValue:modulo},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "LIKE", fieldValue:uo.substring(0,3)+"%"}];
                }
            } else if (cdr){
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "=", fieldValue:cdr},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
            }  else if (uo) {
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'centro_responsabilita.cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
            }
            var postGae = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $http.post(urlRestProxy + app+'/', postGae, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        $scope.elencoGae = data.elements;
                        if (data.elements.length === 1){
                            $scope.ordineMissioneModel.gae = data.elements[0].cd_linea_attivita;
                        }
                    } else {
                        $scope.elencoGae = [];
                    }
                }
            }).error(function (data) {
                ui.error(data);
            });
        } else {
            $scope.elencoGae = [];
        }
    }

    $scope.restCapitoli = function(anno){
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.VOCE;
            var varOrderBy = [{name: 'cd_elemento_voce', type: 'ASC'}];
            var varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'ti_elemento_voce', operator: "=", fieldValue:"C"},
                              {condition: 'AND', fieldName: 'fl_solo_residuo', operator: "=", fieldValue:false},
                              {condition: 'AND', fieldName: 'ti_appartenenza', operator: "=", fieldValue:"D"}];
            var postVoce = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $http.post(urlRestProxy + app+'/', postVoce, {params: {proxyURL: url}}).success(function (data) {
                if (data){
	            	var listaVoci = data.elements;
                    if (listaVoci){
                        $scope.elencoVoci = [];
                        if (listaVoci.length === 1){
                            $scope.ordineMissioneModel.voce = listaVoci[0];
                        }
	                    var ind = -1;
    	                for (var i=0; i<listaVoci.length; i++) {
        	                ind ++;
            	            $scope.elencoVoci[ind] = $scope.formatResultVoce(listaVoci[i]);
                	    }
                    } else {
                        $scope.elencoVoci = [];
                    }
		        } else {
		            $scope.elencoVoci = [];
		        }
            }).error(function (data) {
                ui.error(data);
            });
    }
    

    $scope.formatResultVoce = function(item) {
      return {
        value: item.cd_elemento_voce,
        text: item.cd_elemento_voce+' '+item.ds_elemento_voce
      };
    }

    $scope.formatResultCds = function(item) {
      return {
        value: item.cd_proprio_unita,
        text: item.cd_proprio_unita+' '+item.ds_unita_organizzativa
      };
    }

    $scope.annullaGae = function(){
      $scope.ordineMissioneModel.gae = null;
      $scope.gaeSelected = null;
    }

    $scope.annullaModulo = function(){
      $scope.annullaGae();
      $scope.ordineMissioneModel.pgProgetto = null;
    }

    $scope.annullaCdr = function(){
      $scope.annullaModulo();
      $scope.ordineMissioneModel.cdrSpesa = null;
    }

    $scope.annullaUo = function(){
      $scope.annullaCdr();
      $scope.ordineMissioneModel.uoSpesa = null;
    }

    $scope.reloadCds = function(cds) {
      $scope.annullaUo();  
      $scope.restUo($scope.ordineMissioneModel.anno, cds, $scope.ordineMissioneModel.uoRich);
    }

    $scope.reloadCdsCompetenza = function(cds) {
      $scope.ordineMissioneModel.uoCompetenza = null;
      $scope.restUoCompetenza($scope.ordineMissioneModel.anno, cds, null);
    }

    $scope.reloadUoWork = function(uo){
        $scope.accountModel = null;
        $sessionStorage.accountWork = $scope.accountModel;
        $scope.elencoPersone = [];
        $scope.userWork = null;
        $scope.ordineMissioneModel = {};
        if (uo){
            $scope.disableUo = true;
            var persons = ProxyService.getPersons(uo).then(function(result){
                if (result ){
                    $scope.elencoPersone = result;
                    $scope.disableUo = false;
                }
            });
        }
    }

    $scope.reloadUo = function(uo) {
      $scope.annullaCdr();  
      $scope.restCdr(uo, "N");
    }

    $scope.reloadCdr = function(cdr) {
      $scope.annullaModulo();  
      $scope.restModuli($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.uoSpesa);
    }

    $scope.reloadModulo = function(pgProgetto, cdr, uo) {
      $scope.annullaGae();
      $scope.restGae($scope.ordineMissioneModel.anno, pgProgetto, cdr, uo);
    }

    $scope.tipiMissione = {
        'Italia': 'I',
        'Estera': 'E'
    };

    $scope.luoghiDiPartenza = {
        'Sede di Lavoro': 'S',
        'Residenza/Domicilio Fiscale': 'R'
    };

    $scope.valoriPriorita = {
        'Critica': '1',
        'Importante': '3',
        'Media': '5'
    };

    $scope.trattamenti = {
        'Rimborso Documentato': 'R',
        'Trattamento Alternativo di Missione': 'T'
    };

    $scope.obblighiRientro = {
        'Sì': 'S',
        'No': 'N'
    };

    $scope.onChangeTipoMissione = function() {
        if ($scope.ordineMissioneModel.tipoMissione === 'E') {
            $scope.ordineMissioneModel.trattamento = "R";
            $scope.missioneEstera = true;
        } else {
            $scope.ordineMissioneModel.trattamento = null;
            $scope.missioneEstera = null;
            $scope.ordineMissioneModel.nazione = null;
        }
    };

    var dateInizioFineDiverse = function() {
        if ($scope.ordineMissioneModel.dataInizioMissione === undefined || 
            $scope.ordineMissioneModel.dataFineMissione === undefined ||
            $scope.ordineMissioneModel.dataInizioMissione == null || 
            $scope.ordineMissioneModel.dataFineMissione === null ||
            $scope.ordineMissioneModel.dataInizioMissione === "" || 
            $scope.ordineMissioneModel.dataFineMissione === "" ||
            $scope.ordineMissioneModel.dataFineMissione === $scope.ordineMissioneModel.dataInizioMissione) {
          $scope.showObbligoRientro = null;
        } else {
            var dataInizio = moment($scope.ordineMissioneModel.dataInizioMissione).format("DD/MM/YYYY");
            var dataFine = moment($scope.ordineMissioneModel.dataFineMissione).format("DD/MM/YYYY");
            if (dataInizio != dataFine){
                $scope.showObbligoRientro = true;
            } else {
                $scope.showObbligoRientro = null;
            }
        }
    }

    $scope.onChangeDateInizioFine = function() {
        dateInizioFineDiverse();
    }

    $scope.esisteOrdineMissione = function() {
        if ($scope.ordineMissioneModel.id === undefined || 
            $scope.ordineMissioneModel.id === "") {
          return null;
        } else {
          return true;
        }
    }

    var impostaDisabilitaOrdineMissione = function() {
        if ($scope.esisteOrdineMissione && ($scope.ordineMissioneModel.stato === 'DEF' || $scope.ordineMissioneModel.statoFlusso === 'APP' || ($scope.ordineMissioneModel.stato === 'CON' && 
            ($scope.ordineMissioneModel.stateFlows === 'ANNULLATO' ||
                $scope.ordineMissioneModel.stateFlows === 'FIRMA SPESA' ||
                $scope.ordineMissioneModel.stateFlows === 'FIRMA UO' ||
                $scope.ordineMissioneModel.stateFlows === 'FIRMATO')))) {
          return true;
        } else {
          return false;
        }
    }

    $scope.inizializzaFormPerModifica = function(){
        $scope.showEsisteOrdineMissione = true;
        if ($scope.ordineMissioneModel.statoFlusso === "INV" && $scope.ordineMissioneModel.stato === "INS" && $scope.ordineMissioneModel.commentFlows){
	        $scope.showCommentFlows = true;
        } else {
	        $scope.showCommentFlows = false;
        }
        if ($scope.ordineMissioneModel.tipoMissione === 'E') {
            $scope.missioneEstera = true;
        } else {
            $scope.missioneEstera = null;
        }
        if ($scope.validazione === 'S') {
            $scope.ordineMissioneModel.daValidazione = "S";
        }

        $scope.disabilitaOrdineMissione = impostaDisabilitaOrdineMissione();
        dateInizioFineDiverse();
    }

    $scope.estraiCdsRichFromAccount = function(account){
        if (account.codice_uo){
            return account.codice_uo.substring(0,3);
        }
        return "";
    }

    $scope.inizializzaFormPerInserimento = function(account){
        $scope.ordineMissioneModel = {tipoMissione:'I', priorita:'5', nominativo:account.lastName+" "+account.firstName, 
                                        comuneResidenzaRich:account.comune_residenza+" - "+account.cap_residenza, 
                                        indirizzoResidenzaRich:account.indirizzo_completo_residenza, 
                                        qualificaRich:account.profilo, livelloRich:account.livello, codiceFiscale:account.codice_fiscale, 
                                        dataNascita:account.data_nascita, luogoNascita:account.comune_nascita, validato:'N', 
                                        datoreLavoroRich:account.struttura_appartenenza, matricola:account.matricola,
            uoRich:ProxyService.buildUoRichiedenteSiglaFromUoSiper(account), cdsRich:$scope.estraiCdsRichFromAccount(account)};
        $scope.missioneEstera = null;
        $scope.ordineMissioneModel.uid = account.login;
        var today = $scope.today();
        $scope.ordineMissioneModel.dataInserimento = today;
        $scope.ordineMissioneModel.anno = today.getFullYear();
        $scope.showObbligoRientro = null;
        $scope.disabilitaOrdineMissione = false;
    }

    $scope.gestioneInCasoDiErrore = function(){
        $scope.error = true;
    }

    $scope.validateImpegno = function(){
        $scope.restImpegno();
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione dell'Ordine di Missione Numero: "+$scope.ordineMissioneModel.numero+" del "+$filter('date')($scope.ordineMissioneModel.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteOrdineMissione);
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per confermare l'Ordine di Missione Numero: "+$scope.ordineMissioneModel.numero+" del "+$filter('date')($scope.ordineMissioneModel.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e l'ordine non sarà più modificabile. Si desidera Continuare?", confirmOrdineMissione);
    }

    var confirmOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.confirm($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
	                    ui.ok_message("Ordine di Missione confermato e inviato all'approvazione.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
    }

    $scope.ritornaMittenteOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.return_sender($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Ordine di Missione sbloccato.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
    }

    $scope.validateOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.confirm_validate($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Ordine di Missione confermato e inviato all'approvazione.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
    }

    $scope.finalizeOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.finalize($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Ordine di Missione Completato.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
    }

    var deleteOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.delete({ids:$scope.ordineMissioneModel.id},
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.idMissione = null;
                        $scope.ordineMissioneModel = {}
                        $scope.inizializzaFormPerInserimento($sessionStorage.account);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
    }

    var serviziRestInizialiInserimento = function(){
        $scope.restNazioni();
        $scope.restCds($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.cdsRich);
        $scope.reloadCds($scope.ordineMissioneModel.cdsRich);
        $scope.restCapitoli($scope.ordineMissioneModel.anno);
        $scope.restCdsCompetenza($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.cdsRich);
    }

    $scope.reloadUserWork = function(uid){
        if (uid){
            for (var i=0; i<$scope.elencoPersone.length; i++) {
                if (uid == $scope.elencoPersone[i].uid){
                    var data = $scope.elencoPersone[i];
                    var userWork = ProxyService.buildPerson(data);

                    $scope.accountModel = userWork;
                    $sessionStorage.accountWork = userWork;
                    $scope.inizializzaFormPerInserimento($scope.accountModel);
                    serviziRestInizialiInserimento();
                }
            }
        }
    }

    $scope.goAutoPropria = function () {
      if ($scope.ordineMissioneModel.id){
        if ($scope.validazione){
            $location.path('/ordine-missione/auto-propria/'+$scope.ordineMissioneModel.id+'/'+$scope.validazione);
        } else {
            if ($scope.disabilitaOrdineMissione){
                $location.path('/ordine-missione/auto-propria/'+$scope.ordineMissioneModel.id+'/'+"D");
            } else {
                $location.path('/ordine-missione/auto-propria/'+$scope.ordineMissioneModel.id+'/'+"N");
            }
        }
      } else {
        ui.error("Per poter inserire i dati dell'auto propria è necessario prima salvare l'ordine di missione");
      }
    }

    $scope.goAnticipo = function () {
      if ($scope.ordineMissioneModel.id){
        if ($scope.validazione){
            $location.path('/ordine-missione/richiesta-anticipo/'+$scope.ordineMissioneModel.id+'/'+$scope.validazione);
        } else {
            if ($scope.disabilitaOrdineMissione){
                $location.path('/ordine-missione/richiesta-anticipo/'+$scope.ordineMissioneModel.id+'/'+"D");
            } else {
                $location.path('/ordine-missione/richiesta-anticipo/'+$scope.ordineMissioneModel.id+'/'+"N");
            }
        }
      } else {
        ui.error("Per poter inserire i dati dell'anticipo è necessario prima salvare l'ordine di missione");
      }
    }

    $scope.doPrintOrdineMissione = function(idOrdineMissione){
      $scope.ordineMissioneModel.stampaInCorso=true;
      $http.get('app/rest/ordineMissione/print/json',{params: {idMissione: idOrdineMissione}})
        .success(function (data) {
            delete $scope.ordineMissioneModel.stampaInCorso;
//            var file = new Blob([data], {type: 'application/pdf'});
//            var fileURL = URL.createObjectURL(file);
//            window.open(fileURL);
        }).error(function (data) {
            delete $scope.ordineMissioneModel.stampaInCorso;
            ui.error(data);
        }); 
    }

    $scope.previousPage = function () {
      parent.history.back();
    }

    $scope.save = function () {
        controlliPrimaDelSalvataggio();
        if ($scope.esisteOrdineMissione()){
            $rootScope.salvataggio = true;
            OrdineMissioneService.modify($scope.ordineMissioneModel,
                    function (value, responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.ordineMissioneModel = value;
                    },
                    function (httpResponse) {
                            $rootScope.salvataggio = false;
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                    }
            );
        } else {
            $rootScope.salvataggio = true;
            OrdineMissioneService.add($scope.ordineMissioneModel,
                    function (value, responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.ordineMissioneModel = value;
                        $scope.elencoPersone = null;
                        $scope.uoForUsersSpecial = null;
                        $scope.inizializzaFormPerModifica();
                        var path = $location.path();
                        $location.path(path+'/'+$scope.ordineMissioneModel.id);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                        if (httpResponse.status === 200) {
                        } else {
                            if (httpResponse.data.message){
                                ui.error(httpResponse.data.message);
                            } else {
                                ui.error(httpResponse.data);
                            }
                        }
                    }
            );
        }
    }

    $('#fileupload')
      .fileupload(
        { url: 'app/rest/ordineMissione/uploadAllegati/'+$routeParams.idMissione,
          dataType: 'json',
          progressInterval: 1000,
          add: function (e, data) {
            if($(".attachment_box").length > 0) { 
                alert("E' permesso il caricamento di un solo file alla volta")
                return;
            }
            $scope.messageTitle = 'CARICAMENTO IN CORSO ('+data.files[0].name+')';
            $scope.messages = null;
            $scope.loading = true;
            $scope.$apply();
            data.submit();
          },
          progressall: function (e, data) {
            $('#progress .progress-bar > p').remove();
            $('<p/>').text('Uploading...').appendTo($('#progress .progress-bar'));
            $('#progress .progress-bar').css(
                'width',
                75 + '%'
            );
          },
          fail: function(e, data) {
            $scope.loading = false;
            if (data.jqXHR.status===200) {
              $.each(data.files, function (index, file) {
                $scope.messageTitle = 'CARICAMENTO EFFETTUATO ('+data.files[0].name+')';
                $scope.messages =['Il file ('+file.name+') è stato caricato correttamente.'];
                $('<p style="color:green"/>').text(file.name).appendTo('#files');
              });
            } else {
              $scope.messages = data.jqXHR.responseText;
              $.each(data.files, function (index, file) {
                $scope.messageTitle = 'ERRORI ('+file.name+')';
                $('<p style="color:red"/>').text(file.name).appendTo('#files');
              });
            }
            $('#progress .progress-bar > p').remove();
            $('<p/>').text('Loaded').appendTo($('#progress .progress-bar'));
            $('#progress .progress-bar').css(
              'width', 100 + '%'
            );
            $scope.$apply();
          },
          beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+$scope.accessToken);
          }   
        })
      .prop('disabled', !$.support.fileInput)
      .parent().addClass($.support.fileInput ? undefined : 'disabled');

    $scope.idMissione = $routeParams.idMissione;
    $scope.validazione = $routeParams.validazione;
    $scope.accessToken = AccessToken.get();
    $sessionStorage.accountWork = null;

    if (isInQuery() || ($scope.ordineMissioneModel != null && $scope.ordineMissioneModel.idMissione)){
        ElencoOrdiniMissioneService.findById($scope.idMissione).then(function(data){
            var model = data;
            if (model){
                if (model.uid == $sessionStorage.account.login){
                    $scope.accountModel = $sessionStorage.account;
                    $sessionStorage.accountWork = $scope.accountModel;
                } else {
                    var person = ProxyService.getPerson(model.uid).then(function(result){
                        if (result){
                            $scope.accountModel = result;
                            $sessionStorage.accountWork = $scope.accountModel;
                        }
                    });
                }
                $scope.restNazioni();
                $scope.restCds(model.anno, model.cdsSpesa);
                $scope.restCdsCompetenza(model.anno, model.cdsCompetenza);
                $scope.restUo(model.anno, model.cdsSpesa, model.uoSpesa);
                $scope.restUoCompetenza(model.anno, model.cdsCompetenza, model.uoCompetenza);
                $scope.restCdr(model.uoSpesa, "S");
                $scope.restModuli(model.anno, model.uoSpesa);
                $scope.restGae(model.anno, model.pgProgetto, model.cdrSpesa, model.uoSpesa);
                $scope.restCapitoli(model.anno);
                $scope.ordineMissioneModel = model;
                $scope.inizializzaFormPerModifica();
            }
        });
    } else {
        var accountLog = $sessionStorage.account;
        var uoForUsersSpecial = accountLog.uoForUsersSpecial;
        if (uoForUsersSpecial){
            $scope.userSpecial = true;
            var anno = $scope.today().getFullYear();
            var elenco = ProxyService.getUos(anno, null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog)).then(function(result){
	            $scope.uoForUsersSpecial = [];
	        	if (result && result.data){
			        var uos = result.data.elements;
			        var ind = -1;
	                for (var i=0; i<uos.length; i++) {
	                    for (var k=0; k<uoForUsersSpecial.length; k++) {
	                        if (uos[i].cd_unita_organizzativa == ProxyService.buildUoSiglaFromUoSiper(uoForUsersSpecial[k].codice_uo)){
	                                ind ++;
	                                $scope.uoForUsersSpecial[ind] = uos[i];
	                        }
	                    }
	                }
	                if ($scope.uoForUsersSpecial.length === 1){
	                    $scope.uoWorkForSpecialUser = $scope.uoForUsersSpecial[0];
	                }
	            } 
            });
        } else {
            $scope.accountModel = $sessionStorage.account;
            $sessionStorage.accountWork = $scope.accountModel;
            $scope.inizializzaFormPerInserimento($scope.accountModel);
            serviziRestInizialiInserimento();
        }
    }
});
