        'use strict';

missioniApp.factory('RichiestaAnticipoService', function ($resource) {
        return $resource('api/rest/ordineMissione/anticipo/:ids', {}, {
            'confirm':  { method: 'PUT', params:{confirm:true}}
        });
    });

missioniApp.controller('AnticipoOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, RichiestaAnticipoService, ElencoOrdiniMissioneService, ui, COSTANTI, AccessToken, DateService) {
    
    $scope.validazione = $routeParams.validazione;
    $scope.idOrdineMissione = $routeParams.idOrdineMissione;
    $scope.accessToken = AccessToken.get();
    $scope.accountModel = $sessionStorage.accountWork;

    $scope.confirmDelete = function () {
        ui.confirmCRUD("Confermi l'eliminazione della richiesta di anticipo per l'ordine di missione numero "+$scope.autoNoleggioOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.autoNoleggioOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAnticipo);
    }

    var inizializzaDati = function(){
            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
                $scope.autoNoleggioOrdineMissioneModel = {ordineMissione:data};
            });
    }

    $http.get('api/rest/ordineMissione/anticipo/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
        var datiAnticipoOrdineMissione = response.data;
        if (datiAnticipoOrdineMissione.id === undefined){
            inizializzaDati();
            $scope.isOrdineMissioneConfermato = false;
        } else {
            $scope.autoNoleggioOrdineMissioneModel = datiAnticipoOrdineMissione;
            $scope.viewAttachments($scope.autoNoleggioOrdineMissioneModel.id);
            if ($scope.autoNoleggioOrdineMissioneModel.ordineMissione.stato==='CON'){
                $scope.isOrdineMissioneConfermato = true;
            } else {
                $scope.isOrdineMissioneConfermato = false;
            }
        }
    });

    $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.autoNoleggioOrdineMissioneModel.id){
                $http.put('api/rest/ordineMissione/anticipo/modify', $scope.autoNoleggioOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.viewAttachments($scope.autoNoleggioOrdineMissioneModel.id);
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            } else {
                $http.post('api/rest/ordineMissione/anticipo/create', $scope.autoNoleggioOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.autoNoleggioOrdineMissioneModel = data;
                    $scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments = false;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            }
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per rendere definitiva la richiesta di anticipo di " + $scope.autoNoleggioOrdineMissioneModel.importo+" per l'Ordine di Missione Numero: "+$scope.autoNoleggioOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.autoNoleggioOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e la richiesta di anticipo non sarà più modificabile. Si desidera Continuare?", confirmAnticipo);
    }

    var confirmAnticipo = function () {
            $rootScope.salvataggio = true;
            RichiestaAnticipoService.confirm($scope.autoNoleggioOrdineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteAnticipo = function () {
        var idAnticipo = $scope.autoNoleggioOrdineMissioneModel.id;
            $rootScope.salvataggio = true;
            $http.delete('api/rest/ordineMissione/anticipo/' + idAnticipo).success(
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

    $scope.viewAttachments = function (idAnticipo) {
        if (!$scope.autoNoleggioOrdineMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/ordineMissione/anticipo/viewAttachments/' + idAnticipo).then(function (data) {
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
//        $scope.autoNoleggioOrdineMissioneModel.viewAttachment = true;
    }
});
