'use strict';

missioniApp.factory('AutoNoleggioOrdineMissioneService', function ($http) {
        return {
            findAutoNoleggio: function(idMissione) {
                var promise = $http.get('api/rest/ordineMissione/autoNoleggio/get', {params: {idMissione: idMissione}}).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findSpostamenti: function(idAutoNoleggio) {
                var promise = $http.get('api/rest/ordineMissione/autoNoleggio/getSpostamenti', {params: {idAutoNoleggio: idAutoNoleggio}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });


missioniApp.controller('AutoNoleggioOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, AccessToken, AutoNoleggioOrdineMissioneService, ElencoOrdiniMissioneService, ui, COSTANTI, DateService) {

    $scope.disabledfields = true;

    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.aggiungiRigaSpostamento = function () {
      $scope.addSpostamentoAutoNoleggio = true;
      $scope.newSpostamentoAutoNoleggio = {};
    }

    $scope.confirmDeleteSpostamenti = function (index) {
        var spostamentoDaEliminare = $scope.spostamentiAutoNoleggio[index];
        ui.confirmCRUD("Confermi l'eliminazione dello spostamento da  "+spostamentoDaEliminare.percorsoDa+" a "+spostamentoDaEliminare.percorsoA+"?", deleteSpostamenti, index);
    }

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta del autoNoleggio per l'ordine di missione numero "+$scope.autoNoleggioOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.autoNoleggioOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAutoNoleggio);
    }


    // Nel tuo controller AngularJS
    $scope.autoNoleggioOrdineMissioneModelBackup = {}; // Stato backup

    var saveCurrentState = function () {
      // Salva lo stato corrente prima del cambiamento
      $scope.autoNoleggioOrdineMissioneModelBackup = angular.copy($scope.autoNoleggioOrdineMissioneModel);
    };

    var restorePreviousState = function () {
      // Ripristina lo stato precedente al click
      $scope.autoNoleggioOrdineMissioneModel = angular.copy($scope.autoNoleggioOrdineMissioneModelBackup);
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



        var deleteAutoNoleggio = function () {
            var idAutoNoleggio = $scope.autoNoleggioOrdineMissioneModel.id;
                $rootScope.salvataggio = true;
                $http.delete('api/rest/ordineMissione/autoNoleggio/' + idAutoNoleggio).success(
                        function (data) {
                            $rootScope.salvataggio = false;
                            inizializzaDati();
                            $scope.spostamentiAutoNoleggio = [];
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
        if ($scope.autoNoleggioOrdineMissioneModel && $scope.autoNoleggioOrdineMissioneModel.id && !$scope.editing){
            return true;
        }
        return false;
    }

    var inizializzaDati = function(){
            $scope.autoNoleggioOrdineMissioneModel = {};
            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
            $scope.autoNoleggioOrdineMissioneModel.ordineMissione = data;
            });
        };


$http.get('api/rest/ordineMissione/autoNoleggio/get', { params: { idMissione: $scope.idOrdineMissione } }).then(function (response) {
    var datiAutoNoleggioOrdineMissione = response.data;
    if (datiAutoNoleggioOrdineMissione.id === undefined) {
        inizializzaDati();
        $scope.isOrdineMissioneConfermato = false;
    } else {
        AutoNoleggioOrdineMissioneService.findSpostamenti(datiAutoNoleggioOrdineMissione.id).then(function (data) {
            $scope.spostamentiAutoNoleggio = data;
        });
         $scope.autoNoleggioOrdineMissioneModel = datiAutoNoleggioOrdineMissione;
         $scope.viewAttachments($scope.autoNoleggioOrdineMissioneModel.id);
         if ($scope.autoNoleggioOrdineMissioneModel.ordineMissione.stato === 'CON') {
             $scope.isOrdineMissioneConfermato = true;
         } else {
             $scope.isOrdineMissioneConfermato = false;
         }
    }
});


$scope.save = function() {
    $rootScope.salvataggio = true;

    if ($scope.autoNoleggioOrdineMissioneModel.id) {
        $http.put('api/rest/ordineMissione/autoNoleggio/modify', $scope.autoNoleggioOrdineMissioneModel)
            .success(function(data) {
                $rootScope.salvataggio = false;
                $scope.viewAttachments($scope.autoNoleggioOrdineMissioneModel.id);
            })
            .error(function(errorData, status) {
                $rootScope.salvataggio = false;
                handleSaveError(errorData, status);
            });
    } else {
        $http.post('api/rest/ordineMissione/autoNoleggio/create', $scope.autoNoleggioOrdineMissioneModel)
            .success(function(data) {
                $rootScope.salvataggio = false;
                $scope.autoNoleggioOrdineMissioneModel = data;
                $scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments = false;
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
        var idSpostamento = $scope.spostamentiAutoNoleggio[index].id;
            $rootScope.salvataggio = true;
            $http.delete('api/rest/ordineMissione/autoNoleggio/spostamenti/' + idSpostamento).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        $scope.spostamentiAutoNoleggio.splice(index,1);
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var annullaDatiNuovaRiga = function () {
      delete $scope.addSpostamentoAutoNoleggio;
      delete $scope.newSpostamentoAutoNoleggio;
      delete $scope.error;
    };

    $scope.undoAddSpostamentoAutoNoleggio = function () {
      annullaDatiNuovaRiga();
      $scope.addSpostamentoAutoNoleggio = false;
    };



    $scope.insertSpostamentoAutoNoleggio = function (newRigaSpostamento) {
        newRigaSpostamento.autoNoleggio = $scope.autoNoleggioOrdineMissioneModel;
            $rootScope.salvataggio = true;
            $http.post('api/rest/ordineMissione/autoNoleggio/createSpostamento', newRigaSpostamento).success(function(data){
                    $rootScope.salvataggio = false;
                    if (!$scope.spostamentiAutoNoleggio){
                        $scope.spostamentiAutoNoleggio = [];
                    }
                    $scope.spostamentiAutoNoleggio.push(data);
                    $scope.undoAddSpostamentoAutoNoleggio();
            }).error(function (data) {
                $rootScope.salvataggio = false;
            });
    }



    $scope.modifySpostamento = function (spostamentoAutoNoleggio) {
        $rootScope.salvataggio = true;
        $http.put('api/rest/ordineMissione/autoNoleggio/modifySpostamento', spostamentoAutoNoleggio).success(function(data){
            $rootScope.salvataggio = false;
            undoEditingSpostamento(spostamentoAutoNoleggio);
        }).error(function (data) {
            $rootScope.salvataggio = false;
        });
    }

    $scope.ricerca = function () {
        AutoNoleggioService.findMissioni($scope.cdsRich, $scope.daNumero, $scope.aNumero).then(function(data){
            $scope.ordiniMissione = data;
        });
    }

    $scope.previousPage = function () {
      parent.history.back();
    }




    $scope.viewAttachments = function (idAutoNoleggio) {
        if (!$scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/ordineMissione/autoNoleggio/viewAttachments/' + idAutoNoleggio).then(function (data) {
                $scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.autoNoleggioOrdineMissioneModel.attachments = attachments;
            }, function () {
                $scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments = false;
                $scope.autoNoleggioOrdineMissioneModel.attachmentsExists = false;
                $scope.autoNoleggioOrdineMissioneModel.attachments = {};
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
                var attachments = $scope.autoNoleggioOrdineMissioneModel.attachments;
                if (attachments && Object.keys(attachments).length > 0){
                    var newAttachments = attachments.filter(function(el){
                        return el.id !== attachment.id;
                    });
                    $scope.autoNoleggioOrdineMissioneModel.attachments = newAttachments;
                    if (Object.keys(newAttachments).length = 0){
                        $scope.autoNoleggioOrdineMissioneModel.attachmentsExists = false;
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
