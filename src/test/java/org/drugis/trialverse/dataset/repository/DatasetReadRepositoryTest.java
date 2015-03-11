package org.drugis.trialverse.dataset.repository;


import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.rdf.model.Model;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.drugis.trialverse.dataset.factory.HttpClientFactory;
import org.drugis.trialverse.dataset.factory.JenaFactory;
import org.drugis.trialverse.dataset.model.VersionMapping;
import org.drugis.trialverse.dataset.repository.impl.DatasetReadRepositoryImpl;
import org.drugis.trialverse.security.Account;
import org.drugis.trialverse.util.Namespaces;
import org.drugis.trialverse.util.WebConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DatasetReadRepositoryTest {

    @Mock
    HttpClientFactory httpClientFactory;

    @Mock
    WebConstants webConstants;

    @Mock
    JenaFactory jenaFactory;

    @Mock
    VersionMappingRepository versionMappingRepository;

    @InjectMocks
    DatasetReadRepository datasetReadRepository;

    HttpClient mockHttpClient;
    HttpResponse mockResponse;

    @Before
    public void init() throws IOException {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);

        webConstants = mock(WebConstants.class);
        jenaFactory = mock(JenaFactory.class);

        datasetReadRepository = new DatasetReadRepositoryImpl();
        MockitoAnnotations.initMocks(this);

        when(webConstants.getTriplestoreBaseUri()).thenReturn("baseUri");
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(httpClientFactory.build()).thenReturn(mockHttpClient);
    }

    @Test
    public void testQueryDatasets() throws Exception {
        Account account = mock(Account.class);
        String user1 = "user1";
        when(account.getUsername()).thenReturn(user1);
        String datasetLocation = "http://localhost:8080/datasets/3df7cf37-a38a-49d8-ad7c-dfc71cef0509";
        String versionKey = "http://localhost:8080/versions/23ded5aa-1f3c-45e4-8449-a042ef0e9079";
        VersionMapping versionMapping = new VersionMapping(1, datasetLocation, user1, versionKey);
        String datasetLocation2 = "http://localhost:8080/datasets/e6633cb9-0b0b-4814-82cc-ce26a1242f2e";
        String versionKey2 = "http://localhost:8080/versions/144df61d-64dd-4ec9-9d69-f17bf690a733";
        VersionMapping versionMapping2 = new VersionMapping(2, datasetLocation2, user1, versionKey2 );
        List<VersionMapping> mockResult = Arrays.asList(versionMapping, versionMapping2);
        when(versionMappingRepository.findMappingsByUsername(user1)).thenReturn(mockResult);
        when(webConstants.getTriplestoreBaseUri()).thenReturn("http://localhost:8080/");

        Model model = datasetReadRepository.queryDatasets(account);

        assertNotNull(model);
    }

    @Test
    public void testGetDataset() {
        String datasetUUID = "uuid";
        DatasetAccessor accessor = mock(DatasetAccessor.class);
        Model mockModel = mock(Model.class);
        when(accessor.getModel(Namespaces.DATASET_NAMESPACE + datasetUUID)).thenReturn(mockModel);
        when(jenaFactory.getDatasetAccessor()).thenReturn(accessor);

        Model model = datasetReadRepository.getDataset(datasetUUID);

        assertEquals(mockModel, model);
        verify(jenaFactory).getDatasetAccessor();
    }

    @Test
    public void testQueryDatasetWithDetails() {
        Account account = mock(Account.class);
        when(account.getUsername()).thenReturn("pietje@precies.gov");
        Model model = datasetReadRepository.queryDatasets(account);

    }

    @Test
    public void testIsOwnerWhenQuerySaysTrue() throws IOException {
        String datasetUUID = "datasetUUID";
        Principal principal = mock(Principal.class);
        HttpResponse mockResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("boolean", true);
        when(mockResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(jsonObject.toJSONString()));
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        Boolean result = datasetReadRepository.isOwner(datasetUUID, principal);

        assertTrue(result);
    }

    @Test
    public void testIsOwnerWhenQuerySaysFalse() throws IOException {
        String datasetUUID = "datasetUUID";
        Principal principal = mock(Principal.class);
        HttpResponse mockResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("boolean", false);
        when(mockResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(jsonObject.toJSONString()));
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        Boolean result = datasetReadRepository.isOwner(datasetUUID, principal);

        assertFalse(result);
    }

    @Test
    public void testContainsStudyWithShortName() throws IOException {
        String datasetUUID = "uuid-1";
        String shortName = "shortName";

        InputStream stream = IOUtils.toInputStream("{\"boolean\":true}");
        when(mockResponse.getEntity().getContent()).thenReturn(stream);
        Boolean result = datasetReadRepository.containsStudyWithShortname(datasetUUID, shortName);
        assertTrue(result);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(jenaFactory);
    }


    /**
     * parses the response from the HttpResponse, use for debugging only !!!
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static String parseResponse(HttpResponse response) throws Exception {
        String result = null;
        BufferedReader reader = null;
        try {
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(response.getEntity().getContent())));
            } else {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            }
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }
}