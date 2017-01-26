'use strict';

/* Controller */

missioniApp.factory('DatiIstitutoService', function ($resource) {
    return {
      get: function(cds, anno){
        return $resource('app/rest/datiIstituto/:ids', {}, {
            'get': { method: 'GET', params: {istituto:cds, anno:anno}, isArray: false}
        }).get();
      }
    }
  });

missioniApp.factory('DatiIstitutoServiceCud', function ($resource) {
    
        return $resource('app/rest/autoPropria/:ids', {}, {
            'add':  { method: 'POST'},
            'modify':  { method: 'PUT'},
            'delete':  { method: 'DELETE'}
        });
    });

