package org.drugis.addis.analyses.service.impl;

import org.apache.commons.lang.NotImplementedException;
import org.drugis.addis.analyses.*;
import org.drugis.addis.analyses.repository.AnalysisRepository;
import org.drugis.addis.analyses.repository.MetaBenefitRiskAnalysisRepository;
import org.drugis.addis.analyses.repository.NetworkMetaAnalysisRepository;
import org.drugis.addis.analyses.repository.SingleStudyBenefitRiskAnalysisRepository;
import org.drugis.addis.analyses.service.AnalysisService;
import org.drugis.addis.covariates.Covariate;
import org.drugis.addis.covariates.CovariateRepository;
import org.drugis.addis.exception.MethodNotAllowedException;
import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.interventions.model.AbstractIntervention;
import org.drugis.addis.interventions.model.CombinationIntervention;
import org.drugis.addis.interventions.model.InterventionSet;
import org.drugis.addis.interventions.model.SingleIntervention;
import org.drugis.addis.interventions.repository.InterventionRepository;
import org.drugis.addis.interventions.service.InterventionService;
import org.drugis.addis.models.Model;
import org.drugis.addis.models.service.ModelService;
import org.drugis.addis.outcomes.Outcome;
import org.drugis.addis.outcomes.repository.OutcomeRepository;
import org.drugis.addis.projects.Project;
import org.drugis.addis.projects.repository.ProjectRepository;
import org.drugis.addis.projects.service.ProjectService;
import org.drugis.addis.security.Account;
import org.drugis.addis.trialverse.model.trialdata.AbstractSemanticIntervention;
import org.drugis.addis.trialverse.model.trialdata.TrialDataStudy;
import org.drugis.addis.trialverse.service.MappingService;
import org.drugis.addis.trialverse.service.TriplestoreService;
import org.drugis.addis.trialverse.service.impl.ReadValueException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by daan on 22-5-14.
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

  @Inject
  private AnalysisRepository analysisRepository;

  @Inject
  private NetworkMetaAnalysisRepository networkMetaAnalysisRepository;

  @Inject
  private SingleStudyBenefitRiskAnalysisRepository singleStudyBenefitRiskAnalysisRepository;

  @Inject
  private MetaBenefitRiskAnalysisRepository metaBenefitRiskAnalysisRepository;

  @Inject
  private ProjectService projectService;

  @Inject
  private ModelService modelService;

  @Inject
  private OutcomeRepository outcomeRepository;

  @Inject
  private InterventionRepository interventionRepository;

  @Inject
  private TriplestoreService triplestoreService;

  @Inject
  private ProjectRepository projectRepository;

  @Inject
  private MappingService mappingService;

  @Inject
  private CovariateRepository covariateRepository;

  @Inject
  private InterventionService interventionService;

  @Override
  public void checkCoordinates(Integer projectId, Integer analysisId) throws ResourceDoesNotExistException {
    AbstractAnalysis analysis = analysisRepository.get(analysisId);
    if (!analysis.getProjectId().equals(projectId)) {
      throw new ResourceDoesNotExistException();
    }
  }

  @Override
  public NetworkMetaAnalysis updateNetworkMetaAnalysis(Account user, NetworkMetaAnalysis analysis) throws ResourceDoesNotExistException, MethodNotAllowedException, SQLException, IOException {
    projectService.checkProjectExistsAndModifiable(user, analysis.getProjectId());
    checkProjectIdChange(analysis);

    if (!modelService.findByAnalysis(analysis.getId()).isEmpty()) {
      // can not update locked exception
      throw new MethodNotAllowedException();
    }

    // do not allow selection of outcome that is not in the project
    if (analysis.getOutcome() != null && !analysis.getOutcome().getProject().equals(analysis.getProjectId())) {
      throw new ResourceDoesNotExistException();
    }

    return networkMetaAnalysisRepository.update(analysis);
  }

  @Override
  public List<MbrOutcomeInclusion> buildInitialOutcomeInclusions(Integer projectId, Integer metabenefitRiskAnalysisId) throws SQLException, IOException {
    Collection<Outcome> outcomes = outcomeRepository.query(projectId);
    List<Integer> outcomeIds = outcomes.stream()
            .map(Outcome::getId)
            .collect(Collectors.toList());
    List<NetworkMetaAnalysis> networkMetaAnalyses = networkMetaAnalysisRepository.queryByOutcomes(projectId, outcomeIds);
    List<Model> models = modelService.findNetworkModelsByProject(projectId);
    return outcomes.stream()
            .filter(o -> findValidNetworkMetaAnalysis(networkMetaAnalyses, models, o).isPresent())
            .map(o -> {
              //noinspection ConstantConditions -- already checked in filter
              NetworkMetaAnalysis validNma = findValidNetworkMetaAnalysis(networkMetaAnalyses, models, o).get();
              return new MbrOutcomeInclusion(metabenefitRiskAnalysisId, o.getId(), validNma.getId(), selectModelId(validNma, models));
            })
            .collect(Collectors.toList());
  }

  private Optional<NetworkMetaAnalysis> findValidNetworkMetaAnalysis(List<NetworkMetaAnalysis> networkMetaAnalyses, List<Model> models, Outcome o) {
    return networkMetaAnalyses
            .stream()
            .filter(nma -> nma.getOutcome() != null && Objects.equals(nma.getOutcome().getId(), o.getId()))
            .filter(nma -> analysisHasModel(models, nma))
            .findFirst();
  }

  private Integer selectModelId(NetworkMetaAnalysis networkMetaAnalysis, List<Model> consistencyModels) {

    List<Model> analysisModels = consistencyModels.stream()
        .filter(model -> !model.getArchived() && model.getAnalysisId().equals(networkMetaAnalysis.getId()))
        .collect(Collectors.toList());
    if (networkMetaAnalysis.getPrimaryModel() != null) {
      Optional<Model> primaryModel = analysisModels.stream()
              .filter(m -> m.getId().equals(networkMetaAnalysis.getPrimaryModel()))
              .findFirst();
      return primaryModel.orElse(null).getId();
    } else {
      return analysisModels.stream()
              .sorted(Comparator.comparing(Model::getTitle))
              .findFirst().orElse(null).getId();
    }
  }

  private boolean analysisHasModel(List<Model> models, NetworkMetaAnalysis nma) {
    return models
            .stream()
            .anyMatch(m -> m.getAnalysisId().equals(nma.getId()) && ! m.getArchived());
  }

  @Override
  public NetworkMetaAnalysis createNetworkMetaAnalysis(Account user, AnalysisCommand analysisCommand) throws ResourceDoesNotExistException, MethodNotAllowedException {
    projectService.checkProjectExistsAndModifiable(user, analysisCommand.getProjectId());
    return networkMetaAnalysisRepository.create(analysisCommand);
  }

  @Override
  public SingleStudyBenefitRiskAnalysis createSingleStudyBenefitRiskAnalysis(Account user, AnalysisCommand analysisCommand) throws ResourceDoesNotExistException, MethodNotAllowedException {
    projectService.checkProjectExistsAndModifiable(user, analysisCommand.getProjectId());
    return singleStudyBenefitRiskAnalysisRepository.create(analysisCommand);
  }

  @Override
  public MetaBenefitRiskAnalysis createMetaBenefitRiskAnalysis(Account user, AnalysisCommand analysisCommand) throws MethodNotAllowedException, SQLException, IOException, ResourceDoesNotExistException {
    projectService.checkProjectExistsAndModifiable(user, analysisCommand.getProjectId());
    return metaBenefitRiskAnalysisRepository.create(user, analysisCommand);
  }

  @Override
  public void checkProjectIdChange(AbstractAnalysis analysis) throws ResourceDoesNotExistException, MethodNotAllowedException {
    // do not allow changing of project ID
    AbstractAnalysis oldAnalysis = analysisRepository.get(analysis.getId());
    if (!oldAnalysis.getProjectId().equals(analysis.getProjectId())) {
      throw new ResourceDoesNotExistException();
    }
  }

  @Override
  public Map<URI, TrialDataStudy> matchInterventions(Map<URI, TrialDataStudy> studyData, List<AbstractSemanticIntervention> interventions) {
    return null;
  }

  @Override
  public Set<AbstractIntervention> getIncludedInterventions(AbstractAnalysis analysis) throws ResourceDoesNotExistException {
    List<Integer> interventionInclusionsIds = analysis.getInterventionInclusions().stream()
            .map(InterventionInclusion::getInterventionId)
            .collect(Collectors.toList());
    return interventionRepository.query(analysis.getProjectId()).stream()
            .filter(i -> interventionInclusionsIds.contains(i.getId()))
            .collect(Collectors.toSet());
  }

  private List<Covariate> getIncludedCovariates(NetworkMetaAnalysis analysis) throws ResourceDoesNotExistException {
    List<Integer> includedCovariates = analysis.getCovariateInclusions().stream()
            .map(CovariateInclusion::getCovariateId)
            .collect(Collectors.toList());
    return covariateRepository.findByProject(analysis.getProjectId()).stream()
            .filter(i -> includedCovariates.contains(i.getId()))
            .collect(Collectors.toList());
  }

  @Override
  public List<TrialDataStudy> buildEvidenceTable(Integer projectId, Integer analysisId) throws ResourceDoesNotExistException, ReadValueException, URISyntaxException {
    Project project = projectRepository.get(projectId);
    AbstractAnalysis analysis = analysisRepository.get(analysisId);

    // Interventions
    Set<AbstractIntervention> includedInterventions = getIncludedInterventions(analysis);
    Set<SingleIntervention> singleInterventions = getSingleInterventions(includedInterventions);
    Set<URI> includedInterventionUris = singleInterventions.stream()
            .map(SingleIntervention::getSemanticInterventionUri)
            .collect(Collectors.toSet());

    List<TrialDataStudy> trialData = Collections.emptyList();
    String namespaceUid = mappingService.getVersionedUuid(project.getNamespaceUid());
    URI datasetVersion = project.getDatasetVersion();

    if (analysis instanceof NetworkMetaAnalysis) {
      NetworkMetaAnalysis networkMetaAnalysis = (NetworkMetaAnalysis) analysis;
      if (networkMetaAnalysis.getOutcome() == null) {
        // no outcome set, therefore no need to build a evidence table
        return trialData;
      }

      // Covariates
      Set<String> includedCovariates = getIncludedCovariates(networkMetaAnalysis).stream()
              .map(Covariate::getDefinitionKey)
              .collect(Collectors.toSet());

      trialData = triplestoreService.getNetworkData(namespaceUid, datasetVersion,
              networkMetaAnalysis.getOutcome().getSemanticOutcomeUri(), includedInterventionUris, includedCovariates);

    } else if (analysis instanceof SingleStudyBenefitRiskAnalysis) {
      SingleStudyBenefitRiskAnalysis singleStudyBenefitRiskAnalysis = (SingleStudyBenefitRiskAnalysis) analysis;

      Set<URI> outcomeUris = singleStudyBenefitRiskAnalysis.getSelectedOutcomes().stream().map(Outcome::getSemanticOutcomeUri).collect(Collectors.toSet());

      trialData = triplestoreService.getSingleStudyData(namespaceUid,
              singleStudyBenefitRiskAnalysis.getStudyGraphUri(), datasetVersion, outcomeUris, includedInterventionUris);

    } else {
      throw new NotImplementedException("not yet implemented for other analysis types");
    }

    // add matching data;
    trialData = triplestoreService.addMatchingInformation(includedInterventions, trialData);

    // filter studies that don't have any matched interventions on any arms
    trialData = trialData.stream().filter(trialDataStudy -> {
      Integer nMatchedInterventions = trialDataStudy.getTrialDataArms().stream()
              .mapToInt(a -> a.getMatchedProjectInterventionIds().size())
              .sum();
      return nMatchedInterventions > 0;
    }).collect(Collectors.toList());
    return trialData;
  }

  @Override
  public Set<SingleIntervention> getSingleInterventions(Set<AbstractIntervention> includedInterventions) throws ResourceDoesNotExistException {
    Set<SingleIntervention> singleInterventions = includedInterventions.stream()
            .filter(ai -> ai instanceof SingleIntervention)
            .map(ai -> (SingleIntervention) ai)
            .collect(Collectors.toSet());

    List<CombinationIntervention> combinationInterventions = includedInterventions.stream()
            .filter(ai -> ai instanceof CombinationIntervention)
            .map(ai -> (CombinationIntervention) ai)
            .collect(Collectors.toList());

    List<InterventionSet> interventionSets = includedInterventions.stream()
            .filter(ai -> ai instanceof InterventionSet)
            .map(ai -> (InterventionSet) ai)
            .collect(Collectors.toList());

    singleInterventions.addAll(interventionService.resolveCombinations(combinationInterventions));
    singleInterventions.addAll(interventionService.resolveInterventionSets(interventionSets));
    return singleInterventions;
  }
}
