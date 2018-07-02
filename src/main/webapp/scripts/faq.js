'use strict';


missioniApp.controller('FaqController', function ($scope, AccessToken, ConfigService) {
    ConfigService.getFaq().then(function(data){
        $scope.faq = data;
    });        
    $scope.hoverIn = function(){
        this.hoverEdit = true;
    };

    $scope.hoverOut = function(){
        this.hoverEdit = false;
    };

});
