'use strict';

missioniApp.factory('RimborsoMissioneService', function ($resource, DateUtils) {
        return $resource('api/rest/rimborsoMissione/:ids', {}, {
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
            'confirm':  { method: 'PUT', params:{confirm:true, daValidazione:"N"}},
            'confirm_validate':  { method: 'PUT', params:{confirm:true, daValidazione:"S"}},
            'return_sender':  { method: 'PUT', params:{confirm:false, daValidazione:"R"}},
            'finalize':  { method: 'PUT', params:{confirm:false, daValidazione:"D"}}
        });
    });

missioniApp.controller('RimborsoMissioneController', function ($rootScope, $scope, $routeParams, $sessionStorage, RimborsoMissioneService, OrdineMissioneService, ProxyService, ElencoOrdiniMissioneService, ElencoRimborsiMissioneService, AccessToken,
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
    };

    var isInQuery = function(){
        if ($scope.idMissione === undefined || $scope.idMissione === "" ) {
            return false;
        } else {
            return true;
        }
    }
    
    $scope.reloadOrdineMissione = function(idOrdineMissione){
        $scope.rimborsoMissioneModel = {};
        $scope.listaAltriRimborsi = {};
        $scope.esistonoAltriRimborsi = false;


        for (var i=0; i<$scope.elencoOrdiniMissione.length; i++) {
            if ($scope.elencoOrdiniMissione[i].id === idOrdineMissione){
                var ordineMissioneSelected = $scope.elencoOrdiniMissione[i];
                $scope.inizializzaFormPerInserimento($scope.accountModel);
                serviziRestInizialiInserimento();

                $scope.rimborsoMissioneModel.idOrdineMissione = idOrdineMissione;
                $scope.rimborsoMissioneModel.ordineMissione = ordineMissioneSelected;
                var today = $scope.oggi;
                $scope.rimborsoMissioneModel.dataInserimento = today;
                $scope.rimborsoMissioneModel.anno = today.getFullYear();

                if (!$scope.rimborsoMissioneModel.comuneResidenzaRich){
                    $scope.rimborsoMissioneModel.comuneResidenzaRich = ordineMissioneSelected.comuneResidenzaRich;
                }
                if (!$scope.rimborsoMissioneModel.indirizzoResidenzaRich){
                    $scope.rimborsoMissioneModel.indirizzoResidenzaRich = ordineMissioneSelected.indirizzoResidenzaRich;
                }

                $scope.rimborsoMissioneModel.domicilioFiscaleRich = ordineMissioneSelected.domicilioFiscaleRich;
                $scope.rimborsoMissioneModel.datoreLavoroRich = ordineMissioneSelected.datoreLavoroRich;
                $scope.rimborsoMissioneModel.contrattoRich = ordineMissioneSelected.contrattoRich;
                $scope.rimborsoMissioneModel.qualificaRich = ordineMissioneSelected.qualificaRich;
                $scope.rimborsoMissioneModel.livelloRich = ordineMissioneSelected.livelloRich;
                $scope.rimborsoMissioneModel.priorita = ordineMissioneSelected.priorita;
                $scope.rimborsoMissioneModel.oggetto = ordineMissioneSelected.oggetto;
                $scope.rimborsoMissioneModel.destinazione = ordineMissioneSelected.destinazione;
                $scope.rimborsoMissioneModel.nazione = ordineMissioneSelected.nazione;
                $scope.rimborsoMissioneModel.tipoMissione = ordineMissioneSelected.tipoMissione;
                $scope.rimborsoMissioneModel.trattamento = ordineMissioneSelected.trattamento;
                $scope.rimborsoMissioneModel.dataInizioMissione = ordineMissioneSelected.dataInizioMissione;
                $scope.rimborsoMissioneModel.dataFineMissione = ordineMissioneSelected.dataFineMissione;

                $scope.rimborsoMissioneModel.voce = ordineMissioneSelected.voce;
                $scope.rimborsoMissioneModel.gae = ordineMissioneSelected.gae;
                $scope.rimborsoMissioneModel.cdsRich = ordineMissioneSelected.cdsRich;
                $scope.rimborsoMissioneModel.uoRich = ordineMissioneSelected.uoRich;
                $scope.rimborsoMissioneModel.cdrRich = ordineMissioneSelected.cdrRich;
                $scope.rimborsoMissioneModel.cdsSpesa = ordineMissioneSelected.cdsSpesa;
                $scope.rimborsoMissioneModel.uoSpesa = ordineMissioneSelected.uoSpesa;
                $scope.rimborsoMissioneModel.cdrSpesa = ordineMissioneSelected.cdrSpesa;
                $scope.rimborsoMissioneModel.cdsCompetenza = ordineMissioneSelected.cdsCompetenza;
                $scope.rimborsoMissioneModel.uoCompetenza = ordineMissioneSelected.uoCompetenza;
                $scope.rimborsoMissioneModel.pgProgetto = ordineMissioneSelected.pgProgetto;
                $scope.rimborsoMissioneModel.cdcdsObbligazione = ordineMissioneSelected.cdcdsObbligazione;
                $scope.rimborsoMissioneModel.esercizioOriginaleObbligazione = ordineMissioneSelected.esercizioOriginaleObbligazione;
                $scope.rimborsoMissioneModel.esercizioObbligazione = ordineMissioneSelected.esercizioObbligazione;
                $scope.rimborsoMissioneModel.pgObbligazione = ordineMissioneSelected.pgObbligazione;
                $scope.rimborsoMissioneModel.utilizzoTaxi = ordineMissioneSelected.utilizzoTaxi;
                $scope.rimborsoMissioneModel.utilizzoAutoNoleggioServizio = ordineMissioneSelected.utilizzoAutoServizio;
                $scope.rimborsoMissioneModel.personaleAlSeguito = ordineMissioneSelected.personaleAlSeguito;
                $scope.rimborsoMissioneModel.utilizzoAutoNoleggio = ordineMissioneSelected.utilizzoAutoNoleggio;
                $scope.rimborsoMissioneModel.noteUtilizzoTaxiNoleggio = ordineMissioneSelected.noteUtilizzoTaxiNoleggio;
                $scope.rimborsoMissioneModel.partenzaDa = ordineMissioneSelected.partenzaDa;
                $scope.rimborsoMissioneModel.importoPresunto = ordineMissioneSelected.importoPresunto;
                $scope.rimborsoMissioneModel.cup = ordineMissioneSelected.cup;
                $scope.rimborsoMissioneModel.cug = ordineMissioneSelected.cug;
                $scope.rimborsoMissioneModel.presidente = ordineMissioneSelected.presidente;
                $scope.rimborsoMissioneModel.totaleRimborsoComplessivo = 0;
                $scope.rimborsoMissioneModel.totaleRimborsoSenzaAnticipi = 0;
                $scope.rimborsoMissioneModel.autoPropria = ordineMissioneSelected.utilizzoAutoPropria;
                if ($scope.rimborsoMissioneModel.uoSpesa){
                    $scope.restUo($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.cdsSpesa, $scope.rimborsoMissioneModel.uoSpesa);
                    $scope.restModuli($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.uoSpesa);
                    $scope.restGae($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.pgProgetto, $scope.rimborsoMissioneModel.cdrSpesa, $scope.rimborsoMissioneModel.uoSpesa);
                }
                if ($scope.rimborsoMissioneModel.cdsCompetenza){
                    $scope.restCdsCompetenza($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.cdsCompetenza);
                }
                if ($scope.rimborsoMissioneModel.uoCompetenza){
                    $scope.restUoCompetenza($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.cdsCompetenza, $scope.rimborsoMissioneModel.uoCompetenza);
                }
                if ($scope.rimborsoMissioneModel.cdrSpesa){
                    $scope.restCdr($scope.rimborsoMissioneModel.uoSpesa, "S");
                }
                $scope.rimborsoMissioneModel.anticipoRicevuto = "N";
                $scope.rimborsoMissioneModel.speseTerziRicevute = "N";
                $scope.rimborsoMissioneModel.rimborso0 = "N";
                inizializzaForm();
                $scope.recuperoDatiDivisa();
                $scope.recuperoDatiAltriRimborsi($scope.rimborsoMissioneModel.idOrdineMissione);
                break;
            }
        }
    }

    $scope.restOrdiniMissioneDaRimborsare = function(userWork, ordiniGiaRimborsati){
        ElencoOrdiniMissioneService.findMissioniDaRimborsare(userWork.login, ordiniGiaRimborsati).then(function(data){
            $scope.elencoOrdiniMissione = data;
        });
    }

    $scope.recuperoDatiTerzoSigla = function(userWork){
        if (userWork.codice_fiscale){
            ProxyService.getTerzo(userWork.codice_fiscale).then(function(ret){
                if (ret && ret.data && ret.data.elements){
                    $scope.terzoSigla = ret.data.elements[0];
                    $scope.recuperoDatiInquadramento(userWork, $scope.terzoSigla);
                    $scope.recuperoDatiModalitaPagamento(userWork, $scope.terzoSigla);
                }
            });
        } else {
            ui.error("Codice Fiscale non presente.");
        }
    }

    $scope.recuperoDatiDivisa = function(){
        var dataInizio = moment($scope.rimborsoMissioneModel.dataInizioMissione).format("DD/MM/YYYY");

        ProxyService.getDatiDivisa("EURO").then(function(ret){
            if (ret && ret.data){
                $scope.divisa = ret.data.elements;
            }
        });
    }

    $scope.recuperoDatiAltriRimborsi = function(idOrdineMissione){
        ElencoRimborsiMissioneService.findRimborsiMissione(null, null, null, null, null, null, null, null, null, null, null, idOrdineMissione, "S").then(function(ret){
            $scope.listaAltriRimborsi = false;
            var newRet = [];
            if (ret && ret.length > 0){
                for (var i=0; i<ret.length; i++) {
                    if (ret[i].id != $scope.rimborsoMissioneModel.id){
                        newRet.push(ret[i]);
                    }
                }    
                if (newRet && newRet.length > 0){
                    $scope.esistonoAltriRimborsi = true;
                    $scope.listaAltriRimborsi = newRet;
                } else {
                    $scope.esistonoAltriRimborsi = false;
                }   
            } else {
                $scope.esistonoAltriRimborsi = false;
            }
        });
    }

    $scope.recuperoDatiInquadramento = function(userWork, terzoSigla){
        ProxyService.getInquadramento(terzoSigla.cd_anag).then(function(ret){
            if (ret && ret.data && ret.data.elements && ret.data.elements.length > 0){
                $scope.inquadramento = ret.data.elements;
            } else {
                ui.error("Inquadramento non trovato");
            }
        });
    }


    var recuperoDatiMandatoMissioneSigla = function(rimborsoMissione){
        ProxyService.getMandatiMissioneSigla(rimborsoMissione).then(function(ret){
            $scope.rimborsoMissioneModel.statoPagamento = 'Missione in Liquidazione';
            if (ret && ret.data && ret.data.elements){
                var mandatiMissione = ret.data.elements;
                if (mandatiMissione.length > 0) {
                    var mandato = mandatiMissione[0];
                    var msgMandato = 'Mandato '+mandato.pg_mandato;
                    if (mandato.dt_pagamento){
                        $scope.rimborsoMissioneModel.statoPagamento = msgMandato+' pagato il '+moment(mandato.dt_pagamento).format("DD/MM/YYYY");
                    } else if (mandato.dt_trasmissione){
                        $scope.rimborsoMissioneModel.statoPagamento = msgMandato+' inviato in Banca il '+moment(mandato.dt_trasmissione).format("DD/MM/YYYY");
                    } else if (mandato.dt_emissione){
                        $scope.rimborsoMissioneModel.statoPagamento = msgMandato+' emesso il '+moment(mandato.dt_emissione).format("DD/MM/YYYY")+', non ancora inviato in Banca';
                    } 
                }
            }
        });
    }

    $scope.recuperoDatiModalitaPagamento = function(userWork, terzoSigla){
        ProxyService.getModalitaPagamento(terzoSigla.cd_terzo).then(function(ret){
            if (ret && ret.data && ret.data.elements){
                $scope.modalitaPagamentos = ret.data.elements;
            }
        });
    }

    $scope.recuperoDatiTerzoModalitaPagamento = function(terzoSigla, tipoPagamento){
        ProxyService.getTerzoModalitaPagamento(terzoSigla, tipoPagamento).then(function(ret){
            if (ret && ret.data && ret.data.elements){
                $scope.terzoModalitaPagamentos = ret.data.elements;
                if ($scope.rimborsoMissioneModel && $scope.terzoModalitaPagamentos && $scope.terzoModalitaPagamentos.length == 1) {
                    if (!$scope.rimborsoMissioneModel.pgBanca){
                        $scope.rimborsoMissioneModel.pgBanca = $scope.terzoModalitaPagamentos[0].pg_banca;
                        $scope.rimborsoMissioneModel.iban = $scope.terzoModalitaPagamentos[0].codice_iban;
                    }
                }
            }
        });
    }

    $scope.recuperoDatiMandato = function(terzoSigla, annoMandato, numeroMandato){
        ProxyService.getMandato(terzoSigla.cd_terzo, annoMandato, numeroMandato).then(function(ret){
            if (ret && ret.data && ret.data.elements){
                $scope.datiMandato = ret.data.elements[0];
                if ($scope.datiMandato){
                    $scope.rimborsoMissioneModel.anticipoImporto = $scope.datiMandato.im_pagato_incassato;
                } else {
                    ui.error("Il mandato indicato non esiste");
                }
            }
        });
    }

    $scope.validateMandato = function () {
      if ($scope.rimborsoMissioneModel.anticipoAnnoMandato && $scope.rimborsoMissioneModel.anticipoNumeroMandato){
        $scope.datiMandato = $scope.recuperoDatiMandato($scope.terzoSigla, $scope.rimborsoMissioneModel.anticipoAnnoMandato, $scope.rimborsoMissioneModel.anticipoNumeroMandato);        
      } else {
        ui.error("Indicare l'anno e il numero del mandato");
      }
    }

    var controlliPrimaDelSalvataggio = function(){
        if ($scope.impegnoSelected){
            if ($scope.uoSpesaSelected.cd_unita_organizzativa) {
                $scope.rimborsoMissioneModel.uoSpesa = $scope.uoSpesaSelected.cd_unita_organizzativa;
                $scope.rimborsoMissioneModel.objUoSpesa = $scope.uoSpesaSelected;
            }
        }
        if ($scope.rimborsoMissioneModel.trattamento=== 'R'){
            $scope.rimborsoMissioneModel.dataInizioEstero = null;
            $scope.rimborsoMissioneModel.dataFineEstero = null;
        }
        return $scope.impostaInquadramento();
    }

    $scope.impostaInquadramento = function(){
        if ($scope.terzoSigla && $scope.inquadramento){
            var trovatoInquadramento = false;
            var inqMisto = null;
            var rappMisto = null;
            var trovatoInquadramentoMistoInizio = false;
            var trovatoInquadramentoMistoFine = false;
            var dataFineTroncata = new Date(new Date($scope.rimborsoMissioneModel.dataFineMissione).setHours(0,0,0,0));
            var dataInizioTroncata = new Date(new Date($scope.rimborsoMissioneModel.dataInizioMissione).setHours(0,0,0,0));
            for (var i=0; i<$scope.inquadramento.length; i++) {
                var inquadramento = $scope.inquadramento[i];
                if (inquadramento.cd_tipo_rapporto == "DIP"){
                    if (new Date(inquadramento.dt_ini_validita) <= dataInizioTroncata && 
                        new Date(inquadramento.dt_fin_validita) >= dataFineTroncata){
                        $scope.rimborsoMissioneModel.inquadramento = inquadramento.pg_rif_inquadramento;
                        $scope.rimborsoMissioneModel.cdTipoRapporto = inquadramento.cd_tipo_rapporto;
                        trovatoInquadramento = true;
                    } else {
                        if (new Date(inquadramento.dt_ini_validita) <= dataInizioTroncata && 
                            new Date(inquadramento.dt_fin_validita) >= dataInizioTroncata){
                            inqMisto = inquadramento.pg_rif_inquadramento;
                            rappMisto = inquadramento.cd_tipo_rapporto;
                            trovatoInquadramentoMistoInizio = true;
                        }
                        if (new Date(inquadramento.dt_ini_validita) <= dataFineTroncata && 
                            new Date(inquadramento.dt_fin_validita) >= dataFineTroncata){
                            trovatoInquadramentoMistoFine = true;
                        }
                    }
                }
            }
            if (!trovatoInquadramento){
                for (var i=0; i<$scope.inquadramento.length; i++) {
                    var inquadramento = $scope.inquadramento[i];
                    if (new Date(inquadramento.dt_ini_validita) <= dataInizioTroncata && 
                        new Date(inquadramento.dt_fin_validita) >= dataFineTroncata){
                        $scope.rimborsoMissioneModel.inquadramento = inquadramento.pg_rif_inquadramento;
                        $scope.rimborsoMissioneModel.cdTipoRapporto = inquadramento.cd_tipo_rapporto;
                        trovatoInquadramento = true;
                    } else {
                        if (!trovatoInquadramentoMistoInizio || !trovatoInquadramentoMistoFine){
                            if (new Date(inquadramento.dt_ini_validita) <= dataInizioTroncata && 
                                new Date(inquadramento.dt_fin_validita) >= dataInizioTroncata){
                                inqMisto = inquadramento.pg_rif_inquadramento;
                                rappMisto = inquadramento.cd_tipo_rapporto;
                                trovatoInquadramentoMistoInizio = true;
                            }
                            if (new Date(inquadramento.dt_ini_validita) <= dataFineTroncata && 
                                new Date(inquadramento.dt_fin_validita) >= dataFineTroncata){
                                trovatoInquadramentoMistoFine = true;
                            }
                        }
                    }
                }
            }

            if (!trovatoInquadramento){
                if (trovatoInquadramentoMistoInizio && trovatoInquadramentoMistoFine){
                    $scope.rimborsoMissioneModel.inquadramento = inqMisto;
                    $scope.rimborsoMissioneModel.cdTipoRapporto = rappMisto;
                    return true;
                } else {
                    ui.error("Per la data della missione non esiste un inquadramento valido.");
                    return false;
                }

            }
        }
        return true;
    }

    $scope.formatResultCdr = function(item) {
      return item.cd_centro_responsabilita+' '+item.ds_cdr;
    }

    $scope.undoCds = function(){
        $scope.rimborsoMissioneModel.cdsSpesa = null;
    };

    $scope.undoVoce = function(){
        $scope.rimborsoMissioneModel.voce = null;
    };

    var caricaCds = function(cds, listaCds){
        if (listaCds){
            if (listaCds.length === 1){
                $scope.rimborsoMissioneModel.cdsRich = listaCds[0].cd_proprio_unita;
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
                    if ($scope.rimborsoMissioneModel){
                        $scope.rimborsoMissioneModel.cdsRich = cds;
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
                $scope.rimborsoMissioneModel.cdsCompetenza = $scope.formatResultCds(listaCds[0]);
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
                    if ($scope.rimborsoMissioneModel){
                        $scope.rimborsoMissioneModel.cdsCompetenza = cds;
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

    $scope.onChangeDataDettaglio = function() {
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.dataSpesa){
            $scope.restTipiSpesa($scope.newDettaglioSpesa.dataSpesa, $scope.rimborsoMissioneModel.tipoMissione);
        } else {
            $scope.tipi_spesa = {};
        }
    }

    $scope.onChangeTerzoModpag = function() {
        var pgBanca = $scope.rimborsoMissioneModel.pgBanca;
        if (pgBanca){
            for (var i=0; i<$scope.terzoModalitaPagamentos.length; i++) {
                var terzoModalitaPagamento = $scope.terzoModalitaPagamentos[i];
                if (terzoModalitaPagamento.pg_banca === pgBanca){
                    $scope.rimborsoMissioneModel.iban = terzoModalitaPagamento.codice_iban;
                }
            }
        }
    }

    $scope.onChangeModpag = function(modpag) {
        var modpag = $scope.rimborsoMissioneModel.modpag;
        if (modpag){
            for (var i=0; i<$scope.modalitaPagamentos.length; i++) {
                var modalitaPagamento = $scope.modalitaPagamentos[i];
                if (modalitaPagamento.cd_modalita_pag === modpag){
                    $scope.rimborsoMissioneModel.tipoPagamento = modalitaPagamento.ti_pagamento;
                    $scope.recuperoDatiTerzoModalitaPagamento($scope.terzoSigla.cd_terzo, modalitaPagamento.ti_pagamento);
                    $scope.rimborsoMissioneModel.iban = null;
                    $scope.rimborsoMissioneModel.pgBanca = null;
                }
            }
        }
    }

    $scope.restTipiSpesa = function(data, tipoMissione){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.SIGLA;
        var url = SIGLA_REST.TIPO_SPESA;
        var objectPostTipiSpesaOrderBy = [{name: 'ds_ti_spesa', type: 'ASC'}];
        var objectPostTipiSpesaClauses = null;
        if (tipoMissione == "I"){
            objectPostTipiSpesaClauses = [{condition: 'AND', fieldName: 'ti_area_geografica', operator: "!=", fieldValue:'E'},
                              {condition: 'AND', fieldName: 'dt_inizio_validita', operator: ">=", fieldValue:data},
                              {condition: 'AND', fieldName: 'dt_fine_validita', operator: "<=", fieldValue:data}];
        } else {
            objectPostTipiSpesaClauses = [{condition: 'AND', fieldName: 'ti_area_geografica', operator: "!=", fieldValue:'I'},
                              {condition: 'AND', fieldName: 'dt_inizio_validita', operator: ">=", fieldValue:data},
                              {condition: 'AND', fieldName: 'dt_fine_validita', operator: "<=", fieldValue:data}];
        }
        
        var objectPostTipiSpesa = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostTipiSpesaOrderBy, clauses:objectPostTipiSpesaClauses}
        $http.post(urlRestProxy + app+'/', objectPostTipiSpesa, {params: {proxyURL: url}}).success(function (data) {
            if (data)
                $scope.tipi_spesa = data.elements;
        });
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
		                $scope.rimborsoMissioneModel.uoSpesa = $scope.elencoUo[0].cd_unita_organizzativa;
                        $scope.restCdr($scope.rimborsoMissioneModel.uoSpesa,"N");
		            }
		        }
        	} else {
		        $scope.elencoUo = [];
        	}
        });
    }
    
    $scope.restUoRich = function(anno, cds, uoRich){
        var uos = ProxyService.getUos(anno, cds, uoRich).then(function(result){
            if (result && result.data){
                if (result.data.elements){
                    if (result.data.elements.length === 1){
                        $scope.rimborsoMissioneModel.uoRich = result.data.elements[0].cd_unita_organizzativa;
                        $scope.restCdrRich($scope.rimborsoMissioneModel.uoRich,"N");
                    }
                }
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
                            $scope.rimborsoMissioneModel.uoCompetenza = $scope.elencoUoCompetenza[0].cd_unita_organizzativa;
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
                            $scope.rimborsoMissioneModel.cdrSpesa = data.elements[0].cd_centro_responsabilita;
                            if (daQuery != 'S'){
                                $scope.restModuli($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.uoSpesa);
                                $scope.restGae($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.pgProgetto, $scope.rimborsoMissioneModel.cdrSpesa, $scope.rimborsoMissioneModel.uoSpesa);
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
    
    $scope.restCdrRich = function(uo, daQuery){
        if (uo){
            var app = APP_FOR_REST.SIGLA;
            var url = SIGLA_REST.CDR;
            var objectPostCdrOrderBy = [{name: 'cd_centro_responsabilita', type: 'ASC'}];
            var objectPostCdrClauses = [{condition: 'AND', fieldName: 'cd_unita_organizzativa', operator: "=", fieldValue:uo}];
            var objectPostCdr = {activePage:0, maxItemsPerPage:COSTANTI.DEFAULT_VALUE_MAX_ITEM_FOR_PAGE_SIGLA_REST, orderBy:objectPostCdrOrderBy, clauses:objectPostCdrClauses}
            $http.post(urlRestProxy + app+'/', objectPostCdr, {params: {proxyURL: url}}).success(function (data) {
                if (data){
                    if (data.elements){
                        if (data.elements.length === 1){
                            $scope.rimborsoMissioneModel.cdrRich = data.elements[0].cd_centro_responsabilita;
                        }
                    }
                }
            }).error(function (data) {
            });
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
                            $scope.rimborsoMissioneModel.modulo = data.elements[0].pg_progetto;
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
        if ($scope.rimborsoMissioneModel.esercizioOriginaleObbligazione && $scope.rimborsoMissioneModel.pgObbligazione){
            var app = APP_FOR_REST.SIGLA;
            var url = null;
            var varClauses = [];
            if ($scope.gaeSelected){
                url = SIGLA_REST.IMPEGNO_GAE;
                varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.rimborsoMissioneModel.anno},
                              {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.rimborsoMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.rimborsoMissioneModel.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.rimborsoMissioneModel.pgObbligazione},
                              {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:$scope.rimborsoMissioneModel.gaeSelected}];
            } else {
                url = SIGLA_REST.IMPEGNO;
                varClauses = [{condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:$scope.rimborsoMissioneModel.cdsSpesa},
                              {condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:$scope.rimborsoMissioneModel.anno},
                              {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:$scope.rimborsoMissioneModel.esercizioOriginaleObbligazione},
                              {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:$scope.rimborsoMissioneModel. pgObbligazione}];
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
                            $scope.rimborsoMissioneModel.gae = data.elements[0].cd_linea_attivita;
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
                            $scope.rimborsoMissioneModel.voce = listaVoci[0];
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
      $scope.rimborsoMissioneModel.gae = null;
      $scope.gaeSelected = null;
    }

    $scope.annullaModulo = function(){
      $scope.annullaGae();
      $scope.rimborsoMissioneModel.pgProgetto = null;
    }

    $scope.annullaCdr = function(){
      $scope.annullaModulo();
      $scope.rimborsoMissioneModel.cdrSpesa = null;
    }

    $scope.annullaUo = function(){
      $scope.annullaCdr();
      $scope.rimborsoMissioneModel.uoSpesa = null;
    }

    $scope.reloadCds = function(cds) {
      $scope.annullaUo();  
      $scope.restUo($scope.rimborsoMissioneModel.anno, cds, $scope.rimborsoMissioneModel.uoSpesa);
    }

    $scope.reloadCdsRich = function(cds) {
      $scope.restUoRich($scope.rimborsoMissioneModel.anno, cds, $scope.rimborsoMissioneModel.uoRich);
    }

    $scope.reloadCdsCompetenza = function(cds) {
      $scope.rimborsoMissioneModel.uoCompetenza = null;
      $scope.restUoCompetenza($scope.rimborsoMissioneModel.anno, cds, null);
    }

    $scope.gestioneUtenteAbilitatoValidare = function (uo){
        $scope.utenteAbilitatoValidareUo = 'N';
        var uoForUsersSpecial= $sessionStorage.account.uoForUsersSpecial;
        if(uo){
            var uoSiper = uo.replace('.','');
            if (uoForUsersSpecial){
                for (var k=0; k<uoForUsersSpecial.length; k++) {
                    var uoForUserSpecial = uoForUsersSpecial[k];
                    if (uoSiper == uoForUserSpecial.codice_uo && uoForUserSpecial.ordine_da_validare == 'S'){
                    $scope.utenteAbilitatoValidareUo = 'S';
                    }
                }
            }
        } 
    }

    $scope.gestioneUtenteAbilitatoValidareContrAmm = function (uo){
        $scope.utenteAbilitatoValidareUoContrAmm = 'N';
        if (uo){
            var uoForUsersSpecial= $sessionStorage.account.uoForUsersSpecial;
            var uoSiper = uo.replace('.','');
            if (uoForUsersSpecial){
                for (var k=0; k<uoForUsersSpecial.length; k++) {
                    var uoForUserSpecial = uoForUsersSpecial[k];
                    if (uoSiper == uoForUserSpecial.codice_uo && uoForUserSpecial.ordine_da_validare == 'S'){
                    $scope.utenteAbilitatoValidareUoContrAmm = 'S';
                    }
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
        $scope.rimborsoMissioneModel = {};
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
      $scope.restModuli($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.uoSpesa);
      $scope.restGae($scope.rimborsoMissioneModel.anno, null, cdr, $scope.rimborsoMissioneModel.uoSpesa);
    }

    $scope.reloadModulo = function(pgProgetto, cdr, uo) {
      $scope.annullaGae();
      $scope.restGae($scope.rimborsoMissioneModel.anno, pgProgetto, cdr, uo);
    }

    $scope.tipiMissione = ProxyService.valueTipiMissione;

    $scope.luoghiDiPartenza = ProxyService.valueLuoghiDiPartenza;

    $scope.valoriPriorita = ProxyService.valuePriorita;

    $scope.trattamenti = ProxyService.valueTrattamenti;

    $scope.onChangeTipoMissione = function() {
        if ($scope.rimborsoMissioneModel.tipoMissione === 'E') {
            if (!$scope.rimborsoMissioneModel.trattamento){
                $scope.rimborsoMissioneModel.trattamento = "R";
                $scope.missioneEsteraConTam = false;
            }
            $scope.missioneEstera = true;
        } else {
            $scope.rimborsoMissioneModel.trattamento = "R";
            $scope.missioneEstera = null;
            $scope.missioneEsteraConTam = null;
            $scope.rimborsoMissioneModel.nazione = null;
            $scope.rimborsoMissioneModel.dataInizioEstero = null;
            $scope.rimborsoMissioneModel.dataFineEstero = null;
        }
    };

    $scope.onChangeTrattamento = function() {
        if ($scope.rimborsoMissioneModel.tipoMissione === 'E') {
            if ($scope.rimborsoMissioneModel.trattamento=== 'T'){
                $scope.missioneEsteraConTam = true;
            } else {
                $scope.missioneEsteraConTam = false;
                $scope.rimborsoMissioneModel.dataInizioEstero = null;
                $scope.rimborsoMissioneModel.dataFineEstero = null;
            }
        } else {
            $scope.rimborsoMissioneModel.dataInizioEstero = null;
            $scope.rimborsoMissioneModel.dataFineEstero = null;
            $scope.missioneEsteraConTam = false;
        }
    };

    var dateInizioFineDiverse = function() {
        if ($scope.rimborsoMissioneModel.dataInizioMissione === undefined || 
            $scope.rimborsoMissioneModel.dataFineMissione === undefined ||
            $scope.rimborsoMissioneModel.dataInizioMissione == null || 
            $scope.rimborsoMissioneModel.dataFineMissione === null ||
            $scope.rimborsoMissioneModel.dataInizioMissione === "" || 
            $scope.rimborsoMissioneModel.dataFineMissione === "" ||
            $scope.rimborsoMissioneModel.dataFineMissione === $scope.rimborsoMissioneModel.dataInizioMissione) {
          $scope.showObbligoRientro = null;
        } else {
            var dataInizio = moment($scope.rimborsoMissioneModel.dataInizioMissione).format("DD/MM/YYYY");
            var dataFine = moment($scope.rimborsoMissioneModel.dataFineMissione).format("DD/MM/YYYY");
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

    $scope.onChangeDateInizioFineFrontiera = function() {
    }

    $scope.esisteRimborsoMissione = function() {
        if ($scope.rimborsoMissioneModel.id === undefined || 
            $scope.rimborsoMissioneModel.id === "") {
          return null;
        } else {
          return true;
        }
    }

    var impostadisabilitaRimborsoMissione = function() {
        if ($scope.esisteRimborsoMissione && ($scope.rimborsoMissioneModel.stato === 'DEF' || 
            $scope.rimborsoMissioneModel.statoFlusso === 'APP' || $scope.rimborsoMissioneModel.stato === 'ANN' || $scope.rimborsoMissioneModel.stato === 'ANA' || 
            ($scope.rimborsoMissioneModel.stato === 'CON' && 
               ($scope.rimborsoMissioneModel.stateFlows === 'ANNULLATO' ||
                $scope.rimborsoMissioneModel.stateFlows === 'FIRMA SPESA RIMBORSO' ||
                $scope.rimborsoMissioneModel.stateFlows === 'FIRMA UO RIMBORSO' ||
                $scope.rimborsoMissioneModel.stateFlows === 'FIRMATO')))) {
          return true;
        } else {
          return false;
        }
    }

    $scope.inizializzaFormPerModifica = function(){
        $scope.showEsisteRimborsoMissione = true;
        if ($scope.rimborsoMissioneModel.statoFlusso === 'INV' && $scope.rimborsoMissioneModel.stato === 'INS' && $scope.rimborsoMissioneModel.commentFlows){
	        $scope.showCommentFlows = true;
        } else {
	        $scope.showCommentFlows = false;
        }
        if ($scope.validazione === 'S') {
            $scope.rimborsoMissioneModel.daValidazione = "S";
        }
        $scope.disabilitaRimborsoMissione = impostadisabilitaRimborsoMissione();
        
        if ($scope.rimborsoMissioneModel && $scope.rimborsoMissioneModel.ordineMissione){
            $scope.elencoOrdiniMissione = [];
            $scope.elencoOrdiniMissione.push($scope.rimborsoMissioneModel.ordineMissione);
        }

        inizializzaForm();
    }

    var inizializzaForm = function(){
        if ($scope.rimborsoMissioneModel.tipoMissione === 'E') {
            if ($scope.rimborsoMissioneModel.trattamento === 'T'){
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
        $scope.rimborsoMissioneModel = {nominativo:account.lastName+" "+account.firstName, 
                                        qualificaRich:account.profilo, livelloRich:account.livello, codiceFiscale:account.codice_fiscale, 
                                        dataNascita:account.data_nascita, luogoNascita:account.comune_nascita, validato:'N', 
                                        datoreLavoroRich:account.struttura_appartenenza, matricola:account.matricola,
            uoRich:ProxyService.buildUoRichiedenteSiglaFromUoSiper(account), cdsRich:$scope.estraiCdsRichFromAccount(account)};
        if (account.comune_residenza && account.cap_residenza){
            $scope.rimborsoMissioneModel.comuneResidenzaRich = account.comune_residenza+" - "+account.cap_residenza;
        }
        if (account.comune_residenza){
            $scope.rimborsoMissioneModel.comuneResidenzaRich = account.comune_residenza;
        }
        if (account.indirizzo_completo_residenza){
            $scope.rimborsoMissioneModel.indirizzoResidenzaRich = account.indirizzo_completo_residenza; 
        }

        $scope.missioneEsteraConTam = null;
        $scope.missioneEstera = null;
        $scope.rimborsoMissioneModel.uid = account.login;
        var today = $scope.oggi;
        $scope.rimborsoMissioneModel.dataInserimento = today;
        $scope.rimborsoMissioneModel.anno = today.getFullYear();
        $scope.showObbligoRientro = null;
        if ($scope.terzoSigla && $scope.terzoSigla.cd_terzo){
            $scope.rimborsoMissioneModel.cdTerzoSigla = $scope.terzoSigla.cd_terzo;
        }
        $scope.disabilitaRimborsoMissione = false;
    }

    $scope.gestioneInCasoDiErrore = function(){
        $scope.error = true;
    }

    $scope.validateImpegno = function(){
        $scope.restImpegno();
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione del Rimborso Missione Numero: "+$scope.rimborsoMissioneModel.numero+" del "+$filter('date')($scope.rimborsoMissioneModel.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteRimborsoMissione);
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per confermare il Rimborso Missione Numero: "+$scope.rimborsoMissioneModel.numero+" del "+$filter('date')($scope.rimborsoMissioneModel.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e il rimborso non sarà più modificabile. Si desidera Continuare?", confirmOrdineMissione);
    }

    var confirmOrdineMissione = function () {
            $rootScope.salvataggio = true;
            RimborsoMissioneService.confirm($scope.rimborsoMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
	                    ui.ok_message("Rimborso Missione confermato e inviato all'approvazione.");
                        ElencoRimborsiMissioneService.findById($scope.rimborsoMissioneModel.id).then(function(data){
                            $scope.rimborsoMissioneModel = data;
                            $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.validateRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            RimborsoMissioneService.confirm_validate($scope.rimborsoMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Rimborso Missione confermato e inviato all'approvazione.");
                        ElencoRimborsiMissioneService.findById($scope.rimborsoMissioneModel.id).then(function(data){
                            $scope.rimborsoMissioneModel = data;
                            $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.ritornaMittenteRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            RimborsoMissioneService.return_sender($scope.rimborsoMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Rimborso Missione respinto al mittente.");
                        ElencoRimborsiMissioneService.findById($scope.rimborsoMissioneModel.id).then(function(data){
                            $scope.rimborsoMissioneModel = data;
                            $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.finalizeRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            RimborsoMissioneService.finalize($scope.rimborsoMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        ui.ok_message("Rimborso Missione Completato.");
                        ElencoRimborsiMissioneService.findById($scope.rimborsoMissioneModel.id).then(function(data){
                            $scope.rimborsoMissioneModel = data;
                            $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                            $scope.inizializzaFormPerModifica();
                        });
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteRimborsoMissione = function () {
            $rootScope.salvataggio = true;
            RimborsoMissioneService.delete({ids:$scope.rimborsoMissioneModel.id},
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                        $scope.idMissione = null;
                        $scope.rimborsoMissioneModel = {}
                        $scope.inizializzaFormPerInserimento($sessionStorage.account);
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var serviziRestInizialiInserimento = function(){
        $scope.restNazioni();
        $scope.restCds($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.cdsRich);
        $scope.reloadCdsRich($scope.rimborsoMissioneModel.cdsRich);
        $scope.restCapitoli($scope.rimborsoMissioneModel.anno);
        $scope.restCdsCompetenza($scope.rimborsoMissioneModel.anno, $scope.rimborsoMissioneModel.cdsRich);
    }

    $scope.reloadUserWork = function(uid){
        if (uid){
            for (var i=0; i<$scope.elencoPersone.length; i++) {
                if (uid == $scope.elencoPersone[i].uid){
                    var data = $scope.elencoPersone[i];
                    var userWork = ProxyService.buildPerson(data);
                    $scope.recuperoDatiTerzoSigla(userWork);
                    $scope.restOrdiniMissioneDaRimborsare(userWork, $scope.giaRimborsato);
                    $scope.accountModel = userWork;
                    $sessionStorage.accountWork = userWork;
                }
            }
        }
    }

    $scope.goDettagliSpesa = function () {
      if ($scope.rimborsoMissioneModel.id){
        var dataInizio = null;
        var dataFine = null;
        if ($scope.rimborsoMissioneModel.tipoMissione === 'E' && $scope.rimborsoMissioneModel.trattamento === 'T') {
            if (!$scope.rimborsoMissioneModel.dataInizioEstero || !$scope.rimborsoMissioneModel.dataFineEstero){
                ui.error("Valorizzare la data di imbarco in partenza e la data di sbarco del ritorno.");
            }
            dataInizio = $scope.rimborsoMissioneModel.dataInizioEstero;
            dataFine = $scope.rimborsoMissioneModel.dataFineEstero;
        } else {
            if (!$scope.rimborsoMissioneModel.dataInizioMissione || !$scope.rimborsoMissioneModel.dataFineMissione){
                ui.error("Valorizzare le date inizio e fine missione.");
            }
            dataInizio = $scope.rimborsoMissioneModel.dataInizioMissione;
            dataFine = $scope.rimborsoMissioneModel.dataFineMissione;
        } 

        if ($scope.validazione){
            $location.path('/rimborso-missione/rimborso-missione-dettagli/'+$scope.rimborsoMissioneModel.id+'/'+$scope.validazione+'/'+dataInizio+'/'+dataFine);
        } else {
            if ($scope.disabilitaRimborsoMissione){
                $location.path('/rimborso-missione/rimborso-missione-dettagli/'+$scope.rimborsoMissioneModel.id+'/'+"D"+'/'+dataInizio+'/'+dataFine);
            } else {
                $location.path('/rimborso-missione/rimborso-missione-dettagli/'+$scope.rimborsoMissioneModel.id+'/'+"N"+'/'+dataInizio+'/'+dataFine);
            }
        }
      } else {
        ui.error("Per poter inserire i dati di dettaglio delle spese è necessario prima salvare il rimborso della missione");
      }
    }

    $scope.doPrintRimborsoMissione = function(idRimborsoMissione){
      $scope.rimborsoMissioneModel.stampaInCorso=true;
      $http.get('api/rest/rimborsoMissione/print/json',{params: {idMissione: idRimborsoMissione}})
        .success(function (data) {
            delete $scope.rimborsoMissioneModel.stampaInCorso;
//            var file = new Blob([data], {type: 'application/pdf'});
//            var fileURL = URL.createObjectURL(file);
//            window.open(fileURL);
        }).error(function (data) {
            delete $scope.rimborsoMissioneModel.stampaInCorso;
        }); 
    }

    $scope.previousPage = function () {
      parent.history.back();
    }

    $scope.editImpegno= function (impegno) {
      impegno.editing = true;
    }

    var undoEditingImpegno = function (impegno) {
      delete impegno.editing;
    }

    $scope.undoImpegno = function (impegno) {
      undoEditingImpegno(impegno);
    }

    $scope.confirmDeleteImpegno = function (index) {
        var impegnoDaEliminare = $scope.impegni[index];
        ui.confirmCRUD("Confermi l'eliminazione dell'impegno "+impegnoDaEliminare.esercizioOriginaleObbligazione+" - "+impegnoDaEliminare.pgObbligazione+"?", deleteImpegno, index);
    }

    var deleteImpegno  = function (index) {
        var id = $scope.impegni[index].id;
            $rootScope.salvataggio = true;
            $http.delete('api/rest/rimborsoMissione/impegno/' + id).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.impegni.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var annullaDatiNuovaRigaImpegno = function () {
      delete $scope.addImpegno;
      delete $scope.newImpegno;
    }

    $scope.undoAddImpegno = function () {
        annullaDatiNuovaRigaImpegno();
    }

    $scope.aggiungiRigaImpegno = function () {
      $scope.addImpegno = true;
      $scope.newImpegno = {};
    }

    var aggiornaDatiFinanziari = function (rimborsoMissione) {
        $scope.rimborsoMissioneModel.gae = rimborsoMissione.gae;
        $scope.rimborsoMissioneModel.voce = rimborsoMissione.voce;
    }



    $scope.insertImpegno = function (newRigaImpegno) {
        newRigaImpegno.rimborsoMissione = $scope.rimborsoMissioneModel;
            $rootScope.salvataggio = true;
            $http.post('api/rest/rimborsoMissione/impegno/create', newRigaImpegno).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.impegni){
                        $scope.impegni = [];
                    }
                    $scope.impegni.push(data);
                    aggiornaDatiFinanziari(data.rimborsoMissione);
                    $scope.undoAddImpegno();
            }).error(function (data) {
                $rootScope.salvataggio = false;
            });
    }

    $scope.modifyImpegno = function (impegno) {
        $rootScope.salvataggio = true;
        $http.put('api/rest/rimborsoMissione/impegno/modify', impegno).success(function(data){
            $rootScope.salvataggio = false;
            aggiornaDatiFinanziari(data.rimborsoMissione);
            undoEditingImpegno(impegno);
        }).error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.confirmDeleteAttachment = function (attachment, idRimborsoMissione) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment, idRimborsoMissione) {
        $rootScope.salvataggio = true;
        var x = $http.get('api/rest/deleteAttachment/' + attachment.id+'/'+attachment.idMissione);
        var y = x.then(function (result) {
            var attachments = $scope.rimborsoMissioneModel.attachments;
            if (attachments && Object.keys(attachments).length > 0){
                var newAttachments = attachments.filter(function(el){
                    return el.id !== attachment.id;
                });
                $scope.rimborsoMissioneModel.attachments = newAttachments;
                if (Object.keys(newAttachments).length = 0){
                    $scope.rimborsoMissioneModel.attachmentsExists = false;
                }
            }
            $rootScope.salvataggio = false;
            ui.ok();
        });
        x.error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.onChangeGiaRimborsato = function (giaRimborsato) {
        $scope.restOrdiniMissioneDaRimborsare($sessionStorage.accountWork, giaRimborsato);
    }

    $scope.viewAttachments = function (idRimborsoMissione) {
        if (!$scope.rimborsoMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/rimborsoMissione/viewAttachments/' + idRimborsoMissione).then(function (data) {
                $scope.rimborsoMissioneModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;  
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.rimborsoMissioneModel.attachments = attachments;
            }, function () {
                $scope.rimborsoMissioneModel.isFireSearchAttachments = false;
                $scope.rimborsoMissioneModel.attachmentsExists = false;
                $scope.rimborsoMissioneModel.attachments = {};
            });
        }
    }

    $scope.viewAttachmentsUndo = function () {
        if ($scope.rimborsoMissioneModel.stato == 'ANA'){
            $http.get('api/rest/annullamentoRimborsoMissione/viewAttachmentsFromRimborso/' + $scope.rimborsoMissioneModel.id).then(function (data) {
                var attachments = data.data;
                $scope.rimborsoMissioneModel.attachmentsUndo = attachments;
            }, function () {
                $scope.rimborsoMissioneModel.attachmentsUndo = {};
            });
        }
    }

    $scope.save = function () {
        var ret = controlliPrimaDelSalvataggio();
        if (ret){
            if ($scope.esisteRimborsoMissione()){
                $rootScope.salvataggio = true;
                RimborsoMissioneService.modify($scope.rimborsoMissioneModel,
                        function (value, responseHeaders) {
                            $scope.rimborsoMissioneModel = value;
                            $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                            $rootScope.salvataggio = false;
                        },
                        function (httpResponse) {
                                $rootScope.salvataggio = false;
                        }
                );
            } else {
                $rootScope.salvataggio = true;
                RimborsoMissioneService.add($scope.rimborsoMissioneModel,
                        function (value, responseHeaders) {
                            $rootScope.salvataggio = false;
                            $scope.rimborsoMissioneModel = value;
                            $scope.elencoPersone = null;
                            $scope.uoForUsersSpecial = null;
                            $scope.inizializzaFormPerModifica();
                            $scope.rimborsoMissioneModel.isFireSearchAttachments = false;
                            var path = $location.path();
                            $location.path(path+'/'+$scope.rimborsoMissioneModel.id);
                        },
                        function (httpResponse) {
                            $rootScope.salvataggio = false;
                        }
                );
            }
        }
    }


    $scope.idMissione = $routeParams.idMissione;
    $scope.validazione = $routeParams.validazione;
    $scope.accessToken = AccessToken.get();
    $sessionStorage.accountWork = null;

    if (isInQuery() || ($scope.rimborsoMissioneModel != null && $scope.rimborsoMissioneModel.idMissione)){
        ElencoRimborsiMissioneService.findById($scope.idMissione).then(function(data){
            var model = data;
            if (model){
                if (model.uid == $sessionStorage.account.login){
                    $scope.accountModel = $sessionStorage.account;
                    $sessionStorage.accountWork = $scope.accountModel;
                    $scope.recuperoDatiTerzoSigla($scope.accountModel);
                    if (model.tipoPagamento){
                        $scope.recuperoDatiTerzoModalitaPagamento(model.cdTerzoSigla, model.tipoPagamento);
                    }
                } else {
                    var person = ProxyService.getPerson(model.uid).then(function(result){
                        if (result){
                            $scope.accountModel = result;
                            $sessionStorage.accountWork = $scope.accountModel;
                            $scope.recuperoDatiTerzoSigla($scope.accountModel);
                            if (model.tipoPagamento){
                                $scope.recuperoDatiTerzoModalitaPagamento(model.cdTerzoSigla, model.tipoPagamento);
                            }
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
                $scope.rimborsoMissioneModel = model;
                $scope.viewAttachments($scope.rimborsoMissioneModel.id);
                $scope.recuperoDatiAltriRimborsi($scope.rimborsoMissioneModel.ordineMissione.id);
                if ($scope.rimborsoMissioneModel.stato == 'ANA'){
                    $scope.viewAttachmentsUndo();
                }

                $scope.inizializzaFormPerModifica();
                $scope.today();
                if ($scope.rimborsoMissioneModel.validaAmm == 'N'){
                    $scope.gestioneUtenteAbilitatoValidareContrAmm($scope.rimborsoMissioneModel.uoContrAmm);
                    $scope.utenteAbilitatoValidareUo = 'N';
                } else {
                    $scope.gestioneUtenteAbilitatoValidare($scope.rimborsoMissioneModel.uoSpesa);
                }
                ElencoRimborsiMissioneService.findRimborsoImpegni(model.id).then(function(result){
                    if (result.data && result.data.length > 0){
                        $scope.impegni = result.data;
                    }
                });
                if ($scope.rimborsoMissioneModel.stato == 'DEF' && $scope.rimborsoMissioneModel.cdCdsSigla){
                    recuperoDatiMandatoMissioneSigla($scope.rimborsoMissioneModel);
                }

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
            $scope.restOrdiniMissioneDaRimborsare($sessionStorage.accountWork, $scope.giaRimborsato);
            $scope.today();
            $scope.recuperoDatiTerzoSigla($scope.accountModel);
        }
    }
});
