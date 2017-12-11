 missioniApp.controller('ConfigController', function ($scope, $http) {
     $scope.successRefresh = false;

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
