'use strict';

missioniApp.factory('TaxiOrdineMissioneService', function ($http) {
        return {
            findTaxi: function(idMissione) {
                var promise = $http.get('api/rest/ordineMissione/taxi/get', {params: {idMissione: idMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findSpostamenti: function(idTaxiOrdineMissione) {
                var promise = $http.get('api/rest/ordineMissione/taxi/getSpostamenti', {params: {idTaxiOrdineMissione: idTaxiOrdineMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('TaxiOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, AutoProprieService, DatiPatenteServiceUser, TaxiOrdineMissioneService, ElencoOrdiniMissioneService, ui, COSTANTI, DateService) {

    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiRigaSpostamento = function () {
      $scope.addSpostamentoTaxi = true;
      $scope.newSpostamentoTaxi = {};
    }

    $scope.changeAuto = function(taxi) {
        if (taxi){
            $scope.taxiOrdineMissioneModel.targa = taxi.targa;
            $scope.taxiOrdineMissioneModel.cartaCircolazione = taxi.cartaCircolazione;
            $scope.taxiOrdineMissioneModel.marca = taxi.marca;
            $scope.taxiOrdineMissioneModel.modello = taxi.modello;
            $scope.taxiOrdineMissioneModel.polizzaAssicurativa = taxi.polizzaAssicurativa;
        }
    }

    $scope.confirmDeleteSpostamenti = function (index) {
        var spostamentoDaEliminare = $scope.spostamentiTaxi[index];
        ui.confirmCRUD("Confermi l'eliminazione dello spostamento da  "+spostamentoDaEliminare.percorsoDa+" a "+spostamentoDaEliminare.percorsoA+"?", deleteSpostamenti, index);
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta dell'auto propria per l'ordine di missione numero "+$scope.taxiOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.taxiOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteTaxi);
    }

    $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);

    $scope.editSpostamento= function (spostamento) {
      spostamento.editing = true;
    }

    var undoEditingSpostamento = function (spostamento) {
      delete spostamento.editing;
    }

    $scope.undoSpostamento = function (spostamento) {
      undoEditingSpostamento(spostamento);
    }

    $scope.edit= function () {
      $scope.editing = true;
    }

    var undoEditing = function () {
      $scope.editing = false;
    }

    $scope.undo = function () {
      undoEditing();
    }

    $scope.isDisabilitataModificaAuto = function (){
        if ($scope.taxiOrdineMissioneModel && $scope.taxiOrdineMissioneModel.id && !$scope.editing){
            return true;
        }
        return false;
    }

    var inizializzaDati = function(){
        DatiPatenteServiceUser.get($scope.accountModel.login).$promise.then(function(datiPatente){
            $scope.taxiOrdineMissioneModel = {numeroPatente:datiPatente.numero, dataRilascioPatente:datiPatente.dataRilascio,
                    dataScadenzaPatente:datiPatente.dataScadenza, entePatente:datiPatente.ente};

            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
                $scope.taxiOrdineMissioneModel.ordineMissione = data;
            });
        });
    }

    $http.get('api/rest/ordineMissione/taxi/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
        var datiTaxiOrdineMissione = response.data;
        if (datiTaxiOrdineMissione.id === undefined){
            inizializzaDati();
        } else {
            $scope.taxiOrdineMissioneModel = datiTaxiOrdineMissione;
            $http.get('api/rest/ordineMissione/taxi/getSpostamenti', {params: {idTaxiOrdineMissione: $scope.taxiOrdineMissioneModel.id}}).then(function (response) {
                $scope.spostamentiTaxi = response.data;
            });
        }
    });

   $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.taxiOrdineMissioneModel.id){
                $http.put('api/rest/ordineMissione/taxi/modify', $scope.taxiOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            } else {
                $http.post('api/rest/ordineMissione/taxi/create', $scope.taxiOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.taxiOrdineMissioneModel = data;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            }
            undoEditing();
    }

    var deleteSpostamenti = function (index) {
        var idSpostamento = $scope.spostamentiTaxi[index].id;
            $rootScope.salvataggio = true;
            $http.delete('api/rest/ordineMissione/taxi/spostamenti/' + idSpostamento).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.spostamentiTaxi.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteTaxi = function () {
        var idTaxi = $scope.taxiOrdineMissioneModel.id;
                        $rootScope.salvataggio = true;
            $http.delete('api/rest/ordineMissione/taxi/' + idTaxi).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        inizializzaDati();
                        $scope.spostamentiTaxi = [];
                        undoEditing();
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var annullaDatiNuovaRiga = function () {
      delete $scope.addSpostamentoTaxi;
      delete $scope.newSpostamentoTaxi;
      delete $scope.error;
    }

    $scope.undoAddSpostamentoTaxi = function () {
        annullaDatiNuovaRiga();
    }

    $scope.insertSpostamentoTaxi = function (newRigaSpostamento) {
        newRigaSpostamento.ordineMissioneTaxi = $scope.taxiOrdineMissioneModel;
            $rootScope.salvataggio = true;
            $http.post('api/rest/ordineMissione/taxi/createSpostamento', newRigaSpostamento).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.spostamentiTaxi){
                        $scope.spostamentiTaxi = [];
                    }
                    $scope.spostamentiTaxi.push(data);
                    $scope.undoAddSpostamentoTaxi();
            }).error(function (data) {
                $rootScope.salvataggio = false;
            });
    }

    $scope.modifySpostamento = function (spostamentoTaxi) {
        $rootScope.salvataggio = true;
        $http.put('api/rest/ordineMissione/taxi/modifySpostamento', spostamentoTaxi).success(function(data){
            $rootScope.salvataggio = false;
            undoEditingSpostamento(spostamentoTaxi);
        }).error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.ricerca = function () {
        TaxiOrdineMissioneService.findMissioni($scope.cdsRich, $scope.daNumero, $scope.aNumero).then(function(data){
            $scope.ordiniMissione = data;
        });
    }

    $scope.previousPage = function () {
      parent.history.back();
    }
});
