'use strict';

/* App Module */
var httpHeaders;

var missioniApp = angular.module('missioniApp', ['http-auth-interceptor', 'tmh.dynamicLocale', 'ng-currency',
    'ngResource', 'ngRoute', 'ngCookies', 'ngStorage', 'missioniAppUtils', 'pascalprecht.translate', 'truncate','selectize', 'ui.select', 'ngSanitize','blueimp.fileupload']);

missioniApp
  .service('ui', function () {
    function modalFn(title, content, callback, objectToCRUD) {

      var modal = $('<div class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="modale" aria-hidden="false" style="display: block;">'),
        modalDialog = $('<div class="modal-dialog modal-sm"></div>'),
        modalContent = $('<div class="modal-content"></div>'),
        modalHeader = $('<div class="modal-header"></div>'),
        modalBody = $('<div class="modal-body">' + content + '</div>'),
        modalFooter = $('<div class="modal-footer"></div>');

      modalHeader
        .append('<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>')
        .append('<h4 class="modal-title">' + title + '</h4>');

      var conferma = $('<button type="button" class="btn btn-default" data-dismiss="modal">Conferma</button>').on('click', function(){
        if (objectToCRUD != null || objectToCRUD != undefined)
          callback(objectToCRUD);
        else
          callback();
      })

      if (callback)
        modalFooter
          .append(conferma)
          .append('<button type="button" class="btn btn-default" data-dismiss="modal">Annulla</button>');

      modalContent
        .append(modalHeader)
        .append(modalBody)
        .append(modalFooter)
        .appendTo(modalDialog);

      modal
        .append(modalDialog)
        .modal("show");

      return modal;

    }

    return {
      modal: modalFn,
      message: function (message) {
        modalFn('<span class="red glyphicon glyphicon-remove-sign"></span> Messaggio', message);
      },
      ok: function () {
        modalFn('<span class="red glyphicon glyphicon-ok-sign"></span> OK', "Operazione Effettuata");
      },
      ok_message: function (message) {
        modalFn('<span class="red glyphicon glyphicon-ok-sign"></span> OK', message);
      },
      error: function (message) {
        modalFn('<span class="red glyphicon glyphicon-remove-sign"></span> Errore', message);
      },
      confirm: function (message, callback) {
        modalFn('<span class="red glyphicon glyphicon-remove-sign"></span> Richiesta', message, callback);
      },
      confirmCRUD: function (message, callback, objectToCRUD) {
        modalFn('<span class="red glyphicon glyphicon-remove-sign"></span> Richiesta', message, callback, objectToCRUD);
      },
      confirmCRUD: function (message, callback, objectToCRUD, param2) {
        modalFn('<span class="red glyphicon glyphicon-remove-sign"></span> Richiesta', message, callback, objectToCRUD, param2);
      }
    };
  })
    .factory('missioniHttpInterceptor', function ($q, ui) {
        return {
            responseError: function (rejection) {
                if (rejection.status === 400 || rejection.status === 401 ||
                    rejection.status === 403 || rejection.status === 406 ||
                    rejection.status === 412 || rejection.status === 500) {
                    if (rejection.data.isFromApplication)
                        ui.error(rejection.data.message);
                }
                return $q.reject(rejection);
            }
        };
    })

    .config(function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, USER_ROLES) {
            $routeProvider
                .when('/', {
                    templateUrl: 'views/main.html',
                    controller: 'HomeController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/manual', {
                    templateUrl: 'views/manual.html',
                    controller: 'ManualController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/release', {
                    templateUrl: 'views/release.html',
                    controller: 'ReleaseController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/faq', {
                    templateUrl: 'views/faq.html',
                    controller: 'FaqController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/helpdesk/:idHelpdesk?', {
                    templateUrl: 'views/helpdesk.html',
                    controller: 'HelpdeskController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/register', {
                    templateUrl: 'views/register.html',
                    controller: 'RegisterController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/activate', {
                    templateUrl: 'views/activate.html',
                    controller: 'ActivationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/error', {
                    templateUrl: 'views/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/settings', {
                    templateUrl: 'views/settings.html',
                    controller: 'SettingsController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/config', {
                    templateUrl: 'views/config.html',
                    controller: 'ConfigController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/autoPropria', {
                    templateUrl: 'views/autoPropria.html',
                    controller: 'AutoPropriaController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/datiPatente', {
                    templateUrl: 'views/datiPatente.html',
                    controller: 'DatiPatenteController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/ordine-missione/:idMissione?/:validazione?', {
                    templateUrl: 'views/ordine-missione.html',
                    controller: 'OrdineMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/rimborso-missione/:idMissione?/:validazione?', {
                    templateUrl: 'views/rimborso-missione.html',
                    controller: 'RimborsoMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/elenco-ordini-missione', {
                    templateUrl: 'views/elenco-ordini-missione.html',
                    controller: 'ElencoOrdiniMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/annullamento-ordine-missione/:idMissione?/:validazione?', {
                    templateUrl: 'views/annullamento-ordine-missione.html',
                    controller: 'AnnullamentoOrdineMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/annullamento-rimborso-missione/:idMissione?/:validazione?', {
                    templateUrl: 'views/annullamento-rimborso-missione.html',
                    controller: 'AnnullamentoRimborsoMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/ordini-missione-da-rendere-definitivi', {
                    templateUrl: 'views/ordini-missione-da-rendere-definitivi.html',
                    controller: 'OrdiniMissioneDaRendereDefinitiviController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/elenco-rimborsi-missione', {
                    templateUrl: 'views/elenco-rimborsi-missione.html',
                    controller: 'ElencoRimborsiMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/rimborso-missione/rimborso-missione-dettagli/:idRimborsoMissione/:validazione?/:inizioMissione/:fineMissione', {
                    templateUrl: 'views/rimborso-missione/rimborso-missione-dettagli.html',
                    controller: 'RimborsoMissioneDettagliController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/ordine-missione/auto-propria/:idOrdineMissione/:validazione?', {
                    templateUrl: 'views/ordine-missione/auto-propria.html',
                    controller: 'AutoPropriaOrdineMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/ordine-missione/richiesta-anticipo/:idOrdineMissione/:validazione?', {
                    templateUrl: 'views/ordine-missione/richiesta-anticipo.html',
                    controller: 'AnticipoOrdineMissioneController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/sessions', {
                    templateUrl: 'views/sessions.html',
                    controller: 'SessionsController',
                    resolve:{
                        resolvedSessions:['Sessions', function (Sessions) {
                            return Sessions.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/metrics', {
                    templateUrl: 'views/metrics.html',
                    controller: 'MetricsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/health', {
                    templateUrl: 'views/health.html',
                    controller: 'HealthController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/configuration', {
                    templateUrl: 'views/configuration.html',
                    controller: 'ConfigurationController',
                    resolve:{
                        resolvedConfiguration:['ConfigurationService', function (ConfigurationService) {
                            return ConfigurationService.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/audits', {
                    templateUrl: 'views/audits.html',
                    controller: 'AuditsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logout', {
                    templateUrl: 'views/main.html',
                    controller: 'LogoutController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/docs', {
                    templateUrl: 'views/docs.html',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .otherwise({
                    templateUrl: 'views/main.html',
                    controller: 'HomeController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                });

            // Initialize angular-translate
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $httpProvider.interceptors.push('missioniHttpInterceptor');

            $translateProvider.preferredLanguage('it');

            $translateProvider.useCookieStorage();

            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js')
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
            httpHeaders = $httpProvider.defaults.headers;
        })
        .run(function($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES) {
                $rootScope.authenticated = false;
                $rootScope.$on('$routeChangeStart', function (event, next) {
                    $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
                    $rootScope.userRoles = USER_ROLES;
                    if (next.access){
                        AuthenticationSharedService.valid(next.access.authorizedRoles);
                    } else {
                        var role = new Array();
                        AuthenticationSharedService.valid(role);
                    }
                });

                // Call when the the client is confirmed
                $rootScope.$on('event:auth-loginConfirmed', function(data) {
                    $rootScope.authenticated = true;
                    if ($location.path() === "/login") {
                        var search = $location.search();
                        if (search.redirect !== undefined) {
                            $location.path(search.redirect).search('redirect', null).replace();
                        } else {
                            $location.path('/').replace();
                        }
                    }
                });

                // Call when the 401 response is returned by the server
                $rootScope.$on('event:auth-loginRequired', function(rejection) {
                    Session.invalidate();
                    $rootScope.authenticated = false;
                    if ($location.path() !== "" && $location.path() !== "/register" &&
                            $location.path() !== "/activate" && $location.path() !== "/login") {
                        $rootScope.salvataggio = false;
                        var redirect = $location.path();
                        $location.path('/login').search('redirect', redirect).replace();
                    }
                });

                // Call when the 403 response is returned by the server
                $rootScope.$on('event:auth-notAuthorized', function(rejection) {
                    $rootScope.errorMessage = 'errors.403';
                    $location.path('/error').replace();
                });

                // Call when the user logs out
                $rootScope.$on('event:auth-loginCancelled', function() {
                    $location.path('');
                });
        });
