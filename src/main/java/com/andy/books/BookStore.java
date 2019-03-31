package com.andy.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.util.Map;

@Controller
@SpringBootApplication
public class BookStore {

  private final SearchService searchService;
  @Value("${spring.datasource.url}")
  private String dbUrl;

  public BookStore(SearchService searchService) {
    this.searchService = searchService;
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(BookStore.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }


  @RequestMapping("/hello")
  String hello(Map<String, Object> model) {
    model.put("science", "Hello from Andy's book store");
    return "hello";
  }

  @RequestMapping("/search")
  public ModelAndView search(@RequestParam(value="q", defaultValue="") String query) {
    if (query.isEmpty())
      return new ModelAndView("index");

    searchService.search(query);

    return new ModelAndView("searchResults");

  }
}
