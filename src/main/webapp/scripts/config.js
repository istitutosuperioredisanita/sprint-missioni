missioniApp.factory('ConfigService', function ($http, ui) {
        return {
            getReleaseNotes: function() {
                var promise = $http.get('api/rest/config/releaseNotes').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            getMessage: function() {
                var promise = $http.get('api/rest/config/message').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            getFaq: function() {
                var promise = $http.get('api/rest/config/faq').then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

 missioniApp.controller('ConfigController', function ($scope, $http) {
     $scope.successRefresh = false;

     $scope.sendMessage = function(message) {
        $http.post('api/rest/config/message/', message).success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.refresh = function() {
        $http.get('api/rest/config/refresh').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.refresh = function() {
        $http.get('api/rest/config/refresh').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.refreshCache = function() {
        $http.get('api/rest/config/refreshCache').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.refreshCacheTerzoCompenso = function() {
        $http.get('api/rest/config/refreshCacheTerzoCompenso').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.ribaltaDatiIstituti = function() {
        $http.get('api/rest/datiIstituto/ribalta').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.reinviaCoda = function() {
        $http.get('api/rest/config/resendQueue').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.populateSignerMissioni = function() {
        $http.get('api/rest/config/populateSignerMissioni').success(function(result){
            $scope.successRefresh = true;
        });
     }
     $scope.aggiornaDatiPersonaleNonDipendente = function() {
        $http.get('api/rest/config/aggiornaPersonaleNonDipendente').success(function(result){
            $scope.successRefresh = true;
        });
     }
 });
