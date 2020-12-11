'use strict';

missioniApp.factory('AnnullamentoOrdineMissioneService', function ($resource, DateUtils) {
        return $resource('api/rest/annullamentoOrdineMissione/:ids', {}, {
            'get': { method: 'GET', isArray: true},
            'add':  { method: 'POST',
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
            'modify':  { method: 'PUT'},
            'delete':  { method: 'DELETE'},
            'confirm':  { method: 'PUT', params:{confirm:true, daValidazione:"N"}},
            'confirm_validate':  { method: 'PUT', params:{confirm:true, daValidazione:"S"}}
        });
    });

missioniApp.controller('AnnullamentoOrdineMissioneController', function ($rootScope, $scope, $routeParams, $sessionStorage, AnnullamentoOrdineMissioneService, OrdineMissioneService, 
            ProxyService, ElencoOrdiniMissioneService, ElencoRimborsiMissioneService, AccessToken,
            ui, $location, $filter, $http, COSTANTI, APP_FOR_REST, SIGLA_REST, URL_REST, TIPO_PAGAMENTO, Session, DateService) {

    $scope.giaRimborsato = "N";
    var urlRestProxy = URL_REST.STANDARD;
    $scope.today = function() {
        // Today + 1 day - needed if the current day must be included
        var today = DateService.today().then(function(result){
            if (result){
                var oggi = new Date(result.getFullYear(), result.getMonth(), result.getDate()); // create new date
                $scope.oggi = oggi;
                return oggi;
            }
        });
        return today;
    }

    var isInQuery = function(){
        if ($scope.idMissione === undefined || $scope.idMissione === "" ) {
            return false;
        } else {
            return true;
        }
    }
    
    $scope.reloadOrdineMissione = function(idOrdineMissione){
        $scope.annullamentoModel = {};

        for (var i=0; i<$scope.elencoOrdiniMissione.length; i++) {
            if ($scope.elencoOrdiniMissione[i].id === idOrdineMissione){
                var ordineMissioneSelected = $scope.elencoOrdiniMissione[i];
                $scope.inizializzaFormPerInserimento($scope.accountModel);
                serviziRestInizialiInserimento();

                $scope.annullamentoModel.idOrdineMissione = idOrdineMissione;
                $scope.annullamentoModel.ordineMissione = ordineMissioneSelected;
                var today = $scope.oggi;
                $scope.annullamentoModel.dataInserimento = today;
                $scope.annullamentoModel.anno = today.getFullYear();

                $scope.annullamentoModel.comuneResidenzaRich = ordineMissioneSelected.comuneResidenzaRich;
                $scope.annullamentoModel.indirizzoResidenzaRich = ordineMissioneSelected.indirizzoResidenzaRich;
                $scope.annullamentoModel.domicilioFiscaleRich = ordineMissioneSelected.domicilioFiscaleRich;
                $scope.annullamentoModel.datoreLavoroRich = ordineMissioneSelected.datoreLavoroRich;
                $scope.annullamentoModel.contrattoRich = ordineMissioneSelected.contrattoRich;
                $scope.annullamentoModel.qualificaRich = ordineMissioneSelected.qualificaRich;
                $scope.annullamentoModel.livelloRich = ordineMissioneSelected.livelloRich;
                $scope.annullamentoModel.ordineMissione.priorita = ordineMissioneSelected.priorita;
                $scope.annullamentoModel.ordineMissione.oggetto = ordineMissioneSelected.oggetto;
                $scope.annullamentoModel.ordineMissione.destinazione = ordineMissioneSelected.destinazione;
                $scope.annullamentoModel.ordineMissione.nazione = ordineMissioneSelected.nazione;
                $scope.annullamentoModel.ordineMissione.tipoMissione = ordineMissioneSelected.tipoMissione;
                $scope.annullamentoModel.ordineMissione.trattamento = ordineMissioneSelected.trattamento;
                $scope.annullamentoModel.ordineMissione.dataInizioMissione = ordineMissioneSelected.dataInizioMissione;
                $scope.annullamentoModel.ordineMissione.dataFineMissione = ordineMissioneSelected.dataFineMissione;

                $scope.annullamentoModel.ordineMissione.voce = ordineMissioneSelected.voce;
                $scope.annullamentoModel.ordineMissione.gae = ordineMissioneSelected.gae;
                $scope.annullamentoModel.ordineMissione.cdsRich = ordineMissioneSelected.cdsRich;
                $scope.annullamentoModel.ordineMissione.uoRich = ordineMissioneSelected.uoRich;
                $scope.annullamentoModel.ordineMissione.cdrRich = ordineMissioneSelected.cdrRich;
                $scope.annullamentoModel.ordineMissione.cdsSpesa = ordineMissioneSelected.cdsSpesa;
                $scope.annullamentoModel.ordineMissione.uoSpesa = ordineMissioneSelected.uoSpesa;
                $scope.annullamentoModel.ordineMissione.cdrSpesa = ordineMissioneSelected.cdrSpesa;
                $scope.annullamentoModel.ordineMissione.cdsCompetenza = ordineMissioneSelected.cdsCompetenza;
                $scope.annullamentoModel.ordineMissione.uoCompetenza = ordineMissioneSelected.uoCompetenza;
                $scope.annullamentoModel.ordineMissione.pgProgetto = ordineMissioneSelected.pgProgetto;
                $scope.annullamentoModel.ordineMissione.cdcdsObbligazione = ordineMissioneSelected.cdcdsObbligazione;
                $scope.annullamentoModel.ordineMissione.esercizioOriginaleObbligazione = ordineMissioneSelected.esercizioOriginaleObbligazione;
                $scope.annullamentoModel.ordineMissione.esercizioObbligazione = ordineMissioneSelected.esercizioObbligazione;
                $scope.annullamentoModel.ordineMissione.pgObbligazione = ordineMissioneSelected.pgObbligazione;
                $scope.annullamentoModel.ordineMissione.utilizzoTaxi = ordineMissioneSelected.utilizzoTaxi;
                $scope.annullamentoModel.ordineMissione.utilizzoAutoNoleggioServizio = ordineMissioneSelected.utilizzoAutoServizio;
                $scope.annullamentoModel.ordineMissione.personaleAlSeguito = ordineMissioneSelected.personaleAlSeguito;
                $scope.annullamentoModel.ordineMissione.utilizzoAutoNoleggio = ordineMissioneSelected.utilizzoAutoNoleggio;
                $scope.annullamentoModel.ordineMissione.noteUtilizzoTaxiNoleggio = ordineMissioneSelected.noteUtilizzoTaxiNoleggio;
                $scope.annullamentoModel.ordineMissione.partenzaDa = ordineMissioneSelected.partenzaDa;
                $scope.annullamentoModel.ordineMissione.importoPresunto = ordineMissioneSelected.importoPresunto;
                $scope.annullamentoModel.ordineMissione.obblighiRientro = ordineMissioneSelected.obblighiRientro;
                $scope.annullamentoModel.ordineMissione.missioneGratuita = ordineMissioneSelected.missioneGratuita;
                $scope.annullamentoModel.ordineMissione.cup = ordineMissioneSelected.cup;
                $scope.annullamentoModel.ordineMissione.cug = ordineMissioneSelected.cug;
                $scope.annullamentoModel.ordineMissione.presidente = ordineMissioneSelected.presidente;
                if ($scope.annullamentoModel.ordineMissione.uoSpesa){
                    $scope.restUo($scope.annullamentoModel.ordineMissione.anno, $scope.annullamentoModel.ordineMissione.cdsSpesa, $scope.annullamentoModel.ordineMissione.uoSpesa);
                    $scope.restModuli($scope.annullamentoModel.ordineMissione.anno, $scope.annullamentoModel.ordineMissione.uoSpesa);
                    $scope.restGae($scope.annullamentoModel.ordineMissione.anno, $scope.annullamentoModel.ordineMissione.pgProgetto, $scope.annullamentoModel.ordineMissione.cdrSpesa, $scope.annullamentoModel.ordineMissione.uoSpesa);
                }
                if ($scope.annullamentoModel.ordineMissione.cdsCompetenza){
                    $scope.restCdsCompetenza($scope.annullamentoModel.ordineMissione.anno, $scope.annullamentoModel.ordineMissione.cdsCompetenza);
                }
                if ($scope.annullamentoModel.ordineMissione.uoCompetenza){
                    $scope.restUoCompetenza($scope.annullamentoModel.ordineMissione.anno, $scope.annullamentoModel.ordineMissione.cdsCompetenza, $scope.annullamentoModel.ordineMissione.uoCompetenza);
                }
                if ($scope.annullamentoModel.ordineMissione.cdrSpesa){
                    $scope.restCdr($scope.annullamentoModel.ordineMissione.uoSpesa, "S");
                }
                inizializzaForm();
                $scope.recuperoDatiDivisa();
                break;
            }
        }
    }

    $scope.restOrdiniMissioneDaAnnullare = function(userWork){
        ElencoOrdiniMissioneService.findMissioniDaAnnullare(userWork.login).then(function(data){
            $scope.elencoOrdiniMissione = data;
        });
    }

    $scope.recuperoDatiDivisa = function(){
        var dataInizio = moment($scope.annullamentoModel.ordineMissione.dataInizioMissione).format("DD/MM/YYYY");

        ProxyService.getDatiDivisa("EURO").then(function(ret){
            if (ret && ret.data){
                $scope.divisa = ret.data.elements;
            }
        });
    }

    $scope.formatResultCdr = function(item) {
      return item.cd_centro_responsabilita+' '+item.ds_cdr;
    }

    var caricaCds = function(cds, listaCds){
        if (listaCds){
            if (listaCds.length === 1){
                $scope.annullamentoModel.ordineMissione.cdsRich = $scope.formatResultCds(listaCds[0]);
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
//                    if ($scope.rimborsoMissioneModel){
//                        $scope.rimborsoMissioneModel.cdsRich = cds;
//                    }
                }
            }
        } else {
            $scope.elencoCds = [];
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
		                $scope.annullamentoModel.ordineMissione.uoSpesa = $scope.elencoUo[0].cd_unita_organizzativa;
                        $scope.restCdr($scope.annullamentoModel.ordineMissione.uoSpesa,"N");
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
                            $scope.annullamentoModel.ordineMissione.uoCompetenza = $scope.elencoUoCompetenza[0].cd_unita_organizzativa;
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
                            $scope.annullamentoModel.ordineMissione.cdrSpesa = data.elements[0].cd_centro_responsabilita;
                            if (daQuery != 'S'){
                                $scope.restModuli($scope.annullamentoModel.anno, $scope.annullamentoModel.ordineMissione.uoSpesa);
                                $scope.restGae($scope.annullamentoModel.anno, $scope.annullamentoModel.ordineMissione.pgProgetto, $scope.annullamentoModel.ordineMissione.cdrSpesa, $scope.annullamentoModel.ordineMissione.uoSpesa);
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
                            $scope.annullamentoModel.ordineMissione.modulo = data.elements[0].pg_progetto;
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
        if ($scope.annullamentoModel.ordineMissione.esercizioOriginaleObbligazione && $scope.annullamentoModel.ordineMissione.pgObbligazione){
            var app = APP_FOR_REST.SIGLA;
            var url = null;
            var varClauses = [];
            if ($scope.gaeSelected){
                url = SIGLA_REST.IMPEGNO_GAE;
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.anno},
                              {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.pgObbligazione},
                              {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.gaeSelected}];
            } else {
                url = SIGLA_REST.IMPEGNO;
                varClauses = [{condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.anno},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.annullamentoModel.ordineMissione. pgObbligazione}];
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
                            $scope.annullamentoModel.ordineMissione.gae = data.elements[0].cd_linea_attivita;
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
                            $scope.annullamentoModel.ordineMissione.voce = listaVoci[0];
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

    $scope.reloadUoWork = function(uo){
        $scope.gestioneUtenteAbilitatoValidare(uo);
        $scope.accountModel = null;
        $sessionStorage.accountWork = $scope.accountModel;
        $scope.elencoPersone = [];
        $scope.userWork = null;
        $scope.annullamentoModel = {};
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

    $scope.luoghiDiPartenza = {
        'Sede di Lavoro': 'S',
        'Residenza/Domicilio Fiscale': 'R'
    };

    $scope.trattamenti = {
        'Rimborso Documentato': 'R',
        'Trattamento Alternativo di Missione': 'T'
    };

    $scope.obblighiRientro = {
        'Sì': 'S',
        'No': 'N'
    };

    $scope.tipiMissione = {
        'Italia': 'I',
        'Estera': 'E'
    };

    $scope.onChangeTipoMissione = function() {
        if ($scope.annullamentoModel.ordineMissione.tipoMissione === 'E') {
            if (!$scope.annullamentoModel.ordineMissione.trattamento){
                $scope.annullamentoModel.ordineMissione.trattamento = "R";
                $scope.missioneEsteraConTam = false;
            }
            $scope.missioneEstera = true;
        } else {
            $scope.annullamentoModel.ordineMissione.trattamento = "R";
            $scope.missioneEstera = null;
            $scope.missioneEsteraConTam = null;
            $scope.annullamentoModel.ordineMissione.nazione = null;
            $scope.annullamentoModel.ordineMissione.dataInizioEstero = null;
            $scope.annullamentoModel.ordineMissione.dataFineEstero = null;
        }
    };

    var dateInizioFineDiverse = function() {
        if ($scope.annullamentoModel.ordineMissione.dataInizioMissione === undefined || 
            $scope.annullamentoModel.ordineMissione.dataFineMissione === undefined ||
            $scope.annullamentoModel.ordineMissione.dataInizioMissione == null || 
            $scope.annullamentoModel.ordineMissione.dataFineMissione === null ||
            $scope.annullamentoModel.ordineMissione.dataInizioMissione === "" || 
            $scope.annullamentoModel.ordineMissione.dataFineMissione === "" ||
            $scope.annullamentoModel.ordineMissione.dataFineMissione === $scope.annullamentoModel.ordineMissione.dataInizioMissione) {
          $scope.showObbligoRientro = null;
        } else {
            var dataInizio = moment($scope.annullamentoModel.ordineMissione.dataInizioMissione).format("DD/MM/YYYY");
            var dataFine = moment($scope.annullamentoModel.ordineMissione.dataFineMissione).format("DD/MM/YYYY");
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

    $scope.esisteAnnullamento = function() {
        if ($scope.annullamentoModel.id === undefined || 
            $scope.annullamentoModel.id === "") {
          return null;
        } else {
          return true;
        }
    }

    var impostaDisabilitaAnnullamento = function() {
        if ($scope.esisteAnnullamento && ($scope.annullamentoModel.stato === 'DEF' || 
            $scope.annullamentoModel.statoFlusso === 'APP' || $scope.annullamentoModel.stato === 'ANN' || 
            ($scope.annullamentoModel.stato === 'CON' && 
               ($scope.annullamentoModel.stateFlows === 'ANNULLATO' ||
                $scope.annullamentoModel.stateFlows === 'FIRMA SPESA REVOCA' ||
                $scope.annullamentoModel.stateFlows === 'FIRMA UO REVOCA' ||
                $scope.annullamentoModel.stateFlows === 'FIRMATO')))) {
          return true;
        } else {
          return false;
        }
    }

    $scope.inizializzaFormPerModifica = function(){
        $scope.annullamentoModel.idOrdineMissione = $scope.annullamentoModel.ordineMissione.id;
        $scope.showEsisteAnnullamento = true;
        if ($scope.annullamentoModel.statoFlusso === "INV" && $scope.annullamentoModel.stato === "INS" && $scope.annullamentoModel.commentFlows){
	        $scope.showCommentFlows = true;
        } else {
	        $scope.showCommentFlows = false;
        }
        $scope.disabilitaAnnullamento = impostaDisabilitaAnnullamento();
        
        if ($scope.annullamentoModel && $scope.annullamentoModel.ordineMissione){
            $scope.elencoOrdiniMissione = [];
            $scope.elencoOrdiniMissione.push($scope.annullamentoModel.ordineMissione);
        }

        inizializzaForm();
    }

    $scope.gestioneUtenteAbilitatoValidare = function (uo){
        $scope.utenteAbilitatoValidareUo = 'N';
        var uoForUsersSpecial= $sessionStorage.account.uoForUsersSpecial;
            if (uo){
            var uoSiper = uo.replace('.','');
            for (var k=0; k<uoForUsersSpecial.length; k++) {
                var uoForUserSpecial = uoForUsersSpecial[k];
                if (uoSiper == uoForUserSpecial.codice_uo && uoForUserSpecial.ordine_da_validare == 'S'){
                $scope.utenteAbilitatoValidareUo = 'S';
                }
            }
        }
    }

    var inizializzaForm = function(){
        if ($scope.annullamentoModel.ordineMissione.tipoMissione === 'E') {
            if ($scope.annullamentoModel.ordineMissione.trattamento === 'T'){
                $scope.missioneEsteraConTam = true;
            } else {
                $scope.missioneEsteraConTam = false;
            }
        } else {
            $scope.missioneEstera = null;
        }
        $scope.onChangeTipoMissione();
        dateInizioFineDiverse();
    }

    $scope.estraiCdsRichFromAccount = function(account){
        if (account.codice_uo){
            return account.codice_uo.substring(0,3);
        }
        return "";
    }

    $scope.inizializzaFormPerInserimento = function(account){
        $scope.annullamentoModel = {nominativo:account.lastName+" "+account.firstName, 
                                        qualificaRich:account.profilo, livelloRich:account.livello, codiceFiscale:account.codice_fiscale, 
                                        dataNascita:account.data_nascita, luogoNascita:account.comune_nascita, validato:'N', 
                                        datoreLavoroRich:account.struttura_appartenenza, matricola:account.matricola,
            uoRich:ProxyService.buildUoRichiedenteSiglaFromUoSiper(account), cdsRich:$scope.estraiCdsRichFromAccount(account)};
        if (account.comune_residenza && account.cap_residenza){
            $scope.annullamentoModel.comuneResidenzaRich = account.comune_residenza+" - "+account.cap_residenza;
        }
        if (account.comune_residenza){
            $scope.annullamentoModel.comuneResidenzaRich = account.comune_residenza;
        }
        if (account.indirizzo_completo_residenza){
            $scope.annullamentoModel.indirizzoResidenzaRich = account.indirizzo_completo_residenza; 
        }

        $scope.missioneEsteraConTam = null;
        $scope.missioneEstera = null;
        $scope.annullamentoModel.uid = account.login;
        var today = $scope.oggi;
        $scope.annullamentoModel.dataInserimento = today;
        $scope.annullamentoModel.anno = today.getFullYear();
        $scope.showObbligoRientro = null;
        $scope.disabilitaAnnullamento = false;
    }

    $scope.gestioneInCasoDiErrore = function(){
        $scope.error = true;
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione dell'Annullamento Ordine di Missione Numero: "+$scope.annullamentoModel.ordineMissione.numero+" del "+$filter('date')($scope.annullamentoModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAnnullamentoOrdineMissione);
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per confermare l'Annullamento dell'Ordine di Missione Numero: "+$scope.annullamentoModel.ordineMissione.numero+" del "+$filter('date')($scope.annullamentoModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e la richiesta di annullamento non sarà più modificabile. Si desidera Continuare?", confirmAnnullamentoOrdineMissione);
    }

    var confirmAnnullamentoOrdineMissione = function () {
            $rootScope.salvataggio = true;
            AnnullamentoOrdineMissioneService.confirm($scope.annullamentoModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
	                    ui.ok_message("Annullamento Ordine di Missione confermato e inviato all'approvazione.");
                
                        ElencoOrdiniMissioneService.findAnnullamentoById($scope.annullamentoModel.id).then(function(data){
                            $scope.annullamentoModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.validateAnnullamentoOrdineMissione = function () {
            $rootScope.salvataggio = true;
            AnnullamentoOrdineMissioneService.confirm_validate($scope.annullamentoModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Annullamento Ordine di Missione confermato e inviato all'approvazione.");
                        ElencoOrdiniMissioneService.findAnnullamentoById($scope.annullamentoModel.id).then(function(data){
                            $scope.annullamentoModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteAnnullamentoOrdineMissione = function () {
            $rootScope.salvataggio = true;
            AnnullamentoOrdineMissioneService.delete({ids:$scope.annullamentoModel.id},
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.showEsisteAnnullamento = false;
                        $scope.idMissione = null;
                        $scope.annullamentoModel = {}
                        $scope.inizializzaFormPerInserimento($sessionStorage.account);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var serviziRestInizialiInserimento = function(){
        $scope.restNazioni();
        $scope.restCds($scope.annullamentoModel.anno, $scope.annullamentoModel.cdsRich);
        $scope.reloadCds($scope.annullamentoModel.cdsRich);
        $scope.restCapitoli($scope.annullamentoModel.anno);
        $scope.restCdsCompetenza($scope.annullamentoModel.anno, $scope.annullamentoModel.cdsRich);
    }

    $scope.reloadCds = function(cds) {
      $scope.annullaUo();  
      $scope.restUo($scope.annullamentoModel.anno, cds, $scope.annullamentoModel.uoRich);
    }

    $scope.annullaGae = function(){
      $scope.annullamentoModel.gae = null;
      $scope.gaeSelected = null;
    }

    $scope.annullaModulo = function(){
      $scope.annullaGae();
      $scope.annullamentoModel.pgProgetto = null;
    }

    $scope.annullaCdr = function(){
      $scope.annullaModulo();
      $scope.annullamentoModel.cdrSpesa = null;
    }

    $scope.annullaUo = function(){
      $scope.annullaCdr();
      $scope.annullamentoModel.uoSpesa = null;
    }

    $scope.reloadUserWork = function(uid){
        if (uid){
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.restOrdiniMissioneDaAnnullare(result);
                    $scope.accountModel = result;
                    $sessionStorage.accountWork = result;
                }
            });
        }
    }

    $scope.doPrintAnnullamentoOrdineMissione = function(idAnnullamentoOrdineMissione){
      $scope.annullamentoModel.stampaInCorso=true;
      $http.get('api/rest/annullamentoOrdineMissione/print/json',{params: {idMissione: idAnnullamentoOrdineMissione}})
        .success(function (data) {
            delete $scope.annullamentoModel.stampaInCorso;
        }).error(function (data) {
            delete $scope.annullamentoModel.stampaInCorso;
        }); 
    }

    $scope.previousPage = function () {
      parent.history.back();
    }

    $scope.confirmDeleteAttachment = function (attachment) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.get('api/rest/deleteAttachment/' + attachment.id);
        var y = x.then(function (result) {
            var attachments = $scope.annullamentoModel.attachments;
            if (attachments && Object.keys(attachments).length > 0){
                var newAttachments = attachments.filter(function(el){
                    return el.id !== attachment.id;
                });
                $scope.annullamentoModel.attachments = newAttachments;
                if (Object.keys(newAttachments).length = 0){
                    $scope.annullamentoModel.attachmentsExists = false;
                }
            }
            $rootScope.salvataggio = false;
            ui.ok();
        });
        x.error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.ritornaMittenteAnnullamento = function () {
            $rootScope.salvataggio = true;
            AnnullamentoOrdineMissioneService.return_sender($scope.annullamentoModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Annullamento Ordine di Missione respinto al mittente.");
                        AnnullamentoOrdineMissioneService.get($scope.annullamentoModel.id).then(function(data){
                            $scope.annullamentoModel = data;
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.save = function () {
            if ($scope.esisteAnnullamento()){
                $rootScope.salvataggio = true;
                AnnullamentoOrdineMissioneService.modify($scope.annullamentoModel,
                        function (value, responseHeaders) {
                            $scope.annullamentoModel = value;
                            $scope.annullamentoModel.idOrdineMissione = $scope.annullamentoModel.ordineMissione.id;
                            $rootScope.salvataggio = false;
                        },
                        function (httpResponse) {
                                $rootScope.salvataggio = false;
                        }
                );
            } else {
                $rootScope.salvataggio = true;
                AnnullamentoOrdineMissioneService.add($scope.annullamentoModel,
                        function (value, responseHeaders) {
                            $rootScope.salvataggio = false;
                            $scope.annullamentoModel = value;
                            $scope.elencoPersone = null;
                            $scope.uoForUsersSpecial = null;
                            $scope.inizializzaFormPerModifica();
                            $scope.annullamentoModel.isFireSearchAttachments = false;
                            var path = $location.path();
                            $location.path(path+'/'+$scope.annullamentoModel.id);
                        },
                        function (httpResponse) {
                            $rootScope.salvataggio = false;
                        }
                );
            }
    }


    $scope.idMissione = $routeParams.idMissione;
    $scope.validazione = $routeParams.validazione;
    $scope.accessToken = AccessToken.get();
    $sessionStorage.accountWork = null;

    if (isInQuery() || ($scope.annullamentoModel != null && $scope.annullamentoModel.idMissione)){
        ElencoOrdiniMissioneService.findAnnullamentoById($scope.idMissione).then(function(data){
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
                                $scope.restCds(model.anno, model.ordineMissione.cdsSpesa);
                                $scope.restCdsCompetenza(model.anno, model.ordineMissione.cdsCompetenza);
                                $scope.restUo(model.anno, model.ordineMissione.cdsSpesa, model.ordineMissione.uoSpesa);
                                $scope.restUoCompetenza(model.anno, model.ordineMissione.cdsCompetenza, model.ordineMissione.uoCompetenza);
                                $scope.restCdr(model.ordineMissione.uoSpesa, "S");
                                $scope.restModuli(model.anno, model.ordineMissione.uoSpesa);
                                $scope.restGae(model.anno, model.ordineMissione.pgProgetto, model.ordineMissione.cdrSpesa, model.ordineMissione.uoSpesa);
                                $scope.restCapitoli(model.anno);
                                $scope.annullamentoModel = model;
                                $scope.inizializzaFormPerModifica();
                                $scope.today();
                                $scope.gestioneUtenteAbilitatoValidare(model.ordineMissione.uoSpesa);
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
            $scope.restOrdiniMissioneDaAnnullare($sessionStorage.accountWork);
            $scope.today();
        }
    }
});
