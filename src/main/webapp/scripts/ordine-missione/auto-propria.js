'use strict';

missioniApp.factory('AutoPropriaOrdineMissioneService', function ($http) {
        return {
            findAutoPropria: function(idMissione) {
                var promise = $http.get('app/rest/ordineMissione/autoPropria/get', {params: {idMissione: idMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findSpostamenti: function(idAutoPropriaOrdineMissione) {
                var promise = $http.get('app/rest/ordineMissione/autoPropria/getSpostamenti', {params: {idAutoPropriaOrdineMissione: idAutoPropriaOrdineMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('AutoPropriaOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, AutoProprieService, DatiPatenteServiceUser, AutoPropriaOrdineMissioneService, ElencoOrdiniMissioneService, ui, COSTANTI) {
    
    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiRigaSpostamento = function () {
      $scope.addSpostamentoAutoPropria = true;
      $scope.newSpostamentoAutoPropria = {};
    }

    $scope.changeAuto = function(autoPropria) {
        if (autoPropria){
            $scope.autoPropriaOrdineMissioneModel.targa = autoPropria.targa;
            $scope.autoPropriaOrdineMissioneModel.cartaCircolazione = autoPropria.cartaCircolazione;
            $scope.autoPropriaOrdineMissioneModel.marca = autoPropria.marca;
            $scope.autoPropriaOrdineMissioneModel.modello = autoPropria.modello;
            $scope.autoPropriaOrdineMissioneModel.polizzaAssicurativa = autoPropria.polizzaAssicurativa;
        }
    }

    $scope.confirmDeleteSpostamenti = function (index) {
        var spostamentoDaEliminare = $scope.spostamentiAutoPropria[index];
        ui.confirmCRUD("Confermi l'eliminazione dello spostamento da  "+spostamentoDaEliminare.percorsoDa+" a "+spostamentoDaEliminare.percorsoA+"?", deleteSpostamenti, index);
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta dell'auto propria per l'ordine di missione numero "+$scope.autoPropriaOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.autoPropriaOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAutoPropria);
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
        if ($scope.autoPropriaOrdineMissioneModel && $scope.autoPropriaOrdineMissioneModel.id && !$scope.editing){
            return true;
        }
        return false;
    }

    var inizializzaDati = function(){
        DatiPatenteServiceUser.get($scope.accountModel.login).$promise.then(function(datiPatente){
            $scope.autoPropriaOrdineMissioneModel = {numeroPatente:datiPatente.numero, dataRilascioPatente:datiPatente.dataRilascio, 
                    dataScadenzaPatente:datiPatente.dataScadenza, entePatente:datiPatente.ente};

            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
                $scope.autoPropriaOrdineMissioneModel.ordineMissione = data;
            });
        });
    }

    $http.get('app/rest/ordineMissione/autoPropria/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
        var datiAutoPropriaOrdineMissione = response.data;
        if (datiAutoPropriaOrdineMissione.id === undefined){
            inizializzaDati();
        } else {
            $scope.autoPropriaOrdineMissioneModel = datiAutoPropriaOrdineMissione;
            $http.get('app/rest/ordineMissione/autoPropria/getSpostamenti', {params: {idAutoPropriaOrdineMissione: $scope.autoPropriaOrdineMissioneModel.id}}).then(function (response) {
                $scope.spostamentiAutoPropria = response.data;
            });
        }
    });


    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date

            $scope.ordineMissioneModel.dataInserimento = $filter('date')(today, "dd-MM-yyyy");
    };

    $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.autoPropriaOrdineMissioneModel.id){
                $http.put('app/rest/ordineMissione/autoPropria/modify', $scope.autoPropriaOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                    ui.error(data);
                });
            } else {
                $http.post('app/rest/ordineMissione/autoPropria/create', $scope.autoPropriaOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.autoPropriaOrdineMissioneModel = data;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                    ui.error(data);
                });
            }
            undoEditing();
    }

    var deleteSpostamenti = function (index) {
        var idSpostamento = $scope.spostamentiAutoPropria[index].id;
            $rootScope.salvataggio = true;
            $http.delete('app/rest/ordineMissione/autoPropria/spostamenti/' + idSpostamento).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.spostamentiAutoPropria.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                        ui.error(data);
                    }
            );
    }

    var deleteAutoPropria = function () {
        var idAutoPropria = $scope.autoPropriaOrdineMissioneModel.id;
                        $rootScope.salvataggio = true;
            $http.delete('app/rest/ordineMissione/autoPropria/' + idAutoPropria).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        inizializzaDati();
                        $scope.spostamentiAutoPropria = [];
                        undoEditing();
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                        ui.error(data);
                    }
            );
    }

    var annullaDatiNuovaRiga = function () {
      delete $scope.addSpostamentoAutoPropria;
      delete $scope.newSpostamentoAutoPropria;
      delete $scope.error;
    }

    $scope.undoAddSpostamentoAutoPropria = function () {
        annullaDatiNuovaRiga();
    }

    $scope.insertSpostamentoAutoPropria = function (newRigaSpostamento) {
        newRigaSpostamento.ordineMissioneAutoPropria = $scope.autoPropriaOrdineMissioneModel;
            $rootScope.salvataggio = true;
            $http.post('app/rest/ordineMissione/autoPropria/createSpostamento', newRigaSpostamento).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.spostamentiAutoPropria){
                        $scope.spostamentiAutoPropria = [];
                    }
                    $scope.spostamentiAutoPropria.push(data);
                    $scope.undoAddSpostamentoAutoPropria();
            }).error(function (data) {
                $rootScope.salvataggio = false;
                ui.error(data);
            });
    }

    $scope.modifySpostamento = function (spostamentoAutoPropria) {
        $rootScope.salvataggio = true;
        $http.put('app/rest/ordineMissione/autoPropria/modifySpostamento', spostamentoAutoPropria).success(function(data){
            $rootScope.salvataggio = false;
            undoEditingSpostamento(spostamentoAutoPropria);
        }).error(function (data) {
            $rootScope.salvataggio = false;
            ui.error(data);
        });
    }

    $scope.ricerca = function () {
        AutoPropriaOrdineMissioneService.findMissioni($scope.cdsRich, $scope.daNumero, $scope.aNumero).then(function(data){
            $scope.ordiniMissione = data;
        });
    }

    $scope.previousPage = function () {
      parent.history.back();
    }
});
