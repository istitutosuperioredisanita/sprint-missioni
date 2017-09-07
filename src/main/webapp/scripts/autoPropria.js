'use strict';

/* Controller */

missioniApp.factory('AutoProprieService', function ($resource) {
    return {
      get: function(user){
        return $resource('api/rest/autoPropria/:ids', {}, {
            'get': { method: 'GET', params: {user:user}, isArray: true}
        }).get();
      }
    }
  });

missioniApp.factory('AutoProprieServiceCud', function ($resource) {
    
        return $resource('api/rest/autoPropria/:ids', {}, {
            'add':  { method: 'POST'},
            'modify':  { method: 'PUT'},
            'delete':  { method: 'DELETE'}
        });
    });

missioniApp.controller('AutoPropriaController', function ($scope, AutoProprieService, AutoProprieServiceCud, ProxyService, $sessionStorage, ui) {
    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date
            return today;
    };

    $scope.userSpecialBuild = function(accountLog, uoForUsersSpecial){
      var anno = $scope.today().getFullYear();
      var elenco = ProxyService.getUos(anno, null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog));
      var res = elenco.then(function(result){
          $scope.uoForUsersSpecial = [];
          if (result && result.data){
              var uos = result.data.elements;
              var ind = -1;
              for (var i=0; i<uos.length; i++) {
                 for (var k=0; k<uoForUsersSpecial.length; k++) {
                    if (uos[i].cd_unita_organizzativa == ProxyService.buildUoSiglaFromUoSiper(uoForUsersSpecial[k].codice_uo)){
                        ind ++;
                        $scope.uoForUsersSpecial[ind] = uos[i];
                     }
                  }
              }
              if ($scope.uoForUsersSpecial.length === 1){
                  $scope.uoWorkForSpecialUser = $scope.uoForUsersSpecial[0];
              }
              return uos;
          } else {
              $scope.accountModel = accountLog;
              return uos;
          }
      });
      return res;
    }

        $scope.success = null;
        $scope.delSuccess = null;
        $scope.error = null;
        var accountLog = $sessionStorage.account;
        var uoForUsersSpecial = accountLog.uoForUsersSpecial;
        if (uoForUsersSpecial){
            $scope.userSpecial = true;
            var res = $scope.userSpecialBuild(accountLog, uoForUsersSpecial);
            var a = res.then(function(result){
              var dat = result;
              if ($scope.accountModel){
                $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);
              }
            });
        } else {
            $scope.accountModel = accountLog;
            $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);
        }

    $scope.saveButtonDisable = function (rigaAutoPropria) {
      return rigaAutoPropria===undefined || 
             rigaAutoPropria.marca === undefined ||
             rigaAutoPropria.modello === undefined ||
             rigaAutoPropria.targa === undefined ||
             rigaAutoPropria.cartaCircolazione === undefined ||
             rigaAutoPropria.polizzaAssicurativa === undefined|| 
             rigaAutoPropria.marca === "" ||
             rigaAutoPropria.modello === "" ||
             rigaAutoPropria.targa === "" ||
             rigaAutoPropria.cartaCircolazione === "" ||
             rigaAutoPropria.polizzaAssicurativa === "";
    }

    $scope.confirmDelete = function (index) {
        $scope.success = null;
        $scope.delSuccess = null;
        var autoPropriaEliminata = $scope.autoProprie[index];
        ui.confirmCRUD("Confermi l'eliminazione dell'auto "+autoPropriaEliminata.marca+" - "+autoPropriaEliminata.modello+"?", deleteAutoPropria, index);
//        deleteAutoPropria(index);
    }

    $scope.edit= function (autoPropria) {
      autoPropria.editing = true;
      $scope.success = null;
      $scope.delSuccess = null;
    }

    var undoEditing = function (autoPropria) {
      delete autoPropria.editing;
      annullaMessaggiErrore();
    }

    $scope.undo = function (autoPropria) {
      undoEditing(autoPropria);
    }

    var annullaMessaggiErrore = function () {
      delete $scope.errorTargaExists;
      delete $scope.error;
    }

    $scope.addAutoPropria = function () {
      $scope.addRigaAutoPropria = true;
      $scope.newAutoPropria = {uid: $scope.accountModel.login};
      $scope.success = null;
      $scope.delSuccess = null;
      annullaMessaggiErrore();
    }

    var annullaDatiNuovaRiga = function () {
      delete $scope.addRigaAutoPropria;
      delete $scope.newAutoPropria;
      annullaMessaggiErrore();
    }

    $scope.undoAddAutoPropria = function () {
        annullaDatiNuovaRiga();
    }

    $scope.insertAutoPropria = function (newRigaAutoPropria) {
      newRigaAutoPropria.uid = $scope.accountModel.login;
            AutoProprieServiceCud.add(newRigaAutoPropria,
                    function (value, responseHeaders) {
                        $scope.autoProprie.push(value);
                        $scope.success = true;
                        annullaDatiNuovaRiga();
                    },
                    function (httpResponse) {
                        if (httpResponse.status === 400 && httpResponse.data === "TARGA_ALREADY_EXISTS") {
                            $scope.error = null;
                            $scope.errorTargaExists = true;
                        } else {
                            $scope.error = true;
                        }
                    }
            );
    }

    $scope.modifyAutoPropria = function (autoPropriaModificata) {
            AutoProprieServiceCud.modify(autoPropriaModificata,
                    function (value, responseHeaders) {
                        $scope.success = true;
                        undoEditing(autoPropriaModificata);
                    },
                    function (httpResponse) {
                        if (httpResponse.status === 400 && httpResponse.data === "TARGA_ALREADY_EXISTS") {
                            $scope.error = null;
                            $scope.errorTargaExists = true;
                        } else {
                            $scope.error = true;
                        }
                    }
            );
    }

    var deleteAutoPropria = function (index) {
            AutoProprieServiceCud.delete({ids:$scope.autoProprie[index].id},
                    function (responseHeaders) {
                        $scope.autoProprie.splice(index,1);
                        $scope.delSuccess = true;
//                        annullaDatiNuovaRiga();
                    },
                    function (httpResponse) {
                        if (httpResponse.status === 400 && httpResponse.data === "TARGA_ALREADY_EXISTS") {
                            $scope.error = null;
                            $scope.errorTargaExists = true;
                        } else {
                            $scope.error = true;
                        }
                    }
            );
    }

    $scope.reloadUserWork = function(uid){
        $scope.success = null;
        $scope.delSuccess = null;
        $scope.error = null;
        if (uid){
            for (var i=0; i<$scope.elencoPersone.length; i++) {
                if (uid == $scope.elencoPersone[i].uid){
                    var data = $scope.elencoPersone[i];
                    var userWork = ProxyService.buildPerson(data);

                    $scope.accountModel = userWork;
                    $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);
                }
            }
        }
    }

    $scope.reloadUoWork = function(uo){
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.success = null;
        $scope.delSuccess = null;
        $scope.error = null;
        $scope.userWork = null;
        if (uo){
            $scope.disableUo = true;
            var persons = ProxyService.getPersons(uo).then(function(result){
                if (result ){
                    $scope.elencoPersone = result;
                    $scope.disableUo = false;
                }
            });
        }
    }
});
