'use strict';

missioniApp.factory('AnnullamentoRimborsoMissioneService', function ($resource, DateUtils) {
        return $resource('api/rest/annullamentoRimborsoMissione/:ids', {}, {
            'get': { method: 'GET', isArray: true},
            'add':  { method: 'POST',
                 transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dataInserimento = DateUtils.convertLocalDateToServer(copy.dataInserimento);
                    return angular.toJson(copy);
                }
            },
            'modify':  { method: 'PUT'},
            'delete':  { method: 'DELETE'},
            'confirm':  { method: 'PUT', params:{confirm:true, daValidazione:"N"}}
        });
    });

missioniApp.controller('AnnullamentoRimborsoMissioneController', function ($rootScope, $scope, $routeParams, $sessionStorage, AnnullamentoRimborsoMissioneService, RimborsoMissioneService, 
            ProxyService, ElencoRimborsiMissioneService, AccessToken,
            ui, $location, $filter, $http, COSTANTI, APP_FOR_REST, SIGLA_REST, URL_REST, TIPO_PAGAMENTO, Session, DateService) {

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
    
    $scope.reloadRimborsoMissione = function(idRimborsoMissione){
        $scope.annullamentoModel = {};

        for (var i=0; i<$scope.elencoRimborsiMissione.length; i++) {
            if ($scope.elencoRimborsiMissione[i].id === idRimborsoMissione){
                var rimborsoMissioneSelected = $scope.elencoRimborsiMissione[i];
                $scope.inizializzaFormPerInserimento($scope.accountModel);
                serviziRestInizialiInserimento();

                $scope.annullamentoModel.idRimborsoMissione = idRimborsoMissione;
                $scope.annullamentoModel.rimborsoMissione = rimborsoMissioneSelected;
                var today = $scope.oggi;
                $scope.annullamentoModel.dataInserimento = today;
                $scope.annullamentoModel.anno = today.getFullYear();

                $scope.annullamentoModel.comuneResidenzaRich = rimborsoMissioneSelected.comuneResidenzaRich;
                $scope.annullamentoModel.indirizzoResidenzaRich = rimborsoMissioneSelected.indirizzoResidenzaRich;
                $scope.annullamentoModel.domicilioFiscaleRich = rimborsoMissioneSelected.domicilioFiscaleRich;
                $scope.annullamentoModel.datoreLavoroRich = rimborsoMissioneSelected.datoreLavoroRich;
                $scope.annullamentoModel.contrattoRich = rimborsoMissioneSelected.contrattoRich;
                $scope.annullamentoModel.qualificaRich = rimborsoMissioneSelected.qualificaRich;
                $scope.annullamentoModel.livelloRich = rimborsoMissioneSelected.livelloRich;
                $scope.annullamentoModel.rimborsoMissione.priorita = rimborsoMissioneSelected.priorita;
                $scope.annullamentoModel.rimborsoMissione.oggetto = rimborsoMissioneSelected.oggetto;
                $scope.annullamentoModel.rimborsoMissione.destinazione = rimborsoMissioneSelected.destinazione;
                $scope.annullamentoModel.rimborsoMissione.nazione = rimborsoMissioneSelected.nazione;
                $scope.annullamentoModel.rimborsoMissione.tipoMissione = rimborsoMissioneSelected.tipoMissione;
                $scope.annullamentoModel.rimborsoMissione.trattamento = rimborsoMissioneSelected.trattamento;
                $scope.annullamentoModel.rimborsoMissione.dataInizioMissione = rimborsoMissioneSelected.dataInizioMissione;
                $scope.annullamentoModel.rimborsoMissione.dataFineMissione = rimborsoMissioneSelected.dataFineMissione;
                $scope.annullamentoModel.rimborsoMissione.dataInizioEstero = rimborsoMissioneSelected.dataInizioEstero;
                $scope.annullamentoModel.rimborsoMissione.dataFineEstero = rimborsoMissioneSelected.dataFineEstero;
                $scope.annullamentoModel.rimborsoMissione.voce = rimborsoMissioneSelected.voce;
                $scope.annullamentoModel.rimborsoMissione.gae = rimborsoMissioneSelected.gae;
                $scope.annullamentoModel.rimborsoMissione.cdsRich = rimborsoMissioneSelected.cdsRich;
                $scope.annullamentoModel.rimborsoMissione.uoRich = rimborsoMissioneSelected.uoRich;
                $scope.annullamentoModel.rimborsoMissione.cdrRich = rimborsoMissioneSelected.cdrRich;
                $scope.annullamentoModel.rimborsoMissione.cdsSpesa = rimborsoMissioneSelected.cdsSpesa;
                $scope.annullamentoModel.rimborsoMissione.uoSpesa = rimborsoMissioneSelected.uoSpesa;
                $scope.annullamentoModel.rimborsoMissione.cdrSpesa = rimborsoMissioneSelected.cdrSpesa;
                $scope.annullamentoModel.rimborsoMissione.cdsCompetenza = rimborsoMissioneSelected.cdsCompetenza;
                $scope.annullamentoModel.rimborsoMissione.uoCompetenza = rimborsoMissioneSelected.uoCompetenza;
                $scope.annullamentoModel.rimborsoMissione.pgProgetto = rimborsoMissioneSelected.pgProgetto;
                $scope.annullamentoModel.rimborsoMissione.utilizzoTaxi = rimborsoMissioneSelected.utilizzoTaxi;
                $scope.annullamentoModel.rimborsoMissione.utilizzoAutoNoleggioServizio = rimborsoMissioneSelected.utilizzoAutoServizio;
                $scope.annullamentoModel.rimborsoMissione.personaleAlSeguito = rimborsoMissioneSelected.personaleAlSeguito;
                $scope.annullamentoModel.rimborsoMissione.utilizzoAutoNoleggio = rimborsoMissioneSelected.utilizzoAutoNoleggio;
                $scope.annullamentoModel.rimborsoMissione.noteUtilizzoTaxiNoleggio = rimborsoMissioneSelected.noteUtilizzoTaxiNoleggio;
                $scope.annullamentoModel.modpag = rimborsoMissioneSelected.modpag;
                $scope.annullamentoModel.tipoPagamento = rimborsoMissioneSelected.tipoPagamento;
                $scope.annullamentoModel.pgBanca = rimborsoMissioneSelected.pgBanca;
                $scope.annullamentoModel.iban = rimborsoMissioneSelected.iban;
                $scope.annullamentoModel.cdTerzoSigla = rimborsoMissioneSelected.cdTerzoSigla;
                $scope.annullamentoModel.rimborsoMissione.cup = rimborsoMissioneSelected.cup;
                if ($scope.annullamentoModel.rimborsoMissione.uoSpesa){
                    $scope.restUo($scope.annullamentoModel.rimborsoMissione.anno, $scope.annullamentoModel.rimborsoMissione.cdsSpesa, $scope.annullamentoModel.rimborsoMissione.uoSpesa);
                    $scope.restModuli($scope.annullamentoModel.rimborsoMissione.anno, $scope.annullamentoModel.rimborsoMissione.uoSpesa);
                    $scope.restGae($scope.annullamentoModel.rimborsoMissione.anno, $scope.annullamentoModel.rimborsoMissione.pgProgetto, $scope.annullamentoModel.rimborsoMissione.cdrSpesa, $scope.annullamentoModel.rimborsoMissione.uoSpesa);
                }
                if ($scope.annullamentoModel.rimborsoMissione.cdsCompetenza){
                    $scope.restCdsCompetenza($scope.annullamentoModel.rimborsoMissione.anno, $scope.annullamentoModel.rimborsoMissione.cdsCompetenza);
                }
                if ($scope.annullamentoModel.rimborsoMissione.uoCompetenza){
                    $scope.restUoCompetenza($scope.annullamentoModel.rimborsoMissione.anno, $scope.annullamentoModel.rimborsoMissione.cdsCompetenza, $scope.annullamentoModel.rimborsoMissione.uoCompetenza);
                }
                if ($scope.annullamentoModel.rimborsoMissione.cdrSpesa){
                    $scope.restCdr($scope.annullamentoModel.rimborsoMissione.uoSpesa, "S");
                }
                if ($scope.annullamentoModel.tipoPagamento && $scope.annullamentoModel.cdTerzoSigla){
                    $scope.recuperoDatiModalitaPagamento($scope.annullamentoModel.cdTerzoSigla);
                    $scope.recuperoDatiTerzoModalitaPagamento($scope.annullamentoModel.cdTerzoSigla, $scope.annullamentoModel.tipoPagamento);
                }

                inizializzaForm();
                $scope.recuperoDatiDivisa();
                break;
            }
        }
    }

    $scope.recuperoDatiModalitaPagamento = function(terzoSigla){
        ProxyService.getModalitaPagamento(terzoSigla).then(function(ret){
            if (ret && ret.data && ret.data.elements){
                $scope.modalitaPagamentos = ret.data.elements;
            }
        });
    }

    $scope.recuperoDatiTerzoModalitaPagamento = function(terzoSigla, tipoPagamento){
        ProxyService.getTerzoModalitaPagamento(terzoSigla, tipoPagamento).then(function(ret){
            if (ret && ret.data && ret.data.elements){
                $scope.terzoModalitaPagamentos = ret.data.elements;
            }
        });
    }

    $scope.restRimborsiMissioneDaAnnullare = function(userWork){
        ElencoRimborsiMissioneService.findRimborsiMissioneDaAnnullare(userWork.login).then(function(data){
            $scope.elencoRimborsiMissione = data.data;
        });
    }

    $scope.recuperoDatiDivisa = function(){
        var dataInizio = moment($scope.annullamentoModel.rimborsoMissione.dataInizioMissione).format("DD/MM/YYYY");

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
                $scope.annullamentoModel.rimborsoMissione.cdsRich = $scope.formatResultCds(listaCds[0]);
            } else {
                if (cds){
                    $scope.elencoCds = [];
                    var ind = 0;
                    for (var i=0; i<listaCds.length; i++) {
                        if (listaCds[i].cd_proprio_unita === cds){
                            $scope.elencoCds[0] = $scope.formatResultCds(listaCds[i]);
                        } else {
                            ind ++;
                            $scope.elencoCds[ind] = $scope.formatResultCds(listaCds[i]);
                        }
                    }
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
		                $scope.annullamentoModel.rimborsoMissione.uoSpesa = $scope.elencoUo[0].cd_unita_organizzativa;
                        $scope.restCdr($scope.annullamentoModel.rimborsoMissione.uoSpesa,"N");
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
                            $scope.annullamentoModel.rimborsoMissione.uoCompetenza = $scope.elencoUoCompetenza[0].cd_unita_organizzativa;
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
                            $scope.annullamentoModel.rimborsoMissione.cdrSpesa = data.elements[0].cd_centro_responsabilita;
                            if (daQuery != 'S'){
                                $scope.restModuli($scope.annullamentoModel.anno, $scope.annullamentoModel.rimborsoMissione.uoSpesa);
                                $scope.restGae($scope.annullamentoModel.anno, $scope.annullamentoModel.rimborsoMissione.pgProgetto, $scope.annullamentoModel.rimborsoMissione.cdrSpesa, $scope.annullamentoModel.rimborsoMissione.uoSpesa);
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
                            $scope.annullamentoModel.rimborsoMissione.modulo = data.elements[0].pg_progetto;
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
        if ($scope.annullamentoModel.rimborsoMissione.esercizioOriginaleObbligazione && $scope.annullamentoModel.rimborsoMissione.pgObbligazione){
            var app = APP_FOR_REST.SIGLA;
            var url = null;
            var varClauses = [];
            if ($scope.gaeSelected){
                url = SIGLA_REST.IMPEGNO_GAE;
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.anno},
                              {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.pgObbligazione},
                              {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.gaeSelected}];
            } else {
                url = SIGLA_REST.IMPEGNO;
                varClauses = [{condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.anno},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.annullamentoModel.rimborsoMissione. pgObbligazione}];
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
                            $scope.annullamentoModel.rimborsoMissione.gae = data.elements[0].cd_linea_attivita;
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
                            $scope.annullamentoModel.rimborsoMissione.voce = listaVoci[0];
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
        $scope.gestioneUtenteAbilitatoValidare(uo, null);
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
        if ($scope.annullamentoModel.rimborsoMissione.tipoMissione === 'E') {
            if (!$scope.annullamentoModel.rimborsoMissione.trattamento){
                $scope.annullamentoModel.rimborsoMissione.trattamento = "R";
                $scope.missioneEsteraConTam = false;
            }
            $scope.missioneEstera = true;
        } else {
            $scope.annullamentoModel.rimborsoMissione.trattamento = "R";
            $scope.missioneEstera = null;
            $scope.missioneEsteraConTam = null;
            $scope.annullamentoModel.rimborsoMissione.nazione = null;
            $scope.annullamentoModel.rimborsoMissione.dataInizioEstero = null;
            $scope.annullamentoModel.rimborsoMissione.dataFineEstero = null;
        }
    };

    var dateInizioFineDiverse = function() {
        if ($scope.annullamentoModel.rimborsoMissione.dataInizioMissione === undefined || 
            $scope.annullamentoModel.rimborsoMissione.dataFineMissione === undefined ||
            $scope.annullamentoModel.rimborsoMissione.dataInizioMissione == null || 
            $scope.annullamentoModel.rimborsoMissione.dataFineMissione === null ||
            $scope.annullamentoModel.rimborsoMissione.dataInizioMissione === "" || 
            $scope.annullamentoModel.rimborsoMissione.dataFineMissione === "" ||
            $scope.annullamentoModel.rimborsoMissione.dataFineMissione === $scope.annullamentoModel.rimborsoMissione.dataInizioMissione) {
          $scope.showObbligoRientro = null;
        } else {
            var dataInizio = moment($scope.annullamentoModel.rimborsoMissione.dataInizioMissione).format("DD/MM/YYYY");
            var dataFine = moment($scope.annullamentoModel.rimborsoMissione.dataFineMissione).format("DD/MM/YYYY");
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
            $scope.annullamentoModel.statoFlusso === 'APP' || $scope.annullamentoModel.stato === 'ANN' )) {
          return true;
        } else {
          return false;
        }
    }

    $scope.inizializzaFormPerModifica = function(){
        $scope.annullamentoModel.idRimborsoMissione = $scope.annullamentoModel.rimborsoMissione.id;
        $scope.showEsisteAnnullamento = true;
	    $scope.showCommentFlows = false;
        $scope.disabilitaAnnullamento = impostaDisabilitaAnnullamento();
        
        if ($scope.annullamentoModel && $scope.annullamentoModel.rimborsoMissione){
            $scope.elencoRimborsiMissione = [];
            $scope.elencoRimborsiMissione.push($scope.annullamentoModel.rimborsoMissione);
        }

        inizializzaForm();
    }

    $scope.gestioneUtenteAbilitatoValidare = function (uo, annullamentoModel){
        $scope.utenteAbilitatoValidareUo = 'N';
        var uoForUsersSpecial= $sessionStorage.account.uoForUsersSpecial;
        if (uo){
            var uoSiper = uo.replace('.','');
            for (var k=0; k<uoForUsersSpecial.length; k++) {
                var uoForUserSpecial = uoForUsersSpecial[k];
                if (uoSiper == uoForUserSpecial.codice_uo && (uoForUserSpecial.ordine_da_validare == 'S' || (annullamentoModel != null && annullamentoModel.isUoDaValidare == 'N'))){
                    $scope.utenteAbilitatoValidareUo = 'S';
                }
            }
        }
    }

    var inizializzaForm = function(){
        if ($scope.annullamentoModel.rimborsoMissione.tipoMissione === 'E') {
            if ($scope.annullamentoModel.rimborsoMissione.trattamento === 'T'){
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
        ui.confirmCRUD("Confermi l'eliminazione dell'Annullamento Rimborso Missione Numero: "+$scope.annullamentoModel.rimborsoMissione.numero+" del "+$filter('date')($scope.annullamentoModel.rimborsoMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAnnullamentoRimborsoMissione);
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per annullare il Rimborso Missione Numero: "+$scope.annullamentoModel.rimborsoMissione.numero+" del "+$filter('date')($scope.annullamentoModel.rimborsoMissione.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione cancellerà anche la missione in SIGLA. Si desidera Continuare?", confirmAnnullamentoRimborsoMissione);
    }

    var confirmAnnullamentoRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            AnnullamentoRimborsoMissioneService.confirm($scope.annullamentoModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
	                    ui.ok_message("Rimborso Missione annullato.");
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

    var deleteAnnullamentoRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            AnnullamentoRimborsoMissioneService.delete({ids:$scope.annullamentoModel.id},
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
                    $scope.restRimborsiMissioneDaAnnullare(result);
                    $scope.accountModel = result;
                    $sessionStorage.accountWork = result;
                }
            });
        }
    }

    $scope.previousPage = function () {
      parent.history.back();
    }

    $scope.confirmDeleteAttachment = function (attachment) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.get('api/rest/annullamentoRimborsoMissione/deleteAttachment/' + attachment.id+'/'+$scope.annullamentoModel.id);
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

    $scope.viewAttachments = function (idAnnullamento) {
        if (!$scope.annullamentoModel.isFireSearchAttachments){
            $http.get('api/rest/annullamentoRimborsoMissione/viewAttachments/' + idAnnullamento).then(function (data) {
                $scope.annullamentoModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;  
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.annullamentoModel.attachments = attachments;
            }, function () {
                $scope.annullamentoModel.isFireSearchAttachments = false;
                $scope.annullamentoModel.attachmentsExists = false;
                $scope.annullamentoModel.attachments = {};
            });
        }
//        $scope.anticipoOrdineMissioneModel.viewAttachment = true;
    }

    $scope.save = function () {
            if ($scope.esisteAnnullamento()){
                $rootScope.salvataggio = true;
                AnnullamentoRimborsoMissioneService.modify($scope.annullamentoModel,
                        function (value, responseHeaders) {
                            $scope.annullamentoModel = value;
                            $scope.viewAttachments($scope.annullamentoModel.id);
                            $scope.annullamentoModel.idRimborsoMissione = $scope.annullamentoModel.rimborsoMissione.id;
                            $rootScope.salvataggio = false;
                        },
                        function (httpResponse) {
                                $rootScope.salvataggio = false;
                        }
                );
            } else {
                $rootScope.salvataggio = true;
                AnnullamentoRimborsoMissioneService.add($scope.annullamentoModel,
                        function (value, responseHeaders) {
                            $rootScope.salvataggio = false;
                            $scope.annullamentoModel = value;
                            $scope.elencoPersone = null;
                            $scope.uoForUsersSpecial = null;
                            $scope.inizializzaFormPerModifica();
                            $scope.viewAttachments($scope.annullamentoModel.id);
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
        ElencoRimborsiMissioneService.findAnnullamentoById($scope.idMissione).then(function(data){
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
                                $scope.restCds(model.anno, model.rimborsoMissione.cdsSpesa);
                                $scope.restCdsCompetenza(model.anno, model.rimborsoMissione.cdsCompetenza);
                                $scope.restUo(model.anno, model.rimborsoMissione.cdsSpesa, model.rimborsoMissione.uoSpesa);
                                $scope.restUoCompetenza(model.anno, model.rimborsoMissione.cdsCompetenza, model.rimborsoMissione.uoCompetenza);
                                $scope.restCdr(model.rimborsoMissione.uoSpesa, "S");
                                $scope.restModuli(model.anno, model.rimborsoMissione.uoSpesa);
                                $scope.restGae(model.anno, model.rimborsoMissione.pgProgetto, model.rimborsoMissione.cdrSpesa, model.rimborsoMissione.uoSpesa);
                                $scope.restCapitoli(model.anno);
                                $scope.annullamentoModel = model;
                                $scope.viewAttachments($scope.annullamentoModel.id);
                                $scope.inizializzaFormPerModifica();
                                $scope.today();
                                $scope.gestioneUtenteAbilitatoValidare(model.rimborsoMissione.uoSpesa, model);
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
            $scope.restRimborsiMissioneDaAnnullare($sessionStorage.accountWork);
            $scope.today();
        }
    }
});
