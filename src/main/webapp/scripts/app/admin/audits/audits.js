'use strict';

angular.module('missioniApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('audits', {
                parent: '***REMOVED***',
                url: '/audits',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'audits.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/***REMOVED***/audits/audits.html',
                        controller: 'AuditsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('audits');
                        return $translate.refresh();
                    }]
                }
            });
    });
