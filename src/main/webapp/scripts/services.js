'use strict';

/* Services */

missioniApp.factory('LanguageService', function ($http, $translate, LANGUAGES) {
        return {
            getBy: function(language) {
                if (language == undefined) {
                    language = $translate.storage().get('NG_TRANSLATE_LANG_KEY');
                }
                if (language == undefined) {
                    language = 'en';
                }

                var promise =  $http.get('i18n/' + language + '.json').then(function(response) {
                    return LANGUAGES;
                });
                return promise;
            }
        };
    });

missioniApp.factory('Register', function ($resource) {
        return $resource('app/rest/register', {}, {
        });
    });

missioniApp.factory('Activate', function ($resource) {
        return $resource('app/rest/activate', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    });

missioniApp.factory('Account', function ($resource) {
        return $resource('app/rest/account', {}, {
        });
    });

missioniApp.factory('AccountFromToken', function ($resource) {
        return $resource('app/rest/ldap/account/token', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    });

missioniApp.factory('AccountLDAP', function ($resource) {
        return $resource('app/rest/ldap', {}, {
        });
    });

missioniApp.factory('Password', function ($resource) {
        return $resource('app/rest/account/change_password', {}, {
        });
    });

missioniApp.factory('Sessions', function ($resource) {
        return $resource('app/rest/account/sessions/:series', {}, {
            'get': { method: 'GET', isArray: true}
        });
    });

missioniApp.factory('MetricsService',function ($http) {
    		return {
            get: function() {
                var promise = $http.get('metrics/metrics').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    });

missioniApp.factory('ThreadDumpService', function ($http) {
        return {
            dump: function() {
                var promise = $http.get('dump').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    });

missioniApp.factory('HealthCheckService', function ($rootScope, $http) {
        return {
            check: function() {
                var promise = $http.get('health').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    });

missioniApp.factory('ConfigurationService', function ($rootScope, $filter, $http) {
    return {
        get: function() {
            var promise = $http.get('configprops').then(function(response){
                var properties = [];
                angular.forEach(response.data, function(data) {
                    properties.push(data);
                });
                var orderBy = $filter('orderBy');
                return orderBy(properties, 'prefix');;
            });
            return promise;
        }
    };
});

missioniApp.factory('LogsService', function ($resource) {
        return $resource('app/rest/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel':  { method: 'PUT'}
        });
    });

'use strict';

missioniApp.factory('modalService', function () {

    function modal (modalTitle, bodyContent) {

      var modalContent = $('<div class="modal-content"></div>');

      if (modalTitle) {
        $('<div class="modal-header"></div>')
          .append('<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>')
          .append('<h4 class="modal-title">' + modalTitle + '</h4>')
          .appendTo(modalContent);
      }

      $('<div class="modal-body"></div>')
        .append(bodyContent)
        .appendTo(modalContent);

      $('<div class="modal-footer"></div>')
        .append('<button type="button" class="btn btn-default" data-dismiss="modal">Chiudi</button>')
        .appendTo(modalContent);

      return $('<div class="modal-dialog modal-lg"></div>').append(modalContent);

    }

    return {
      modal: modal,
      simpleModal: function (title, url) {
        var modalContent = modal(title, '<img src="' + url + '" />');
        $('<div class="modal fade role="dialog" tabindex="-1"></div>').append(modalContent).modal();
      }
    };

  });

missioniApp.factory('AuditsService', function ($http) {
        return {
            findAll: function() {
                var promise = $http.get('app/rest/audits/all').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findByDates: function(fromDate, toDate) {
                var promise = $http.get('app/rest/audits/byDates', {params: {fromDate: fromDate, toDate: toDate}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    });

missioniApp.factory('Session', function () {
        this.create = function (login, matricola, firstName, lastName, email, userRoles, comune_nascita, data_nascita, comune_residenza, indirizzo_residenza, num_civico_residenza, cap_residenza, provincia_residenza, codice_fiscale, profilo, struttura_appartenenza, codice_sede, codice_uo, livello, allUoForUsersSpecial, uoForUsersSpecial, isAccountLDAP) {
            this.login = login;
            this.matricola = matricola;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.userRoles = userRoles;
            this.comune_nascita = comune_nascita;
            this.data_nascita = data_nascita;
            this.comune_residenza = comune_residenza;
            this.indirizzo_residenza = indirizzo_residenza;
            this.num_civico_residenza = num_civico_residenza;
            this.cap_residenza = cap_residenza;
            this.provincia_residenza = provincia_residenza;
            this.codice_fiscale = codice_fiscale;
            this.profilo = profilo;
            this.struttura_appartenenza = struttura_appartenenza;
            this.codice_sede = codice_sede;
            this.codice_uo = codice_uo;
            this.livello = livello;
            this.allUoForUsersSpecial = allUoForUsersSpecial;
            this.uoForUsersSpecial = uoForUsersSpecial;
            this.livello = livello;

            this.isAccountLDAP = isAccountLDAP;
        };
        this.invalidate = function () {
            this.login = null;
            this.matricola = null;
            this.firstName = null;
            this.lastName = null;
            this.email = null;
            this.userRoles = null;
            this.comune_nascita = null;
            this.data_nascita = null;
            this.comune_residenza = null;
            this.indirizzo_residenza = null;
            this.num_civico_residenza = null;
            this.cap_residenza = null;
            this.provincia_residenza = null;
            this.codice_fiscale = null;
            this.profilo = null;
            this.struttura_appartenenza = null;
            this.codice_sede = null;
            this.codice_uo = null;
            this.livello = null;
            this.allUoForUsersSpecial = null;
            this.uoForUsersSpecial = null;
            this.isAccountLDAP = null;
        };
        return this;
    });

missioniApp.factory('AuthenticationSharedService', function ($rootScope, $http, authService, Session, Account, AccountLDAP, Base64Service, AccessToken, AccountFromToken, $sessionStorage) {
        return {
            login: function (param) {
                var data = "username=" + param.username + "&password=" + param.password + "&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=missioniapp";
                $http.post('oauth/token', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        "Accept": "application/json",
                        "Authorization": "Basic " + Base64Service.encode("missioniapp" + ':' + "mySecretOAuthSecret")
                    },
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    httpHeaders.common['Authorization'] = 'Bearer ' + data.access_token;
                    AccessToken.set(data);
                    AccountLDAP.get(function(data) {
                        if (data.isLDAPAccount) {
                            httpHeaders.common['X-Proxy-Authorization'] = 'Basic ' + Base64Service.encode(param.username + ':' + param.password);
                            $http.get(
                                'app/proxy/SIPER?proxyURL=json/userinfo/' + param.username
                            ).success(function (data, status, headers, config) {
                                delete httpHeaders.common['X-Proxy-Authorization'];
                                Session.create(param.username, data.matricola, data.nome, data.cognome, data.email_comunicazioni, ['ROLE_USER'],
                                    data.comune_nascita, data.data_nascita, data.comune_residenza, data.indirizzo_residenza,
                                    data.num_civico_residenza, data.cap_residenza, data.provincia_residenza, data.codice_fiscale,
                                    data.profilo, data.struttura_appartenenza, data.codice_sede, data.codice_uo, data.livello_profilo, data.allUoForUsersSpecial, data.uoForUsersSpecial, true);
                                $rootScope.account = Session;
                                $sessionStorage.account = Session;
                                authService.loginConfirmed(data);
                            }).error(function (data, status, headers, config) {
                                delete httpHeaders.common['X-Proxy-Authorization'];
                            });
                        } else {
                            Account.get(function(data) {
                                Session.create(data.login, null, data.firstName, data.lastName, data.email, data.roles);
                                $rootScope.account = Session;
                                $sessionStorage.account = Session;
                                authService.loginConfirmed(data);
                            });
                        }
                    });
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticated = false;
                    $rootScope.authenticationError = true;
                    Session.invalidate();
                    AccessToken.remove();
                    delete httpHeaders.common['Authorization'];
                    $rootScope.$broadcast('event:auth-loginRequired', data);
                });
            },
            valid: function (authorizedRoles) {
                var token = AccessToken.get();
                if(token !== null) {
                    httpHeaders.common['Authorization'] = 'Bearer ' + token;
                }

                $http.get('protected/authentication_check.gif', {
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    if (!Session.login || token != undefined) {
                        if (token == undefined || AccessToken.expired()) {
                            $rootScope.$broadcast("event:auth-loginRequired");
                            return;
                        }
                        AccountLDAP.get(function(data) {
                            if (!data.isLDAPAccount) {
                                Account.get(function(data) {
                                    Session.create(data.login, null, data.firstName, data.lastName, data.email, data.roles);
                                    $rootScope.account = Session;
                                    $sessionStorage.account = Session;
                                    if (!$rootScope.isAuthorized(authorizedRoles)) {
                                        // user is not allowed
                                       $rootScope.$broadcast("event:auth-notAuthorized");
                                    } else {
                                        $rootScope.$broadcast("event:auth-loginConfirmed");
                                    }
                                });
                            } else {
                                AccountFromToken.get({token: token},
                                    function (data, responseHeaders) {
                                        Session.create(data.uid, data.matricola, data.nome, data.cognome, data.email_comunicazioni, ['ROLE_USER'],
                                            data.comune_nascita, data.data_nascita, data.comune_residenza, data.indirizzo_residenza,
                                            data.num_civico_residenza, data.cap_residenza, data.provincia_residenza, data.codice_fiscale,
                                            data.profilo, data.struttura_appartenenza, data.codice_sede, data.codice_uo, data.livello_profilo, data.allUoForUsersSpecial, data.uoForUsersSpecial, true);
                                        $rootScope.account = Session;
                                        $sessionStorage.account = Session;
                                        if (!$rootScope.isAuthorized(authorizedRoles)) {
                                            // user is not allowed
                                           $rootScope.$broadcast("event:auth-notAuthorized");
                                        } else {
                                            $rootScope.$broadcast("event:auth-loginConfirmed");
                                        }
                                    },
                                    function (httpResponse) {
                                        $scope.success = null;
                                        $scope.error = "ERROR";
                                    });

                            }
                        });
                    }else{
                        if (!$rootScope.isAuthorized(authorizedRoles)) {
                                // user is not allowed
                                $rootScope.$broadcast("event:auth-notAuthorized");
                        } else {
                                $rootScope.$broadcast("event:auth-loginConfirmed");
                        }
                    }
                }).error(function (data, status, headers, config) {
                    if (!$rootScope.isAuthorized(authorizedRoles)) {
                        $rootScope.$broadcast('event:auth-loginRequired', data);
                    }
                });
            },
            isAuthorized: function (authorizedRoles) {
                if (!angular.isArray(authorizedRoles)) {
                    if (authorizedRoles == '*') {
                        return true;
                    }

                    authorizedRoles = [authorizedRoles];
                }

                var isAuthorized = false;
                angular.forEach(authorizedRoles, function(authorizedRole) {
                    var authorized = (!!Session.login &&
                        Session.userRoles.indexOf(authorizedRole) !== -1);

                    if (authorized || authorizedRole == '*') {
                        isAuthorized = true;
                    }
                });

                return isAuthorized;
            },
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.authenticated = false;
                $rootScope.account = null;
                $sessionStorage.account = null;
                AccessToken.remove();

                $http.get('app/logout');
                Session.invalidate();
                delete httpHeaders.common['Authorization'];
                authService.loginCancelled();
            }
        };
    });
