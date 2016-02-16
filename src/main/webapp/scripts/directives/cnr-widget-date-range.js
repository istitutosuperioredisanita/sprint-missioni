'use strict';

angular.module('missioniApp')
  .directive('cnrWidgetDateRange', function () {
    function eventChange (scope, start, end){
      start.on('changeDate', function (event) {
        var newDate = event.date;
        scope.startDate = newDate;

        end.datepicker("setStartDate",newDate);

        scope.$apply();
      });

      end.on('changeDate', function (event) {
        var newDate = event.date;
        scope.endDate = newDate;

        start.datepicker("setEndDate",newDate);

        scope.$apply();
      });
    }

    return {
      restrict: 'AE',
      scope: {
        startDate: '=',
        endDate: '=',
        idMissione: '='
      },
      templateUrl: 'views/datepicker-range.html',
      link: function(scope, element, attrs) {
        scope.startLabel = attrs.startLabel;
        scope.endLabel = attrs.endLabel;
        if (scope.idMissione){
          var init = true;
          scope.$watch('startDate', function (startValue) {
            if (startValue && init) {

              init = false;

              var inputs = $(element).find('input');
              var start = $(inputs[0]);
              var end = $(inputs[1]);
              scope.startDate = new Date(startValue);
              scope.endDate = new Date(scope.endDate);

              start.datepicker({
                todayBtn: "linked",
                todayHighlight: true,
                defaultDate: scope.startDate,
                startDate: scope.endDate,
                format: "dd/mm/yyyy",
                weekStart: 1,
                language:"it"
              });

              end.datepicker({
                todayBtn: "linked",
                todayHighlight: true,
                defaultDate: scope.startDate,
                startDate: scope.endDate,
                format: "dd/mm/yyyy",
                weekStart: 1,
                language:"it"
              });
              eventChange(scope, startTime, endTime);
            }
          });
        } else {
          var inputs = $(element).find('input');
          var start = $(inputs[0]);
          var end = $(inputs[1]);
          start.datepicker({
                todayBtn: "linked",
                todayHighlight: true,
                format: "dd/mm/yyyy",
                weekStart: 1,
                language:"it"
          });

          end.datepicker({
                todayBtn: "linked",
                todayHighlight: true,
                format: "dd/mm/yyyy",
                weekStart: 1,
                language:"it"
          });

          eventChange(scope, start, end);
        }
      }
    };
  });
