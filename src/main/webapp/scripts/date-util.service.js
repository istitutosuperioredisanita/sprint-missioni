(function() {
    'use strict';

    angular
        .module('missioniApp')
        .factory('DateUtils', DateUtils);

    DateUtils.$inject = ['$filter'];

    function DateUtils ($filter) {

        var service = {
            convertDateTimeFromServer : convertDateTimeFromServer,
            convertLocalDateFromServer : convertLocalDateFromServer,
            convertLocalDateToServer : convertLocalDateToServer,
            convertLocalDateTimeToServer : convertLocalDateTimeToServer,
            dateformat : dateformat
        };

        return service;

        function convertDateTimeFromServer (date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        }

        function convertLocalDateFromServer (date) {
            if (date) {
                var dateString = date.split('/');
                return new Date(dateString[2], dateString[1] - 1, dateString[0]);
            }
            return null;
        }

        function convertLocalDateToServer (date) {
            if (date) {
                return $filter('date')(date, 'dd/MM/yyyy');
            } else {
                return null;
            }
        }

        function convertLocalDateTimeToServer (dateTime) {
            if (dateTime) {
                return $filter('date')(dateTime, 'dd/MM/yyyy HH:mm');
            } else {
                return null;
            }
        }

        function dateformat () {
            return 'dd/MM/yyyy';
        }
    }

})();