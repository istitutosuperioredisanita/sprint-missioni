'use strict';

missioniApp.controller('ReleaseController', function ($scope, ConfigService) {
    var recuperoReleaseNotes = function(){
                var x = ConfigService.getReleaseNotes();
                var y = x.then(function (result) {
                    if (result){
                        $scope.releases = [];
                        var release = {content: result};
                        $scope.releases[0] = release;
                    }
                });
    }
    recuperoReleaseNotes();
    
    $scope.previousPage = function () {
      parent.history.back();
    }

});
