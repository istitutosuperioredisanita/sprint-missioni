'use strict';

/* Dati Patente */

missioniApp.factory('DatiPatenteService', function ($resource) {
        return $resource('api/rest/datiPatente', {}, {
        });
    });

missioniApp.factory('DatiPatenteServiceUser', function ($resource) {
    return {
      get: function(user){
        return $resource('api/rest/datiPatente', {}, {
            'get': { method: 'GET', params: {user:user}, isArray: false}
        }).get();
      }
    }
    });

missioniApp.controller('DatiPatenteController', function ($scope, DatiPatenteService, DatiPatenteServiceUser, ProxyService, $sessionStorage, DateService) {

    $scope.userSpecialBuild = function(accountLog, uoForUsersSpecial){
        var lista = DateService.today().then(function(result){
            if (result){
                var anno = result.getFullYear();
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
                            $scope.reloadUoWork($scope.uoWorkForSpecialUser.cd_unita_organizzativa);
                        }
                        return uos;
                    } else {
                        $scope.accountModel = accountLog;
                        return uos;
                    }
                });
                return res;
            }
        });
        return lista;
    }

        var accountLog = $sessionStorage.account;
        var uoForUsersSpecial = accountLog.uoForUsersSpecial;
        if (uoForUsersSpecial){
            $scope.userSpecial = true;
            var res = $scope.userSpecialBuild(accountLog, uoForUsersSpecial);
            var a = res.then(function(result){
              var dat = result;
              if ($scope.accountModel){
                $scope.datiPatenteModel = DatiPatenteServiceUser.get($scope.accountModel.login);
              }
            });
        } else {
            $scope.accountModel = accountLog;
            $scope.datiPatenteModel = DatiPatenteServiceUser.get($scope.accountModel.login);
        }

        $scope.success = null;
        $scope.error = null;

        $scope.save = function () {
            $scope.success = null;
            $scope.error = null;
            $scope.errorNumeroExists = null;
            $scope.datiPatenteModel.uid = $scope.accountModel.login;
            DatiPatenteService.save($scope.datiPatenteModel,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                },
                function (httpResponse) {
                    if (httpResponse.status === 400 && httpResponse.data === "numero patente già esistente") {
                        $scope.errorNumeroExists = "ERROR";
                    } else {
                        $scope.error = "ERROR";
                    }
                });
        };

    $scope.reloadUserWork = function(uid){
        $scope.success = null;
        $scope.delSuccess = null;
        $scope.error = null;
        if (uid){
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.accountModel = result;
                    $scope.autoProprie = AutoProprieService.get($scope.accountModel.login);
                }
            });
        }
    }

    $scope.reloadUserWork = function(uid){
        $scope.success = null;
        $scope.error = null;
        if (uid){
            var person = ProxyService.getPerson(uid).then(function(result){
                if (result){
                    $scope.accountModel = result;
                    var datiPatente = DatiPatenteServiceUser.get($scope.accountModel.login);
                        $scope.datiPatenteModel = datiPatente;
                }
            });
        }
    }

    $scope.reloadUoWork = function(uo){
        $scope.accountModel = null;
        $scope.elencoPersone = [];
        $scope.success = null;
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
