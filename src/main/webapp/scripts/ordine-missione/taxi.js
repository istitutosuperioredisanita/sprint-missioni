'use strict';

missioniApp.factory('TaxiOrdineMissioneService', function ($http) {
        return {
            findTaxi: function(idMissione) {
                var promise = $http.get('api/rest/ordineMissione/taxi/get', {params: {idMissione: idMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findSpostamenti: function(idTaxi) {
                var promise = $http.get('api/rest/ordineMissione/taxi/getSpostamenti', {params: {idTaxi: idTaxi}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });


missioniApp.controller('TaxiOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, TaxiOrdineMissioneService, ElencoOrdiniMissioneService, ui, COSTANTI, DateService) {

    $scope.disabledfields = true;

    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiRigaSpostamento = function () {
      $scope.addSpostamentoTaxi = true;
      $scope.newSpostamentoTaxi = {};
    }

    $scope.confirmDeleteSpostamenti = function (index) {
        var spostamentoDaEliminare = $scope.spostamentiTaxi[index];
        ui.confirmCRUD("Confermi l'eliminazione dello spostamento da  "+spostamentoDaEliminare.percorsoDa+" a "+spostamentoDaEliminare.percorsoA+"?", deleteSpostamenti, index);
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta del taxi per l'ordine di missione numero "+$scope.taxiOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.taxiOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteTaxi);
    }


    // Nel tuo controller AngularJS
    $scope.taxiOrdineMissioneModelBackup = {}; // Stato backup

    var saveCurrentState = function () {
      // Salva lo stato corrente prima del cambiamento
      $scope.taxiOrdineMissioneModelBackup = angular.copy($scope.taxiOrdineMissioneModel);
    };

    var restorePreviousState = function () {
      // Ripristina lo stato precedente al click
      $scope.taxiOrdineMissioneModel = angular.copy($scope.taxiOrdineMissioneModelBackup);
    };

 $scope.edit = function () {
     saveCurrentState(); // Salva lo stato corrente prima della modifica
      $scope.editing = true;
      $scope.disabledfields = false;
    }


    $scope.undo = function () {
      restorePreviousState(); // Annulla l'azione
          $scope.editing = false;
            $scope.disabledfields = true;
    };




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



    $scope.editSpostamento= function (spostamento) {
      spostamento.editing = true;
    }

    var undoEditingSpostamento = function (spostamento) {
      delete spostamento.editing;
    }

    $scope.undoSpostamento = function (spostamento) {
      undoEditingSpostamento(spostamento);
    }

    var undoEditing = function () {
      $scope.editing = false;
    }



    $scope.isDisabilitataModificaAuto = function (){
        if ($scope.taxiOrdineMissioneModel && $scope.taxiOrdineMissioneModel.id && !$scope.editing){
            return true;
        }
        return false;
    }

    var inizializzaDati = function(){
            $scope.taxiOrdineMissioneModel = {};
            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
            $scope.taxiOrdineMissioneModel.ordineMissione = data;
            });
        };


$http.get('api/rest/ordineMissione/taxi/get', { params: { idMissione: $scope.idOrdineMissione } }).then(function (response) {
    var datiTaxiOrdineMissione = response.data;
    if (datiTaxiOrdineMissione.id === undefined) {
        inizializzaDati();
        $scope.isOrdineMissioneConfermato = false;
    } else {
        TaxiOrdineMissioneService.findSpostamenti(datiTaxiOrdineMissione.id).then(function (data) {
            $scope.spostamentiTaxi = data;
        });
         $scope.taxiOrdineMissioneModel = datiTaxiOrdineMissione;
         $scope.viewAttachments($scope.taxiOrdineMissioneModel.id);
         if ($scope.taxiOrdineMissioneModel.ordineMissione.stato === 'CON') {
             $scope.isOrdineMissioneConfermato = true;
         } else {
             $scope.isOrdineMissioneConfermato = false;
         }
    }
});


$scope.save = function() {
    $rootScope.salvataggio = true;

    if ($scope.taxiOrdineMissioneModel.id) {
        $http.put('api/rest/ordineMissione/taxi/modify', $scope.taxiOrdineMissioneModel)
            .success(function(data) {
                $rootScope.salvataggio = false;
                $scope.viewAttachments($scope.taxiOrdineMissioneModel.id);
            })
            .error(function(errorData, status) {
                $rootScope.salvataggio = false;
                handleSaveError(errorData, status);
            });
    } else {
        $http.post('api/rest/ordineMissione/taxi/create', $scope.taxiOrdineMissioneModel)
            .success(function(data) {
                $rootScope.salvataggio = false;
                $scope.taxiOrdineMissioneModel = data;
                $scope.taxiOrdineMissioneModel.isFireSearchAttachments = false;
            })
            .error(function(errorData, status) {
                $rootScope.salvataggio = false;
                handleSaveError(errorData, status);
            });
    }
    $scope.disabledfields = true;
    undoEditing();
};

function handleSaveError(errorData, status) {
    if (errorData && errorData.codiceErrore === 'ERRGEN') {
        alert("Errore AwesomeException: " + errorData.messaggioErrore);
    }
    $scope.disabledfields = false;
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

    var annullaDatiNuovaRiga = function () {
      delete $scope.addSpostamentoTaxi;
      delete $scope.newSpostamentoTaxi;
      delete $scope.error;
    };

    $scope.undoAddSpostamentoTaxi = function () {
      annullaDatiNuovaRiga();
      $scope.addSpostamentoTaxi = false;
    };



    $scope.insertSpostamentoTaxi = function (newRigaSpostamento) {
        newRigaSpostamento.taxi = $scope.taxiOrdineMissioneModel;
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
        TaxiService.findMissioni($scope.cdsRich, $scope.daNumero, $scope.aNumero).then(function(data){
            $scope.ordiniMissione = data;
        });
    }

    $scope.previousPage = function () {
      parent.history.back();
    }




    $scope.viewAttachments = function (idTaxi) {
        if (!$scope.taxiOrdineMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/ordineMissione/taxi/viewAttachments/' + idTaxi).then(function (data) {
                $scope.taxiOrdineMissioneModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.taxiOrdineMissioneModel.attachments = attachments;
            }, function () {
                $scope.taxiOrdineMissioneModel.isFireSearchAttachments = false;
                $scope.taxiOrdineMissioneModel.attachmentsExists = false;
                $scope.taxiOrdineMissioneModel.attachments = {};
            });
        }
    }

        $scope.confirmDeleteAttachment = function (attachment, idOrdineMissione) {
            ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
        }
    
    
        var deleteAttachment = function (attachment) {
            $rootScope.salvataggio = true;
            var x = $http.delete('api/rest/ordine/deleteAttachment?id=' + attachment.id+'&idOrdine=' + $routeParams.idOrdineMissione);
            var y = x.then(function (result) {
                var attachments = $scope.taxiOrdineMissioneModel.attachments;
                if (attachments && Object.keys(attachments).length > 0){
                    var newAttachments = attachments.filter(function(el){
                        return el.id !== attachment.id;
                    });
                    $scope.taxiOrdineMissioneModel.attachments = newAttachments;
                    if (Object.keys(newAttachments).length = 0){
                        $scope.taxiOrdineMissioneModel.attachmentsExists = false;
                    }
                }
                $rootScope.salvataggio = false;
                ui.ok();
            });
            x.error(function (data) {
                $rootScope.salvataggio = false;
            });
        }

});
