'use strict';

angular.module('missioniApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
