'use strict';

angular.module('missioniApp')
  .directive('cnrWidgetDatetimeRange', function () {
    function eventChange (scope, startTime, endTime){
      startTime.on('dp.change', function (event) {
        var newDate = event.date._d;
        scope.startDatetime = newDate;

        endTime.data('DateTimePicker').minDate(newDate);

        scope.$apply();
        if (scope.fnChange!==undefined)
          scope.fnChange();

        scope.$apply();
        }
      );

      endTime.on('dp.change', function (event) {
        var newDate = event.date._d;
        scope.endDatetime = newDate;
        startTime.data('DateTimePicker').maxDate(newDate);

        scope.$apply();
        if (scope.fnChange!==undefined)
          scope.fnChange();

        scope.$apply();
        }
      );
    };

    return {
      restrict: 'AE',
      scope: {
        startDatetime: '=',
        endDatetime: '=',
        fnChange: '=',
        idMissione: '=',
        isInModifica: '=',
        isAttiva: '=',
        disabilitato: '='
      },
      templateUrl: 'views/datetimepicker-range.html',
      link: function(scope, element, attrs) {
        scope.startLabel = attrs.startLabel;
        scope.endLabel = attrs.endLabel;
        if (scope.idMissione && (!scope.isInModifica || (scope.isInModifica && scope.isAttiva && !scope.startDatetime))){
          var init = true;
          scope.$watch('startDatetime', function (startValue) {
            if (startValue && init) {

              init = false;

              var inputs = $(element).find('input');
              var startTime = $(inputs[0]);
              var endTime = $(inputs[1]);
              scope.startDatetime = new Date(startValue);
              scope.endDatetime = new Date(scope.endDatetime);

              startTime.datetimepicker({
                widgetPositioning: {
                  horizontal: 'right',
                  vertical: 'auto'
                },
                useCurrent: false,
                defaultDate: scope.startDatetime,
                sideBySide: true,
                maxDate: scope.endDatetime,
                locale:'it'
              });

              endTime.datetimepicker({
                widgetPositioning: {
                  horizontal: 'right',
                  vertical: 'auto'
                },
                useCurrent: false,
                defaultDate: scope.endDatetime,
                sideBySide: true,
                minDate: scope.startDatetime,
                locale:'it'
              });
              eventChange(scope, startTime, endTime);
            }
          });
        } else {
          var inputs = $(element).find('input');
          var startTime = $(inputs[0]);
          var endTime = $(inputs[1]);
          startTime.datetimepicker({
                widgetPositioning: {
                  horizontal: 'right',
                  vertical: 'auto'
                },
                useCurrent: false,
                sideBySide: true,
                locale:'it'
          });

          endTime.datetimepicker({
                widgetPositioning: {
                  horizontal: 'right',
                  vertical: 'auto'
                },
                useCurrent: false,
                sideBySide: true,
                locale:'it'
          });

          eventChange(scope, startTime, endTime);
        }
      }
    };
  });