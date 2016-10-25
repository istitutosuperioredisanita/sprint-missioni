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

missioniApp.controller('RimborsoMissioneDettagliController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, RimborsoMissioneDettagliService, ui, COSTANTI) {
    
    $scope.validazione = $routeParams.validazione;
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
        newDettaglioSpesa.rimborsoMissione = $scope.rimborsoMissioneModel;
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
                totale = totale + dettagliSpese[i].importoEuro;
            }
        }
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro){
            totale = totale + $scope.newDettaglioSpesa.importoEuro;
        }
        return totale;
    }


   var inizializzaDati = function(){
        ElencoRimborsoMissioneService.findById($scope.idRimborsoMissione).then(function(data){
            $scope.rimborsoMissione = data;
        });
    }

    inizializzaDati();
    $scope.dettagliSpese = RimborsoMissioneDettagliService.findDettagli($scope.idRimborsoMissione);
    if ($scope.dettagliSpese && $scope.dettagliSpese[0]){
        $scope.getTotaleDettagliSpesa;
    }


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
