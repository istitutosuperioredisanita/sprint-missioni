'use strict';

/* Direttore uo */

missioniApp.factory('DirettoreUoService', function($http) {
    var recuperoDirettore = function(username){
        var direttore = [];
        return $http.get('api/rest/direttore', {params: {username: username}}).then(function (data) {
            if (data){
                if (data.elements){
                    direttore = data.elements;
                    return direttore;
                } else {
                    direttore = data;
                    return direttore;
                } 
            } else {
                return direttore;
            }
        },
        function (){
            return "";
        }
        );
    }
    return { getDirettore: recuperoDirettore};
});
