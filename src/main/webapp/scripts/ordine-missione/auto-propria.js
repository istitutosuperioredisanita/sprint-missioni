'use strict';

missioniApp.factory('AutoPropriaOrdineMissioneService', function($http) {
    return {
        findAutoPropria: function(idMissione) {
            var promise = $http.get('api/rest/ordineMissione/autoPropria/get', {
                params: {
                    idMissione: idMissione
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        },
        findSpostamenti: function(idAutoPropriaOrdineMissione) {
            var promise = $http.get('api/rest/ordineMissione/autoPropria/getSpostamenti', {
                params: {
                    idAutoPropriaOrdineMissione: idAutoPropriaOrdineMissione
                }
            }).then(function(response) {
                return response.data;
            });
            return promise;
        }
    }
});

missioniApp.controller('AutoPropriaOrdineMissioneController', function($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, AutoProprieService, DatiPatenteServiceUser, AutoPropriaOrdineMissioneService, ElencoOrdiniMissioneService, ui, COSTANTI, DateService) {

    $scope.disabledfields = true;


    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiRigaSpostamento = function() {
        $scope.addSpostamentoAutoPropria = true;
        $scope.newSpostamentoAutoPropria = {};
    }

    $scope.changeAuto = function(autoPropria) {
        if (autoPropria) {
            $scope.autoPropriaOrdineMissioneModel.targa = autoPropria.targa;
            $scope.autoPropriaOrdineMissioneModel.cartaCircolazione = autoPropria.cartaCircolazione;
            $scope.autoPropriaOrdineMissioneModel.marca = autoPropria.marca;
            $scope.autoPropriaOrdineMissioneModel.modello = autoPropria.modello;
            $scope.autoPropriaOrdineMissioneModel.polizzaAssicurativa = autoPropria.polizzaAssicurativa;
        }
    }

    $scope.confirmDeleteSpostamenti = function(index) {
        var spostamentoDaEliminare = $scope.spostamentiAutoPropria[index];
        ui.confirmCRUD("Confermi l'eliminazione dello spostamento da  " + spostamentoDaEliminare.percorsoDa + " a " + spostamentoDaEliminare.percorsoA + "?", deleteSpostamenti, index);
    }

    $scope.confirmDelete = function() {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta dell'auto propria per l'ordine di missione numero " + $scope.autoPropriaOrdineMissioneModel.ordineMissione.numero + " del " + $filter('date')($scope.autoPropriaOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA) + "?", deleteAutoPropria);
    }

    $scope.autoPropriaOrdineMissioneModelBackup = {}; // Stato backup

    var saveCurrentState = function() {
        // Salva lo stato corrente prima del cambiamento
        $scope.autoPropriaOrdineMissioneModelBackup = angular.copy($scope.autoPropriaOrdineMissioneModel);
    };

    var restorePreviousState = function() {
        // Ripristina lo stato precedente al click
        $scope.autoPropriaOrdineMissioneModel = angular.copy($scope.autoPropriaOrdineMissioneModelBackup);
    };

    $scope.edit = function() {
        saveCurrentState(); // Salva lo stato corrente prima della modifica
        $scope.editing = true;
        $scope.disabledfields = false;
    }


    $scope.undo = function() {
        restorePreviousState(); // Annulla l'azione
        $scope.editing = false;
        $scope.disabledfields = true;
    };


    $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);

    $scope.editSpostamento = function(spostamento) {
        spostamento.editing = true;
    }

    var undoEditingSpostamento = function(spostamento) {
        delete spostamento.editing;
    }

    $scope.undoSpostamento = function(spostamento) {
        undoEditingSpostamento(spostamento);
    }

    var undoEditing = function() {
        $scope.editing = false;
    }

    $scope.isDisabilitataModificaAuto = function() {
        if ($scope.autoPropriaOrdineMissioneModel && $scope.autoPropriaOrdineMissioneModel.id && !$scope.editing) {
            return true;
        }
        return false;
    }

    var inizializzaDati = function() {
        DatiPatenteServiceUser.get($scope.accountModel.login).$promise.then(function(datiPatente) {
            $scope.autoPropriaOrdineMissioneModel = {
                numeroPatente: datiPatente.numero,
                dataRilascioPatente: datiPatente.dataRilascio,
                dataScadenzaPatente: datiPatente.dataScadenza,
                entePatente: datiPatente.ente
            };

            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data) {
                $scope.autoPropriaOrdineMissioneModel.ordineMissione = data;
            });
        });
    }

    $http.get('api/rest/ordineMissione/autoPropria/get', {
        params: {
            idMissione: $scope.idOrdineMissione
        }
    }).then(function(response) {
        var datiAutoPropriaOrdineMissione = response.data;
        if (datiAutoPropriaOrdineMissione.id === undefined) {
            inizializzaDati();
        } else {
            $scope.autoPropriaOrdineMissioneModel = datiAutoPropriaOrdineMissione;
            $http.get('api/rest/ordineMissione/autoPropria/getSpostamenti', {
                params: {
                    idAutoPropriaOrdineMissione: $scope.autoPropriaOrdineMissioneModel.id
                }
            }).then(function(response) {
                $scope.spostamentiAutoPropria = response.data;
            });
        }
    });

    $scope.save = function() {
        $rootScope.salvataggio = true;
        if ($scope.autoPropriaOrdineMissioneModel.id) {
            $http.put('api/rest/ordineMissione/autoPropria/modify', $scope.autoPropriaOrdineMissioneModel)
                .then(function(response) {
                    $rootScope.salvataggio = false;
                })
                .catch(function(error) {
                    $rootScope.salvataggio = false;
                    handleSaveError(error.data, error.status);
                });
        } else {
            $http.post('api/rest/ordineMissione/autoPropria/create', $scope.autoPropriaOrdineMissioneModel)
                .then(function(response) {
                    $rootScope.salvataggio = false;
                    $scope.autoPropriaOrdineMissioneModel = response.data;
                })
                .catch(function(error) {
                    $rootScope.salvataggio = false;
                    handleSaveError(error.data, error.status);
                });
        }
        $scope.disabledfields = true;
        undoEditing();
    }

    function handleSaveError(errorData, status) {
        if (status === 400) {
            $scope.autoPropriaSelected = null;
            restorePreviousState();
        }
        $scope.disabledfields = false;
    }


    var deleteSpostamenti = function(index) {
        var idSpostamento = $scope.spostamentiAutoPropria[index].id;
        $rootScope.salvataggio = true;
        $http.delete('api/rest/ordineMissione/autoPropria/spostamenti/' + idSpostamento).success(
            function(data) {
                $rootScope.salvataggio = false;
                $scope.spostamentiAutoPropria.splice(index, 1);
            }).error(
            function(data) {
                $rootScope.salvataggio = false;
            }
        );
    }

    var deleteAutoPropria = function() {
        var idAutoPropria = $scope.autoPropriaOrdineMissioneModel.id;
        $rootScope.salvataggio = true;
        $http.delete('api/rest/ordineMissione/autoPropria/' + idAutoPropria)
            .then(function(response) {
                $rootScope.salvataggio = false;
                inizializzaDati();
                $scope.spostamentiAutoPropria = [];
                undoEditing();
                $scope.autoPropriaSelected = null;
            })
            .catch(function(error) {
                $rootScope.salvataggio = false;
            });
    }

    var annullaDatiNuovaRiga = function() {
        delete $scope.addSpostamentoAutoPropria;
        delete $scope.newSpostamentoAutoPropria;
        delete $scope.error;
    }

    $scope.undoAddSpostamentoAutoPropria = function() {
        annullaDatiNuovaRiga();
    }

    $scope.insertSpostamentoAutoPropria = function(newRigaSpostamento) {
        newRigaSpostamento.ordineMissioneAutoPropria = $scope.autoPropriaOrdineMissioneModel;
        $rootScope.salvataggio = true;
        $http.post('api/rest/ordineMissione/autoPropria/createSpostamento', newRigaSpostamento).success(function(data) {
            $rootScope.salvataggio = false;
            if (!$scope.spostamentiAutoPropria) {
                $scope.spostamentiAutoPropria = [];
            }
            $scope.spostamentiAutoPropria.push(data);
            $scope.undoAddSpostamentoAutoPropria();
        }).error(function(data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.modifySpostamento = function(spostamentoAutoPropria) {
        $rootScope.salvataggio = true;
        $http.put('api/rest/ordineMissione/autoPropria/modifySpostamento', spostamentoAutoPropria).success(function(data) {
            $rootScope.salvataggio = false;
            undoEditingSpostamento(spostamentoAutoPropria);
        }).error(function(data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.ricerca = function() {
        AutoPropriaOrdineMissioneService.findMissioni($scope.cdsRich, $scope.daNumero, $scope.aNumero).then(function(data) {
            $scope.ordiniMissione = data;
        });
    }

    $scope.previousPage = function() {
        parent.history.back();
    }
});