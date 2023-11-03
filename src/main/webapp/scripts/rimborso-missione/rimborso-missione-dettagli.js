'use strict';

missioniApp.factory('RimborsoMissioneDettagliService', function (DateUtils, $http) {
        return {
            findDettagli: function(idRimborsoMissione) {
                var promise = $http.get('api/rest/rimborsoMissione/dettagli/get', {params: {idRimborsoMissione: idRimborsoMissione}}).then(function (response) {
                    if (response.data){
                        var dati = angular.copy(response.data);
                        for (var i=0; i<dati.length; i++) {
                            var dettaglio = dati[i];
                            dettaglio.dataSpesa = DateUtils.convertLocalDateFromServer(dettaglio.dataSpesa);
                        }
                        return dati;
                    }
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('RimborsoMissioneDettagliController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, RimborsoMissioneDettagliService, ProxyService, ElencoRimborsiMissioneService, ui, COSTANTI, DateUtils, DateService) {
    
    $scope.validazione = $routeParams.validazione;
    $scope.inizioMissione = $routeParams.inizioMissione;
    $scope.fineMissione = $routeParams.fineMissione;
    $scope.idRimborsoMissione = $routeParams.idRimborsoMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    var recuperoImpegni = function(idRimborsoMissione){
        $scope.impegni = [];
        ElencoRimborsiMissioneService.findRimborsoImpegni($scope.idRimborsoMissione).then(function(result){
            if (result.data && result.data.length > 0){
                $scope.impegni = result.data;
            }
        });
    }

    recuperoImpegni($scope.idRimborsoMissione);

    $scope.aggiungiDettaglioSpesa = function () {
      $scope.addDettaglioSpesa = true;
      $scope.newDettaglioSpesa = {};
      inizializzaNuovaRiga($scope.newDettaglioSpesa);
    }

    var caricaImpegnoSingolo = function (dettaglioSpesa){
        if ($scope.impegni && $scope.impegni.length == 1){
            var impegno = $scope.impegni[0];
            dettaglioSpesa.esercizioOriginaleObbligazione = impegno.esercizioOriginaleObbligazione;
            dettaglioSpesa.idRimborsoImpegni = impegno.id;
            dettaglioSpesa.pgObbligazione = impegno.pgObbligazione;
            dettaglioSpesa.voce = impegno.voce;
            dettaglioSpesa.dsVoce = impegno.dsVoce;
        }
    }

    var inizializzaNuovaRiga = function(dettaglioSpesa){
        $scope.newDettaglioSpesa.flSpesaAnticipata = "N";
        $scope.newDettaglioSpesa.cdDivisa = "EURO";
        $scope.newDettaglioSpesa.cambio = 1;
        $scope.tipi_pasto = [];
        $scope.rimborsoKm = null;
        caricaImpegnoSingolo(dettaglioSpesa);
    }

    $scope.confirmDeleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        ui.confirmCRUD("Confermi l'eliminazione del dettaglio spesa: "+dettaglioSpesaDaEliminare.dsTiSpesa+" del "+$filter('date')(dettaglioSpesaDaEliminare.dataSpesa, COSTANTI.FORMATO_DATA)+"?", deleteDettaglioSpesa, index);
    }

    var deleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        $rootScope.salvataggio = true;
        $http.delete('api/rest/rimborsoMissione/dettagli/' + dettaglioSpesaDaEliminare.id).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.dettagliSpese.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.confirmDeleteAttachment = function (attachment) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.delete('api/rest/deleteAttachment?id=' + attachment.id+'&idRimborsoMissione='+$routeParams.idRimborsoMissione);
        var y = x.then(function (result) {
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                for (var i=0; i<$scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                    if (dettaglio.id === attachment.idMissione){
                        var attachments = dettaglio.attachments;
                        if (attachments && Object.keys(attachments).length > 0){
                            var newAttachments = attachments.filter(function(el){
                                return el.id !== attachment.id;
                            });
                            $scope.dettagliSpese[i].attachments = newAttachments;
                            if (Object.keys(newAttachments).length = 0){
                                $scope.dettagliSpese[i].attachmentsExists = false;
                            }
                        }
                    }
                }
            }
            $rootScope.salvataggio = false;
            ui.ok();
        });
        x.error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.$on('cambioData', function(event, data) {
        onChangeDataDettaglio();
    });

    $scope.onChangeKm= function (kmPercorsi, idDettaglioSpesa, dettaglioSpesa) {
        if (kmPercorsi ){
            if (idDettaglioSpesa){
                var dataFormatted = $filter('date')(dettaglioSpesa.dataSpesa, "dd/MM/yyyy");
                var tipi = ProxyService.getRimborsoKm("P", dataFormatted, 1).then(function(result){
                    if (result && result.data && result.data.elements && result.data.elements.length > 0){
                        var importo = Math.round((kmPercorsi * result.data.elements[0].indennita_chilometrica) * 100) / 100;
                        dettaglioSpesa.importo = importo;
                        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                            for (var i=0; i<$scope.dettagliSpese.length; i++) {
                                var dettaglio = $scope.dettagliSpese[i];
                                if (dettaglio.id === idDettaglioSpesa){
                                    dettaglio.importoEuro = importo;
                                }
                            }
                        }
                    }
                });
            } else if ($scope.rimborsoKm && $scope.rimborsoKm.indennita_chilometrica){
                $scope.newDettaglioSpesa.importoEuro = Math.round((kmPercorsi * $scope.rimborsoKm.indennita_chilometrica) * 100) / 100;
            }
        }
    }

    $scope.cambioSpesaAnticipata = function (spesaAnticipata) {
        if (spesaAnticipata == "S"){
            ui.message("Questa opzione selezionata indica che la spesa è stata sostenuta direttamente dall'Ente(tramite agenzia viaggi o altro), quindi non riguarda anticipi ricevuti per la missione. Per questo la spesa non verrà conteggiata nella missione.");
        }
    }

    var onChangeDataDettaglio = function () {
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.dataSpesa){
            recuperoTipiSpesa($scope.newDettaglioSpesa.dataSpesa);
        }
    }

    var prepareModifyDetail = function (dettaglioSpesa) {
        if (dettaglioSpesa && dettaglioSpesa.dataSpesa){
            recuperoTipiSpesa(dettaglioSpesa.dataSpesa);
        }
    }

    var recuperoTipiSpesa = function(dataSpesa){
        recuperoTipiSpesa(dataSpesa, null);
    }


    var recuperoTipiSpesa = function(dataSpesa, cdTipoSpesa){
        $scope.tipi_spesa = [];
        var dataFormatted = $filter('date')(dataSpesa, "dd/MM/yyyy");
        var tipi = ProxyService.getTipiSpesa($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione, $scope.rimborsoMissione.trattamento).then(function(result){
            if (result && result.data){
                $scope.tipi_spesa = result.data.elements;
                if (cdTipoSpesa){
                    $scope.onChangeTipoSpesa(cdTipoSpesa);
                }
            }
        });
    }

    $scope.deselect = function (idDettaglioSpesa) {
        if (idDettaglioSpesa){
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                for (var i=0; i<$scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                    if (dettaglio.id === idDettaglioSpesa){
                        delete dettaglio.viewAttachment;
                    }
                }
            }
        }
    }

    $scope.viewAttachments = function (idDettaglioSpesa) {
        if (idDettaglioSpesa){
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                for (var i=0; i<$scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                    if (dettaglio.id === idDettaglioSpesa){
                        if (!dettaglio.isFireSearchAttachments){
                            $http.get('api/rest/rimborsoMissione/dettagli/viewAttachments/' + idDettaglioSpesa).then(function (data) {
                                  $scope.dettagliSpese[i].isFireSearchAttachments = true;
                                  var attachments = data.data;
                                  if (attachments && Object.keys(attachments).length > 0){
                                    $scope.dettagliSpese[i].attachmentsExists = true;  
                                  } else {
                                    $scope.dettagliSpese[i].attachmentsExists = false;
                                  }
                                  $scope.dettagliSpese[i].attachments = attachments;
                            }, function () {
                                  $scope.dettagliSpese[i].isFireSearchAttachments = false;
                                  $scope.dettagliSpese[i].attachmentsExists = false;
                                  $scope.dettagliSpese[i].attachments = {};
                            });
                        }
                        $scope.dettagliSpese[i].viewAttachment = true;
                        break;
                    }
                }
            }
        }
    }

    var recuperoDettaglioSpesa = function (idDettaglioSpesa) {
        if (idDettaglioSpesa){
            if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                for (var i=0; i<$scope.dettagliSpese.length; i++) {
                    var dettaglio = $scope.dettagliSpese[i];
                    if (dettaglio.id === idDettaglioSpesa){
                        return dettaglio;
                    }
                }
            }
        }
    }

    $scope.onChangeTipoSpesa = function (cdTipoSpesa) {
        if (cdTipoSpesa){
            if ($scope.tipi_spesa && $scope.tipi_spesa.length > 0){
                for (var i=0; i<$scope.tipi_spesa.length; i++) {
                    var tipo_spesa = $scope.tipi_spesa[i];
                    if (tipo_spesa.cd_ti_spesa === cdTipoSpesa){
                        $scope.newDettaglioSpesa.dsTiSpesa = tipo_spesa.ds_ti_spesa;
                        if (tipo_spesa.fl_giustificativo_richiesto == true){
                            $scope.newDettaglioSpesa.giustificativo = 'S';    
                        } else {
                            $scope.newDettaglioSpesa.giustificativo = 'N';
                        }
                        $scope.giustificativo = tipo_spesa.fl_giustificativo_richiesto;
                        $scope.pasto = tipo_spesa.fl_pasto;
                        $scope.rimborso = tipo_spesa.fl_rimborso_km;
                        $scope.trasporto = tipo_spesa.fl_trasporto;
                        $scope.alloggio = tipo_spesa.fl_alloggio;
                        $scope.ammissibileRimborso = tipo_spesa.fl_ammissibile_con_rimborso;
                        if ($scope.pasto){
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "P";
                        } else if ($scope.rimborso){
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "R";
                        } else if ($scope.trasporto){
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "T";
                        } else if ($scope.alloggio){
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "A";
                        } else {
                            $scope.newDettaglioSpesa.tiCdTiSpesa = "N";
                        }
                        if ($scope.pasto && $scope.newDettaglioSpesa.dataSpesa){
                            var dataFormatted = $filter('date')($scope.newDettaglioSpesa.dataSpesa, "dd/MM/yyyy");
                            var tipi = ProxyService.getTipiPasto($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione).then(function(result){
                                if (result && result.data){
                                    $scope.tipi_pasto = result.data.elements;
                                } else {
                                    $scope.tipi_pasto = [];
                                }
                            });
                        }
                        if ($scope.rimborso){
                            var dataFormatted = $filter('date')($scope.newDettaglioSpesa.dataSpesa, "dd/MM/yyyy");
                            var tipi = ProxyService.getRimborsoKm("P", dataFormatted, 1).then(function(result){
                                if (result && result.data && result.data.elements && result.data.elements.length > 0){
                                    $scope.rimborsoKm = result.data.elements[0];
                                } else {
                                    $scope.rimborsoKm = [];
                                }
                            });
                        }
                    }
                }
            }
        }
    }
    $scope.reloadFromTipoSpesa = function (dettaglioSpesa) {
        if (dettaglioSpesa.cdTiSpesa){
            $scope.tipi_spesa = [];
            var dataFormatted = $filter('date')(dettaglioSpesa.dataSpesa, "dd/MM/yyyy");
            var tipi = ProxyService.getTipiSpesa($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione, $scope.rimborsoMissione.trattamento).then(function(result){
                if (result && result.data){
                    $scope.tipi_spesa = result.data.elements;
                    if ($scope.tipi_spesa && $scope.tipi_spesa.length > 0){
                        for (var i=0; i<$scope.tipi_spesa.length; i++) {
                            var tipo_spesa = $scope.tipi_spesa[i];
                            if (tipo_spesa.cd_ti_spesa === dettaglioSpesa.cdTiSpesa){
                                dettaglioSpesa.giustificativo = tipo_spesa.fl_giustificativo_richiesto;
                                dettaglioSpesa.pasto = tipo_spesa.fl_pasto;
                                dettaglioSpesa.rimborso = tipo_spesa.fl_rimborso_km;
                                dettaglioSpesa.trasporto = tipo_spesa.fl_trasporto;
                                dettaglioSpesa.alloggio = tipo_spesa.fl_alloggio;
                                dettaglioSpesa.ammissibileRimborso = tipo_spesa.fl_ammissibile_con_rimborso;
                                if (dettaglioSpesa.pasto && dettaglioSpesa.dataSpesa){
                                    var dataFormatted = $filter('date')(dettaglioSpesa.dataSpesa, "dd/MM/yyyy");
                                    var tipi = ProxyService.getTipiPasto($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione).then(function(result){
                                        if (result && result.data){
                                            $scope.tipi_pasto = result.data.elements;
                                        } else {
                                            $scope.tipi_pasto = [];
                                        }
                                    });
                                }
                                if (dettaglioSpesa.rimborso){
                                    var dataFormatted = $filter('date')(dettaglioSpesa.dataSpesa, "dd/MM/yyyy");
                                    var tipi = ProxyService.getRimborsoKm("P", dataFormatted, 1).then(function(result){
                                        if (result && result.data && result.data.elements && result.data.elements.length > 0){
                                            dettaglioSpesa.rimborsoKm = result.data.elements[0];
                                        } else {
                                            dettaglioSpesa.rimborsoKm = [];
                                        }
                                    });
                                }
                                break;        
                            }
                        }
                    }
                }
            });
        }
    }

    $scope.editDettaglioSpesa= function (dettaglioSpesa) {
      dettaglioSpesa.editing = true;
      $scope.reloadFromTipoSpesa(dettaglioSpesa);
    }

    var undoEditingDettaglioSpesa = function (dettaglioSpesa) {
      delete dettaglioSpesa.editing;
    }

    $scope.undoDettaglioSpesa = function (dettaglioSpesa) {
      undoEditingDettaglioSpesa(dettaglioSpesa);
    }

    var annullaDatiNuovaRiga = function () {
      delete $scope.addDettaglioSpesa;
      delete $scope.newDettaglioSpesa;
    }

    $scope.undoAddDettaglioSpesa = function () {
        annullaDatiNuovaRiga();
    }

    $scope.insertDettaglioSpesa = function (newDettaglioSpesa) {
        newDettaglioSpesa.rimborsoMissione = $scope.rimborsoMissione;
            $rootScope.salvataggio = true;
            newDettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(newDettaglioSpesa.dataSpesa);
            $http.post('api/rest/rimborsoMissione/dettagli/create', newDettaglioSpesa).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.dettagliSpese){
                        $scope.dettagliSpese = [];
                    }
                    if (data){
                        var dettaglio = angular.copy(data);
                        dettaglio.dataSpesa = DateUtils.convertLocalDateFromServer(dettaglio.dataSpesa);
                        dettaglio.importo = Math.round(dettaglio.importoEuro * 100) / 100
                        $scope.dettagliSpese.push(dettaglio);
                    }
                    $scope.undoAddDettaglioSpesa();
            }).error(function (data) {
                $rootScope.salvataggio = false;
            });
    }

    $scope.cambioSpesaAnticipata = function (dettaglioSpesa) {
        if (dettaglioSpesa.flSpesaAnticipata == 'S'){
            dettaglioSpesa.esercizioObbligazione = null;
            dettaglioSpesa.esercizioOriginaleObbligazione = null;
            dettaglioSpesa.cdCdsObbligazione = null;
            dettaglioSpesa.idRimborsoImpegni = null;
            dettaglioSpesa.pgObbligazione = null;
            dettaglioSpesa.voce = null;
            dettaglioSpesa.dsVoce = null;
        } else {
            caricaImpegnoSingolo(dettaglioSpesa);
        }
    }



    $scope.modifyDettaglioSpesa = function (dettaglioSpesa) {
        $rootScope.salvataggio = true;
        dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(dettaglioSpesa.dataSpesa);
        $http.put('api/rest/rimborsoMissione/dettagli/modify', dettaglioSpesa).success(function(data){
            $rootScope.salvataggio = false;
            dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateFromServer(dettaglioSpesa.dataSpesa);
            dettaglioSpesa.esercizioOriginaleObbligazione = data.esercizioOriginaleObbligazione;
            dettaglioSpesa.pgObbligazione = data.pgObbligazione;
            dettaglioSpesa.voce = data.voce;
            dettaglioSpesa.dsVoce = data.dsVoce;
            undoEditingDettaglioSpesa(dettaglioSpesa);
        }).error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.getTotaleDettagliSpesa = function(){
        var totale = 0;
        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
            for (var i=0; i<$scope.dettagliSpese.length; i++) {
                totale = totale + $scope.dettagliSpese[i].importoEuro;
            }
        }
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro){
            totale = totale + $scope.newDettaglioSpesa.importoEuro;
        }
        return totale;
    }

    $scope.getTotaleDettagliSpesaNonAnticipati = function(){
        var totale = 0;
        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
            for (var i=0; i<$scope.dettagliSpese.length; i++) {
                if ($scope.dettagliSpese[i].flSpesaAnticipata =="N"){
                    totale = totale + $scope.dettagliSpese[i].importoEuro;
                }
            }
        }
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro && $scope.newDettaglioSpesa.flSpesaAnticipata =="N"){
            totale = totale + $scope.newDettaglioSpesa.importoEuro;
        }
        return totale;
    }

    var impostadisabilitaRimborsoMissione = function() {
        if ($scope.rimborsoMissione && ($scope.rimborsoMissione.stato === 'DEF' || $scope.rimborsoMissione.statoFlusso === 'APP' || ($scope.rimborsoMissione.stato === 'CON' && 
            ($scope.rimborsoMissione.stateFlows === 'ANNULLATO' ||
                $scope.rimborsoMissione.stateFlows === 'FIRMA SPESA RIMBORSO' ||
                $scope.rimborsoMissione.stateFlows === 'FIRMA UO RIMBORSO' ||
                $scope.rimborsoMissione.stateFlows === 'FIRMATO')))) {
          return true;
        } else {
          return false;
        }
    }

   var inizializzaDati = function(){
        ElencoRimborsiMissioneService.findById($scope.idRimborsoMissione).then(function(data){
            $scope.rimborsoMissione = data;
            if ($scope.rimborsoMissione){
                $scope.disabilita = impostadisabilitaRimborsoMissione();
                RimborsoMissioneDettagliService.findDettagli($scope.idRimborsoMissione).then(function(data){
                    $scope.dettagliSpese = data;
                    if ($scope.dettagliSpese && $scope.dettagliSpese[0]){
                        $scope.getTotaleDettagliSpesa();
                    }
                });
            }
        });
    }

    inizializzaDati();


    $scope.previousPage = function () {
      parent.history.back();
    }
});
