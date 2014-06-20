package org.drugis.addis.models;

import org.drugis.addis.analyses.NetworkMetaAnalysis;
import org.drugis.addis.config.JpaRepositoryTestConfig;
import org.drugis.addis.models.repositories.ModelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {JpaRepositoryTestConfig.class})
public class JpaModelRepositoryTest {

  @PersistenceContext(unitName = "addisCore")
  EntityManager em;

  @Inject
  private ModelRepository modelRepository;

  @Test
  public void testCreate() throws Exception {
    Integer analysisId = -5; // from test-data/sql
    Model model = modelRepository.create(analysisId);
    assertEquals(analysisId, model.getAnalysisId());
    assertNotNull(model.getId());
  }

  @Test
  public void testGet() {
    Integer modelId = 1;
    Integer analysisId = -5;
    Model result = modelRepository.get(modelId);
    assertNotNull(result);
    assertEquals(analysisId, result.getAnalysisId());
  }

  @Test
  public void testFindByAnalysis() {
    NetworkMetaAnalysis networkMetaAnalysisWithModel = em.find(NetworkMetaAnalysis.class, -5);
    Model model = modelRepository.findByAnalysis(networkMetaAnalysisWithModel);
    assertNotNull(model);
    assertEquals(networkMetaAnalysisWithModel.getId(), model.getAnalysisId());

    NetworkMetaAnalysis networkMetaAnalysisWithWithOutModel = em.find(NetworkMetaAnalysis.class, -6);
    model = modelRepository.findByAnalysis(networkMetaAnalysisWithWithOutModel);
    assertNull(model);
  }
}