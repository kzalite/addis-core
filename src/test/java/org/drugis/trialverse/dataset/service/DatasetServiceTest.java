package org.drugis.trialverse.dataset.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.drugis.addis.security.Account;
import org.drugis.addis.security.repository.AccountRepository;
import org.drugis.trialverse.dataset.model.Dataset;
import org.drugis.trialverse.dataset.model.FeaturedDataset;
import org.drugis.trialverse.dataset.model.VersionMapping;
import org.drugis.trialverse.dataset.repository.DatasetReadRepository;
import org.drugis.trialverse.dataset.repository.FeaturedDatasetRepository;
import org.drugis.trialverse.dataset.repository.VersionMappingRepository;
import org.drugis.trialverse.dataset.service.impl.DatasetServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by connor on 9/8/15.
 */
public class DatasetServiceTest {

  @Mock
  private DatasetReadRepository datasetReadRepository;

  @Mock
  private VersionMappingRepository versionMappingRepository;

  @Mock
  private FeaturedDatasetRepository featuredDatasetRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private DatasetService datasetService;

  private Account account = new Account(1, "username", "John", "Lennon", "john@apple.co.uk");

  @Before
  public void setUp() {
    datasetService = new DatasetServiceImpl();
    initMocks(this);
  }

  @Test
  public void testFindDatasetsByUser() throws IOException {

    Model datasetsModel = getModel("mockDatasetsModel.ttl");

    VersionMapping versionMapping = new VersionMapping("versionDatasetUrl", "ownerUid", "http://trialverseDatasetUrl");
    List<VersionMapping> versionMappings = Collections.singletonList(versionMapping);
    when(versionMappingRepository.findMappingsByEmail(account.getEmail())).thenReturn(versionMappings);
    when(datasetReadRepository.queryDataset(versionMapping)).thenReturn(datasetsModel);

    List<Dataset> datasets = datasetService.findDatasets(account);

    assertEquals(1, datasets.size());
    assertEquals("dataset 1", datasets.get(0).getTitle());
  }


  @Test
  public void testDatasetsByUserMixedCase() throws IOException {
    Model datasetsModel = getModel("mockDatasetImported.ttl");

    VersionMapping versionMapping = new VersionMapping("versionDatasetUrl", "ownerUid", "http://trialverseDatasetUrl");
    List<VersionMapping> versionMappings = singletonList(versionMapping);
    when(versionMappingRepository.findMappingsByEmail(account.getEmail())).thenReturn(versionMappings);
    when(datasetReadRepository.queryDataset(versionMapping)).thenReturn(datasetsModel);

    List<Dataset> datasets = datasetService.findDatasets(account);

    assertEquals(1, datasets.size());
    assertEquals("Hansen (2005) anti-depressants review", datasets.get(0).getTitle());
  }


  @Test
  public void testFindFeatured() throws IOException {

    Model datasetsModel = getModel("mockDatasetsModel.ttl");
    Model datasetsModel2 = getModel("mockDatasetsModel2.ttl");

    VersionMapping versionMapping1 = new VersionMapping("versionDatasetUrl", "ownerUid", "http://trialverseDatasetUrl");
    VersionMapping versionMapping2 = new VersionMapping("versionDatasetUrl", "ownerUid", "http://trialverseDatasetUrl2");
    List<VersionMapping> versionMappings = Arrays.asList(versionMapping1, versionMapping2);
    when(featuredDatasetRepository.findAll()).thenReturn(Arrays.asList(new FeaturedDataset("http://trialverseDatasetUrl"), new FeaturedDataset("http://trialverseDatasetUrl2")));
    when(versionMappingRepository.findMappingsByTrialverseDatasetUrls(Arrays.asList(versionMapping1.getDatasetUrl(), versionMapping2.getDatasetUrl()))).thenReturn(versionMappings);
    when(accountRepository.findAccountByEmail("ownerUid")).thenReturn(account);
    when(datasetReadRepository.queryDataset(versionMapping1)).thenReturn(datasetsModel);
    when(datasetReadRepository.queryDataset(versionMapping2)).thenReturn(datasetsModel2);

    List<Dataset> datasets = datasetService.findFeatured();
    assertEquals(2, datasets.size());
  }

  private Model getModel(String ttlFileName) throws IOException {
    InputStream datasetsModelStream = new ClassPathResource(ttlFileName).getInputStream();
    Model datasetsModel = ModelFactory.createDefaultModel();
    datasetsModel.read(datasetsModelStream, null, "TTL");
    return datasetsModel;
  }

}
