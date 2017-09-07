 missioniApp.controller('ConfigController', function ($scope, $http) {
     $scope.successRefresh = false;

     $scope.refresh = function() {
        $http.get('api/rest/config/refresh').success(function(result){
            $scope.successRefresh = true;
        });
     }
 });
