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
    }

    $scope.confirmDeleteDettaglioSpesa = function (index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        ui.confirmCRUD("Confermi l'eliminazione del dettaglio della spesa "+dettaglioSpesaDaEliminare.cdTiSpesa+"?", deleteDettaglioSpesa, index);
    }

    var deleteDettaglioSpesa = function (index) {
        var idDettaglioSpesa = $scope.dettagliSpese[index].id;
            $rootScope.salvataggio = true;
            $http.delete('app/rest/rimborsoMissione/dettaglio/' + idDettaglioSpesa).success(
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

    $scope.$on('cambioData', function(event, data) {
        onChangeDataDettaglio();
    });

    var onChangeDataDettaglio = function () {
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.dataSpesa){
            var dataFormatted = $filter('date')($scope.newDettaglioSpesa.dataSpesa, "dd/MM/yyyy");
            var tipi = ProxyService.getTipiSpesa($scope.rimborsoMissione.inquadramento, dataFormatted, $scope.rimborsoMissione.nazione, $scope.rimborsoMissione.trattamento).then(function(result){
                if (result && result.data){
                    $scope.tipi_spesa = result.data.elements;
                } else {
                    $scope.tipi_spesa = [];
                }
            });
        }
    }

    $scope.onChangeTipoSpesa = function (cdTipoSpesa) {
        if (cdTipoSpesa){
            if ($scope.tipi_spesa && $scope.tipi_spesa.length > 0){
                for (var i=0; i<$scope.tipi_spesa.length; i++) {
                    var tipo_spesa = $scope.tipi_spesa[i];
                    if (tipo_spesa.cd_ti_spesa === cdTipoSpesa){
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
                    }
                }
            }
        }
    }

    $scope.editDettaglioSpesa= function (dettaglioSpesa) {
      dettaglioSpesa.editing = true;
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
            $http.post('app/rest/rimborsoMissione/dettaglio/create', newDettaglioSpesa).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.dettagliSpesa){
                        $scope.dettagliSpesa = [];
                    }
                    $scope.dettagliSpesa.push(data);
                    $scope.undoDettaglioSpesa();
            }).error(function (data) {
                $rootScope.salvataggio = false;
                ui.error(data);
            });
    }

    $scope.modifyDettaglioSpesa = function (dettaglioSpesa) {
        $rootScope.salvataggio = true;
        $http.put('app/rest/rimborsoMissione/dettaglio/modify', dettaglioSpesa).success(function(data){
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


   var inizializzaDati = function(){
        ElencoRimborsiMissioneService.findById($scope.idRimborsoMissione).then(function(data){
            $scope.rimborsoMissione = data;
            if ($scope.rimborsoMissione){
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
