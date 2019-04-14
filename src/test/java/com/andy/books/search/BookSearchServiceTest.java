package com.andy.books.search;

import com.andy.books.BookSearchException;
import com.andy.books.SearchResult;
import com.andy.books.SearchResults;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private BookSearchService service;

    @Mock
    private HttpConnector httpConnector;

    @Before
    public void setup() {
        service = new BookSearchService(httpConnector);
    }

    @Test(expected=BookSearchException.class)
    public void shouldThrowException_whenHttpConnectorFails() throws MalformedURLException {
        when(httpConnector.get(new URL("https://www.googleapis.com/books/v1/volumes?q=legacy+code"))).thenThrow(BookSearchException.class);
        service.searchForIt("legacy code", "0");
    }

    @Test
    public void shouldSearchForLegacyCode() throws Exception {
        when(httpConnector.get(new URL("https://www.googleapis.com/books/v1/volumes?q=legacy+code"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.searchForIt("legacy code", "0");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shouldSearchForCleanCode() throws Exception {
        when(httpConnector.get(new URL("https://www.googleapis.com/books/v1/volumes?q=clean+code"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.searchForIt("clean code", "0");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shouldSearchForCleanCode_andGoToPage4() throws Exception {
        when(httpConnector.get(new URL("https://www.googleapis.com/books/v1/volumes?q=clean+code&startIndex=4"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.searchForIt("clean code", "4");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shoulldIgnoreDodgyPageNumber_whenDodgy() throws Exception {
        when(httpConnector.get(new URL("https://www.googleapis.com/books/v1/volumes?q=clean+code"))).thenReturn(loadCannedJson());

        SearchResults searchResults = service.searchForIt("clean code", "well dodgy");

        verifyWeGetBackCannedData(searchResults);
    }

    @Test
    public void shouldCreateEmptyString_whenNoAuthors() {
        JSONObject volumeInfo = new JSONObject();
        volumeInfo.put("title", "BDD In Action");

        String delimitedString = service.getAuthorsOrEmptyString(volumeInfo);

        assertEquals("", delimitedString);
    }

    @Test
    public void shouldCreateCommaDelimtedAuthors_whenOneAuthor() {
        JSONArray authors = new JSONArray();
        authors.put("Uncle Bob");
        JSONObject volumeInfo = new JSONObject();
        volumeInfo.put("authors", authors);

        String delimitedString = service.getAuthorsOrEmptyString(volumeInfo);

        assertEquals("Uncle Bob", delimitedString);
    }

    @Test
    public void shouldCreateCommaDelimtedAuthors_whenMoreThanOneAuthor() {
        JSONArray authors = new JSONArray();
        authors.put("Erich Gamma");
        authors.put("Richard Helm");
        authors.put("Ralph Johnson");
        authors.put("John Vlissides");
        JSONObject volumeInfo = new JSONObject();
        volumeInfo.put("authors", authors);

        String delimitedString = service.getAuthorsOrEmptyString(volumeInfo);

        assertEquals("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", delimitedString);
    }

    private void verifyWeGetBackCannedData(SearchResults searchResults) {
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

    private String loadCannedJson() throws URISyntaxException, IOException {
        URL resource = getClass().getResource("/legacy_code.json");
        FileInputStream fileInputStream = new FileInputStream(new File(resource.toURI()));
        return IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());
    }
}
