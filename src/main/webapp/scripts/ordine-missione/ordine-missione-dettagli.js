'use strict';

missioniApp.factory('OrdineMissioneDettagliService', function ($http) {
        return {
            findDettagli: function(idOrdineMissione) {
                var promise = $http.get('api/rest/ordineMissione/dettagli/get', {params: {idOrdineMissione: idOrdineMissione}}).then(function (response) {
                    if (response.data){
                        var dati = angular.copy(response.data);
//                        for (var i=0; i<dati.length; i++) {
//                            var dettaglio = dati[i];
//                            dettaglio.dataSpesa = DateUtils.convertLocalDateFromServer(dettaglio.dataSpesa);
//                        }
                        return dati;
                    }
                    return response.data;
                });
                return promise;
            }
        }
});


missioniApp.controller('OrdineMissioneDettagliController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, OrdineMissioneDettagliService, ProxyService, ElencoOrdiniMissioneService, ui, COSTANTI, DateUtils, DateService) {

    $scope.validazione = $routeParams.validazione;
    $scope.inizioMissione = $routeParams.inizioMissione;
    $scope.fineMissione = $routeParams.fineMissione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.backupDettagliSpesa = {}; // Oggetto per salvare i backup

    $scope.salvaStatoDettaglio = function (dettaglioSpesa) {
        if (dettaglioSpesa.id) {
            $scope.backupDettagliSpesa[dettaglioSpesa.id] = angular.copy(dettaglioSpesa);
        }
    };

    $scope.ripristinaStatoDettaglio = function (dettaglioSpesa) {
        if (dettaglioSpesa.id && $scope.backupDettagliSpesa[dettaglioSpesa.id]) {
            angular.copy($scope.backupDettagliSpesa[dettaglioSpesa.id], dettaglioSpesa);
            delete $scope.backupDettagliSpesa[dettaglioSpesa.id]; // Pulisce il backup dopo il ripristino
        }
    };


    $scope.aggiungiDettaglioSpesa = function () {
        $scope.addDettaglioSpesa = true;
        $scope.newDettaglioSpesa = {};
        inizializzaNuovaRiga($scope.newDettaglioSpesa);

        onChangeDettaglio();

    }


    var inizializzaNuovaRiga = function (dettaglioSpesa) {
        $scope.newDettaglioSpesa.cdDivisa = "EURO";
        $scope.newDettaglioSpesa.cambio = 1;
    }

    $scope.confirmDeleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        ui.confirmCRUD("Confermi l'eliminazione del dettaglio spesa presunta: " + dettaglioSpesaDaEliminare.dsTiSpesa + "?", deleteDettaglioSpesa, index);
    }

    var deleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        $rootScope.salvataggio = true;
        $http.delete('api/rest/ordineMissione/dettagli/' + dettaglioSpesaDaEliminare.id).success(
            function (data) {
                $rootScope.salvataggio = false;
                $scope.dettagliSpese.splice(index, 1);

            }).error(
            function (data) {
                $rootScope.salvataggio = false;
            }
        );
    };



    var onChangeDettaglio = function () {
        if ($scope.newDettaglioSpesa) {
            recuperoTipiSpesa($scope.newDettaglioSpesa.dataSpesa, null);
        }
    }

    var prepareModifyDetail = function (dettaglioSpesa) {
        if (dettaglioSpesa) {
            recuperoTipiSpesa(dettaglioSpesa.dataSpesa, null);
        }
    }

    var recuperoTipiSpesa = function (dataSpesa) {
        recuperoTipiSpesa(dataSpesa, null);
    }


    var recuperoTipiSpesa = function(dataSpesa, cdTipoSpesa){
        $scope.tipi_spesa = [];
        var tipi = ProxyService.getTipiSpesa(undefined, undefined, $scope.ordineMissione.nazione, $scope.ordineMissione.trattamento).then(function(result){
            if (result && result.data){
                $scope.tipi_spesa = result.data.elements;
                if (cdTipoSpesa){
                    $scope.onChangeTipoSpesa(cdTipoSpesa);
                }
            }
        });
    }

