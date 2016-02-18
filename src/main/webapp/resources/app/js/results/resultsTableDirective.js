'use strict';
define([], function() {
  var dependencies = ['$q', 'ResultsTableService', 'ResultsService'];

  var resultsTableDirective = function($q, ResultsTableService, ResultsService) {
    return {
      restrict: 'E',
      templateUrl: 'app/js/results/resultsTableDirective.html',
      scope: {
        variable: '=',
        arms: '=',
        groups: '=',
        measurementMoments: '=',
        isEditingAllowed: '='
      },
      link: function(scope) {

        scope.results = ResultsService.queryResults(scope.variable.uri);

        $q.all([scope.arms, scope.measurementMoments, scope.groups, scope.results]).then(function() {
          scope.inputRows = ResultsTableService.createInputRows(scope.variable, scope.arms, scope.groups, scope.measurementMoments, scope.results.$$state.value);
          scope.inputHeaders = ResultsTableService.createHeaders(scope.variable.measurementType);
        });

      }
    };
  };

  return dependencies.concat(resultsTableDirective);
});
