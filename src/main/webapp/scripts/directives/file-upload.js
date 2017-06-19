'use strict';

angular.module('missioniApp')
  .directive('ngUploadForm', [function () {
      return {
        restrict: 'E',
        templateUrl: 'views/file-upload.html',
        scope: {
          allowed: '@',
          url: '@',
          autoUpload: '@',
          sizeLimit: '@',
          ngModel: '=',
          name: '@',
          oggetto: '=',
          dettagliSpese: '=',
          disabilita: '=',
          validazione: '=',
          isFinishRestAttachments: '='
        },
        controller: ['$rootScope', '$scope', '$element', 'fileUpload', 'COSTANTI', 'ui', function (
            $rootScope, $scope, $element, fileUpload, COSTANTI, ui) {
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
            if (data.jqXHR.responseText){
              ui.error("Errore nel caricamento del file. "+ data.jqXHR.responseText);
            } else {
              if (data.total > COSTANTI.DEFAULT_MAX_FILE_SIZE){
                ui.error("Errore nel caricamento del file. Il file ha dimensioni di "+ data.total+" ed è più grande del limite previsto "+COSTANTI.DEFAULT_MAX_FILE_SIZE);
              } else {
                ui.error("Errore generico nel caricamento del file.");
              }
            }
          });

          $scope.options = {
            url: $scope.url,
            dropZone: $element,
            maxFileSize: $scope.sizeLimit,
            autoUpload: $scope.autoUpload,
            maxNumberOfFiles: 1,
            dataType: 'json'
          };
        }]
      };
    }]);
