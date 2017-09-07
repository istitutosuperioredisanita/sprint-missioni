'use strict';

/* Direttore uo */

missioniApp.factory('DirettoreUoService', function($http) {
    var recuperoDirettore = function(uo){
        var direttore = [];
        return $http.get('api/rest/direttore', {params: {uo: uo}}).success(function (data) {
            if (data){
                if (data.elements){
                    direttore = data.elements;
                    return direttore;
                } else {
                    return direttore;
                }
            } else {
                return direttore;
            }
        }).error(function (data) {
        });
    }
    return { getDirettore: recuperoDirettore};
});
