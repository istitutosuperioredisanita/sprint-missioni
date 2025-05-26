'use strict';

missioniApp.factory('OrdineMissioneDettagliService', function($http) {
    return {
        // Recupera i dettagli di spesa per un ordine missione.
        findDettagli: function(idOrdineMissione) {
            var promise = $http.get('api/rest/ordineMissione/dettagli/get', {
                params: {
                    idOrdineMissione: idOrdineMissione
                }
            }).then(function(response) {
                if (response.data) {
                    return angular.copy(response.data);
                }
                return response.data;
            });
            return promise;
        }
    }
});


missioniApp.controller('OrdineMissioneDettagliController', function($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, OrdineMissioneDettagliService, ProxyService, ElencoOrdiniMissioneService, ui, COSTANTI, DateUtils, DateService) {

    // --- Inizializzazione ---
    $scope.validazione = $routeParams.validazione;
    $scope.inizioMissione = $routeParams.inizioMissione;
    $scope.fineMissione = $routeParams.fineMissione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.backupDettagliSpesa = {};

    // Salva lo stato di un dettaglio per annullare le modifiche.
    $scope.salvaStatoDettaglio = function(dettaglioSpesa) {
        if (dettaglioSpesa.id) {
            $scope.backupDettagliSpesa[dettaglioSpesa.id] = angular.copy(dettaglioSpesa);
        }
    };

    // Ripristina lo stato precedente di un dettaglio.
    $scope.ripristinaStatoDettaglio = function(dettaglioSpesa) {
        if (dettaglioSpesa.id && $scope.backupDettagliSpesa[dettaglioSpesa.id]) {
            angular.copy($scope.backupDettagliSpesa[dettaglioSpesa.id], dettaglioSpesa);
            delete $scope.backupDettagliSpesa[dettaglioSpesa.id];
        }
    };


    // Prepara l'interfaccia per aggiungere un nuovo dettaglio spesa.
    $scope.aggiungiDettaglioSpesa = function() {
        $scope.addDettaglioSpesa = true;
        $scope.newDettaglioSpesa = {};
        inizializzaNuovaRiga();

        recuperoTipiSpesa($scope.newDettaglioSpesa.dataSpesa, null, $scope.newDettaglioSpesa);
    };


    // Imposta i valori iniziali per un nuovo dettaglio spesa.
    var inizializzaNuovaRiga = function() {
        $scope.newDettaglioSpesa.cdDivisa = "EURO";
        $scope.newDettaglioSpesa.cambio = 1;
        $scope.newDettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(new Date());
    };

    // Chiede conferma ed elimina un dettaglio spesa.
    $scope.confirmDeleteDettaglioSpesa = function(index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        ui.confirmCRUD("Confermi l'eliminazione del dettaglio spesa presunta: " + dettaglioSpesaDaEliminare.dsTiSpesa + "?", deleteDettaglioSpesa, index);
    };

    // Esegue l'eliminazione effettiva dal backend.
    var deleteDettaglioSpesa = function(index) {
        var dettaglioSpesaDaEliminare = $scope.dettagliSpese[index];
        $rootScope.salvataggio = true;
        $http.delete('api/rest/ordineMissione/dettagli/' + dettaglioSpesaDaEliminare.id).success(
            function(data) {
                $rootScope.salvataggio = false;
                $scope.dettagliSpese.splice(index, 1);
                ui.success("Dettaglio spesa eliminato con successo.");
            }).error(
            function(data, status) {
                $rootScope.salvataggio = false;
                ui.error("Errore durante l'eliminazione del dettaglio spesa.");
            }
        );
    };


    // --- Gestione Tipi Spesa e Dettagli ---
    // Recupera i tipi di spesa e aggiorna il dettaglio.
    var recuperoTipiSpesa = function(dataSpesa, cdTipoSpesaToSelect, dettaglioToUpdate) {
        $scope.tipi_spesa = [];

        // Verifica che i dati dell'ordine missione siano disponibili.
        if (!$scope.ordineMissione || !$scope.ordineMissione.nazione || !$scope.ordineMissione.trattamento) {
            return;
        }

        ProxyService.getTipiSpesa(undefined, undefined, $scope.ordineMissione.nazione, $scope.ordineMissione.trattamento)
            .then(function(result) {
                if (result && result.data && result.data.elements) {
                    $scope.tipi_spesa = result.data.elements;
                    if (cdTipoSpesaToSelect && dettaglioToUpdate) {
                        $scope.onChangeTipoSpesa(dettaglioToUpdate, cdTipoSpesaToSelect);
                    }
                } else {
                    $scope.tipi_spesa = [];
                }
            }, function(error) {
                ui.error("Errore nel caricamento dei tipi di spesa.");
            });
    };

    // Aggiorna le proprietà di un dettaglio spesa in base al tipo selezionato.
    $scope.onChangeTipoSpesa = function(targetDettaglioSpesa, cdTipoSpesaValue) {
        targetDettaglioSpesa = targetDettaglioSpesa || $scope.newDettaglioSpesa;
        var selectedTipoSpesa = null;

        if (!cdTipoSpesaValue && targetDettaglioSpesa && targetDettaglioSpesa.cdTiSpesa) {
            cdTipoSpesaValue = targetDettaglioSpesa.cdTiSpesa;
        }

        if (cdTipoSpesaValue) {
            for (var i = 0; i < $scope.tipi_spesa.length; i++) {
                var tipo_spesa = $scope.tipi_spesa[i];
                if (tipo_spesa.cd_ti_spesa === cdTipoSpesaValue) {
                    selectedTipoSpesa = tipo_spesa;
                    break;
                }
            }
        }

        if (selectedTipoSpesa && targetDettaglioSpesa) {
            // Aggiorna i campi del dettaglio.
            targetDettaglioSpesa.dsTiSpesa = selectedTipoSpesa.ds_ti_spesa;
            targetDettaglioSpesa.giustificativo = selectedTipoSpesa.fl_giustificativo_richiesto ? 'S' : 'N';
            targetDettaglioSpesa.pasto = selectedTipoSpesa.fl_pasto;
            targetDettaglioSpesa.rimborso = selectedTipoSpesa.fl_rimborso_km;
            targetDettaglioSpesa.trasporto = selectedTipoSpesa.fl_trasporto;
            targetDettaglioSpesa.alloggio = selectedTipoSpesa.fl_alloggio;
            targetDettaglioSpesa.ammissibileOrdine = selectedTipoSpesa.fl_ammissibile_con_rimborso;

            // Imposta il tipo di spesa correlato.
            if (targetDettaglioSpesa.pasto) {
                targetDettaglioSpesa.tiCdTiSpesa = "P";
            } else if (targetDettaglioSpesa.rimborso) {
                targetDettaglioSpesa.tiCdTiSpesa = "R";
            } else if (targetDettaglioSpesa.trasporto) {
                targetDettaglioSpesa.tiCdTiSpesa = "T";
            } else if (targetDettaglioSpesa.alloggio) {
                targetDettaglioSpesa.tiCdTiSpesa = "A";
            } else {
                targetDettaglioSpesa.tiCdTiSpesa = "N";
            }
            targetDettaglioSpesa.cdTiSpesa = selectedTipoSpesa.cd_ti_spesa;
        }
    };


    // Abilita la modalità di modifica per un dettaglio esistente.
    $scope.editDettaglioSpesa = function(dettaglioSpesa) {
        $scope.salvaStatoDettaglio(dettaglioSpesa);
        dettaglioSpesa.editing = true;
        recuperoTipiSpesa(dettaglioSpesa.dataSpesa, dettaglioSpesa.cdTiSpesa, dettaglioSpesa);
    };

    // Annulla le modifiche e ripristina lo stato precedente.
    $scope.undoDettaglioSpesa = function(dettaglioSpesa) {
        $scope.ripristinaStatoDettaglio(dettaglioSpesa);
        dettaglioSpesa.editing = false;
    };


    // Cancella i dati e lo stato di aggiunta di una nuova riga.
    var annullaDatiNuovaRiga = function() {
        delete $scope.addDettaglioSpesa;
        delete $scope.newDettaglioSpesa;
    };

    // Annulla l'aggiunta di un nuovo dettaglio.
    $scope.undoAddDettaglioSpesa = function() {
        annullaDatiNuovaRiga();
    };

    // --- Operazioni CRUD ---
    // Inserisce un nuovo dettaglio spesa nel backend.
    $scope.insertDettaglioSpesa = function (newDettaglioSpesa) {
        // Collega il dettaglio all'ordine missione tramite ID.
        newDettaglioSpesa.ordineMissione = $scope.ordineMissione;
        $rootScope.salvataggio = true;

        $http.post('api/rest/ordineMissione/dettagli/create', newDettaglioSpesa).success(function (data) {
            $rootScope.salvataggio = false;
            if (!$scope.dettagliSpese) {
                $scope.dettagliSpese = [];
            }
            if (data) {
                var dettaglio = angular.copy(data);
                dettaglio.dataSpesa = DateUtils.convertLocalDateFromServer(data.dataSpesa);
                dettaglio.importo = Math.round(dettaglio.importoEuro * 100) / 100;
                $scope.dettagliSpese.push(dettaglio);
            }
            $scope.undoAddDettaglioSpesa();
            }).error(function (data) {
                $rootScope.salvataggio = false;
                ui.error("Errore durante la creazione del dettaglio spesa.");
            });
    };


    // Modifica un dettaglio spesa esistente nel backend.
    $scope.modifyDettaglioSpesa = function (dettaglioSpesa) {
        $rootScope.salvataggio = true;
        dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateToServer(dettaglioSpesa.dataSpesa);
        dettaglioSpesa.ordineMissione = { id: $scope.ordineMissione.id };
        $http.put('api/rest/ordineMissione/dettagli/modify', dettaglioSpesa)
            .success(function (data) {
                $rootScope.salvataggio = false;
                dettaglioSpesa.dataSpesa = DateUtils.convertLocalDateFromServer(data.dataSpesa);
                dettaglioSpesa.editing = false;
                delete $scope.backupDettagliSpesa[dettaglioSpesa.id];
                ui.success("Dettaglio spesa modificato con successo.");
            })
            .error(function(data) {
                $rootScope.salvataggio = false;
                ui.error("Errore durante la modifica del dettaglio spesa.");
            });
    };


    // Calcola il totale di tutti i dettagli di spesa.
    $scope.getTotaleDettagliSpesa = function() {
        var totale = 0;
        if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
            for (var i = 0; i < $scope.dettagliSpese.length; i++) {
                totale = totale + $scope.dettagliSpese[i].importoEuro;
            }
        }
        if ($scope.newDettaglioSpesa && $scope.newDettaglioSpesa.importoEuro) {
            totale = totale + $scope.newDettaglioSpesa.importoEuro;
        }
        return totale;
    };


    // Determina se l'ordine missione è in uno stato che disabilita la modifica.
    var impostadisabilitaOrdineMissione = function() {
        if ($scope.ordineMissione && ($scope.ordineMissione.stato === 'DEF' || $scope.ordineMissione.statoFlusso === 'APP' || ($scope.ordineMissione.stato === 'CON' &&
                ($scope.ordineMissione.stateFlows === 'ANNULLATO' ||
                    $scope.ordineMissione.stateFlows === 'FIRMATO')))) {
            return true;
        } else {
            return false;
        }
    };


    // Inizializza i dati del controller: recupera l'ordine missione e i suoi dettagli.
    var inizializzaDati = function() {
        ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data) {
            $scope.ordineMissione = data;
            if ($scope.ordineMissione) {
                $scope.disabilita = impostadisabilitaOrdineMissione();
                OrdineMissioneDettagliService.findDettagli($scope.idOrdineMissione).then(function(data) {
                    $scope.dettagliSpese = data;
                    if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0) {
                        $scope.dettagliSpese.forEach(function(detail) {
                            if (detail.dataSpesa) {
                                detail.dataSpesa = DateUtils.convertLocalDateFromServer(detail.dataSpesa);
                            }
                        });
                        $scope.getTotaleDettagliSpesa();
                    }
                });
            }
        });
    };
    inizializzaDati();


    // Gestisce la navigazione alla pagina precedente con controllo validazione.
    $scope.previousPage = function() {
       var totaleSpese = $scope.getTotaleDettagliSpesa();
       var importoPresunto = $scope.ordineMissione ? $scope.ordineMissione.importoPresunto : null;

       var importoPresuntoNumerico = (importoPresunto !== null && importoPresunto !== undefined) ? parseFloat(importoPresunto) : 0;

       if (importoPresuntoNumerico !== 0 && !isNaN(totaleSpese) && !isNaN(importoPresuntoNumerico) && totaleSpese > importoPresuntoNumerico) {
           var totaleSpeseFormattato = $filter('currency')(totaleSpese, '€', 2);
           var importoPresuntoFormattato = $filter('currency')(importoPresuntoNumerico, '€', 2);
           ui.error("Il totale delle spese (" + totaleSpeseFormattato + ") non può superare l'importo presunto (" + importoPresuntoFormattato + ").");
           return;
       }

       parent.history.back();
   };
});
