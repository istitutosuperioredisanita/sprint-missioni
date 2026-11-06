angular.module('missioniAppUtils', [])
    .service('Base64Service', function () {
        var keyStr = "ABCDEFGHIJKLMNOP" +
            "QRSTUVWXYZabcdef" +
            "ghijklmnopqrstuv" +
            "wxyz0123456789+/" +
            "=";
        this.encode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }

            return output;
        };

        this.decode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            while (i < input.length) {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }
        };
    })
    .factory('StorageService', function ($rootScope) {
        return {

            get: function (key) {
                return JSON.parse(localStorage.getItem(key));
            },

            save: function (key, data) {
                localStorage.setItem(key, JSON.stringify(data));
            },

            remove: function (key) {
                localStorage.removeItem(key);
            },

            clearAll : function () {
                localStorage.clear();
            }
        };
    })
    .factory('AccessToken', function($location, $http, StorageService, $rootScope) {
            var TOKEN = 'token';
            var service = {};
            var token = null;

            service.get = function() {
                // read the token from the localStorage
                if (token == null) {
                    token = StorageService.get(TOKEN);
                }

                if (token != null) {
                    return token.access_token;
                }

                return null;
            };

            service.set = function(oauthResponse) {
                token = {};
                token.access_token = oauthResponse.access_token;
                setExpiresAt(oauthResponse);
                StorageService.save(TOKEN, token);
                return token
            };

            service.remove = function() {
                token = null;
                StorageService.remove(TOKEN);
                return token;
            };

            service.expired = function() {
                return (token && token.expires_at && token.expires_at < new Date().getTime())
            };

            var setExpiresAt = function(oauthResponse) {
                if (token) {
                    var now = new Date();
                    var minutes = parseInt(oauthResponse.expires_in) / 60;
                    token.expires_at = new Date(now.getTime() + minutes*60000).getTime()
                }
            };

            return service;
        });

