'use strict';

missioniApp.factory('OrdineMissioneService', function ($resource, DateUtils) {
        return $resource('api/rest/ordineMissione/:ids', {}, {
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
            'send_to_manager':  { method: 'PUT', params:{confirm:false, daValidazione:"M"}, 
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
            ui, $location, $filter, $http, COSTANTI, APP_FOR_REST, SIGLA_REST, URL_REST, Session, DatiIstitutoService, DateService, DateUtils, MissioniRespinte) {

    var urlRestProxy = URL_REST.STANDARD;
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
        $scope.showResponsabile = false;
    };

    $scope.undoVoce = function(){
        $scope.ordineMissioneModel.voce = null;
    };

    var caricaCds = function(cds, listaCds){
        if (listaCds){
            if (listaCds.length === 1){
                $scope.ordineMissioneModel.cdsSpesa = listaCds[0].cd_proprio_unita;
            } else {
                if (cds){
                    $scope.elencoCds = [];
                    var ind = 0;
                    for (var i=0; i<listaCds.length; i++) {
                        if (listaCds[i].cd_proprio_unita === cds){
                            $scope.elencoCds[0] = listaCds[i];
                            $scope.elencoCds[0].testo = listaCds[i].cd_proprio_unita+" "+listaCds[i].ds_unita_organizzativa;
//                            $scope.elencoCds[0].selected = true;
//                            $scope.elencoCds[0] = listaCds[i];
                        } else {
                            ind ++;
                            $scope.elencoCds[ind] = listaCds[i];
                            $scope.elencoCds[ind].testo = listaCds[i].cd_proprio_unita+" "+listaCds[i].ds_unita_organizzativa;
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
		                $scope.ordineMissioneModel.uoSpesa = $scope.elencoUo[0].cd_unita_organizzativa;
                        $scope.impostaGestioneResponsabileGruppo($scope.ordineMissioneModel.uoSpesa);
                        $scope.restCdr($scope.ordineMissioneModel.uoSpesa,"N");
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
                            $scope.ordineMissioneModel.uoCompetenza = $scope.elencoUoCompetenza[0].cd_unita_organizzativa;
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
            $scope.elencoCdr = [];
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
            });
        } else {
            $scope.elencoCdr = [];
        }
    }
    
    $scope.restModuli = function(anno, uo){
        if (uo){
            $scope.elencoModuli = [];
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.MODULO;
            var varOrderBy = [{name: 'cd_progetto', type: 'ASC'}];
            if (uo.substring(0,3) == COSTANTI.CDS_SAC){
                uo = COSTANTI.UO_STANDARD_SAC;
            }
            var varClauses = [{condition: 'AND', fieldName: 'livello', operator: "=", fieldValue:2},
                              {condition: 'AND', fieldName: 'fl_utilizzabile', operator: "=", fieldValue:true},
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
            var today = new Date();
            var anno = today.getFullYear();
            if ($scope.gaeSelected){
                url = SIGLA_REST.IMPEGNO_GAE;
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.ordineMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.ordineMissioneModel.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.ordineMissioneModel.pgObbligazione},
                              {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:$scope.ordineMissioneModel.gaeSelected}];
            } else {
                url = SIGLA_REST.IMPEGNO;
                varClauses = [{condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.ordineMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
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
            });
        } else {
            $scope.impegnoSelected = [];
        }
    }
    
    $scope.restGae = function(anno, modulo, cdr, uo){
        if (cdr || modulo || uo){
            $scope.elencoGae = [];
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.GAE;
            var varOrderBy = [{name: 'cd_linea_attivita', type: 'ASC'}];
            var varClauses = [];
            if (modulo){
                if (cdr){
                     varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'pg_progetto', operator: "=", fieldValue:modulo},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "=", fieldValue:cdr},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
                } else if (uo) {
                     varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'pg_progetto', operator: "=", fieldValue:modulo},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "LIKE", fieldValue:uo.substring(0,3)+"%"}];
                }
            } else if (cdr){
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "=", fieldValue:cdr},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
            }  else if (uo) {
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:anno},
                              {condition: 'AND', fieldName: 'ti_gestione', operator: "=", fieldValue:"S"},
                              {condition: 'AND', fieldName: 'cd_centro_responsabilita', operator: "LIKE", fieldValue:cdr.substring(0,3)+"%"}];
            }
            var postGae = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $scope.workingRestGae = true;
            $http.post(urlRestProxy + app+'/', postGae, {params: {proxyURL: url}}).success(function (data) {
                $scope.workingRestGae = false;
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
                $scope.workingRestGae = false;
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
                              {condition: 'AND', fieldName: 'fl_missioni', operator: "=", fieldValue:true},
                              {condition: 'AND', fieldName: 'ti_appartenenza', operator: "=", fieldValue:"D"}];
            var postVoce = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:varOrderBy, clauses:varClauses}
            $http.post(urlRestProxy + app+'/', postVoce, {params: {proxyURL: url}}).success(function (data) {
                if (data){
	            	var listaVoci = data.elements;
                    if (listaVoci){
                        $scope.elencoVoci = [];
                        if (listaVoci.length === 1){
                            $scope.ordineMissioneModel.voce = listaVoci[0].cd_elemento_voce;
                        }
	                    var ind = -1;
    	                for (var i=0; i<listaVoci.length; i++) {
        	                ind ++;
            	            $scope.elencoVoci[ind] = listaVoci[i];
                	    }
                    } else {
                        $scope.elencoVoci = [];
                    }
		        } else {
		            $scope.elencoVoci = [];
		        }
            }).error(function (data) {
            });
    }
    

    $scope.onChangeDuplica = function (duplica) {
        if (duplica == 'S'){
            $scope.restOrdiniMissioneDaDuplicare($sessionStorage.accountWork);
        }
    }

    $scope.reloadOrdineMissione = function(idOrdineMissione){

        for (var i=0; i<$scope.elencoOrdiniMissione.length; i++) {
            if ($scope.elencoOrdiniMissione[i].id === idOrdineMissione){
                var ordineMissioneSelected = $scope.elencoOrdiniMissione[i];

                var today = $scope.oggi;
                $scope.ordineMissioneModel.dataInserimento = today;
                $scope.ordineMissioneModel.anno = today.getFullYear();

                $scope.ordineMissioneModel.priorita = ordineMissioneSelected.priorita;
                $scope.ordineMissioneModel.oggetto = ordineMissioneSelected.oggetto;
                $scope.ordineMissioneModel.destinazione = ordineMissioneSelected.destinazione;
                $scope.ordineMissioneModel.nazione = ordineMissioneSelected.nazione;
                $scope.ordineMissioneModel.tipoMissione = ordineMissioneSelected.tipoMissione;
                $scope.ordineMissioneModel.trattamento = ordineMissioneSelected.trattamento;

                if ($scope.ordineMissioneModel.tipoMissione === 'E') {
                    $scope.missioneEstera = true;
                } else {
                    $scope.missioneEstera = null;
                }

                $scope.ordineMissioneModel.voce = ordineMissioneSelected.voce;
                $scope.ordineMissioneModel.gae = ordineMissioneSelected.gae;
                $scope.ordineMissioneModel.cdsSpesa = ordineMissioneSelected.cdsSpesa;
                $scope.ordineMissioneModel.uoSpesa = ordineMissioneSelected.uoSpesa;
                $scope.ordineMissioneModel.cdrSpesa = ordineMissioneSelected.cdrSpesa;
                $scope.ordineMissioneModel.cdsCompetenza = ordineMissioneSelected.cdsCompetenza;
                $scope.ordineMissioneModel.uoCompetenza = ordineMissioneSelected.uoCompetenza;
                $scope.ordineMissioneModel.pgProgetto = ordineMissioneSelected.pgProgetto;
                $scope.ordineMissioneModel.utilizzoTaxi = ordineMissioneSelected.utilizzoTaxi;
                $scope.ordineMissioneModel.utilizzoAutoNoleggioServizio = ordineMissioneSelected.utilizzoAutoServizio;
                $scope.ordineMissioneModel.personaleAlSeguito = ordineMissioneSelected.personaleAlSeguito;
                $scope.ordineMissioneModel.utilizzoAutoNoleggio = ordineMissioneSelected.utilizzoAutoNoleggio;
                $scope.ordineMissioneModel.noteUtilizzoTaxiNoleggio = ordineMissioneSelected.noteUtilizzoTaxiNoleggio;
                $scope.ordineMissioneModel.partenzaDa = ordineMissioneSelected.partenzaDa;
                $scope.ordineMissioneModel.importoPresunto = ordineMissioneSelected.importoPresunto;
                $scope.ordineMissioneModel.missioneGratuita = ordineMissioneSelected.missioneGratuita;
                $scope.ordineMissioneModel.cup = ordineMissioneSelected.cup;
                $scope.ordineMissioneModel.cug = ordineMissioneSelected.cug;
                $scope.ordineMissioneModel.presidente = ordineMissioneSelected.presidente;
                if ($scope.ordineMissioneModel.uoSpesa){
                    $scope.restUo($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.cdsSpesa, $scope.ordineMissioneModel.uoSpesa);
                    $scope.restModuli($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.uoSpesa);
                    $scope.restGae($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.pgProgetto, $scope.ordineMissioneModel.cdrSpesa, $scope.ordineMissioneModel.uoSpesa);
                }
                if ($scope.ordineMissioneModel.cdsCompetenza){
                    $scope.restCdsCompetenza($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.cdsCompetenza);
                }
                if ($scope.ordineMissioneModel.uoCompetenza){
                    $scope.restUoCompetenza($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.cdsCompetenza, $scope.ordineMissioneModel.uoCompetenza);
                }
                if ($scope.ordineMissioneModel.cdrSpesa){
                    $scope.restCdr($scope.ordineMissioneModel.uoSpesa, "S");
                }
                break;
            }
        }
    }

    $scope.restOrdiniMissioneDaDuplicare = function(userWork){
        ElencoOrdiniMissioneService.findMissioniDaDuplicare(userWork.login).then(function(data){
            $scope.elencoOrdiniMissione = data;
        });
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
      $scope.ordineMissioneModel.responsabileGruppo = null;
      $scope.showResponsabile = false;
    }

    $scope.reloadCds = function(cds) {
      $scope.annullaUo();  
      $scope.restUo($scope.ordineMissioneModel.anno, cds, $scope.ordineMissioneModel.uoRich);
    }

    $scope.impostaGestioneResponsabileGruppo = function(uo){
      DatiIstitutoService.get(uo, $scope.ordineMissioneModel.anno).then(function(data){
        if (data.gestioneRespModulo != null && data.gestioneRespModulo == 'S'){
            $scope.getRestForResponsabileGruppo(uo);
            $scope.showResponsabile = true;
        } else {
            $scope.showResponsabile = false;
            $scope.ordineMissioneModel.responsabileGruppo = null;
        }
      });
    }

    $scope.reloadCdsCompetenza = function(cds) {
      $scope.ordineMissioneModel.uoCompetenza = null;
      $scope.restUoCompetenza($scope.ordineMissioneModel.anno, cds, null);
    }

    $scope.gestioneUtenteAbilitatoValidare = function (uo){
        $scope.utenteAbilitatoValidareUo = 'N';
        var uoForUsersSpecial= $sessionStorage.account.uoForUsersSpecial;
        if (uo && uoForUsersSpecial){
            var uoSiper = uo.replace('.','');
            for (var k=0; k<uoForUsersSpecial.length; k++) {
                var uoForUserSpecial = uoForUsersSpecial[k];
                if (uoSiper == uoForUserSpecial.codice_uo && uoForUserSpecial.ordine_da_validare == 'S'){
                    $scope.utenteAbilitatoValidareUo = 'S';
                }
            }
        }
    }

    $scope.reloadUoWork = function(uo){
        $scope.gestioneUtenteAbilitatoValidare(uo);

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

    $scope.getRestForResponsabileGruppo = function (uo){
        $scope.disableResponsabileGruppo = true;
        if (uo){
            var persons = ProxyService.getPersonsForCds($scope.ordineMissioneModel.cdsSpesa, uo, false, true).then(function(result){
                if (result ){
                    $scope.elencoResponsabiliGruppo = result;
                    $scope.disableResponsabileGruppo = false;
                }
            });
        }
    }

    $scope.reloadUo = function(uo) {
      $scope.annullaCdr();  
      $scope.impostaGestioneResponsabileGruppo(uo);
      $scope.restCdr(uo, "N");
    }

    $scope.reloadCdr = function(cdr) {
      $scope.annullaModulo();  
      $scope.restModuli($scope.ordineMissioneModel.anno, $scope.ordineMissioneModel.uoSpesa);
      $scope.restGae($scope.ordineMissioneModel.anno, null, cdr, $scope.ordineMissioneModel.uoSpesa);
    }

    $scope.reloadModulo = function(pgProgetto, cdr, uo) {
      $scope.annullaGae();
      $scope.restGae($scope.ordineMissioneModel.anno, pgProgetto, cdr, uo);

      if ($scope.showResponsabile && pgProgetto && $scope.elencoResponsabiliGruppo){
            for (var i=0; i<$scope.elencoModuli.length; i++) {
                if (pgProgetto == $scope.elencoModuli[i].pg_progetto && $scope.elencoModuli[i].codice_fiscale_responsabile){
                    for (var k=0; i<$scope.elencoResponsabiliGruppo.length; k++) {
                        if ($scope.elencoResponsabiliGruppo[k].codice_fiscale == $scope.elencoModuli[i].codice_fiscale_responsabile){
                            $scope.ordineMissioneModel.responsabileGruppo = $scope.elencoResponsabiliGruppo[k].uid;
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    $scope.tipiMissione = ProxyService.valueTipiMissione;

    $scope.luoghiDiPartenza = ProxyService.valueLuoghiDiPartenza;

    $scope.valoriPriorita = ProxyService.valuePriorita;

    $scope.trattamenti = ProxyService.valueTrattamenti;

    $scope.fondi = ProxyService.valueFondi;

    $scope.obblighiRientro = ProxyService.valueObblighiRientro;

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

    $scope.onChangePartenzaDa = function() {
        if ($scope.ordineMissioneModel.partenzaDa === 'A') {
        } else {
            $scope.ordineMissioneModel.partenzaDaAltro = null;
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
        if ($scope.esisteOrdineMissione && 
            (($scope.ordineMissioneModel.stato === 'INR' && $scope.ordineMissioneModel.responsabileGruppo != $sessionStorage.account.login) || 
              $scope.ordineMissioneModel.stato === 'DEF' || $scope.ordineMissioneModel.stato === 'ANC' || $scope.ordineMissioneModel.statoFlusso === 'APP' || $scope.ordineMissioneModel.stato === 'ANA'||$scope.ordineMissioneModel.stato === 'ANN'||
              ($scope.ordineMissioneModel.stato === 'CON' && 
                ($scope.ordineMissioneModel.statoFlusso === 'INV' ||
                 $scope.ordineMissioneModel.statoFlusso === 'FPF' ||
                 $scope.ordineMissioneModel.statoFlusso === 'APP')))) {
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
        if ($scope.ordineMissioneModel.uoSpesa){
            $scope.impostaGestioneResponsabileGruppo($scope.ordineMissioneModel.uoSpesa);
            if ($scope.ordineMissioneModel.responsabileGruppo && $sessionStorage.account.login == $scope.ordineMissioneModel.responsabileGruppo){
                $scope.isResponsabileGruppoMissione = true;
            }
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

    $scope.inizializzaFormPerInserimento = function(account, restStart){
        $scope.ordineMissioneModel = {tipoMissione:'I', priorita:'1', nominativo:account.lastName+" "+account.firstName, 
                                        qualificaRich:account.profilo, livelloRich:account.livello, codiceFiscale:account.codice_fiscale, 
                                        dataNascita:account.data_nascita, luogoNascita:account.comune_nascita, validato:'N', 
                                        datoreLavoroRich:account.struttura_appartenenza, matricola:account.matricola,
            uoRich:ProxyService.buildUoRichiedenteSiglaFromUoSiper(account), cdsRich:$scope.estraiCdsRichFromAccount(account)};
        
        if (account.comune_residenza && account.cap_residenza){
            $scope.ordineMissioneModel.comuneResidenzaRich = account.comune_residenza+" - "+account.cap_residenza;
        }
        if (account.comune_residenza){
            $scope.ordineMissioneModel.comuneResidenzaRich = account.comune_residenza;
        }
        if (account.indirizzo_completo_residenza){
            $scope.ordineMissioneModel.indirizzoResidenzaRich = account.indirizzo_completo_residenza; 
        }

        $scope.missioneEstera = null;
        $scope.ordineMissioneModel.uid = account.login;
            var today = DateService.today().then(function(result){
                if (result){
                    $scope.oggi = result;
                    $scope.ordineMissioneModel.dataInserimento = $scope.oggi;
                    $scope.ordineMissioneModel.anno = $scope.oggi.getFullYear();
                    $scope.showObbligoRientro = null;
                    $scope.disabilitaOrdineMissione = false;
                    if (restStart){
                        serviziRestInizialiInserimento();
                    }
                    return result;
                }   
            });
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

    $scope.sendToManagerOrdineMissione = function () {
        if ($scope.ordineMissioneModel && $scope.ordineMissioneModel.responsabileGruppo){
          if ($scope.ordineMissioneModel.responsabileGruppo == $scope.ordineMissioneModel.uid){
            $scope.confirm();
          } else {
            ui.confirmCRUD("Si sta per inviare al responsabile del gruppo l'Ordine di Missione Numero: "+$scope.ordineMissioneModel.numero+" del "+$filter('date')($scope.ordineMissioneModel.dataInserimento, COSTANTI.FORMATO_DATA)+". L'ordine non sarà più modificabile. Si desidera Continuare?", inviaResponsabileGruppo);
          }
        } else {
            ui.error("Valorizzare il responsabile del gruppo");
        }
    }

    $scope.cambioPresidenza = function (presidenza) {
        if (presidenza == "S"){
            ui.message("Questa opzione selezionata indica che la missione è per conto della presidenza del CNR");
        }
    }


    var confirmOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.confirm($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
	                    ui.ok_message("Ordine di Missione confermato e inviato all'approvazione.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.viewAttachments($scope.ordineMissioneModel.id);
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.ritornaMittenteOrdineMissione = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.return_sender($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Ordine di Missione respinto al mittente.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.viewAttachments($scope.ordineMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var inviaResponsabileGruppo = function () {
            $rootScope.salvataggio = true;
            OrdineMissioneService.send_to_manager($scope.ordineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Ordine di Missione inviato al responsabile del gruppo.");
                        ElencoOrdiniMissioneService.findById($scope.ordineMissioneModel.id).then(function(data){
                            $scope.ordineMissioneModel = data;
                            $scope.viewAttachments($scope.ordineMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
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
                            $scope.viewAttachments($scope.ordineMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
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
                            $scope.viewAttachments($scope.ordineMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
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
                        $scope.inizializzaFormPerInserimento($sessionStorage.account, false);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
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
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.accountModel = result;
                    $sessionStorage.accountWork = $scope.accountModel;
                    $scope.inizializzaFormPerInserimento($scope.accountModel,true);
                }
            });
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

    $scope.confirmDeleteAttachment = function (attachment, idOrdineMissione) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.get('api/rest/ordine/deleteAttachment/' + attachment.id+'/' + attachment.idMissione);
        var y = x.then(function (result) {
            var attachments = $scope.ordineMissioneModel.attachments;
            if (attachments && Object.keys(attachments).length > 0){
                var newAttachments = attachments.filter(function(el){
                    return el.id !== attachment.id;
                });
                $scope.ordineMissioneModel.attachments = newAttachments;
                if (Object.keys(newAttachments).length = 0){
                    $scope.ordineMissioneModel.attachmentsExists = false;
                }
            }
            $rootScope.salvataggio = false;
            ui.ok();
        });
        x.error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.viewAttachments = function (idOrdineMissione) {
        if (!$scope.ordineMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/ordineMissione/viewAttachments/' + idOrdineMissione).then(function (data) {
                $scope.ordineMissioneModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;  
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.ordineMissioneModel.attachments = attachments;
            }, function () {
                $scope.ordineMissioneModel.isFireSearchAttachments = false;
                $scope.ordineMissioneModel.attachmentsExists = false;
                $scope.ordineMissioneModel.attachments = {};
            });
        }
//        $scope.anticipoOrdineMissioneModel.viewAttachment = true;
    }

    $scope.doPrintOrdineMissione = function(idOrdineMissione){
      $scope.ordineMissioneModel.stampaInCorso=true;
      $http.get('api/rest/ordineMissione/print/json',{params: {idMissione: idOrdineMissione}})
        .success(function (data) {
            delete $scope.ordineMissioneModel.stampaInCorso;
//            var file = new Blob([data], {type: 'application/pdf'});
//            var fileURL = URL.createObjectURL(file);
//            window.open(fileURL);
        }).error(function (data) {
            delete $scope.ordineMissioneModel.stampaInCorso;
        }); 
    }

    $scope.previousPage = function () {
      parent.history.back();
    }

    $scope.save = function () {
        controlliPrimaDelSalvataggio();
        if ($scope.esisteOrdineMissione()){
            $rootScope.salvataggio = true;
            var autoPropria = $scope.ordineMissioneModel.utilizzoAutoPropria;
            var anticipo = $scope.ordineMissioneModel.richiestaAnticipo;
            OrdineMissioneService.modify($scope.ordineMissioneModel,
                    function (value, responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.ordineMissioneModel = value;
                        $scope.ordineMissioneModel.utilizzoAutoPropria = autoPropria;
                        $scope.ordineMissioneModel.richiestaAnticipo = anticipo;
                        $scope.viewAttachments($scope.ordineMissioneModel.id);
                    },
                    function (httpResponse) {
                            $rootScope.salvataggio = false;
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
                        $scope.ordineMissioneModel.isFireSearchAttachments = false;
                        $scope.inizializzaFormPerModifica();
                        var path = $location.path();
                        $location.path(path+'/'+$scope.ordineMissioneModel.id);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
        }
    }

    $scope.histories = [];
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
                $scope.viewAttachments($scope.ordineMissioneModel.id);
                $scope.inizializzaFormPerModifica();
                $scope.gestioneUtenteAbilitatoValidare($scope.ordineMissioneModel.uoSpesa);
            }
        });
    } else {
        var accountLog = $sessionStorage.account;
        var uoForUsersSpecial = accountLog.uoForUsersSpecial;
        if (uoForUsersSpecial){
            $scope.userSpecial = true;
            var today = DateService.today().then(function(result){
                if (result){
                    $scope.oggi = result;
                    var elenco = ProxyService.getUos(result.getFullYear(), null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog)).then(function(result){
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
                                $scope.reloadUoWork($scope.uoWorkForSpecialUser.cd_unita_organizzativa);
                            }
                        } 
                    });
                }   
            });
        } else {
            $scope.accountModel = $sessionStorage.account;
            $sessionStorage.accountWork = $scope.accountModel;
            $scope.inizializzaFormPerInserimento($scope.accountModel, true);
        }
    }
});
