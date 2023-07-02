package searchengine.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import searchengine.repository.SiteRepository;
import searchengine.services.SearchService;
import searchengine.statisticsDto.BadRequest;
import searchengine.statisticsDto.SearchResults;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api")
@Slf4j
public class SearchController {


    private final SearchService searchService;
    private final SiteRepository siteRepository;

    public SearchController(SearchService searchService, SiteRepository siteRepository) {
        this.searchService = searchService;
        this.siteRepository = siteRepository;
    }

    @GetMapping("/search")
    @Operation(summary = "Search")
    public ResponseEntity<Object> search(@RequestParam(name = "query", required = false, defaultValue = "") String query,
                                         @RequestParam(name = "site", required = false, defaultValue = "") String site,
                                         @RequestParam(name = "offset", defaultValue = "0") int offset,
                                         @RequestParam(name = "limit", defaultValue = "20") int limit) {

        ResponseEntity<Object> response = searchService.search(query, site, offset, limit);

        return response;

    }
}
