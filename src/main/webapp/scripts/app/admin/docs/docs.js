'use strict';

angular.module('missioniApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('docs', {
                parent: '***REMOVED***',
                url: '/docs',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'global.menu.***REMOVED***.apidocs'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/***REMOVED***/docs/docs.html'
                    }
                }
            });
    });
