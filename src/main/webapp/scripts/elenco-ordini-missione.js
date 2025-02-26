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
        findMissioniDaInviareAllaFirma: function(user) {
            var promise = $http.get('api/rest/ordiniMissione/listDaInviareAllaFirma', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioniDaApprovare: function(user) {
            var promise = $http.get('api/rest/ordiniMissione/listDaApprovare', {
                params: {
                    user: user
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findMissioniDaConfermare: function(user) {
            var promise = $http.get('api/rest/ordiniMissione/listDaConfermare', {
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
        },
        stampaOrdine: function (idMissione, accessToken) {
            var url = 'api/rest/public/printOrdineMissione?idMissione=' + idMissione + '&token=' + accessToken;
            return $http.get(url, { responseType: 'blob' })
                .then(function (response) {
                    if (response.status === 200 && response.data.size > 0) {
                        return response;
                    } else {
                        throw new Error("Il documento ricevuto è vuoto o la risposta non è valida.");
                    }
                })
                .catch(function (error) {
                    throw error;
                });
        }

    }
});

missioniApp.controller('ElencoOrdiniMissioneController', function($rootScope, $scope, AccessToken, $location, $sessionStorage, ElencoOrdiniMissioneService, $filter, ui, ProxyService, DateService) {

    $scope.statoOrdineMissione = '';
    $scope.statoSecondoFiltroSelezionato = '';

    $scope.valoriStatoOrdineMissione = ProxyService.valueStatoOrdineMissione;
    $scope.valoriFiltroStati = ProxyService.valueFiltroStati;

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

   $scope.goPrintOrdineMissione = function (idMissione, accessToken) {
       ElencoOrdiniMissioneService.stampaOrdine(idMissione, accessToken)
           .then(function (response) {
               // Verifica che la risposta sia valida e contenga un URL
               if (response && response.config && response.config.url) {
                   window.location.href = response.config.url;
               } else {
                ui.error("Errore durante il processo di stampa dell' Ordine di Missione");
               }
           })
           .catch(function (error) {
                ui.error("Errore durante il processo di stampa dell' Ordine di Missione");
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

        if ($scope.ordiniMissione && $scope.ordiniMissione.length > 0) {
            $scope.paginatedItems = $scope.ordiniMissione.slice(startIndex, endIndex);
        } else {
            $scope.paginatedItems = [];
        }
    };



    $scope.ricerca = function() {
        $scope.endSearching = false;
        $rootScope.salvataggio = true;
        $scope.ordiniMissione = null;

        var daDataFormatted = $scope.daData ? $filter('date')($scope.daData, "dd/MM/yyyy") : null;
        var aDataFormatted = $scope.aData ? $filter('date')($scope.aData, "dd/MM/yyyy") : null;
        var daDataMissioneFormatted = $scope.daDataMissione ? $filter('date')($scope.daDataMissione, "dd/MM/yyyy") : null;
        var aDataMissioneFormatted = $scope.aDataMissione ? $filter('date')($scope.aDataMissione, "dd/MM/yyyy") : null;
        var filtroStatiSelezionato = '';

        if($scope.annullati === 'S'){
            filtroStatiSelezionato = 'T';
            $scope.statoSecondoFiltroSelezionato = null;
        } else {
            filtroStatiSelezionato = $scope.statoSecondoFiltroSelezionato ? $scope.statoSecondoFiltroSelezionato.value : 'T';
        }

        // Gestione dello switch in base al filtro selezionato
        switch (filtroStatiSelezionato) {
            case 'T':
                ElencoOrdiniMissioneService.findMissioni($scope.userWork, $scope.anno, $scope.uoWorkForSpecialUser, $scope.daNumero, $scope.aNumero, daDataFormatted, aDataFormatted, $scope.annullati, $scope.respGruppo, $scope.cup, daDataMissioneFormatted, aDataMissioneFormatted, $scope.statoOrdineMissione).then(handleResponse);
                break;

            case 'DA ANN':
                ElencoOrdiniMissioneService.findMissioniDaAnnullare($scope.userWork).then(handleResponse);
                break;

            case 'DA INV':
                ElencoOrdiniMissioneService.findMissioniDaInviareAllaFirma($scope.userWork).then(handleResponse);
                break;

            case 'DA CONF':
                ElencoOrdiniMissioneService.findMissioniDaConfermare($scope.userWork).then(handleResponse);
                break;

            case 'DA APP':
                ElencoOrdiniMissioneService.findMissioniDaApprovare($scope.userWork).then(handleResponse);
                break;

            default:
                console.error('Filtro selezionato non riconosciuto:', filtroStatiSelezionato);
                $scope.endSearching = true;
                $rootScope.salvataggio = false;
                break;
        }
    };

    // Gestisce la risposta dei dati
    function handleResponse(data) {
        if (data && data.length > 0) {
            $scope.ordiniMissione = data || [];
            $scope.totalItems = $scope.ordiniMissione.length;
            $scope.currentPage = 1;
            $scope.loadPaginatedData();
            $scope.messageOrdiniNonEsistenti = false;
            console.log('Tutti gli elementi:', $scope.ordiniMissione);
            console.log('Elementi nella pagina corrente:', $scope.paginatedItems);

        } else {
            // Reset dei risultati precedenti
            $scope.ordiniMissione = [];
            $scope.paginatedItems = [];
            $scope.totalItems = 0;
            $scope.messageOrdiniNonEsistenti = true;
        }
        $scope.endSearching = true;
        $rootScope.salvataggio = false;
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

    $scope.onChangeStatoOrdineMissione = function(stato) {
        $scope.statoOrdineMissione = stato;
        if (stato && stato !== 'T') {
            $scope.statoSecondoFiltroSelezionato = null; // Resetta il filtro Stati
        }
    };

    $scope.onChangeFiltroStati = function(stato) {
        $scope.statoSecondoFiltroSelezionato = stato;
        if (stato && stato.value !== 'T') {
            $scope.statoOrdineMissione = null; // Resetta il filtro Ordini di Missione
        }
    };

        $scope.$watch('statoFiltroSelezionato', function(newValue) {
            if (newValue && newValue !== 'T') {
                $scope.statoOrdineMissione = { value: 'T', stato: 'Tutti' }; // Imposta il valore del filtro Stati su 'T'
            }
        });

});