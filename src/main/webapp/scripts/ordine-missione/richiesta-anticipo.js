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
        ui.confirmCRUD("Confermi l'eliminazione della richiesta di anticipo per l'ordine di missione numero "+$scope.anticipoOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.anticipoOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+"?", deleteAnticipo);
    }

    var inizializzaDati = function(){
            ElencoOrdiniMissioneService.findById($scope.idOrdineMissione).then(function(data){
                $scope.anticipoOrdineMissioneModel = {ordineMissione:data};
            });
    }

    $http.get('api/rest/ordineMissione/anticipo/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
        var datiAnticipoOrdineMissione = response.data;
        if (datiAnticipoOrdineMissione.id === undefined){
            inizializzaDati();
            $scope.isOrdineMissioneConfermato = false;
        } else {
            $scope.anticipoOrdineMissioneModel = datiAnticipoOrdineMissione;
            $scope.viewAttachments($scope.anticipoOrdineMissioneModel.id);
            if ($scope.anticipoOrdineMissioneModel.ordineMissione.stato==='CON'){
                $scope.isOrdineMissioneConfermato = true;
            } else {
                $scope.isOrdineMissioneConfermato = false;
            }
        }
    });

    $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.anticipoOrdineMissioneModel.id){
                $http.put('api/rest/ordineMissione/anticipo/modify', $scope.anticipoOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.viewAttachments($scope.anticipoOrdineMissioneModel.id);
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            } else {
                $http.post('api/rest/ordineMissione/anticipo/create', $scope.anticipoOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.anticipoOrdineMissioneModel = data;
                    $scope.anticipoOrdineMissioneModel.isFireSearchAttachments = false;
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            }
    }

    $scope.confirm = function () {
        ui.confirmCRUD("Si sta per rendere definitiva la richiesta di anticipo di " + $scope.anticipoOrdineMissioneModel.importo+" per l'Ordine di Missione Numero: "+$scope.anticipoOrdineMissioneModel.ordineMissione.numero+" del "+$filter('date')($scope.anticipoOrdineMissioneModel.ordineMissione.dataInserimento, COSTANTI.FORMATO_DATA)+". L'operazione avvierà il processo di autorizzazione e la richiesta di anticipo non sarà più modificabile. Si desidera Continuare?", confirmAnticipo);
    }

    var confirmAnticipo = function () {
            $rootScope.salvataggio = true;
            RichiestaAnticipoService.confirm($scope.anticipoOrdineMissioneModel,
                    function (responseHeaders) {
                        $rootScope.salvataggio = false;
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }

    var deleteAnticipo = function () {
        var idAnticipo = $scope.anticipoOrdineMissioneModel.id;
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
            var attachments = $scope.anticipoOrdineMissioneModel.attachments;
            if (attachments && Object.keys(attachments).length > 0){
                var newAttachments = attachments.filter(function(el){
                    return el.id !== attachment.id;
                });
                $scope.anticipoOrdineMissioneModel.attachments = newAttachments;
                if (Object.keys(newAttachments).length = 0){
                    $scope.anticipoOrdineMissioneModel.attachmentsExists = false;
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
        if (!$scope.anticipoOrdineMissioneModel.isFireSearchAttachments){
            $http.get('api/rest/ordineMissione/anticipo/viewAttachments/' + idAnticipo).then(function (data) {
                $scope.anticipoOrdineMissioneModel.isFireSearchAttachments = true;
                var attachments = data.data;
                if (attachments && Object.keys(attachments).length > 0){
                    $scope.attachmentsExists = true;  
                } else {
                    $scope.attachmentsExists = false;
                }
                $scope.anticipoOrdineMissioneModel.attachments = attachments;
            }, function () {
                $scope.anticipoOrdineMissioneModel.isFireSearchAttachments = false;
                $scope.anticipoOrdineMissioneModel.attachmentsExists = false;
                $scope.anticipoOrdineMissioneModel.attachments = {};
            });
        }
//        $scope.anticipoOrdineMissioneModel.viewAttachment = true;
    }
});
