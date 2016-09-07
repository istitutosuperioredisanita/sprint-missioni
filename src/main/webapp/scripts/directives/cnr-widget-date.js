'use strict';

angular.module('missioniApp')
  .directive('cnrWidgetDate', function () {
    function setDatetime (scope, element){
              var data = element.find('input');
              data.datepicker({
                language: "it", 
                autoclose: true, 
                todayBtn: "linked",
                todayHighlight: true,
                endDate: scope.endDate, 
                startDate: scope.startDate, 
                format: "dd/mm/yyyy",
                weekStart: 1
              }).on('changeDate', function (event) {
                var newDate = event.date;
                scope.ngModelDate = newDate;
                scope.$apply();
              }).on('show', function (event) {
                var newDate = event.date;
                scope.ngModelDate = newDate;
                scope.$apply();
              });
              data.datepicker("update",scope.ngModelDate);
    }

    return {
      restrict: 'AE',
      scope: {
        ngModelDate: '=',
        dateName: '=',
        labelDate: '=',
        endDate: '=',
        startDate: '='
      },
      templateUrl: 'views/datepicker.html',
      link: function link(scope, element, attrs) {
          var init = true;
          scope.$watch('ngModelDate', function (startValue) {
            if (startValue){
              if (init){
                init = false;
                scope.ngModelDate = new Date(startValue);
                setDatetime(scope, element);
              }
            } else {
                setDatetime(scope, element);
            }
          });
      }
    };
  });
