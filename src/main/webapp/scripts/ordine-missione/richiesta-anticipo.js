'use strict';

missioniApp.factory('RichiestaAnticipoService', function ($resource) {
        return $resource('app/rest/ordineMissione/anticipo/:ids', {}, {
            'confirm':  { method: 'PUT', params:{confirm:true}}
        });
    });

missioniApp.controller('AnticipoOrdineMissioneController', function ($scope, $rootScope, $location, $routeParams, $sessionStorage, $http, $filter, RichiestaAnticipoService, ElencoOrdiniMissioneService, ui, COSTANTI, AccessToken) {
    
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

    $http.get('app/rest/ordineMissione/anticipo/get', {params: {idMissione: $scope.idOrdineMissione}}).then(function (response) {
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

    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date
            $scope.ordineMissioneModel.dataInserimento = $filter('date')(today, "dd-MM-yyyy");
    };

    $scope.save = function () {
            $rootScope.salvataggio = true;
            if ($scope.anticipoOrdineMissioneModel.id){
                $http.put('app/rest/ordineMissione/anticipo/modify', $scope.anticipoOrdineMissioneModel).success(function(data){
                    $rootScope.salvataggio = false;
                    $scope.viewAttachments($scope.anticipoOrdineMissioneModel.id);
                }).error(function (data) {
                    $rootScope.salvataggio = false;
                });
            } else {
                $http.post('app/rest/ordineMissione/anticipo/create', $scope.anticipoOrdineMissioneModel).success(function(data){
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
            $http.delete('app/rest/ordineMissione/anticipo/' + idAnticipo).success(
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

    $scope.confirmDeleteAttachment = function (attachment) {
        ui.confirmCRUD("Confermi l'eliminazione del file "+attachment.nomeFile+"?", deleteAttachment, attachment);
    }

    var deleteAttachment = function (attachment) {
        $rootScope.salvataggio = true;
        var x = $http.get('app/rest/deleteAttachment/' + attachment.id);
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
            $http.get('app/rest/ordineMissione/anticipo/viewAttachments/' + idAnticipo).then(function (data) {
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

/*
    $scope.deselect = function () {
        delete $scope.anticipoOrdineMissioneModel.viewAttachment;
    }

    $('#fileupload')
      .fileupload(
        { url: 'app/rest/ordineMissione/anticipo/uploadAllegati/'+anticipoOrdineMissioneModel.id,
          dataType: 'json',
          progressInterval: 1000,
          add: function (e, data) {
            if($(".attachment_box").length > 0) { 
                alert("E' permesso il caricamento di un solo file alla volta")
                return;
            }
            $scope.messageTitle = 'CARICAMENTO IN CORSO ('+data.files[0].name+')';
            $scope.messages = null;
            $scope.loading = true;
            $scope.$apply();
            data.submit();
          },
          progressall: function (e, data) {
            $('#progress .progress-bar > p').remove();
            $('<p/>').text('Uploading...').appendTo($('#progress .progress-bar'));
            $('#progress .progress-bar').css(
                'width',
                75 + '%'
            );
          },
          fail: function(e, data) {
            $scope.loading = false;
            if (data.jqXHR.status===200) {
              $.each(data.files, function (index, file) {
                $scope.messageTitle = 'CARICAMENTO EFFETTUATO ('+data.files[0].name+')';
                $scope.messages =['Il file ('+file.name+') è stato caricato correttamente.'];
                $('<p style="color:green"/>').text(file.name).appendTo('#files');
              });
            } else {
              $scope.messages = data.jqXHR.responseText;
              $.each(data.files, function (index, file) {
                $scope.messageTitle = 'ERRORI ('+file.name+')';
                $('<p style="color:red"/>').text(file.name).appendTo('#files');
              });
            }
            $('#progress .progress-bar > p').remove();
            $('<p/>').text('Loaded').appendTo($('#progress .progress-bar'));
            $('#progress .progress-bar').css(
              'width', 100 + '%'
            );
            $scope.$apply();
          },
          beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+$scope.accessToken);
          }   
        })
      .prop('disabled', !$.support.fileInput)
      .parent().addClass($.support.fileInput ? undefined : 'disabled');
        



        controller: ['$rootScope', '$scope', '$element', 'fileUpload', 'ui', function (
            $rootScope, $scope, $element, fileUpload, ui) {
          $scope.$on('fileuploaddone', function (e, data) {
            $rootScope.salvataggio = false;
            if (data && data.result && data.result.idMissione){
                if ($scope.oggetto){
                  if ($scope.oggetto.id === data.result.idMissione){
                    var attachments = $scope.oggetto.attachments;
                    if (!attachments){
                      attachments = [];
                    }
                    $scope.oggetto.attachmentsExists = true;
                    $scope.oggetto.attachments = attachments;
                    $scope.oggetto.attachments.push(data.result);
                  }
                } else {
                  if ($scope.dettagliSpese && $scope.dettagliSpese.length > 0){
                      for (var i=0; i<$scope.dettagliSpese.length; i++) {
                          var dettaglio = $scope.dettagliSpese[i];
                          if (dettaglio.id === data.result.idMissione){
                              var attachments = dettaglio.attachments;
                              if (!attachments){
                                  attachments = [];
                              }
                              $scope.dettagliSpese[i].attachmentsExists = true;
                              $scope.dettagliSpese[i].attachments = attachments;
                              $scope.dettagliSpese[i].attachments.push(data.result);
                          }
                      }
                  }
                }
            }
          });
          $scope.$on('fileuploadsend', function (xhr) {
            $rootScope.salvataggio = true;
          });
          $scope.$on('fileuploadfail', function (e, data) {
            $rootScope.salvataggio = false;
            ui.error("Errore nel caricamento del file. "+ data.jqXHR.responseText);
          });

          $scope.options = {
            url: $scope.url,
            dropZone: $element,
            maxFileSize: $scope.sizeLimit,
            autoUpload: $scope.autoUpload,
            maxNumberOfFiles: 1,
            dataType: 'json'
          };
        }]*/
});
