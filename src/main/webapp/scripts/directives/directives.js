'use strict';

angular.module('missioniApp')
    .directive('activeMenu', function($translate, $locale, tmhDynamicLocale) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs, controller) {
                var language = attrs.activeMenu;

                scope.$watch(function() {
                    return $translate.use();
                }, function(selectedLanguage) {
                    if (language === selectedLanguage) {
                        tmhDynamicLocale.set(language);
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                });
            }
        };
    })
    .directive('onlyNumbers', function () {
      return  {
        require: 'ngModel',
        link: function (scope, element, attr, ngModelCtrl) {
            function fromUser(text) {
                if (text) {
                    var transformedInput = text.replace(/[^0-9]/g, '');

                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                return undefined;
            }            
            ngModelCtrl.$parsers.push(fromUser);
        }
     };
    })
    .directive('activeLink', function(location) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs, controller) {
                var clazz = attrs.activeLink;
                var path = attrs.href;
                path = path.substring(1); //hack because path does bot return including hashbang
                scope.location = location;
                scope.$watch('location.path()', function(newPath) {
                    if (path === newPath) {
                        element.addClass(clazz);
                    } else {
                        element.removeClass(clazz);
                    }
                });
            }
        };
    }).directive('passwordStrengthBar', function() {
        return {
            replace: true,
            restrict: 'E',
            template: '<div id="strength">' +
                      '<small translate="global.messages.validate.newpassword.strength">Password strength:</small>' +
                      '<ul id="strengthBar">' +
                        '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
                      '</ul>' +
                    '</div>',
            link: function(scope, iElement, attr) {
                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (p) {

                        var _force = 0;
                        var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                        var _lowerLetters = /[a-z]+/.test(p);
                        var _upperLetters = /[A-Z]+/.test(p);
                        var _numbers = /[0-9]+/.test(p);
                        var _symbols = _regex.test(p);

                        var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                        var _passedMatches = $.grep(_flags, function (el) { return el === true; }).length;

                        _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                        _force += _passedMatches * 10;

                        // penality (short password)
                        _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                        // penality (poor variety of characters)
                        _force = (_passedMatches == 1) ? Math.min(_force, 10) : _force;
                        _force = (_passedMatches == 2) ? Math.min(_force, 20) : _force;
                        _force = (_passedMatches == 3) ? Math.min(_force, 40) : _force;

                        return _force;

                    },
                    getColor: function (s) {

                        var idx = 0;
                        if (s <= 10) { idx = 0; }
                        else if (s <= 20) { idx = 1; }
                        else if (s <= 30) { idx = 2; }
                        else if (s <= 40) { idx = 3; }
                        else { idx = 4; }

                        return { idx: idx + 1, col: this.colors[idx] };
                    }
                };
                scope.$watch(attr.passwordToCheck, function(password) {
                    if (password) {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.removeClass('ng-hide');
                        iElement.find('ul').children('li')
                            .css({ "background": "#DDD" })
                            .slice(0, c.idx)
                            .css({ "background": c.col });
                    }
                });
            }
        }
//    }).directive('cnrWidgetDatepicker', function () {
//        return {
//            restrict: 'AE',
//            template: '<input type="text" class="form-control" />',
//            link: function link(scope, element) {
//                element.children('input').datepicker({
//                    language: 'it'
//                }).on('changeDate', function (el) {
//                    var d = el.date.toISOString();
//                    scope.$parent.item['ng-value'] = d;
//                });
//            }
//        };
    }).directive('showValidation', function() {
        return {
            restrict: "A",
            require:'form',
            link: function(scope, element, attrs, formCtrl) {
                element.find('.form-group').each(function() {
                    var $formGroup=$(this);
                    var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                    if ($inputs.length > 0) {
                        $inputs.each(function() {
                            var $input=$(this);
                            scope.$watch(function() {
                                return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                            }, function(isInvalid) {
                                $formGroup.toggleClass('has-error', isInvalid);
                            });
                        });
                    }
                });
            }
        };
    }).filter('datetimeMissioni', function () {
        return function (input) {
            if (input) {
              input = moment(input).format("DD/MM/YYYY HH:mm");
            }
            return input;
        };
    }).filter('dateMissioni', function () {
        return function (input) {
            if (input) {
              input = moment(input).format("DD/MM/YYYY");
            }
            return input;
        };
  }).filter('myNumberFilter', function(){

    return function(number) {
      return number;
//      var language = siteLanguage.getLanguage();
//      switch(language) {
//          case "de":
          // return the variant of number for "de" here
 //         break;
 //         case "fr":
          // return the variant of number for "fr" here
 //         break;
 //         default:
          // return english variant here.
 //         break;
        };
}).directive('inputFormatDate', function () {
    return {
      require: 'ngModel',
      link: function link(scope, element, attrs, ngModelController, $filter) {
        ngModelController.$parsers.push(function(data) {
          if (data){
            var dataFormatted = $filter('date')(data, "YYYY-MM-DD");
            return dataFormatted;
          }
          return data;
        });

        ngModelController.$formatters.push(function(data) {
          if (data){
            var dataFormatted = $filter('date')(data, "YYYY-MM-DD");
            return dataFormatted;
          }
          return data;
        });
      }
    }  
  }).directive('cnrWidgetSelect', function () {
    return {
      restrict: 'E',
      scope: {
        options: '=',
        myModel: '=',
        myFormat: '=',
        fnChange: '='
      },
      link: function (scope, element, attrs, $log) {
        var elementoSelect;
        scope.$watch('options', function (model) {
          scope.mappedOptions = _.map(scope.options, function (item) {
            return scope.myFormat(item);
          });
          elementoSelect = element.children('select').css({
          }).selectize({
//            placeholder: attrs.placeholder,
//            selected: "044"
          });

//        elementoSelect.on('change', function (event) {
//            var key = event.val;
//            scope.myModel = key;
//            if (scope.fnChange!==undefined)
//              scope.fnChange(key);
//            scope.$digest();
//          });
        });
        scope.$watch('myModel', function (model) {
          if (model){
          }
        });
      },
      templateUrl: 'views/select2.html'
    };
  }).directive('infoAccount', function () {
        function viewBanner (scope, account){
          scope.accountBanner = [];
          if (account != null){
            if (account.matricola){
              scope.accountBanner.nominativo = account.lastName+" "+account.firstName+"("+account.matricola+")";
            } else {
              scope.accountBanner.nominativo = account.lastName+" "+account.firstName;
            }
            if (account.comune_residenza && account.cap_residenza){
              scope.accountBanner.comuneResidenzaRich = account.comune_residenza+" - "+account.cap_residenza; 
            }
            if (account.comune_residenza){
              scope.accountBanner.comuneResidenzaRich = account.comune_residenza; 
            }
            if (account.indirizzo_completo_residenza){
              scope.accountBanner.indirizzoResidenzaRich = account.indirizzo_completo_residenza; 
            }

            if (account.profilo){
                scope.accountBanner.qualificaRich = account.profilo.trim();
            }
            scope.accountBanner.livelloRich = account.livello;
            scope.accountBanner.codiceFiscale = account.codice_fiscale; 
            scope.accountBanner.dataNascita = account.data_nascita; 
            scope.accountBanner.strutturaAppartenenza = account.struttura_appartenenza; 
            scope.accountBanner.luogoNascita = account.comune_nascita;

            //alert(JSON.stringify(account));

          }
        } 
    return {
      restrict: 'AE',
      scope: {
        account: '='
      },
      templateUrl: 'views/banner-account.html',
      link: function(scope) {
        viewBanner(scope, scope.account);
        scope.$watch('account', function(account){
          viewBanner(scope, account);
        });
      }
    };
  }).directive('barWorking', function () {
        function viewBar (scope, salvataggio){
          scope.salvataggioBar = salvataggio;
        } 
    return {
      restrict: 'AE',
      scope: {
        salvataggio: '='
      },
      templateUrl: 'views/bar-working.html',
      link: function(scope) {
        viewBar(scope, scope.salvataggio);
        scope.$watch('salvataggio', function(salvataggio){
          viewBar(scope, salvataggio);
        });
      }
    };
  }).directive('cnrModalInfoImpegno', function ($log, $http, APP_FOR_REST, SIGLA_REST, URL_REST, ui) {
    return {
      restrict: 'E',
      scope: {
        fondi: '=',
        anno: '=',
        cds: '=',
        uo: '=',
        gae: '=',
        annoImpegno: '=',
        numero: '='
      },
      templateUrl: 'views/cnr-modal-info-impegno.html',
      link: function (scope) {

        scope.validateImpegno = function () {
          $log.debug('sono in debug');
	        if (scope.annoImpegno && scope.numero){
            if (scope.fondi && scope.fondi == 'C' && scope.anno != scope.annoImpegno){
                ui.error("Incongruenza tra fondi e anno impegno");
            } else if (scope.fondi && scope.fondi == 'R' && scope.anno <= scope.annoImpegno){
                ui.error("Incongruenza tra fondi e anno impegno");
	          } else {
              var app = APP_FOR_REST.SIGLA;
              var url = null;
              var varClauses = [];
              var urlRestProxy = URL_REST.STANDARD;
              if (scope.gae){
                  url = SIGLA_REST.IMPEGNO_GAE;
                  varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:scope.anno},
                                {condition: 'AND', fieldName: 'cdUnitaOrganizzativa', operator: "=", fieldValue:scope.uo},
                                {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:scope.cds},
                                {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:scope.annoImpegno},
                                {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:scope.numero},
                                {condition: 'AND', fieldName: 'cdLineaAttivita', operator: "=", fieldValue:scope.gae}];
              } else {
                  url = SIGLA_REST.IMPEGNO;
                  varClauses = [{condition: 'AND', fieldName: 'esercizio', operator: "=", fieldValue:scope.anno},
                                {condition: 'AND', fieldName: 'cdCds', operator: "=", fieldValue:scope.cds},
                                {condition: 'AND', fieldName: 'cdUnitaOrganizzativa', operator: "=", fieldValue:scope.uo},
                                {condition: 'AND', fieldName: 'esercizioOriginale', operator: "=", fieldValue:scope.annoImpegno},
                                {condition: 'AND', fieldName: 'pgObbligazione', operator: "=", fieldValue:scope.numero}];
              }
              var varOrderBy = [{name: 'esercizio', type: 'DESC'}];
              var postImpegno = {activePage:0, maxItemsPerPage:1000, orderBy:varOrderBy, clauses:varClauses}
              $http.post(urlRestProxy + app+'/', postImpegno, {params: {proxyURL: url}}).success(function (data) {
                  if (data){
                      if (data.elements){
                          var impegnoSelected = data.elements[0];
                          if (impegnoSelected){
                      scope.pgObbligazione = impegnoSelected.pgObbligazione;
                      scope.descrizione = impegnoSelected.dsObbligazione;
                      $('#exampleModal').modal('toggle');
                      if (impegnoSelected.esercizio === impegnoSelected.esercizioOriginale){
                        scope.disponibilita = impegnoSelected.imScadenzaComp - impegnoSelected.imAssociatoDocAmmComp;
                      } else {
                        scope.disponibilita = impegnoSelected.imScadenzaRes - impegnoSelected.imAssociatoDocAmmRes;
                      }
                    scope.disponibilita = scope.disponibilita.toString().replace(".",",");
                        } else {
                        ui.error("Impegno non esistente");
                        }
                      } else {
                      ui.error("Impegno non esistente");
                      }
                  }
              }).error(function (data) {
              });
            }
	        } else {
	        }
    
        };

      }
    };
  }).directive('cnrModalHistory', function ($log, $http, MissioniRespinte) {
    return {
      restrict: 'E',
      scope: {
        idMissione: '=',
        tipoMissione: '='
      },
      templateUrl: 'views/cnr-modal-history.html',
      link: function (scope) {
        scope.getHistory = function(){
            if (scope.idMissione){
                MissioniRespinte.findHistoryMissioniRespinte(scope.tipoMissione, scope.idMissione).then(function(data){
                    scope.histories = data;
                    $('#modalHistory').modal('toggle');
                });
            }
        }
      }
    };
  }).directive("numberFormat", [
  '$filter', function(filter) {
    return {
      replace: false,
      restrict: "A",
      require: "?ngModel",
      link: function(scope, element, attrs, ngModel) {
        var numberFormat;
        if (!ngModel) {
          return;
        }
        ngModel.$render = function() {
          return element.val(ngModel.$viewValue);
        };
        var numberFilter = filter('myNumberFilter');
        return ngModel.$formatters.push(function(value) {
          return numberFilter(value);
        });
      }
    };
  }
]);
