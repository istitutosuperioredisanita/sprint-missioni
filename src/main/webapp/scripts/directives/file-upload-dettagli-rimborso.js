'use strict';

angular.module('missioniApp')
  .directive('ngUploadForm', [function () {
      return {
        restrict: 'E',
        templateUrl: 'views/file-upload-dettagli-rimborso.html',
        scope: {
          allowed: '@',
          url: '@',
          autoUpload: '@',
          sizeLimit: '@',
          ngModel: '=',
          name: '@',
          dettagliSpese: '=',
          disabilita: '=',
          validazione: '=',
          isFinishRestAttachments: '='
        },
        controller: ['$rootScope', '$scope', '$element', 'fileUpload', 'ui', function (
            $rootScope, $scope, $element, fileUpload, ui) {
          $scope.$on('fileuploaddone', function (e, data) {
            $rootScope.salvataggio = false;
            if (data && data.result && data.result.idMissione){
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
        }]
      };
    }]);
