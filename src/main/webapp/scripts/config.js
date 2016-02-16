 missioniApp.controller('ConfigController', function ($scope, $http) {
     $scope.successRefresh = false;

     $scope.refresh = function() {
        $http.get('app/rest/config/refresh').success(function(result){
            $scope.successRefresh = true;
        });
     }
 });
