'use strict';
define(
  ['angular',
    'require',
    'jQuery',
    'foundation',
    'angular-ui-router',
    'controllers',
    'directives',
    'filters',
    'resources'],
  function (angular, require, $) {
    var dependencies = ['ui.router', 'addis.controllers', 'addis.directives', 'addis.resources', 'addis.filters'];
    var app = angular.module('addis', dependencies);

    app.run(['$rootScope', '$window', '$http', function ($rootScope, $window, $http) {
      var csrfToken = $window.config._csrf_token;
      var csrfHeader = $window.config._csrf_header;

      $http.defaults.headers.common[csrfHeader] = csrfToken;
      $rootScope.$on('$viewContentLoaded', function () {
        $(document).foundation();
      });

    }]);

    app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      var baseTemplatePath = "app/views/";

      // Default route
      $stateProvider.state('projects',
        { url: '/projects',
          templateUrl: baseTemplatePath + 'projects.html',
          controller: "ProjectsController" });
      $urlRouterProvider.otherwise('/projects');
    }]);


    return app;
});