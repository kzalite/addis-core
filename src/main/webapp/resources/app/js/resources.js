'use strict';
var requires = [
  'resources/evidenceTableResource',
  'resources/projectResource',
  'resources/trialverseResource',
  'resources/semanticOutcomeResource',
  'resources/semanticInterventionResource',
  'resources/outcomeResource',
  'resources/interventionResource',
  'resources/analysisResource',
  'resources/trialverseStudyResource',
  'resources/projectStudiesResource',
  'resources/trialverseStudiesWithDetailsResource',
  'resources/scenarioResource',
  'resources/scaledUnitResource',
  'resources/subProblemResource',
  'resources/orderingResource',
  'resources/toggleColumnsResource'
];
define(requires.concat(['angular', 'angular-resource']), function(
  EvidenceTableResource,
  ProjectResource,
  TrialverseResource,
  SemanticOutcomeResource,
  SemanticInterventionResource,
  OutcomeResource,
  InterventionResource,
  AnalysisResource,
  TrialverseStudyResource,
  ProjectStudiesResource,
  TrialverseStudiesWithDetailsResource,
  ScenarioResource,
  ScaledUnitResource,
  SubProblemResource,
  OrderingResource,
  ToggleColumnsResource,
  angular
) {
  return angular.module('addis.resources', ['ngResource'])
    .factory('EvidenceTableResource', EvidenceTableResource)
    .factory('ProjectResource', ProjectResource)
    .factory('TrialverseResource', TrialverseResource)
    .factory('SemanticOutcomeResource', SemanticOutcomeResource)
    .factory('SemanticInterventionResource', SemanticInterventionResource)
    .factory('OutcomeResource', OutcomeResource)
    .factory('InterventionResource', InterventionResource)
    .factory('AnalysisResource', AnalysisResource)
    .factory('TrialverseStudyResource', TrialverseStudyResource)
    .factory('ProjectStudiesResource', ProjectStudiesResource)
    .factory('TrialverseStudiesWithDetailsResource', TrialverseStudiesWithDetailsResource)
    .factory('ScenarioResource', ScenarioResource)
    .factory('ScaledUnitResource', ScaledUnitResource)
    .factory('SubProblemResource', SubProblemResource)
    .factory('OrderingResource', OrderingResource)
    .factory('ToggleColumnsResource',ToggleColumnsResource)
    ;
});