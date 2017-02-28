'use strict';

missioniApp.factory('ElencoRimborsiMissioneService', function ($http, ui) {
        return {
            findRimborsiMissione: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
                var promise = $http.get('app/rest/rimborsoMissione/list', {params: {user:user, anno: anno, cdsRich: cdsRich, daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData, annoOrdine: annoOrdine, daNumeroOrdine: daNumeroOrdine, aNumeroOrdine: aNumeroOrdine}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findRimborsiMissioneDaRendereDefinitive: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
                var promise = $http.get('app/rest/rimborsoMissione/listToFinal', {params: {user:user, anno: anno, cdsRich: cdsRich, daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData, uoRich: uoRich, annoOrdine: annoOrdine, daNumeroOrdine: daNumeroOrdine, aNumeroOrdine: aNumeroOrdine}}).success(function (response) {
                    return response.data;
                });
                return promise;
            },
            findById: function(id) {
                var promise = $http.get('app/rest/rimborsoMissione/getById', {params: {id: id}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findListToValidate: function() {
                var promise = $http.get('app/rest/rimborsoMissione/listToValidate').success(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('ElencoRimborsiMissioneController', function ($scope, $location, $sessionStorage, ElencoRimborsiMissioneService, $filter, ui, ProxyService) {

    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date
            return today;
    };

    $scope.tipiMissione = {
      		'Italia': 'I',
      		'Estera': 'E'
    };

    $scope.valoriPriorita = {
            'Critica': '1',
            'Importante': '3',
            'Media': '5'
    };

    $scope.statoMissione = {
        'Annullato': 'ANN',
        'Confermato': 'CON',
        'Inserito': 'INS',
        'Definitivo': 'DEF'
    };

    $scope.ricerca = function () {
        $scope.endSearching = false;
        $scope.rimborsiMissione = null;
        ElencoRimborsiMissioneService.findRimborsiMissione($scope.userWork, $scope.anno, null, $scope.daNumero, $scope.aNumero, $scope.daData, $scope.aData, $scope.annoOrdine, $scope.daNumeroOrdine, $scope.aNumeroOrdine).then(function(data){
            if (data && data.length > 0){
                $scope.rimborsiMissione = data;
                $scope.messageRimborsiNonEsistenti = false;
            } else {
                $scope.messageRimborsiNonEsistenti = true;
            }
            $scope.endSearching = true;
        });        
    }

    $scope.doSelectRimborsoMissione = function (rimborsoMissione) {
        $location.path('/rimborso-missione/'+rimborsoMissione.id);
    };

    $scope.reloadUserWork = function(uid){
        $scope.rimborsiMissione = [];
        $scope.messageRimborsiNonEsistenti = false;
        if (uid){
            for (var i=0; i<$scope.elencoPersone.length; i++) {
                if (uid == $scope.elencoPersone[i].uid){
                    var data = $scope.elencoPersone[i];
                    var userWork = ProxyService.buildPerson(data);

                    $scope.accountModel = userWork;
                }
            }
        }
    }

    $scope.reloadUoWork = function(uo){
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.ordiniMissione = [];
        $scope.messageRimborsiNonEsistenti = false;
        $scope.userWork = null;
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

    var accountLog = $sessionStorage.account;
    var uoForUsersSpecial = accountLog.uoForUsersSpecial;
    if (uoForUsersSpecial){
        $scope.userSpecial = true;
        var anno = $scope.today().getFullYear();
        var elenco = ProxyService.getUos(anno, null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog)).then(function(result){
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
                }
            } else {
                $scope.accountModel = accountLog;
            }
        });
    } else {
        $scope.accountModel = accountLog;
    }
});