angular.module('missioniAppUtils')
    .service('ExcelExportService', function ($q, $http) {

        var EXCELJS_URL = 'bower_components/exceljs/dist/exceljs.min.js';
        var loadingPromise = null;

        function loadExcelJS() {
            if (window.ExcelJS) {
                return $q.when(window.ExcelJS);
            }
            if (!loadingPromise) {
                var deferred = $q.defer();
                var script = document.createElement('script');
                script.src = EXCELJS_URL;
                script.async = true;
                script.onload = function () {
                    if (window.ExcelJS) {
                        deferred.resolve(window.ExcelJS);
                    } else {
                        loadingPromise = null;
                        deferred.reject(new Error('Libreria ExcelJS non disponibile'));
                    }
                };
                script.onerror = function () {
                    loadingPromise = null;
                    deferred.reject(new Error('Impossibile caricare la libreria ExcelJS'));
                };
                document.head.appendChild(script);
                loadingPromise = deferred.promise;
            }
            return loadingPromise;
        }

        // ExcelJS scrive in UTC
        function toExcelDate(d) {
            return new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate(),
                d.getHours(), d.getMinutes(), d.getSeconds()));
        }

        this.toDate = function (value) {
            if (value === null || value === undefined || value === '') {
                return null;
            }
            var d;
            if (value instanceof Date) {
                d = value;
            } else if (typeof value === 'number') {
                d = new Date(value);
            } else if (angular.isArray(value)) {
                d = new Date(value[0], (value[1] || 1) - 1, value[2] || 1, value[3] || 0, value[4] || 0, value[5] || 0);
            } else {
                var s = String(value).trim();
                var it = /^(\d{1,2})\/(\d{1,2})\/(\d{4})(?:[ T](\d{1,2}):(\d{2})(?::(\d{2}))?)?$/.exec(s);
                var isoData = /^(\d{4})-(\d{2})-(\d{2})$/.exec(s);
                if (it) {
                    d = new Date(+it[3], +it[2] - 1, +it[1], +(it[4] || 0), +(it[5] || 0), +(it[6] || 0));
                } else if (isoData) {
                    d = new Date(+isoData[1], +isoData[2] - 1, +isoData[3]);
                } else {
                    d = new Date(s.replace(/\[[^\]]*\]$/, ''));
                }
            }
            return isNaN(d.getTime()) ? null : d;
        };

        function download(buffer, fileName) {
            var blob = new Blob([buffer], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            });
            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveOrOpenBlob(blob, fileName);
                return;
            }
            var url = URL.createObjectURL(blob);
            var a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            setTimeout(function () {
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }, 0);
        }

        var COLORE_INTESTAZIONE = 'FF1F4E79';  // blu scuro
        var COLORE_RIGA_ALTERNA = 'FFF2F6FA';  // azzurro molto chiaro
        var COLORE_BORDO = 'FFD0D7DE';         // grigio chiaro
        var COLORE_SOTTOTITOLO = 'FF595959';   // grigio scuro

        // spazio per la freccia del filtro
        var MARGINE_FILTRO = 3;

        function intestazioneSuDueRighe(testo) {
            var parole = testo.split(/\s+/);
            if (testo.length <= 12 || parole.length < 2) {
                return testo;
            }
            var migliore = 1;
            var lunghezzaMigliore = testo.length;
            for (var i = 1; i < parole.length; i++) {
                var lunghezza = Math.max(parole.slice(0, i).join(' ').length, parole.slice(i).join(' ').length);
                if (lunghezza < lunghezzaMigliore) {
                    lunghezzaMigliore = lunghezza;
                    migliore = i;
                }
            }
            return parole.slice(0, migliore).join(' ') + '\n' + parole.slice(migliore).join(' ');
        }

        function lunghezzaRigaIntestazione(testo) {
            return Math.max.apply(null, intestazioneSuDueRighe(testo).split('\n').map(function (riga) {
                return riga.length;
            }));
        }

        var LARGHEZZA_MASSIMA = 50;
        var ALTEZZA_RIGA_TESTO = 15;

        function larghezzaMinimaColonna(col) {
            var testo = pulisciIntestazione(col.header);
            return Math.ceil(lunghezzaRigaIntestazione(testo) * 1.15) + 2 * MARGINE_FILTRO;
        }

        function lunghezzaValore(value, col) {
            if (value === null || value === undefined) {
                return 0;
            }
            if (value instanceof Date) {
                return col.dateOnly ? 10 : 16;
            }
            return Math.max.apply(null, String(value).split('\n').map(function (riga) {
                return riga.length;
            }));
        }

        function righeTesto(value, larghezza) {
            if (typeof value !== 'string' || !value) {
                return 1;
            }
            var caratteriPerRiga = Math.max(1, (larghezza - 2) / 1.1);
            return value.split('\n').reduce(function (totale, riga) {
                return totale + Math.max(1, Math.ceil(riga.length / caratteriPerRiga));
            }, 0);
        }

        function pulisciIntestazione(testo) {
            return String(testo || '').replace(/\s*\*\s*$/, '');
        }

        var cacheNominativi = {};

        // "Cognome Nome" per uid
        this.nominativi = function (uids, personeNote) {
            var risultato = {};
            var daCercare = [];
            (personeNote || []).forEach(function (p) {
                if (p && p.uid && (p.cognome || p.nome)) {
                    cacheNominativi[p.uid] = [p.cognome, p.nome].filter(Boolean).join(' ');
                }
            });
            (uids || []).forEach(function (uid) {
                if (!uid || risultato.hasOwnProperty(uid)) {
                    return;
                }
                if (cacheNominativi[uid]) {
                    risultato[uid] = cacheNominativi[uid];
                } else {
                    risultato[uid] = null;
                    daCercare.push(uid);
                }
            });
            var richieste = daCercare.map(function (uid) {
                return $http.get('api/account-info', { params: { username: uid } }).then(function (response) {
                    var p = response.data;
                    if (p && (p.cognome || p.nome)) {
                        cacheNominativi[uid] = [p.cognome, p.nome].filter(Boolean).join(' ');
                        risultato[uid] = cacheNominativi[uid];
                    }
                }, function () {
                });
            });
            return $q.all(richieste).then(function () {
                return risultato;
            });
        };

        // columns: [{ header, value(row), maxWidth, dateOnly, align }]
        this.export = function (fileName, sheetName, columns, rows, options) {
            rows = rows || [];
            options = options || {};
            var name = fileName || 'export';
            if (!/\.xlsx$/i.test(name)) {
                name += '.xlsx';
            }
            var safeSheet = (sheetName || 'Foglio1').replace(/[\\\/\?\*\[\]:]/g, '').substring(0, 31);

            return loadExcelJS().then(function (ExcelJS) {
                var workbook = new ExcelJS.Workbook();
                workbook.creator = 'Missioni';
                workbook.created = new Date();
                workbook.title = options.title || safeSheet;

                var righeTitolo = (options.title ? 1 : 0) + (options.subtitle ? 1 : 0);
                var rigaIntestazione = righeTitolo > 0 ? righeTitolo + 2 : 1;
                var numeroColonne = Math.max(1, columns.length);

                var sheet = workbook.addWorksheet(safeSheet, {
                    views: [{ state: 'frozen', ySplit: rigaIntestazione, showGridLines: false }],
                    pageSetup: {
                        paperSize: 9,              // A4
                        orientation: 'landscape',
                        fitToPage: true,
                        fitToWidth: 1,
                        fitToHeight: 0,
                        printTitlesRow: rigaIntestazione + ':' + rigaIntestazione,
                        margins: { left: 0.4, right: 0.4, top: 0.5, bottom: 0.5, header: 0.3, footer: 0.3 }
                    },
                    headerFooter: {
                        oddFooter: '&L' + (options.title || safeSheet) + '&RPagina &P di &N'
                    }
                });

                var valori = rows.map(function (row) {
                    return columns.map(function (col) {
                        var value;
                        try {
                            value = col.value(row);
                        } catch (e) {
                            value = null;
                        }
                        if (value instanceof Date) {
                            return isNaN(value.getTime()) ? null : value;
                        }
                        return value === undefined || value === '' ? null : value;
                    });
                });

                var larghezze = columns.map(function (col, i) {
                    var massimo = col.maxWidth || LARGHEZZA_MASSIMA;
                    var contenuto = 0;
                    valori.forEach(function (riga) {
                        contenuto = Math.max(contenuto, lunghezzaValore(riga[i], col));
                    });
                    var larghezza = Math.min(massimo, Math.ceil(contenuto * 1.1) + 2);
                    return Math.max(larghezzaMinimaColonna(col), larghezza);
                });

                sheet.columns = columns.map(function (col, i) {
                    return { key: 'c' + i, width: larghezze[i] };
                });

                if (options.title) {
                    var rigaTitolo = sheet.getRow(1);
                    rigaTitolo.getCell(1).value = options.title;
                    rigaTitolo.getCell(1).font = { bold: true, size: 14, color: { argb: COLORE_INTESTAZIONE } };
                    rigaTitolo.height = 22;
                    sheet.mergeCells(1, 1, 1, numeroColonne);
                }
                if (options.subtitle) {
                    var n = options.title ? 2 : 1;
                    sheet.getRow(n).getCell(1).value = options.subtitle;
                    sheet.getRow(n).getCell(1).font = { italic: true, size: 10, color: { argb: COLORE_SOTTOTITOLO } };
                    sheet.mergeCells(n, 1, n, numeroColonne);
                }

                var bordo = { style: 'thin', color: { argb: COLORE_BORDO } };
                var intestazione = sheet.getRow(rigaIntestazione);
                intestazione.height = 30;
                columns.forEach(function (col, i) {
                    var cell = intestazione.getCell(i + 1);
                    cell.value = intestazioneSuDueRighe(pulisciIntestazione(col.header));
                    cell.font = { bold: true, color: { argb: 'FFFFFFFF' } };
                    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: COLORE_INTESTAZIONE } };
                    cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
                    cell.border = { top: bordo, left: bordo, bottom: bordo, right: bordo };
                });

                valori.forEach(function (riga, indice) {
                    var excelRow = sheet.getRow(rigaIntestazione + 1 + indice);
                    var righeMassime = 1;
                    columns.forEach(function (col, i) {
                        var value = riga[i];
                        righeMassime = Math.max(righeMassime, righeTesto(value, larghezze[i]));
                        if (value instanceof Date) {
                            value = toExcelDate(value);
                        }

                        var cell = excelRow.getCell(i + 1);
                        cell.value = value;
                        var isData = value instanceof Date;
                        if (isData) {
                            cell.numFmt = col.dateOnly ? 'dd/mm/yyyy' : 'dd/mm/yyyy hh:mm';
                        }
                        var allineamento = col.align ||
                            (isData || col.dateOnly || typeof value === 'number' ? 'center' : 'left');
                        cell.alignment = { vertical: 'top', horizontal: allineamento, wrapText: true };
                        cell.border = { top: bordo, left: bordo, bottom: bordo, right: bordo };
                        if (indice % 2 === 1) {
                            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: COLORE_RIGA_ALTERNA } };
                        }
                    });
                    if (righeMassime > 1) {
                        excelRow.height = righeMassime * ALTEZZA_RIGA_TESTO + 2;
                    }
                });

                if (columns.length > 0) {
                    sheet.autoFilter = {
                        from: { row: rigaIntestazione, column: 1 },
                        to: { row: rigaIntestazione + rows.length, column: columns.length }
                    };
                }

                return workbook.xlsx.writeBuffer();
            }).then(function (buffer) {
                download(buffer, name);
            });
        };
    });

// filtri degli elenchi, ripristinati solo tornando con "Chiudi"
angular.module('missioniAppUtils')
    .service('StatoElencoService', function ($sessionStorage) {

        var VALIDITA_RIPRISTINO_MS = 30000;

        function chiaveRipristino(chiave) {
            return chiave + '_ripristina';
        }

        this.salva = function (chiave, stato) {
            $sessionStorage[chiave] = stato;
            delete $sessionStorage[chiaveRipristino(chiave)];
        };

        this.richiediRipristino = function (chiave) {
            if ($sessionStorage[chiave]) {
                $sessionStorage[chiaveRipristino(chiave)] = new Date().getTime();
            }
        };

        this.leggi = function (chiave) {
            var stato = $sessionStorage[chiave];
            var richiesta = $sessionStorage[chiaveRipristino(chiave)];
            delete $sessionStorage[chiave];
            delete $sessionStorage[chiaveRipristino(chiave)];
            if (stato && richiesta && (new Date().getTime() - richiesta) <= VALIDITA_RIPRISTINO_MS) {
                return stato;
            }
            return null;
        };
    });
