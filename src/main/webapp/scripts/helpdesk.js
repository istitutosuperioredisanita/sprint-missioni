'use strict';

missioniApp.controller('HelpdeskController', function ($scope, $rootScope, $location, $routeParams, $http, $filter, AccessToken, ui, URL_REST, APP_FOR_REST, OIL_REST) {
    $scope.accessToken = AccessToken.get();

    $scope.restCategorie = function(){
        var urlRestProxy = URL_REST.STANDARD;
        var app = APP_FOR_REST.OIL;
        var url = OIL_REST.CATEGORIE;
        $http.get(urlRestProxy + app+'/'+'?proxyURL='+url).success(function (data) {
            if (data)
                $scope.categorie = data.sottocategorie;
        });
    }        

    $scope.send = function(){
          HelpdeskService.add($scope.helpdeskModel,
                    function (value, responseHeaders) {
                        $rootScope.salvataggio = false;
                    },
                    function (httpResponse) {
                        $rootScope.salvataggio = false;
                    }
            );
    }        

    var initMapHelpDesk = function() {
      $scope.restCategorie();
        delete $scope.helpdeskModel;
        $('#files').children().remove();
        $('button[name="sendMail"]').unbind( "click" );
        $('button[name="sendMail"]').click(function () {
          var hdDataModel = {};
          hdDataModel.titolo = $scope.helpdeskModel.titolo;
          hdDataModel.descrizione = $scope.helpdeskModel.descrizione;
          hdDataModel.nota = $scope.helpdeskModel.nota;
          hdDataModel.categoria = $scope.helpdeskModel.categoria;
          hdDataModel.idSegnalazione = $scope.idHelpdesk;
          for (var k=0; k<$scope.categorie.length; k++) {
            if (hdDataModel.categoria == $scope.categorie[k].id){
              hdDataModel.categoriaDescrizione = $scope.categorie[k].descrizione;
            }
          }
          $http.post('api/rest/helpdesk/sendWithoutAttachment', hdDataModel)
            .success(function (data) {
              initMapHelpDesk();
              ui.message("Segnalazione Inviata.");
          });
        });
    }

    $scope.idHelpdesk = $routeParams.idHelpdesk;

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
                if ($scope.helpdeskModel.titolo){
                  data.formData.append("titolo", $scope.helpdeskModel.titolo);
                }
                if ($scope.helpdeskModel.descrizione){
                  data.formData.append("descrizione", $scope.helpdeskModel.descrizione);
                }
                if ($scope.helpdeskModel.nota){
                  data.formData.append("nota", $scope.helpdeskModel.nota);
                }
                if ($scope.idHelpdesk){
                  data.formData.append("idSegnalazione", $scope.idHelpdesk);
                }
                if ($scope.helpdeskModel.categoria){
                  data.formData.append("categoria", $scope.helpdeskModel.categoria);
                }
                for (var k=0; k<$scope.categorie.length; k++) {
                  if ($scope.helpdeskModel.categoria == $scope.categorie[k].id){
                    data.formData.append("categoriaDescrizione", $scope.categorie[k].descrizione);
                  }
                }
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
 
    $scope.sendMailButtonDisable = function (attestatoBuoniPasto) {
      return (($scope.helpdeskModel==undefined || $scope.helpdeskModel.titolo===undefined || 
             $scope.helpdeskModel.descrizione===undefined || $scope.helpdeskModel.categoria===undefined) && !$scope.idHelpdesk) ||
            (($scope.helpdeskModel==undefined || $scope.helpdeskModel.nota===undefined) && $scope.idHelpdesk);
    }

    $scope.trustAsHtml = function(html) {
      return $sce.trustAsHtml(html);
    }
  });
