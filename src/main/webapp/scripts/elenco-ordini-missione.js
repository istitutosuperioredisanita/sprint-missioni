'use strict';

missioniApp.factory('ElencoOrdiniMissioneService', function($http, ui, DateUtils) {
    return {
        findMissioniDaDuplicare: function(user) {
            var promise = $http.get('api/rest/ordiniMissione/listDaDuplicare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioniDaAnnullare: function(user) {
            var promise = $http.get('api/rest/ordiniMissione/listDaAnnullare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioniDaRimborsare: function(user, giaRimborsato) {
            var promise = $http.get('api/rest/ordiniMissione/listDaRimborsare', {
                params: {
                    user: user,
                    giaRimborsato: giaRimborsato
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioni: function(user, anno, uoRich, daNumero, aNumero, daData, aData, includiMissioniAnnullate, respGruppo, cup, daDataMissione, aDataMissione, statoOrdineMissione) {
            var promise = $http.get('api/rest/ordiniMissione/list', {
                params: {
                    user: user,
                    anno: anno,
                    uoRich: uoRich,
                    daNumero: daNumero,
                    aNumero: aNumero,
                    daData: daData,
                    aData: aData,
                    includiMissioniAnnullate: includiMissioniAnnullate,
                    respGruppo: respGruppo,
                    cup: cup,
                    daDataMissione: daDataMissione,
                    aDataMissione: aDataMissione,
                    giaRimborsato: statoOrdineMissione
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioniDaRendereDefinitive: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich) {
            var promise = $http.get('api/rest/ordiniMissione/listToFinal', {
                params: {
                    user: user,
                    anno: anno,
                    cdsRich: cdsRich,
                    daNumero: daNumero,
                    aNumero: aNumero,
                    daData: daData,
                    aData: aData,
                    uoRich: uoRich
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        findById: function(id) {
            var promise = $http.get('api/rest/ordineMissione/getById', {
                params: {
                    id: id
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findAnnullamentoById: function(id) {
            var promise = $http.get('api/rest/annullamentoOrdineMissione/getById', {
                params: {
                    id: id
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findListAnnullamentiToValidate: function(uo) {
            var promise = $http.get('api/rest/annullamentoOrdineMissione/listToValidate', {
                params: {
                    uoRich: uo
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findListToValidate: function(uo) {
            var promise = $http.get('api/rest/ordiniMissione/listToValidate', {
                params: {
                    uoRich: uo
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        getDatiUo: function(uo) {
            var promise = $http.get('api/rest/datiUO', {
                params: {
                    uo: uo
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        }
    }
});

missioniApp.controller('ElencoOrdiniMissioneController', function($rootScope, $scope, AccessToken, $location, $sessionStorage, ElencoOrdiniMissioneService, $filter, ui, ProxyService, DateService) {

    $scope.statoOrdineMissione = '';
    $scope.valoriStatoOrdineMissione = ProxyService.valueStatoOrdineMissione;

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

    $scope.onChangeStatoOrdineMissione = function(stato) {
        $scope.statoOrdineMissione = stato;
    };


    $scope.ricerca = function() {

        $scope.endSearching = false;
        $rootScope.salvataggio = true;
        $scope.ordiniMissione = null;
        var daDataFormatted = null;
        var aDataFormatted = null;
        var daDataMissioneFormatted = null;
        var aDataMissioneFormatted = null;
        if ($scope.daData) {
            daDataFormatted = $filter('date')($scope.daData, "dd/MM/yyyy");
        }
        if ($scope.aData) {
            aDataFormatted = $filter('date')($scope.aData, "dd/MM/yyyy");
        }
        if ($scope.daDataMissione) {
            daDataMissioneFormatted = $filter('date')($scope.daDataMissione, "dd/MM/yyyy");
        }
        if ($scope.aDataMissione) {
            aDataMissioneFormatted = $filter('date')($scope.aDataMissione, "dd/MM/yyyy");
        }

        ElencoOrdiniMissioneService.findMissioni($scope.userWork, $scope.anno, $scope.uoWorkForSpecialUser, $scope.daNumero, $scope.aNumero, daDataFormatted, aDataFormatted, $scope.annullati, $scope.respGruppo, $scope.cup, daDataMissioneFormatted, aDataMissioneFormatted, $scope.statoOrdineMissione).then(function(data) {
            if (data && data.length > 0) {
                $scope.ordiniMissione = data;
                $scope.messageOrdiniNonEsistenti = false;
            } else {
                $scope.messageOrdiniNonEsistenti = true;
            }
            $scope.endSearching = true;
            $rootScope.salvataggio = false;
        });
    }

    $scope.doSelectOrdineMissione = function(ordineMissione) {
        $location.path('/ordine-missione/' + ordineMissione.id);
    };

    $scope.doOrderMissioni = function(item) {
        if ($scope.predicate !== item)
            delete $scope.reverse;

        $scope.predicate = item;

        if ($scope.reverse === undefined)
            $scope.reverse = false;
        else if ($scope.reverse) {
            delete $scope.predicate;
            delete $scope.reverse;
        } else
            $scope.reverse = true;
    };

    $scope.stampa = function() {
        window.print();
    };

    $scope.reloadUserWork = function(uid) {
        if (uid) {
            $scope.userWork = uid;
        }
        $scope.ordiniMissione = [];
        $scope.messageOrdiniNonEsistenti = false;
        if (uid) {
            var person = ProxyService.getPerson(uid).then(function(result) {
                if (result) {
                    $scope.accountModel = result;
                }
            });
        }
    }

    $scope.reloadUoWork = function(uo) {
        if (uo) {
            $scope.uoWorkForSpecialUser = uo;
        }
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.ordiniMissione = [];
        $scope.messageOrdiniNonEsistenti = false;
        $scope.userWork = null;
        if (uo) {
            $scope.disableUo = true;
            var persons = ProxyService.getPersons(uo).then(function(result) {
                if (result) {
                    $scope.elencoPersone = result;
                    $scope.disableUo = false;
                }
            });
        }
    }

    $scope.accessToken = AccessToken.get();
    $scope.rowsPerPage = 30;
    $scope.predicate = 'ordineMissione.dataInserimento';
    $scope.reverse = true;

    var accountLog = $sessionStorage.account;
    var uoForUsersSpecial = accountLog.uoForUsersSpecial;
    if (uoForUsersSpecial) {
        $scope.userSpecial = true;
        var today = DateService.today().then(function(result) {
            if (result) {
                var elenco = ProxyService.getUos(result.getFullYear(), null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog)).then(function(result) {
                    $scope.uoForUsersSpecial = [];
                    if (result && result.data) {
                        var uos = result.data.elements;
                        var ind = -1;
                        for (var i = 0; i < uos.length; i++) {
                            for (var k = 0; k < uoForUsersSpecial.length; k++) {
                                if (uos[i].cd_unita_organizzativa == ProxyService.buildUoSiglaFromUoSiper(uoForUsersSpecial[k].codice_uo)) {
                                    ind++;
                                    $scope.uoForUsersSpecial[ind] = uos[i];
                                }
                            }
                        }
                        if ($scope.uoForUsersSpecial.length === 1) {
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