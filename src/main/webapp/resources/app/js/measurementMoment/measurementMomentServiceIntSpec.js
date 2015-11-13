'use strict';
define(['angular', 'angular-mocks'], function(angular) {
  fdescribe('the measurement moment service', function() {

    var mockStudyUuid = 'mockStudyUuid';
    var rootScope, q;
    var remotestoreServiceStub;
    var studyService = jasmine.createSpyObj('StudyService', ['getJsonGraph', 'saveJsonGraph']);
    var epochServiceStub = jasmine.createSpyObj('EpochService', ['queryItems']);
    var measurementMomentService;


    beforeEach(function() {
      module('trialverse.measurementMoment', function($provide) {
        $provide.value('StudyService', studyService);
        $provide.value('EpochService', epochServiceStub);
      });
    });

    beforeEach(module('trialverse.util'));

    beforeEach(inject(function($q, $rootScope, MeasurementMomentService, StudyService) {
      q = $q;
      rootScope = $rootScope;
      measurementMomentService = MeasurementMomentService;

      studyService = StudyService;

      // mock stub services
      var queryEpochsDeferred = $q.defer();
      var mockEpochs = [{
        uri: 'http://trials.drugis.org/instances/aaa',
        duration: 'P14D',
        label: 'Washout',
        pos: 0,
        isPrimary: false
      }, {
        uri: 'http://trials.drugis.org/instances/bbb',
        label: 'Randomization',
        duration: 'PT0S',
        pos: 1,
        isPrimary: true
      }];
      queryEpochsDeferred.resolve(mockEpochs);
      epochServiceStub.queryItems.and.returnValue(queryEpochsDeferred.promise);

      rootScope.$digest();
    }));


    describe('query measurement moments', function() {
      beforeEach(function() {
        var graphJsonObject = [{
          "@id": "http://trials.drugis.org/instances/4bf82c72-02de-48cb-8925-2062b6b82234",
          "@type": "ontology:MeasurementMoment",
          "relative_to_anchor": "ontology:anchorEpochStart",
          "relative_to_epoch": "http://trials.drugis.org/instances/bbb",
          "time_offset": "PT0S",
          "label": "At start of epoch 1"
        }];
        var graphDefer = q.defer();
        var getGraphPromise = graphDefer.promise;
        graphDefer.resolve(graphJsonObject);
        studyService.getJsonGraph.and.returnValue(getGraphPromise);
      });
      it('should return the measurements', function(done) {
        measurementMomentService.queryItems().then(function(result) {
          expect(result.length).toBe(1);
          var measurementMoment = result[0];
          expect(measurementMoment.label).toEqual('At start of epoch 1');
          expect(measurementMoment.epochLabel).toEqual('Randomization');
          expect(measurementMoment.relativeToAnchor).toEqual('ontology:anchorEpochStart');
          expect(measurementMoment.offset).toEqual('PT0S');
          done();
        });
        rootScope.$digest();
      });
    });

    fdescribe('add measurement moments', function() {
      var newMoment = {
        label: 'At start of new epoch',
        epoch: {
          uri: 'http://trials.drugis.org/instances/bbb'
        },
        relativeToAnchor: 'ontology:anchorEpochStart',
        offset: 'PT0S'
      };

      beforeEach(function() {
        var graphJsonObject = [];
        var graphDefer = q.defer();
        var getGraphPromise = graphDefer.promise;
        graphDefer.resolve(graphJsonObject);
        studyService.getJsonGraph.and.returnValue(getGraphPromise);
      });

      it('should add the measurement', function(done) {
        measurementMomentService.addItem(newMoment).then(function() {
          measurementMomentService.queryItems().then(function(result) {
            expect(result.length).toBe(1);
            var measurementMoment = result[0];
            expect(measurementMoment.label).toEqual('At start of new epoch');
            expect(measurementMoment.epochLabel).toEqual('Randomization');
            expect(measurementMoment.relativeToAnchor).toEqual('ontology:anchorEpochStart');
            expect(measurementMoment.offset).toEqual('PT0S');
            done();
          });
        });
        rootScope.$digest();
      });
    });

    // describe('delete measurement moment', function() {
    //   beforeEach(function(done) {
    //     testUtils.loadTestGraph('queryMeasurementMomentsGraph.ttl', graphUri);
    //     testUtils.remoteStoreStubQuery(remotestoreServiceStub, graphUri, q);
    //     testUtils.remoteStoreStubUpdate(remotestoreServiceStub, graphUri, q);
    //     done();
    //   });

    //   it('should remove the measurementmoment', function(done) {
    //     var mmToRemove = {
    //       label: 'At start of new epoch',
    //       uri: 'http://trials.drugis.org/instances/2634b5b6-d557-4b38-8c48-ec08fa12d435'
    //     };
    //     measurementMomentService.deleteItem(mmToRemove).then(function() {
    //       measurementMomentService.queryItems().then(function(result) {
    //         expect(result.length).toBe(1);
    //         done();
    //       });
    //     });
    //     rootScope.$digest();
    //   });
    // });


  });
});