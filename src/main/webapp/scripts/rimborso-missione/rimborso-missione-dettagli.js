'use strict';

missioniApp.factory('RimborsoMissioneDettagliService', function ($http) {
        return {
            findDettagli: function(idRimborsoMissione) {
                var promise = $http.get('app/rest/rimborsoMissione/dettagli/get', {params: {idRimborsoMissione: idRimborsoMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('RimborsoMissioneDettagliController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, RimborsoMissioneDettagliService, ProxyService, ElencoRimborsiMissioneService, ui, COSTANTI) {
    
    $scope.validazione = $routeParams.validazione;
    $scope.inizioMissione = $routeParams.inizioMissione;
    $scope.fineMissione = $routeParams.fineMissione;
    $scope.idRimborsoMissione = $routeParams.idRimborsoMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiDettaglioSpesa = function () {
      $scope.addDettaglioSpesa = true;
      $scope.newDettaglioSpesa = {};
      inizializzaNuovaRiga();
    }

    var inizializzaNuovaRiga = function(){
        $scope.newDettaglioSpesa.flSpesaAnticipata = "N";
        $scope.newDettaglioSpesa.cdDivisa = "EURO";
        $scope.newDettaglioSpesa.cambio = 1;
        $scope.tipi_pasto = [];
        $scope.rimborsoKm = null;
    }

    $scope.confirmDeleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        ui.confirmCRUD("Confermi l'eliminazione del dettaglio spesa: "+dettaglioSpesaDaEliminare.dsTiSpesa+" del "+$filter('date')(dettaglioSpesaDaEliminare.dataSpesa, COSTANTI.FORMATO_DATA)+"?", deleteDettaglioSpesa, index);
    }

    var deleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        $rootScope.salvataggio = true;
        $http.delete('app/rest/rimborsoMissione/dettagli/' + dettaglioSpesaDaEliminare.id).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.dettagliSpese.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                        ui.error(data);
                    }
            );
    }

    $scope.confirmDeleteAttachment = function (attachment) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.get('app/rest/deleteAttachment/' + attachment.id);
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
            ui.error(data);
        });
    }

    $scope.$on('cambioData', function(event, data) {
        onChangeDataDettaglio();
    });

    $scope.onChangeKm= function (kmPercorsi) {
        if (kmPercorsi && $scope.rimborsoKm && $scope.rimborsoKm.indennita_chilometrica){
            $scope.newDettaglioSpesa.importoEuro = kmPercorsi * $scope.rimborsoKm.indennita_chilometrica;
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
                            $http.get('app/rest/rimborsoMissione/dettagli/viewAttachments/' + idDettaglioSpesa).then(function (data) {
                                  $scope.dettagliSpese[i].isFireSearchAttachments = true;
                                  var attachments = data.data;
                                  $scope.dettagliSpese[i].attachmentsExists = attachments && Object.keys(attachments).length > 0;
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
                        $scope.giustificativo = tipo_spesa.fl_giustificativo_richiesto;
                        $scope.pasto = tipo_spesa.fl_pasto;
                        $scope.rimborso = tipo_spesa.fl_rimborso_km;
                        $scope.trasporto = tipo_spesa.fl_trasporto;
                        $scope.alloggio = tipo_spesa.fl_alloggio;
                        $scope.ammissibileRimborso = tipo_spesa.fl_ammissibile_con_rimborso;
                        if ($scope.pasto){
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
                                if (dettaglioSpesa.pasto){
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
            $http.post('app/rest/rimborsoMissione/dettagli/create', newDettaglioSpesa).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.dettagliSpese){
                        $scope.dettagliSpese = [];
                    }
                    $scope.dettagliSpese.push(data);
                    $scope.undoAddDettaglioSpesa();
            }).error(function (data) {
                $rootScope.salvataggio = false;
                ui.error(data);
            });
    }

    $scope.modifyDettaglioSpesa = function (dettaglioSpesa) {
        $rootScope.salvataggio = true;
        $http.put('app/rest/rimborsoMissione/dettagli/modify', dettaglioSpesa).success(function(data){
            $rootScope.salvataggio = false;
            undoEditingDettaglioSpesa(dettaglioSpesa);
        }).error(function (data) {
            $rootScope.salvataggio = false;
            ui.error(data);
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

    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date

            $scope.ordineMissioneModel.dataInserimento = $filter('date')(today, "dd-MM-yyyy");
    };

    $scope.previousPage = function () {
      parent.history.back();
    }
});
