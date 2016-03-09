'use strict';

missioniApp.factory('ElencoOrdiniMissioneService', function ($http, ui) {
        return {
            findMissioni: function(user, anno, cdsRich, daNumero, aNumero, daData, aData) {
                var promise = $http.get('app/rest/ordiniMissione/list', {params: {user:user, anno: anno, cdsRich: cdsRich, daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findMissioniDaRendereDefinitive: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich) {
                var promise = $http.get('app/rest/ordiniMissione/listToFinal', {params: {user:user, anno: anno, cdsRich: cdsRich, daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData, uoRich: uoRich}}).success(function (response) {
                    return response.data;
                }).error(function(response){
                    ui.error(response.message)
                });
                return promise;
            },
            findById: function(id) {
                var promise = $http.get('app/rest/ordineMissione/getById', {params: {id: id}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findListToValidate: function() {
                var promise = $http.get('app/rest/ordiniMissione/listToValidate').success(function (response) {
                    return response.data;
                }).error(function(response){
                    ui.error(response.message)
                });
                return promise;
            }
        }
    });

missioniApp.controller('ElencoOrdiniMissioneController', function ($scope, $location, $sessionStorage, ElencoOrdiniMissioneService, $filter, ui, ProxyService) {

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
        ElencoOrdiniMissioneService.findMissioni($scope.userWork, $scope.anno, null, $scope.daNumero, $scope.aNumero, $scope.daData, $scope.aData).then(function(data){
            if (data && data.length > 0){
                $scope.ordiniMissione = data;
                $scope.messageOrdiniNonEsistenti = false;
            } else {
                $scope.messageOrdiniNonEsistenti = true;
            }
        });        
    }

    $scope.doSelectOrdineMissione = function (ordineMissione) {
        $location.path('/ordine-missione/'+ordineMissione.id);
    };

    $scope.reloadUserWork = function(uid){
        $scope.ordiniMissione = [];
        $scope.messageOrdiniNonEsistenti = false;
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
        $scope.messageOrdiniNonEsistenti = false;
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