//    function aggiornaTestoTipiSpesa(elements) {
//        return elements.map(function (tipoSpesa) {
//            if (tipoSpesa.cd_ti_spesa === "ISCRIZIONE CONGRESSO") {
//                tipoSpesa.displayText = "SPESE CONGRESSI";
//            } else {
//                var pasto = tipoSpesa.fl_pasto;
//                var rimborso = tipoSpesa.fl_rimborso_km;
//                var trasporto = tipoSpesa.fl_trasporto;
//                var alloggio = tipoSpesa.fl_alloggio;
//                if (pasto) {
//                    tipoSpesa.displayText = "VITTO";
//                } else if (rimborso || trasporto) {
//                    tipoSpesa.displayText = "VIAGGIO";
//                } else if (alloggio) {
//                    tipoSpesa.displayText = "ALLOGGIO";
//                } else {
//                    tipoSpesa.displayText = "ALTRO";
//                }
//            }
//            return tipoSpesa;
//        });
//    }
//
//    function rimuoviDuplicatiTipiSpesa(tipiSpesa) {
//        var processedTexts = {};
//        return tipiSpesa.filter(function (tipoSpesa) {
//            if (processedTexts[tipoSpesa.displayText]) {
//                return false; // Salta i duplicati
//            }
//            processedTexts[tipoSpesa.displayText] = true;
//            return true;
//        });
//    }


    $scope.deselect = function (idDettaglioSpesa) {
        if (idDettaglioSpesa) {
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
                for (var i = 0; i < $scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                }
            }
        }
    }



    var recuperoDettaglioSpesa = function (idDettaglioSpesa) {
        if (idDettaglioSpesa) {
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
                for (var i = 0; i < $scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                    if (dettaglio.id === idDettaglioSpesa) {
                        return dettaglio;
                    }
                }
            }
        }
    }


