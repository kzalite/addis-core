package org.drugis.addis;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.ParseException;
import org.drugis.addis.config.TrialverseServiceIntegrationTestConfig;
import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.trialverse.model.*;
import org.drugis.addis.trialverse.model.emun.StudyDataSection;
import org.drugis.addis.trialverse.service.TriplestoreService;
import org.drugis.addis.trialverse.service.impl.TriplestoreServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by connor on 16-4-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TrialverseServiceIntegrationTestConfig.class})
public class TripleStoreServiceIT {

  @Inject
  TriplestoreService triplestoreService;

  private final String testNamespaceUid = "3e6a82b0-582e-4c5d-af93-f1a46a220035";
  private final String testVersionHead = "http://localhost:8080/versions/c585694a-a896-41cb-b93f-36cbbddc5c1a";

  private String nameSpaceUid = "c41d402e-6762-4221-b040-5a244b2aba3f";
  private final String tak491019 = "4d4b11f1-5bfd-4b45-a42b-04b71cc47b01"; //TAK491-019 / NCT00696436
  private final String tak491301 = "521121d5-ccda-4c67-96b7-2b1b41142e99"; //TAK491-301 / NCT00846365
  private final String tak491008 = "dfe24960-8e9b-4a3a-b1b8-d644bb7a7b1c"; //TAK491-008 / NCT00696241
  private final String nonSAE = "7668ae03-fc7d-4d3e-a98e-a66e24247572";
  private final String sbpMeanTroughSitting = "e6bb1301-c130-46c6-ad9c-d4c89a3fbecc";
  private final String azilsartan = "40e4b07f-026d-4f5d-a4c1-54d24917b194";
  private final String placebo = "47cd2e00-a967-4c24-b798-b2d5c37d9553";
  private final String ramipril = "7f0beb7b-87cc-4f9f-8e78-f63aec4676ee";
  private final String spugen = "18bd5e16-5254-4e8d-a171-753d01d22fd4";
  private final String version = "http://localhost:8080/versions/0f85b12d-73c4-4d34-a1ce-9b9d5122dab4";

  @Test
  public void testGetTreatmentActivitiesFixedDose() {

    List<TreatmentActivity> studyTreatmentActivities = triplestoreService.getStudyTreatmentActivities(nameSpaceUid, tak491019);
    assertEquals(6, studyTreatmentActivities.size());
    TreatmentActivity treatmentActivity = studyTreatmentActivities.get(1);
    assertEquals(1, treatmentActivity.getAdministeredDrugs().size());
  }

  @Test
  public void testGetTreatmentActivitiesFlexDose() {
    List<TreatmentActivity> studyTreatmentActivities = triplestoreService.getStudyTreatmentActivities(nameSpaceUid, tak491019);
    assertEquals(6, studyTreatmentActivities.size());
    TreatmentActivity treatmentActivity = studyTreatmentActivities.get(1);
    assertEquals(1, treatmentActivity.getAdministeredDrugs().size());
  }

  @Test
  public void testGetTreatmentActivitiesMultidrugArms() {
    List<TreatmentActivity> studyTreatmentActivities = triplestoreService.getStudyTreatmentActivities(nameSpaceUid, tak491301);
    assertEquals(4, studyTreatmentActivities.size());
    TreatmentActivity treatmentActivity = studyTreatmentActivities.get(0);
    assertEquals(2, treatmentActivity.getAdministeredDrugs().size());
  }

  @Test
  public void testGetPopulationCharacteristics() {
    List<StudyData> studyData = triplestoreService.getStudyData(nameSpaceUid, tak491008, StudyDataSection.BASE_LINE_CHARACTERISTICS);
    assertEquals(2, studyData.size());
  }

  @Test
  public void testGetOutcomes() {
    List<SemanticOutcome> outcomes = triplestoreService.getOutcomes(nameSpaceUid, version);
    assertEquals(95, outcomes.size());
  }

  @Test
  public void testGetOutcomesTestStudy() {
    List<SemanticOutcome> outcomes = triplestoreService.getOutcomes(testNamespaceUid, testVersionHead);
    assertEquals(3, outcomes.size());

  }

  @Test
  public void testGetInterventions() {
    List<SemanticIntervention> interventions = triplestoreService.getInterventions(nameSpaceUid, version);
    assertEquals(7, interventions.size());
  }

  @Test
  public void testGetInterventionsTestStudy() {
    List<SemanticIntervention> interventions = triplestoreService.getInterventions(testNamespaceUid, testVersionHead);
    assertEquals(2, interventions.size());
  }


  @Test
  public void testGetStudies() {
    List<Study> studies = triplestoreService.queryStudies(nameSpaceUid, version);
    assertEquals(5, studies.size());
    assertEquals(59, studies.get(1).getOutcomeUids().size());
    assertTrue(studies.get(1).getOutcomeUids().get(0).startsWith("http://trials.drugis.org/entities/"));
  }

