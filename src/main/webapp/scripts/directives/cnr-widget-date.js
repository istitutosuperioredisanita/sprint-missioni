'use strict';

angular.module('missioniApp')
  .directive('cnrWidgetDate', function () {
    function setDatetime (scope, element){
              var data = element.find('input');
              var dataInizio = null;
              var dataFine = null;
              if (scope.startDate){
                dataInizio = new Date(scope.startDate);
              }
              if (scope.endDate){
                dataFine = new Date(scope.endDate);
              }
              data.datepicker({
                language: "it", 
                autoclose: true, 
                todayBtn: "linked",
                todayHighlight: true,
                endDate: dataFine, 
                startDate: dataInizio, 
                format: "dd/mm/yyyy",
                weekStart: 1
              }).on('changeDate', function (event) {
                var newDate = event.date;
                scope.ngModelDate = newDate;
                scope.$apply();
                scope.$emit('cambioData', newDate);
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
        disabilitato: '=',
        labelDate: '=',
        endDate: '=',
        startDate: '='
      },
      templateUrl: 'views/datepicker.html',
      link: function link(scope, element, attrs) {
          scope.label = attrs.label;
          var init = true;
          scope.$watch('ngModelDate', function (startValue) {
            if (startValue){
              if (init){
                init = false;
                scope.ngModelDate = moment(startValue)._d;
                setDatetime(scope, element);
              }
            } else {
                setDatetime(scope, element);
            }
          });
      }
    };
  });