//        $scope.onChangeTipoSpesa = function (cdTipoSpesa) {
//            if (cdTipoSpesa){
//                if ($scope.tipi_spesa && $scope.tipi_spesa.length > 0){
//                    for (var i=0; i<$scope.tipi_spesa.length; i++) {
//                        var tipo_spesa = $scope.tipi_spesa[i];
//                        if (tipo_spesa.cd_ti_spesa === cdTipoSpesa){
//                            $scope.newDettaglioSpesa.dsTiSpesa = tipo_spesa.ds_ti_spesa;
//                            if (tipo_spesa.fl_giustificativo_richiesto == true){
//                                $scope.newDettaglioSpesa.giustificativo = 'S';
//                            } else {
//                                $scope.newDettaglioSpesa.giustificativo = 'N';
//                            }
//                            $scope.giustificativo = tipo_spesa.fl_giustificativo_richiesto;
//                            $scope.pasto = tipo_spesa.fl_pasto;
//                            $scope.rimborso = tipo_spesa.fl_rimborso_km;
//                            $scope.trasporto = tipo_spesa.fl_trasporto;
//                            $scope.alloggio = tipo_spesa.fl_alloggio;
//                            $scope.ammissibileRimborso = tipo_spesa.fl_ammissibile_con_rimborso;
//                            if ($scope.pasto){
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = "P";
//                            } else if ($scope.rimborso){
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = "R";
//                            } else if ($scope.trasporto){
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = "T";
//                            } else if ($scope.alloggio){
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = "A";
//                            } else {
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = "N";
//                            }
//                            if ($scope.pasto && $scope.newDettaglioSpesa.dataSpesa){
//                                var dataFormatted = $filter('date')($scope.newDettaglioSpesa.dataSpesa, "dd/MM/yyyy");
//                                var tipi = ProxyService.getTipiPasto($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione).then(function(result){
//                                    if (result && result.data){
//                                        $scope.tipi_pasto = result.data.elements;
//                                    } else {
//                                        $scope.tipi_pasto = [];
//                                    }
//                                });
//                            } else {
//                                $scope.newDettaglioSpesa.tiCdTiSpesa = tipo_spesa.fl_rimborso_km ? "R" :
//                                tipo_spesa.fl_trasporto ? "T" :
//                                tipo_spesa.fl_alloggio ? "A" : "N";
//                                // Resetta lo stato del pasto se non richiesto
//                                $scope.tipi_pasto = [];
//                                $scope.newDettaglioSpesa.cdTiPasto = null;
//                            }
//                            if ($scope.rimborso){
//                                var dataFormatted = $filter('date')($scope.newDettaglioSpesa.dataSpesa, "dd/MM/yyyy");
//                                var tipi = ProxyService.getRimborsoKm("P", dataFormatted, 1).then(function(result){
//                                    if (result && result.data && result.data.elements && result.data.elements.length > 0){
//                                        $scope.rimborsoKm = result.data.elements[0];
//                                    } else {
//                                        $scope.rimborsoKm = [];
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//            }
//        }
//


    $scope.onChangeTipoSpesa = function (cdTipoSpesa) {
        if (cdTipoSpesa) {
            if ($scope.tipi_spesa && $scope.tipi_spesa.length > 0) {
                for (var i = 0; i < $scope.tipi_spesa.length; i++) {
                    var tipo_spesa = $scope.tipi_spesa[i];
                    if (tipo_spesa.cd_ti_spesa === cdTipoSpesa) {
                        $scope.newDettaglioSpesa.dsTiSpesa = tipo_spesa.ds_ti_spesa;
                        $scope.pasto = tipo_spesa.fl_pasto;
                        $scope.rimborso = tipo_spesa.fl_rimborso_km;
                        $scope.trasporto = tipo_spesa.fl_trasporto;
                        $scope.alloggio = tipo_spesa.fl_alloggio;
                        $scope.ammissibileOrdine = tipo_spesa.fl_ammissibile_con_rimborso;
                        if ($scope.pasto) {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "P";
                        } else if ($scope.rimborso) {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "R";
                        } else if ($scope.trasporto) {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "T";
                        } else if ($scope.alloggio) {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "A";
                        } else {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "N";
                        }
                        break;
                    }
                }
            }
        }
    }

    $scope.reloadFromTipoSpesa = function (dettaglioSpesa) {
        if (dettaglioSpesa.cdTiSpesa) {
            $scope.tipi_spesa = [];
            ProxyService.getTipiSpesa(undefined, undefined, $scope.ordineMissione.nazione, $scope.ordineMissione.trattamento).then(function (result) {
                if (result && result.data) {
                    $scope.tipi_spesa = rimuoviDuplicatiTipiSpesa(aggiornaTestoTipiSpesa(result.data.elements));


                    if (dettaglioSpesa.cdTiSpesa) {
                        for (var i = 0; i < $scope.tipi_spesa.length; i++) {
                            if ($scope.tipi_spesa[i].cd_ti_spesa === dettaglioSpesa.cdTiSpesa) {
                                dettaglioSpesa.pasto = $scope.tipi_spesa[i].fl_pasto;
                                dettaglioSpesa.rimborso = $scope.tipi_spesa[i].fl_rimborso_km;
                                dettaglioSpesa.trasporto = $scope.tipi_spesa[i].fl_trasporto;
                                dettaglioSpesa.alloggio = $scope.tipi_spesa[i].fl_alloggio;
                                dettaglioSpesa.ammissibileOrdine = $scope.tipi_spesa[i].fl_ammissibile_con_rimborso;
                                break;
                            }
                        }
                    }
                }
            });
        }
    };

    $scope.editDettaglioSpesa = function (dettaglioSpesa) {
        $scope.salvaStatoDettaglio(dettaglioSpesa); // Salva lo stato iniziale
        dettaglioSpesa.editing = true; // Attiva la modalità di modifica
        $scope.reloadFromTipoSpesa(dettaglioSpesa);
    };

    var undoEditingDettaglioSpesa = function (dettaglioSpesa) {
        delete dettaglioSpesa.editing;
    }

    $scope.undoDettaglioSpesa = function (dettaglioSpesa) {
        $scope.ripristinaStatoDettaglio(dettaglioSpesa); // Ripristina lo stato iniziale
        dettaglioSpesa.editing = false; // Esce dalla modalità di modifica
    };


    var annullaDatiNuovaRiga = function () {
        delete $scope.addDettaglioSpesa;
        delete $scope.newDettaglioSpesa;
    }

    $scope.undoAddDettaglioSpesa = function () {
        annullaDatiNuovaRiga();
    }

    $scope.insertDettaglioSpesa = function (newDettaglioSpesa) {
        newDettaglioSpesa.ordineMissione = $scope.ordineMissione;
        $rootScope.salvataggio = true;
        newDettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(newDettaglioSpesa.dataSpesa);


        $http.post('api/rest/ordineMissione/dettagli/create', newDettaglioSpesa).success(function (data) {
            $rootScope.salvataggio = false;
            if (!$scope.dettagliSpese) {
                $scope.dettagliSpese = [];
            }
            if (data) {
                var dettaglio = angular.copy(data);
                dettaglio.dataSpesa = DateUtils.convertLocalDateFromServer(dettaglio.dataSpesa);
                dettaglio.importo = Math.round(dettaglio.importoEuro * 100) / 100
                $scope.dettagliSpese.push(dettaglio);
            }
            $scope.undoAddDettaglioSpesa();
        }).error(function (data) {
            $rootScope.salvataggio = false;
        });
    };



    $scope.modifyDettaglioSpesa = function (dettaglioSpesa) {
        $rootScope.salvataggio = true;
        dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(dettaglioSpesa.dataSpesa);
        $http.put('api/rest/ordineMissione/dettagli/modify', dettaglioSpesa)
            .success(function (data) {
                $rootScope.salvataggio = false;
                dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateFromServer(data.dataSpesa);
                dettaglioSpesa.editing = false;
                delete $scope.backupDettagliSpesa[dettaglioSpesa.id]; // Pulisce il backup dopo il salvataggio
            })
            .error(function (data) {
                $rootScope.salvataggio = false;
            });
    };



    $scope.getTotaleDettagliSpesa = function () {
        var totale = 0;
        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
            for (var i = 0; i < $scope.dettagliSpese.length; i++) {
                totale = totale + $scope.dettagliSpese[i].importoEuro;
            }
        }
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro) {
            totale = totale + $scope.newDettaglioSpesa.importoEuro;
        }
        return totale;
    }