  @Test
  public void testGetSingleStudyMeasurements() {
    List<String> outcomeUids = Arrays.asList(sbpMeanTroughSitting, nonSAE);
    List<String> interventionUids = Arrays.asList(azilsartan, placebo);
    List<TriplestoreServiceImpl.SingleStudyBenefitRiskMeasurementRow> singleStudyMeasurements
            = triplestoreService.getSingleStudyMeasurements(nameSpaceUid, tak491008, version, outcomeUids, interventionUids);
    assertEquals(8, singleStudyMeasurements.size());
    assertFalse(singleStudyMeasurements.get(0).getAlternativeUid().startsWith("ontology"));
  }

  @Test
  public void testGetSingleStudyMeasurementsteststudy() {
    String testStudy = "0a333700-89b0-49fd-a423-37a874ff1e6b";
    String var1 = "4cf45ac4-e287-425a-b838-7b2a2ce0d8b7";
    String var2 = "f7ea96f9-029d-4d4d-8a78-ac4c6cc9b464";
    String var3 = "448daf84-d548-48f0-8660-8b848d9edaf2";
    List<String> outcomeUids = Arrays.asList(var1, var2, var3);
    String drug1 = "32398992-dcfb-4b23-9d49-7ffbf3c4c8ff";
    String drug2 = "84260271-5b92-4b89-a714-da982077c383";
    List<String> interventionUids = Arrays.asList(drug1, drug2);

    List<TriplestoreServiceImpl.SingleStudyBenefitRiskMeasurementRow> singleStudyMeasurements
            = triplestoreService.getSingleStudyMeasurements(testNamespaceUid, testStudy, testVersionHead, outcomeUids, interventionUids);

    assertEquals(2, singleStudyMeasurements.size());
    assertFalse(singleStudyMeasurements.get(0).getAlternativeUid().startsWith("ontology"));
  }

  @Test
  public void testGetTrialData() {
    List<String> interventionUids = Arrays.asList(azilsartan, placebo);
    List<TrialDataStudy> trialData = triplestoreService.getTrialData(nameSpaceUid, version, nonSAE, interventionUids);
    assertEquals(4, trialData.size());
  }

  @Test
  public void testNamespaceGet() {
    Namespace namespace = triplestoreService.getNamespaceHead(nameSpaceUid);
    assertNotNull(namespace);
    assertEquals("EDARBI", namespace.getName());
    assertEquals("The EDARBI dataset", namespace.getDescription());
    assertEquals(new Integer(5), namespace.getNumberOfStudies());
    assertEquals("http://localhost:8080/versions/0f85b12d-73c4-4d34-a1ce-9b9d5122dab4", namespace.getVersion());
  }

  @Test
  public void testQueryNamespaces() throws ParseException {
    Collection<Namespace> namespaces = triplestoreService.queryNameSpaces();
    assertEquals(5, namespaces.size());
  }
  
  @Test
  public void getStudyDetailsTest() throws ResourceDoesNotExistException {
    StudyWithDetails studydetails = triplestoreService.getStudydetails(nameSpaceUid, tak491008);
    assertEquals("TAK491-008 / NCT00696241", studydetails.getName());
    assertEquals(new Integer(1275), studydetails.getStudySize());
    assertEquals("Azilsartan, Olmesartan, Placebo", studydetails.getInvestigationalDrugNames());
    assertEquals(new Integer(5), studydetails.getNumberOfArms());
  }

  @Test
  public void testGetEpochs() {
    JSONArray studyEpochs = triplestoreService.getStudyEpochs(nameSpaceUid, tak491008);
    assertEquals(2, studyEpochs.size());
  }

  @Test
  public void testGetArms() {
    JSONArray studyArms = triplestoreService.getStudyArms(nameSpaceUid, tak491008);
    assertEquals(5, studyArms.size());
  }

  @Test
  public void testGetStudiesWithDetails() {
    List<StudyWithDetails> studyWithDetailses = triplestoreService.queryStudydetailsHead(nameSpaceUid);
    assertEquals(5, studyWithDetailses.size());
  }

// @Test
// public  void testGetSingleStudyMeasurements() {
//   String namespaceUid = "namespaceUid";
//   String studyUid = "studUid";
//   String version = "verrsion";
//   List<String> outcomeUids = Arrays.asList();
//   List<String> alternativeUids = Arrays.asList();
//
//   List<TriplestoreServiceImpl.SingleStudyBenefitRiskMeasurementRow> rows = triplestoreService.getSingleStudyMeasurements(namespaceUid, studyUid, version, outcomeUids, alternativeUids);
//   assertNotNull(rows.get(0));
// }
}
