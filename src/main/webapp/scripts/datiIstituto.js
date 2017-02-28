'use strict';

/* Controller */

missioniApp.factory('DatiIstitutoService', function ($http) {
    return {
      get: function(cds, anno) {
        var promise = $http.get('app/rest/datiIstituto', {params: {istituto: cds, anno:anno}}).then(function (response) {
          return response.data;
        });
        return promise;
      },
    }
});

missioniApp.factory('DatiIstitutoServiceCud', function ($resource) {
    
        return $resource('app/rest/autoPropria/:ids', {}, {
            'add':  { method: 'POST'},
            'modify':  { method: 'PUT'},
            'delete':  { method: 'DELETE'}
        });
    });

