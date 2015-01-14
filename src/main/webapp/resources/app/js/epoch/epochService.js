'use strict';
define([],
  function() {
    var dependencies = ['$q', 'StudyService', 'UUIDService', 'SparqlResource'];
    var EpochService = function($q, StudyService, UUIDService, SparqlResource) {

      var epochQuery = SparqlResource.get({
        name: 'queryEpoch.sparql'
      });

      var addEpochQueryRaw = SparqlResource.get({
        name: 'addEpoch.sparql'
      });

      var addEpochCommentQueryRaw = SparqlResource.get({
        name: 'addEpochComment.sparql'
      });

      var addEpochToEndOfListQueryRaw = SparqlResource.get({
        name: 'addEpochToEndOfList.sparql'
      });

      var setEpochPrimaryQueryRaw = SparqlResource.get({
        name: 'setEpochPrimary.sparql'
      });

      var deleteEpochRaw = SparqlResource.get({
        name: 'deleteEpoch.sparql'
      });

      var editEpochRaw = SparqlResource.get({
        name: 'editEpoch.sparql'
      });

      var removeEpochPrimaryRaw = SparqlResource.get({
        name: 'removeEpochPrimary.sparql'
      });

      var setEpochToPrimaryRaw = SparqlResource.get({
        name: 'setEpochToPrimary.sparql'
      });

      function simpleDurationBuilder(durationObject) {
        if (durationObject.durationType === 'instantaneous') {
          return 'PT0S';
        }

        var duration = 'P';

        if (durationObject.periodType.type === 'time') {
          duration = duration + 'T';
        }

        duration = duration + durationObject.numberOfPeriods + durationObject.periodType.value;
        return duration;
      }

      //http://stackoverflow.com/questions/10834796/validate-that-a-string-is-a-positive-integer
      function isNormalInteger(str) {
        return /^\+?(0|[1-9]\d*)$/.test(str);
      }

      function isValidDuration(duration, periodTypeOptions) {
        if (!duration.durationType) {
          return false;
        } else if (duration.durationType === 'instantaneous') {
          return true;
        } else if (duration.durationType === 'period') {
          var isValidType = _.find(periodTypeOptions, function(option) {
            return option.value === duration.periodType.value;
          });
          var isValidNumberOfPeriods = isNormalInteger(duration.numberOfPeriods);
          return isValidType && isValidNumberOfPeriods;
        } else {
          throw 'invalid duration type';
        }
      }

      function transformDuration(duration) {
        var transformedDuration;

        if (duration.value === 'PT0S') {
          return {
            durationType: 'instantaneous'
          };
        }

        transformedDuration = {
          durationType: 'period',
        };

        if (duration.value[1] === 'T') {
          transformedDuration.value = duration.value.slice(2); // remove 'PT'
        } else {
          transformedDuration.value = duration.value.slice(1); // remove 'P'
        }

        transformedDuration.periodType = {
          value: transformedDuration.value[transformedDuration.value.length - 1]
        };
        var numberOfPeriodsAsString = transformedDuration.value.substr(0, transformedDuration.value.length - 1);
        transformedDuration.numberOfPeriods = parseInt(numberOfPeriodsAsString, 10);

        return transformedDuration;
      }

      function queryItems() {
        return epochQuery.$promise.then(function(query) {
          return StudyService.doNonModifyingQuery(query.data);
        });
      }

      function addItem(item, studyUUID) {
        var uuid = UUIDService.generate();
        var promises = [
          addEpochQueryRaw.$promise,
          addEpochCommentQueryRaw.$promise,
          setEpochPrimaryQueryRaw.$promise,
          addEpochToEndOfListQueryRaw.$promise
        ];
        var durationString = simpleDurationBuilder(item.duration);

        // add epoch
        addEpochQueryRaw.$promise.then(function(query) {
          var addEpochQuery = query.data
            .replace(/\$newUUID/g, uuid)
            .replace('$label', item.label)
            .replace('$duration', durationString);
          promises.push(StudyService.doModifyingQuery(addEpochQuery));
        });
        // optional add comment
        if (item.comment) {
          addEpochCommentQueryRaw.$promise.then(function(query) {
            var addEpochCommentQuery = query.data
              .replace(/\$newUUID/g, uuid)
              .replace('$comment', item.comment);
            promises.push(StudyService.doModifyingQuery(addEpochCommentQuery));
          });
        }
        // optional is_primary
        if (item.isPrimaryEpoch) {
          setEpochPrimaryQueryRaw.$promise.then(function(query) {
            var setEpochPrimaryQuery = query.data
              .replace(/\$studyUUID/g, studyUUID)
              .replace(/\$newUUID/g, uuid);
            promises.push(StudyService.doModifyingQuery(setEpochPrimaryQuery));
          });
        }

        // add epoch to list of has_epochs in study
        addEpochToEndOfListQueryRaw.$promise.then(function(query) {
          var addEpochToEndOfListQuery = query.data
            .replace(/\$elementToInsert/g, uuid);
          promises.push(StudyService.doModifyingQuery(addEpochToEndOfListQuery));
        });

        return $q.all(promises);
      }

      function deleteItem(item) {
        return deleteEpochRaw.$promise.then(function(deleteQueryRaw) {
          var deleteQuery = deleteQueryRaw.data.replace(/\$URI/g, item.uri.value);
          return StudyService.doModifyingQuery(deleteQuery);
        });
      }

      function editItem(oldItem, newItem, studyUuid) {
        
        newItem.duration.value = simpleDurationBuilder(newItem.duration);

        var promises = [removeEpochPrimaryRaw.$promise, setEpochToPrimaryRaw.$promise];

        if (oldItem.isPrimary.value === 'true' && !newItem.isPrimary.value) {
          removeEpochPrimaryRaw.$promise.then(function(queryRaw) {
            var query = queryRaw.data.replace(/\$URI/g, newItem.uri.value)
              .replace(/\$studyUuid/g, studyUuid);
            promises.push(StudyService.doModifyingQuery(query));
          });
        } else if (newItem.isPrimary.value) {
          setEpochToPrimaryRaw.$promise.then(function(queryRaw) {
            var query = queryRaw.data.replace(/\$URI/g, newItem.uri.value)
              .replace(/\$studyUuid/g, studyUuid);
            promises.push(StudyService.doModifyingQuery(query));
          });
        }

        editEpochRaw.$promise.then(function(editQueryRaw) {
          var editQuery = editQueryRaw.data.replace(/\$URI/g, newItem.uri.value)
            .replace(/\$studyUuid/g, studyUuid)
            .replace(/\$newDuration/g, newItem.duration.value)
            .replace(/\$newLabel/g, newItem.label.value)
            .replace(/\$newComment/g, newItem.comment.value);
          promises.push(StudyService.doModifyingQuery(editQuery));
        });

        return $q.all(promises);
      }

      return {
        queryItems: queryItems,
        addItem: addItem,
        deleteItem: deleteItem,
        editItem: editItem,
        transformDuration: transformDuration,
        isValidDuration: isValidDuration
      };
    };
    return dependencies.concat(EpochService);
  });