package org.drugis.addis.models.service;

import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.models.*;
import org.drugis.addis.models.exceptions.InvalidHeterogeneityTypeException;
import org.drugis.addis.models.exceptions.InvalidModelTypeException;
import org.drugis.addis.models.repository.ModelRepository;
import org.drugis.addis.models.service.impl.ModelServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by daan on 24-6-14.
 */
public class ModelServiceTest {


  @Mock
  private ModelRepository modelRepository;

  @InjectMocks
  private ModelService modelService;


  @Before
  public void setUp() throws Exception {
    modelService = new ModelServiceImpl();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCreateNetwork() throws InvalidModelTypeException, ResourceDoesNotExistException, InvalidHeterogeneityTypeException {
    Integer analysisId = 55;
    String modelTitle = "model title";
    String linearModel = Model.LINEAR_MODEL_FIXED;
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    String likelihood = Model.LIKELIHOOD_BINOM;
    String link = Model.LINK_LOG;

    ModelTypeCommand modelTypeCommand = new ModelTypeCommand("network", null);
    HeterogeneityPriorCommand heterogeneityPriorCommand = new HeterogeneityPriorCommand(Model.AUTOMATIC_HETEROGENEITY_PRIOR_TYPE, null);

    ModelCommand modelCommand = new ModelCommand(modelTitle, linearModel, modelTypeCommand, heterogeneityPriorCommand, burnInIterations, inferenceIterations, thinningFactor, likelihood, link);
    Model expectedModel = mock(Model.class);

    Model internalModel = new Model.ModelBuilder()
            .analysisId(analysisId)
            .title(modelTitle)
            .linearModel(linearModel)
            .modelType(Model.NETWORK_MODEL_TYPE)
            .heterogeneityPriorType(Model.AUTOMATIC_HETEROGENEITY_PRIOR_TYPE)
            .burnInIterations(burnInIterations)
            .inferenceIterations(inferenceIterations)
            .thinningFactor(thinningFactor)
            .likelihood(likelihood)
            .link(link)
            .build();

    when(modelRepository.persist(internalModel)).thenReturn(expectedModel);
    Model createdModel = modelService.createModel(analysisId, modelCommand);

    assertEquals(expectedModel, createdModel);
    verify(modelRepository).persist(internalModel);
  }

  @Test
  public void testCreateHeterogeneityStdDevNetwork() throws InvalidModelTypeException, ResourceDoesNotExistException, InvalidHeterogeneityTypeException {
    Integer analysisId = 55;
    String modelTitle = "model title";
    String linearModel = Model.LINEAR_MODEL_FIXED;
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    String likelihood = Model.LIKELIHOOD_BINOM;
    String link = Model.LINK_LOG;

    ModelTypeCommand modelTypeCommand = new ModelTypeCommand("network", null);
    Double lower = 0.5;
    Double upper = 1.0;
    HeterogeneityValuesCommand values = new StdDevValuesCommand(lower, upper);
    HeterogeneityPriorCommand heterogeneityPriorCommand = new HeterogeneityPriorCommand(Model.STD_DEV_HETEROGENEITY_PRIOR_TYPE, values);

    ModelCommand modelCommand = new ModelCommand(modelTitle, linearModel, modelTypeCommand, heterogeneityPriorCommand, burnInIterations, inferenceIterations, thinningFactor, likelihood, link);
    Model expectedModel = mock(Model.class);

    Model internalModel = new Model.ModelBuilder()
        .analysisId(analysisId)
        .title(modelTitle)
        .linearModel(linearModel)
        .modelType(Model.NETWORK_MODEL_TYPE)
        .heterogeneityPriorType(Model.STD_DEV_HETEROGENEITY_PRIOR_TYPE)
        .lower(lower)
        .upper(upper)
        .burnInIterations(burnInIterations)
        .inferenceIterations(inferenceIterations)
        .thinningFactor(thinningFactor)
        .likelihood(likelihood)
        .link(link)
        .build();

    when(modelRepository.persist(internalModel)).thenReturn(expectedModel);
    Model createdModel = modelService.createModel(analysisId, modelCommand);

    assertEquals(expectedModel, createdModel);
    verify(modelRepository).persist(internalModel);
  }
  @Test
  public void testCreateHeterogeneityVarianceNetwork() throws InvalidModelTypeException, ResourceDoesNotExistException, InvalidHeterogeneityTypeException {
    Integer analysisId = 55;
    String modelTitle = "model title";
    String linearModel = Model.LINEAR_MODEL_FIXED;
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    String likelihood = Model.LIKELIHOOD_BINOM;
    String link = Model.LINK_LOG;

    ModelTypeCommand modelTypeCommand = new ModelTypeCommand("network", null);
    Double mean = 2.3;
    Double stdDev = 0.3;
    HeterogeneityValuesCommand values = new VarianceValuesCommand(mean, stdDev);
    HeterogeneityPriorCommand heterogeneityPriorCommand = new HeterogeneityPriorCommand(Model.VARIANCE_HETEROGENEITY_PRIOR_TYPE, values);

    ModelCommand modelCommand = new ModelCommand(modelTitle, linearModel, modelTypeCommand, heterogeneityPriorCommand, burnInIterations, inferenceIterations, thinningFactor, likelihood, link);
    Model expectedModel = mock(Model.class);

    Model internalModel = new Model.ModelBuilder()
        .analysisId(analysisId)
        .title(modelTitle)
        .linearModel(linearModel)
        .modelType(Model.NETWORK_MODEL_TYPE)
        .heterogeneityPriorType(Model.VARIANCE_HETEROGENEITY_PRIOR_TYPE)
        .mean(mean)
        .stdDev(stdDev)
        .burnInIterations(burnInIterations)
        .inferenceIterations(inferenceIterations)
        .thinningFactor(thinningFactor)
        .likelihood(likelihood)
        .link(link)
        .build();

    when(modelRepository.persist(internalModel)).thenReturn(expectedModel);
    Model createdModel = modelService.createModel(analysisId, modelCommand);

    assertEquals(expectedModel, createdModel);
    verify(modelRepository).persist(internalModel);
  }
  @Test
  public void testCreateHeterogeneityPrecisionNetwork() throws InvalidModelTypeException, ResourceDoesNotExistException, InvalidHeterogeneityTypeException {
    Integer analysisId = 55;
    String modelTitle = "model title";
    String linearModel = Model.LINEAR_MODEL_FIXED;
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    String likelihood = Model.LIKELIHOOD_BINOM;
    String link = Model.LINK_LOG;

    ModelTypeCommand modelTypeCommand = new ModelTypeCommand("network", null);
    Double rate = 0.9;
    Double shape = 1.3;
    HeterogeneityValuesCommand values = new PrecisionValuesCommand(rate, shape);
    HeterogeneityPriorCommand heterogeneityPriorCommand = new HeterogeneityPriorCommand(Model.PRECISION_HETEROGENEITY_PRIOR_TYPE, values);

    ModelCommand modelCommand = new ModelCommand(modelTitle, linearModel, modelTypeCommand, heterogeneityPriorCommand, burnInIterations, inferenceIterations, thinningFactor, likelihood, link);
    Model expectedModel = mock(Model.class);

    Model internalModel = new Model.ModelBuilder()
        .analysisId(analysisId)
        .title(modelTitle)
        .linearModel(linearModel)
        .modelType(Model.NETWORK_MODEL_TYPE)
        .heterogeneityPriorType(Model.PRECISION_HETEROGENEITY_PRIOR_TYPE)
        .rate(rate)
        .shape(shape)
        .burnInIterations(burnInIterations)
        .inferenceIterations(inferenceIterations)
        .thinningFactor(thinningFactor)
        .likelihood(likelihood)
        .link(link)
        .build();

    when(modelRepository.persist(internalModel)).thenReturn(expectedModel);
    Model createdModel = modelService.createModel(analysisId, modelCommand);

    assertEquals(expectedModel, createdModel);
    verify(modelRepository).persist(internalModel);
  }

  @Test
  public void testCreatePairwise() throws InvalidModelTypeException, ResourceDoesNotExistException, InvalidHeterogeneityTypeException {
    Integer analysisId = 55;
    String modelTitle = "model title";
    String linearModel = Model.LINEAR_MODEL_FIXED;
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    String likelihood = Model.LIKELIHOOD_BINOM;
    String link = Model.LINK_LOG;

    int fromId = -1;
    String fromName = "fromName";
    NodeCommand from = new NodeCommand(fromId, fromName);
    int toId = -2;
    String toName = "toName";
    NodeCommand to = new NodeCommand(toId, toName);
    DetailsCommand details = new DetailsCommand(from, to);
    ModelTypeCommand modelTypeCommand = new ModelTypeCommand("network", details);
    HeterogeneityPriorCommand heterogeneityPriorCommand = new HeterogeneityPriorCommand(Model.AUTOMATIC_HETEROGENEITY_PRIOR_TYPE, null);
    ModelCommand modelCommand = new ModelCommand(modelTitle, linearModel, modelTypeCommand, heterogeneityPriorCommand, burnInIterations, inferenceIterations, thinningFactor, likelihood, link);
    Model expectedModel = mock(Model.class);

    Model internalModel = new Model.ModelBuilder()
            .analysisId(analysisId)
            .title(modelTitle)
            .linearModel(linearModel)
            .modelType(Model.NETWORK_MODEL_TYPE)
            .heterogeneityPriorType(Model.AUTOMATIC_HETEROGENEITY_PRIOR_TYPE)
            .from(new Model.DetailNode(fromId, fromName))
            .to(new Model.DetailNode(toId, toName))
            .burnInIterations(burnInIterations)
            .inferenceIterations(inferenceIterations)
            .thinningFactor(thinningFactor)
            .likelihood(likelihood)
            .link(link)
            .build();

    when(modelRepository.persist(internalModel)).thenReturn(expectedModel);
    Model createdModel = modelService.createModel(analysisId, modelCommand);

    assertEquals(expectedModel, createdModel);
    verify(modelRepository).persist(internalModel);
  }


  @Test
  public void testQueryModelIsPresent() throws Exception, InvalidModelTypeException, InvalidHeterogeneityTypeException {
    Integer analysisId = -1;
    String modelTitle = "modelTitle";
    String linearModel = "fixed";
    String modelType = "network";
    Integer burnInIterations = 5000;
    Integer inferenceIterations = 20000;
    Integer thinningFactor = 10;
    Model model = new Model.ModelBuilder()
            .id(-10)
            .analysisId(analysisId)
            .title(modelTitle)
            .linearModel(linearModel)
            .modelType(Model.NETWORK_MODEL_TYPE)
            .heterogeneityPriorType(Model.AUTOMATIC_HETEROGENEITY_PRIOR_TYPE)
            .burnInIterations(burnInIterations)
            .inferenceIterations(inferenceIterations)
            .thinningFactor(thinningFactor)
            .build();

    List<Model> models = Collections.singletonList(model);
    when(modelRepository.findByAnalysis(analysisId)).thenReturn(models);
    List<Model> resultList = modelService.query(analysisId);
    assertEquals(1, resultList.size());
    assertEquals(models.get(0), resultList.get(0));
  }


  @Test
  public void testQueryModelIsNotPresent() throws Exception {
    Integer analysisId = -1;
    when(modelRepository.findByAnalysis(analysisId)).thenReturn(new ArrayList<Model>());
    List<Model> resultList = modelService.query(analysisId);
    assertEquals(0, resultList.size());
  }

}