//    $scope.getTotaleDettagliSpesaNonAnticipati = function () {
//        var totale = 0;
//        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
//            for (var i = 0; i < $scope.dettagliSpese.length; i++) {
//                if ($scope.dettagliSpese[i].flSpesaAnticipata == "N") {
//                    totale = totale + $scope.dettagliSpese[i].importoEuro;
//                }
//            }
//        }
//        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro && $scope.newDettaglioSpesa.flSpesaAnticipata == "N") {
//            totale = totale + $scope.newDettaglioSpesa.importoEuro;
//        }
//        return totale;
//    }

    var impostadisabilitaOrdineMissione = function () {
        if ($scope.ordineMissione && ($scope.ordineMissione.stato === 'DEF' || $scope.ordineMissione.statoFlusso === 'APP' || ($scope.ordineMissione.stato === 'CON' &&
            ($scope.ordineMissione.stateFlows === 'ANNULLATO' ||
                $scope.ordineMissione.stateFlows === 'FIRMATO')))) {
            return true;
        } else {
            return false;
        }
    }



    var inizializzaDati = function () {
        ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function (data) {
            $scope.ordineMissione = data;
            if ($scope.ordineMissione) {
                $scope.disabilita = impostadisabilitaOrdineMissione();
                OrdineMissioneDettagliService.findDettagli($scope.idOrdineMissione).then(function (data) {
                    $scope.dettagliSpese = data;
                    console.log("Dettagli spese recuperati:", $scope.dettagliSpese); // <---- LOG AGGIUNTO
                    if ($scope.dettagliSpese && $scope.dettagliSpese[0]) {
                        $scope.getTotaleDettagliSpesa();
                    }
                });
            }
        });
    }

    inizializzaDati();


    $scope.previousPage = function () {
            var totaleSpese = $scope.getTotaleDettagliSpesa();
            if ($scope.ordineMissione && $scope.ordineMissione.importoPresunto < totaleSpese) {
                ui.error("Il totale delle spese (" + Utility.nvl(totaleSpese) + ") non può superare l'importo presunto (" + Utility.nvl($scope.ordineMissione.importoPresunto) + ").");
                return;
            }
            parent.history.back();
        }


});


//function aggiornaTestoTipiSpesa(tipoSpesa) {
//    if (tipoSpesa.cd_ti_spesa === "ISCRIZIONE CONGRESSO") {
//        tipoSpesa.cd_ti_spesa = "SPESE CONGRESSI";
//    } else {
//        var pasto = tipoSpesa.fl_pasto;
//        var rimborso = tipoSpesa.fl_rimborso_km;
//        var trasporto = tipoSpesa.fl_trasporto;
//        var alloggio = tipoSpesa.fl_alloggio;
//        if (pasto) {
//            tipoSpesa.cd_ti_spesa = "VITTO";
//        } else if (rimborso || trasporto) {
//            tipoSpesa.cd_ti_spesa = "VIAGGIO";
//        } else if (alloggio) {
//            tipoSpesa.cd_ti_spesa = "ALLOGGIO";
//        } else {
//            tipoSpesa.cd_ti_spesa = "ALTRO";
//        }
//    }
//    tipoSpesa.displayText = tipoSpesa.cd_ti_spesa;
//    return tipoSpesa.cd_ti_spesa;
//}
//
//function rimuoviDuplicatiTipiSpesa(tipiSpesa) {
//    var processedTexts = {};
//    return tipiSpesa.filter(function (tipoSpesa) {
//        if (processedTexts[tipoSpesa.displayText]) {
//            return false; // Salta i duplicati
//        }
//        processedTexts[tipoSpesa.displayText] = true;
//        return true;
//    });
//}
