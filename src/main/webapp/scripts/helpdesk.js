'use strict';

missioniApp.controller('HelpdeskController', function ($scope, $rootScope, $location, $routeParams, $http, $filter, AccessToken, ui) {
    $scope.accessToken = AccessToken.get();

    $scope.categorieHelpdesk = [
      {'id': '1',
       'descrizione': 'Problemi informatici Ordine di Missione'
      },
      {'id': '2',
       'descrizione': 'Problemi informatici Rimborso Missione'
      },
      {'id': '3',
       'descrizione': 'Informazioni sulla compilazione delle missioni'
      }
    ];

    var initMapHelpDesk = function() {
        delete $scope.helpdeskModel;
        $('#files').children().remove();
        $('button[name="sendMail"]').unbind( "click" );
        $('button[name="sendMail"]').click(function () {
          var hdDataModel = {};
          hdDataModel.subject = $scope.helpdeskModel.subject;
          hdDataModel.message = $scope.helpdeskModel.message;
          hdDataModel.category = $scope.helpdeskModel.category.id;
          hdDataModel.descCategory = $scope.helpdeskModel.category.descrizione;
          $http.post('api/rest/helpdesk/sendWithoutAttachment', hdDataModel)
            .success(function (data) {
              initMapHelpDesk();
              ui.message("Segnalazione Inviata.");
          });
        });
    }

    initMapHelpDesk();

    $('#fileupload').fileupload({
        url: 'api/rest/helpdesk/sendWithAttachment',
        dataType: 'json',
        maxNumberOfFiles: 1,
        progressInterval: 1000,
        add: function (e, data) {
            $('button[name="sendMail"]').unbind( "click" );
            $('button[name="sendMail"]').click(function () {
                data.formData = new FormData();
                data.formData.append("subject", $scope.helpdeskModel.subject);
                data.formData.append("message", $scope.helpdeskModel.message);
                data.formData.append("category", $scope.helpdeskModel.category.id);
                data.formData.append("desc-category", $scope.helpdeskModel.category.descrizione);
                data.submit();
            });
            $('#files').children().remove();
            $.each(data.files, function (index, file) {
                $('<p/>').text(file.name).appendTo($('#files'));
            });
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
                'width',
                progress + '%'
            );
        },
        fail: function(e, data) {
            if (data.jqXHR.status===200)
              ui.message("Segnalazione inviata.");
            else
              ui.error("Errore nell'invio della segnalazione. Riprovare successivamente.")
            initMapHelpDesk();
            $scope.$apply();
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+$scope.accessToken);
        }   
    })
    .prop('disabled', !$.support.fileInput)
    .parent().addClass($.support.fileInput ? undefined : 'disabled');
 
    $scope.formatResultSearchCategoria = function(item) {
      return item.descrizione;
    }

    $scope.sendMailButtonDisable = function (attestatoBuoniPasto) {
      return $scope.helpdeskModel==undefined || $scope.helpdeskModel.subject===undefined || 
             $scope.helpdeskModel.message===undefined || $scope.helpdeskModel.category===undefined;
    }

    $scope.trustAsHtml = function(html) {
      return $sce.trustAsHtml(html);
    }
  });
