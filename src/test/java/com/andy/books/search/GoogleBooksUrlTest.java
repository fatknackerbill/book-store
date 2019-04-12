package com.andy.books.search;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class GoogleBooksUrlTest {

    @Test
    public void shouldCreateEmptyQuery_whenNoQueryPassed() {
        URL theUrl = GoogleBooksUrl.createSearchQuery("");
        assertEquals("https://www.googleapis.com/books/v1/volumes?q=%22%22", theUrl.toString());
    }

    @Test
    public void shouldCreateEscapedBookSearchUrl_whenQueryContainsSpace() {
        URL theUrl = GoogleBooksUrl.createSearchQuery("clean code");
        assertEquals("https://www.googleapis.com/books/v1/volumes?q=clean+code", theUrl.toString());
    }

    @Test
    public void shouldCreateEscapedBookSearchUrl_whenQueryContainsQuotes() {
        URL theUrl = GoogleBooksUrl.createSearchQuery("\"something special\"");
        assertEquals("https://www.googleapis.com/books/v1/volumes?q=%22something+special%22", theUrl.toString());
    }

    @Test
    public void tryToBreakIt() {
        URL theUrl = GoogleBooksUrl.createSearchQuery("\"\"\"\n\n\n\n\n\"\"\"\n\n\n\n\n\"\"\"\n\n\n\n\n\"\"\"\n\n\n\n\n");
        assertEquals("https://www.googleapis.com/books/v1/volumes?q=%22%22%22%0A%0A%0A%0A%0A%22%22%22%0A%0A%0A%0A%0A%22%22%22%0A%0A%0A%0A%0A%22%22%22%0A%0A%0A%0A%0A", theUrl.toString());
    }

}