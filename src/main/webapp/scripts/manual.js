'use strict';

missioniApp.factory('ManualService', function ($http, $resource) {

    return {
            get: function() {
                var promise = $http.get('api/rest/manual').then(function (response) {
                    return response.data;
                });
                return promise;
            }
    }
});

missioniApp.controller('ManualController', function ($scope, AccessToken, ManualService) {
    $scope.accessToken = AccessToken.get();
    ManualService.get().then(function(data){
    $scope.manuals = data;
        });        
});
