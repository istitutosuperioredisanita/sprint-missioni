'use strict';

missioniApp.factory('ElencoRimborsiMissioneService', function ($http, ui) {
        return {
            findRimborsiMissione: function(user, anno, uoRich, daNumero, aNumero, daData, aData, annoOrdine, daNumeroOrdine, aNumeroOrdine, includiMissioniAnnullate, idOrdineMissione, recuperoTotali, cup, daDataMissione, aDataMissione) {
                var promise = $http.get('api/rest/rimborsoMissione/list', {params: {user:user, anno: anno, uoRich: uoRich, 
                        daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData, annoOrdine: annoOrdine, 
                        daNumeroOrdine: daNumeroOrdine, aNumeroOrdine: aNumeroOrdine, 
                        includiMissioniAnnullate: includiMissioniAnnullate, 
                        idOrdineMissione: idOrdineMissione, recuperoTotali: recuperoTotali, cup: cup, daDataMissione: daDataMissione, aDataMissione: aDataMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findRimborsiMissioneDaRendereDefinitive: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
                var promise = $http.get('api/rest/rimborsoMissione/listToFinal', {params: {user:user, anno: anno, cdsRich: cdsRich, daNumero: daNumero, aNumero: aNumero, daData: daData, aData: aData, uoRich: uoRich, annoOrdine: annoOrdine, daNumeroOrdine: daNumeroOrdine, aNumeroOrdine: aNumeroOrdine}}).success(function (response) {
                    return response.data;
                });
                return promise;
            },
            findRimborsiMissioneDaAnnullare: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
                var promise = $http.get('api/rest/rimborsoMissione/listToBeDeleted', {params: {user:user}}).success(function (response) {
                    return response.data;
                });
                return promise;
            },
            findById: function(id) {
                var promise = $http.get('api/rest/rimborsoMissione/getById', {params: {id: id}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findListToValidate: function() {
                var promise = $http.get('api/rest/rimborsoMissione/listToValidate').success(function (response) {
                    return response.data;
                });
                return promise;
            },
            findAnnullamentoById: function(id) {
                var promise = $http.get('api/rest/annullamentoRimborsoMissione/getById', {params: {id: id}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findListAnnullamentiToValidate: function() {
                var promise = $http.get('api/rest/annullamentoRimborsoMissione/listToValidate').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findRimborsoImpegni: function(id) {
                var promise = $http.get('api/rest/rimborsoMissione/impegno/getImpegni', {params: {idRimborsoMissione: id}}).success(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.controller('ElencoRimborsiMissioneController', function ($rootScope, $scope, AccessToken, $location, $sessionStorage, ElencoRimborsiMissioneService, $filter, ui, ProxyService, DateService) {

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
        $rootScope.salvataggio = true;
        $scope.rimborsiMissione = null;
        var daDataFormatted = null;
        var aDataFormatted = null;
        var daDataMissioneFormatted = null;
        var aDataMissioneFormatted = null;
        if ($scope.daData){
            daDataFormatted = $filter('date')($scope.daData, "dd/MM/yyyy");
        }
        if ($scope.aData){
            aDataFormatted = $filter('date')($scope.aData, "dd/MM/yyyy");
        }

        if ($scope.daDataMissione){
            daDataMissioneFormatted = $filter('date')($scope.daDataMissione, "dd/MM/yyyy");
        }
        if ($scope.aDataMissione){
            aDataMissioneFormatted = $filter('date')($scope.aDataMissione, "dd/MM/yyyy");
        }
        ElencoRimborsiMissioneService.findRimborsiMissione($scope.userWork, $scope.anno, $scope.uoWorkForSpecialUser, $scope.daNumero, 
                $scope.aNumero, daDataFormatted, aDataFormatted, $scope.annoOrdine, $scope.daNumeroOrdine, $scope.aNumeroOrdine,
                $scope.annullati,null,null,$scope.cup, daDataMissioneFormatted, aDataMissioneFormatted).then(function(data){
            if (data && data.length > 0){
                $scope.rimborsiMissione = data;
                $scope.messageRimborsiNonEsistenti = false;
            } else {
                $scope.messageRimborsiNonEsistenti = true;
            }
            $scope.endSearching = true;
            $rootScope.salvataggio = false;
        });        
    }

    $scope.doSelectRimborsoMissione = function (rimborsoMissione) {
        $location.path('/rimborso-missione/'+rimborsoMissione.id);
    };

    $scope.reloadUserWork = function(uid){
        if (uid){
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.recuperoDatiTerzoSigla(result);
                    $scope.restOrdiniMissioneDaRimborsare(result, $scope.giaRimborsato);
                    $scope.accountModel = result;
                    $sessionStorage.accountWork = result;
                }
            });
        }
    }

    $scope.reloadUserWork = function(uid){
        if (uid){
            $scope.userWork = uid;
        }
        $scope.rimborsiMissione = [];
        $scope.messageRimborsiNonEsistenti = false;
        if (uid){
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.accountModel = result;
                }
            });
        }
    }

    $scope.stampa = function () {
        window.print();
    };

    $scope.reloadUoWork = function(uo){
        if (uo){
            $scope.uoWorkForSpecialUser = uo;
        }
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.rimborsiMissione = [];
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

    $scope.accessToken = AccessToken.get();
    var accountLog = $sessionStorage.account;
    var uoForUsersSpecial = accountLog.uoForUsersSpecial;
    if (uoForUsersSpecial){
        $scope.userSpecial = true;
        var today = DateService.today().then(function(result){
            if (result){
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
                    } else {
                        $scope.accountModel = accountLog;
                    }
                });
            }
        });
    } else {
        $scope.accountModel = accountLog;
    }
});
