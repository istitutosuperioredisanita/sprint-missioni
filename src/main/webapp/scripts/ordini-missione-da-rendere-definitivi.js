'use strict';

missioniApp.controller('OrdiniMissioneDaRendereDefinitiviController', function ($scope, $location, $sessionStorage, ElencoOrdiniMissioneService, $filter, ui, ProxyService) {

    $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); // create new date
            return today;
    };

    $scope.ricerca = function () {
        ElencoOrdiniMissioneService.findMissioniDaRendereDefinitive($scope.userWork, $scope.anno, null, $scope.daNumero, $scope.aNumero, $scope.daData, $scope.aData, $scope.uoWorkForSpecialUser).then(function(data){
            if (data && data.length > 0){
                $scope.ordiniMissione = data;
                $scope.messageOrdiniNonEsistenti = false;
            } else {
                $scope.messageOrdiniNonEsistenti = true;
            }
        });        
    }

    $scope.doSelectOrdineMissione = function (ordineMissione) {
        $location.path('/ordine-missione/'+ordineMissione.id+'/D');
    };

    $scope.reloadUoWork = function(uo){
        $scope.ordiniMissione = [];
        $scope.messageOrdiniNonEsistenti = false;
        if (uo){
            $scope.disableUo = true;
            $scope.accountModel = $sessionStorage.account;
        }
    }

    var accountLog = $sessionStorage.account;
    var uoForUsersSpecial = accountLog.uoForUsersSpecial;
    if (uoForUsersSpecial){
        $scope.userSpecial = true;
        var anno = $scope.today().getFullYear();
        var elenco = ProxyService.getUos(anno, null, ProxyService.buildUoRichiedenteSiglaFromUoSiper(accountLog)).then(function(result){
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
                    $scope.accountModel = accountLog;
                    $scope.uoWorkForSpecialUser = $scope.uoForUsersSpecial[0];
                }
            } else {
                $scope.accountModel = accountLog;
            }
        });
    } else {
        $scope.accountModel = accountLog;
    }
});
