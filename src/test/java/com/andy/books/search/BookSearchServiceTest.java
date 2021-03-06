package com.andy.books.search;

import com.andy.books.BookSearchException;
import com.andy.books.SearchResult;
import com.andy.books.SearchResults;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookSearchServiceTest {

    private static final String VOLUMES_URL = "https://www.googleapis.com/books/v1/volumes";
    private BookSearchService service;

    @Mock
    private HttpConnector httpConnector;

    @Before
    public void setup() {
        service = new BookSearchService(httpConnector);
    }

    @Test(expected=BookSearchException.class)
    public void shouldThrowException_whenHttpConnectorFails() throws MalformedURLException {
        when(httpConnector.get(new URL(VOLUMES_URL + "?q=legacy+code"))).thenThrow(BookSearchException.class);
        service.search("legacy code", "0");
    }

    @Test
    public void shouldSearchForLegacyCode() throws Exception {
        when(httpConnector.get(new URL(VOLUMES_URL + "?q=legacy+code"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.search("legacy code", "0");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shouldSearchForCleanCode_andGoToPage4() throws Exception {
        when(httpConnector.get(new URL(VOLUMES_URL + "?q=clean+code&startIndex=4"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.search("clean code", "4");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shoulldIgnoreDodgyPageNumber_whenDodgy() throws Exception {
        when(httpConnector.get(new URL(VOLUMES_URL + "?q=clean+code"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.search("clean code", "well dodgy");

        verifyWeGetBackCannedData(searchResults);
    }

    private String loadCannedJson() throws URISyntaxException, IOException {
        URL resource = getClass().getResource("/legacy_code.json");
        FileInputStream fileInputStream = new FileInputStream(new File(resource.toURI()));
        return IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());
    }

    private void verifyWeGetBackCannedData(SearchResults searchResults) {
        assertEquals(1441, searchResults.getTotalItems());
        List<SearchResult> searchResults1 = searchResults.getSearchResults();
        assertNotNull(searchResults1);
        assertEquals(10, searchResults1.size());
        SearchResult searchResult1 = searchResults1.get(0);

        assertEquals("Working Effectively with Legacy Code", searchResult1.getTitle());
        assertEquals("Michael Feathers", searchResult1.getAuthor());
        assertEquals("Prentice Hall Professional", searchResult1.getPublisher());
        assertEquals("http://books.google.com/books/content?id=fB6s_Z6g0gIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                searchResult1.getThumbnail());
        assertEquals("https://play.google.com/store/books/details?id=fB6s_Z6g0gIC&source=gbs_api",
                searchResult1.getLink());

        SearchResult searchResult2 = searchResults1.get(1);
        assertEquals("Working Effectively with Legacy Code", searchResult2.getTitle());
        assertEquals("Michael C. Feathers", searchResult2.getAuthor());
        assertEquals("Prentice Hall", searchResult2.getPublisher());
        assertEquals("http://books.google.com/books/content?id=vlo_nWophSYC&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                searchResult2.getThumbnail());
        assertEquals("http://books.google.co.uk/books?id=vlo_nWophSYC&dq=legacy+code&hl=&source=gbs_api",
                searchResult2.getLink());
    }
}
