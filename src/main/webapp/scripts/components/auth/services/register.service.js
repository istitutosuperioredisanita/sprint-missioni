'use strict';

angular.module('missioniApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


