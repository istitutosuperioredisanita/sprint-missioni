'use strict';

missioniApp.factory('ElencoRimborsiMissioneService', function($http, ui) {
    return {
        findRimborsiMissione: function(user, anno, uoRich, daNumero, aNumero, daData, aData, annoOrdine, daNumeroOrdine, aNumeroOrdine, includiMissioniAnnullate, idOrdineMissione, recuperoTotali, cup, daDataMissione, aDataMissione) {
            var promise = $http.get('api/rest/rimborsoMissione/list', {
                params: {
                    user: user,
                    anno: anno,
                    uoRich: uoRich,
                    daNumero: daNumero,
                    aNumero: aNumero,
                    daData: daData,
                    aData: aData,
                    annoOrdine: annoOrdine,
                    daNumeroOrdine: daNumeroOrdine,
                    aNumeroOrdine: aNumeroOrdine,
                    includiMissioniAnnullate: includiMissioniAnnullate,
                    idOrdineMissione: idOrdineMissione,
                    recuperoTotali: recuperoTotali,
                    cup: cup,
                    daDataMissione: daDataMissione,
                    aDataMissione: aDataMissione
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiMissioneDaRendereDefinitive: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
            var promise = $http.get('api/rest/rimborsoMissione/listToFinal', {
                params: {
                    user: user,
                    anno: anno,
                    cdsRich: cdsRich,
                    daNumero: daNumero,
                    aNumero: aNumero,
                    daData: daData,
                    aData: aData,
                    uoRich: uoRich,
                    annoOrdine: annoOrdine,
                    daNumeroOrdine: daNumeroOrdine,
                    aNumeroOrdine: aNumeroOrdine
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiMissioneDaAnnullare: function(user, anno, cdsRich, daNumero, aNumero, daData, aData, uoRich, annoOrdine, daNumeroOrdine, aNumeroOrdine) {
            var promise = $http.get('api/rest/rimborsoMissione/listToBeDeleted', {
                params: {
                    user: user
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiDaAnnullare: function(user) {
            var promise = $http.get('api/rest/rimborsiMissione/listDaAnnullare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiMissioneDaInviareAllaFirma: function(user) {
            var promise = $http.get('api/rest/rimborsiMissione/listDaInviareAllaFirma', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiMissioneDaApprovare: function(user) {
            var promise = $http.get('api/rest/rimborsiMissione/listDaApprovare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsiMissioneDaConfermare: function(user) {
            var promise = $http.get('api/rest/rimborsiMissione/listDaConfermare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findById: function(id) {
            var promise = $http.get('api/rest/rimborsoMissione/getById', {
                params: {
                    id: id
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findListToValidate: function(uo) {
            var promise = $http.get('api/rest/rimborsoMissione/listToValidate', {
                params: {
                    uoRich: uo
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        findAnnullamentoById: function(id) {
            var promise = $http.get('api/rest/annullamentoRimborsoMissione/getById', {
                params: {
                    id: id
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findListAnnullamentiToValidate: function(uo) {
            var promise = $http.get('api/rest/annullamentoRimborsoMissione/listToValidate', {
                params: {
                    uoRich: uo
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findRimborsoImpegni: function(id) {
            var promise = $http.get('api/rest/rimborsoMissione/impegno/getImpegni', {
                params: {
                    idRimborsoMissione: id
                }
            }).success(function(response) {
                return response.data;
            });
            return promise;
        },
        stampaRimborso: function(idMissione, accessToken) {
            var url = 'api/rest/public/printRimborsoMissione?idMissione=' + idMissione + '&token=' + accessToken;
            return $http.get(url)
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    throw error;
                });
        }
    }
});

missioniApp.controller('ElencoRimborsiMissioneController', function($rootScope, $scope, AccessToken, $location, $sessionStorage, ElencoRimborsiMissioneService,ElencoOrdiniMissioneService, $filter, ui, ProxyService, DateService) {

    $scope.statoSecondoFiltroSelezionato = '';
    $scope.valoriFiltroStatiRimb = ProxyService.valueFiltroStatiRimb;


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

    $scope.onChangeStatoRimborsoMissione = function(stato) {
        $scope.statoSecondoFiltroSelezionato = stato;
    };

    $scope.$watch('annullati', function(newValue) {
        if (newValue === 'S') {
            $scope.statoFiltroSelezionato = { value: 'T', stato: 'Tutti' }; // Imposta il valore del filtro Stati su 'T'
        }
    });

    $scope.goPrintRimborsoMissione = function(idMissione, accessToken) {
        ElencoRimborsiMissioneService.stampaRimborso(idMissione, accessToken)
            .then(function(response) {
                window.location.href = response.config.url;
            })
            .catch(function(error) {
                ui.error("Errore durante il processo di stampa del Rimborso di Missione");
            });
    };


        $scope.currentPage = 1;
        $sessionStorage.rowsPerPage = $scope.rowsPerPage;
        $scope.rowsPerPage = $sessionStorage.rowsPerPage || 10;
        $scope.totalItems = 0;

        $scope.getTotalPages = function() {
            return Math.ceil($scope.totalItems / $scope.rowsPerPage);
        };


        $scope.isFirstPage = function() {
            return $scope.currentPage === 1;
        };

        $scope.isLastPage = function() {
            return $scope.currentPage === $scope.getTotalPages();
        };

        $scope.changePage = function(page) {
            if (page < 1 || page > $scope.getTotalPages()) return; // Pagina fuori intervallo
            $scope.currentPage = page; // Aggiorna la pagina corrente
            $scope.loadPaginatedData(); // Aggiorna i dati visibili
        };


    $scope.updateRowsPerPage = function() {
        $sessionStorage.rowsPerPage = $scope.rowsPerPage; // Salva il valore nel sessionStorage
        $scope.currentPage = 1; // Torna alla prima pagina
        $scope.loadPaginatedData(); // Aggiorna i dati visibili
    };



    $scope.loadPaginatedData = function() {
        const startIndex = ($scope.currentPage - 1) * $scope.rowsPerPage;
        const endIndex = startIndex + parseInt($scope.rowsPerPage, 10);

        if ($scope.rimborsiMissione && $scope.rimborsiMissione.length > 0) {
            $scope.paginatedItems = $scope.rimborsiMissione.slice(startIndex, endIndex);
        } else {
            $scope.paginatedItems = [];
        }
    };


    $scope.ricerca = function() {
        $scope.endSearching = false;
        $rootScope.salvataggio = true;
        $scope.rimborsiMissione = null;

        var daDataFormatted = $scope.daData ? $filter('date')($scope.daData, "dd/MM/yyyy") : null;
        var aDataFormatted = $scope.aData ? $filter('date')($scope.aData, "dd/MM/yyyy") : null;
        var daDataMissioneFormatted = $scope.daDataMissione ? $filter('date')($scope.daDataMissione, "dd/MM/yyyy") : null;
        var aDataMissioneFormatted = $scope.aDataMissione ? $filter('date')($scope.aDataMissione, "dd/MM/yyyy") : null;
        var filtroSelezionato = '';

        if($scope.annullati === 'S'){
            filtroSelezionato = 'T';
            $scope.statoSecondoFiltroSelezionato = null;
        } else {
            filtroSelezionato = $scope.statoSecondoFiltroSelezionato ? $scope.statoSecondoFiltroSelezionato.value : 'T';
        }

        // Gestione dello switch in base al filtro selezionato
        switch (filtroSelezionato) {
            case 'T':
                ElencoRimborsiMissioneService.findRimborsiMissione(
                    $scope.userWork, $scope.anno, $scope.uoWorkForSpecialUser, $scope.daNumero,
                    $scope.aNumero, daDataFormatted, aDataFormatted, $scope.annoOrdine,
                    $scope.daNumeroOrdine, $scope.aNumeroOrdine, $scope.annullati, null,
                    null, $scope.cup, daDataMissioneFormatted, aDataMissioneFormatted
                ).then(handleResponse);
                break;
            case 'DA ANN':
                ElencoRimborsiMissioneService.findRimborsiDaAnnullare($scope.userWork).then(handleResponse);
                break;

            case 'DA INV':
                ElencoRimborsiMissioneService.findRimborsiMissioneDaInviareAllaFirma($scope.userWork).then(handleResponse);
                break;

            case 'DA CONF':
                ElencoRimborsiMissioneService.findRimborsiMissioneDaConfermare($scope.userWork).then(handleResponse);
                break;

            case 'DA APP':
                ElencoRimborsiMissioneService.findRimborsiMissioneDaApprovare($scope.userWork).then(handleResponse);
                break;

            default:
                console.error('Filtro selezionato non riconosciuto:', filtroSelezionato);
                $scope.endSearching = true;
                $rootScope.salvataggio = false;
                break;
        }
    };

    function handleResponse(data) {
        if (data && data.length > 0) {
            $scope.rimborsiMissione = data || [];
            $scope.totalItems = $scope.rimborsiMissione.length;
            $scope.currentPage = 1;
            $scope.loadPaginatedData();
            $scope.messageRimborsiNonEsistenti = false;
        } else {
            // Reset dei risultati precedenti
            $scope.ordiniMissione = [];
            $scope.paginatedItems = [];
            $scope.totalItems = 0;
            $scope.messageRimborsiNonEsistenti = true;
        }
        $scope.endSearching = true;
        $rootScope.salvataggio = false;
    }


    $scope.doSelectRimborsoMissione = function(rimborsoMissione) {
        $location.path('/rimborso-missione/' + rimborsoMissione.id);
    };

    $scope.reloadUserWork = function(uid) {
        if (uid) {
            var person = ProxyService.getPerson(uid).then(function(result) {
                if (result) {
                    $scope.recuperoDatiTerzoSigla(result);
                    $scope.restOrdiniMissioneDaRimborsare(result, $scope.giaRimborsato);
                    $scope.accountModel = result;
                    $sessionStorage.accountWork = result;
                }
            });
        }
    }

    $scope.reloadUserWork = function(uid) {
        if (uid) {
            $scope.userWork = uid;
        }
        $scope.rimborsiMissione = [];
        $scope.messageRimborsiNonEsistenti = false;
        if (uid) {
            var person = ProxyService.getPerson(uid).then(function(result) {
                if (result) {
                    $scope.accountModel = result;
                }
            });
        }
    }

    $scope.stampa = function() {
        window.print();
    };

    $scope.reloadUoWork = function(uo) {
        if (uo) {
            $scope.uoWorkForSpecialUser = uo;
        }
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.rimborsiMissione = [];
        $scope.messageRimborsiNonEsistenti = false;
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