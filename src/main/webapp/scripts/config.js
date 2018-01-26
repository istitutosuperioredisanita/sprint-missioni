missioniApp.factory('ConfigService', function ($http, ui) {
        return {
            getMessage: function() {
                var promise = $http.get('api/rest/config/message').then(function (response) {
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
     $scope.ribaltaDatiIstituti = function() {
        $http.get('api/rest/datiIstituto/ribalta').success(function(result){
            $scope.successRefresh = true;
        });
     }
 });
