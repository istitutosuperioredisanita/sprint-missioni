        'use strict';

missioniApp.factory('TaxiService', function ($resource) {
        return $resource('api/rest/ordineMissione/taxi/:ids', {}, {
            'confirm':  { method: 'PUT', params:{confirm:true}}
        });
    });

missioniApp.controller('TaxiMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, TaxiService, ElencoOrdiniMissioneService, ui, COSTANTI, AccessToken, DateService) {
    
    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta di taxi per l'ordine di missione numero "+$scope.taxiOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.taxiOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteTaxi);
    }

    var inizializzaDati = function(){
            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
                $scope.taxiOrdineMissioneModel = {ordineMissione:data};
            });
    }

    $http.get('api/rest/ordineMissione/taxi/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
        var datiTaxiOrdineMissione = response.data;
        if (datiTaxiOrdineMissione.id === undefined){
            inizializzaDati();
            $scope.isOrdineMissioneConfermato = false;
        } else {
            $scope.taxiOrdineMissioneModel = datiTaxiOrdineMissione;
            $scope.viewAttachments($scope.taxiOrdineMissioneModel.id);
            if ($scope.taxiOrdineMissioneModel.ordineMissione.stato==='CON'){
                $scope.isOrdineMissioneConfermato = true;
            } else {
                $scope.isOrdineMissioneConfermato = false;
            }
        }
    });

    $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.taxiOrdineMissioneModel.id){
                $http.put('api/rest/ordineMissione/taxi/modify', $scope.taxiOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.viewAttachments($scope.taxiOrdineMissioneModel.id);
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            } else {
                $http.post('api/rest/ordineMissione/taxi/create', $scope.taxiOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.taxiOrdineMissioneModel = data;
                    $scope.taxiOrdineMissioneModel.isFireSearchAttachments = false;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            }
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per rendere definitiva la richiesta di taxi di " + $scope.taxiOrdineMissioneModel.importo+" per l'Ordine di Missione Numero: "+$scope.taxiOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.taxiOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e la richiesta di taxi non sarà più modificabile. Si desidera Continuare?", confirmTaxi);
    }

    var confirmTaxi = function () {
            $rootScope.salvataggio = true;
            TaxiService.confirm($scope.taxiOrdineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteTaxi = function () {
        var idTaxi = $scope.taxiOrdineMissioneModel.id;
            $rootScope.salvataggio = true;
            $http.delete('api/rest/ordineMissione/taxi/' + idTaxi).success(
                    function (data) {
                        $rootScope.salvataggio = false;
                        inizializzaDati();
                    }).error(
                    function (data) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    $scope.previousPage = function () {
      parent.history.back();
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
//        $scope.taxiOrdineMissioneModel.viewAttachment = true;
    }
});
