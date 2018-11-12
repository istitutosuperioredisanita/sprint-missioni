'use strict';

angular.module('missioniApp')
    .directive('markdown', function() {
        return {
            restrict: 'AE',
            scope: {
                status: '='
            },
            link: function (scope, element, attrs, controller) {
              var md_content = attrs.content;
              var html_content = markdown.toHTML(md_content);
              $(html_content).appendTo(element);
            }
        };
    });
