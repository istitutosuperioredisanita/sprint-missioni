'use strict';

angular.module('missioniApp')
    .factory('Account', function Account($resource) {
        return $resource('api/ldap-account', {}, {
            'get': { method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        return response;
                    }
                }
            }
        });
    });
