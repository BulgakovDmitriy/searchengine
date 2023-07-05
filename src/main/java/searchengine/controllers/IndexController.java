package searchengine.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import searchengine.services.StatisticsService;
import searchengine.statisticsDto.*;
import org.springframework.web.bind.annotation.*;
import searchengine.services.IndexingService;


@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Search engine API controller", description = "Whole site indexing, certain site reindexing, "
        + "stop indexing, search, site statistics")
public class IndexController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;


    public IndexController(StatisticsService statisticsService, IndexingService indexingService) {

        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get site statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    @Operation(summary = "Site indexing")
    public ResponseEntity<Object> startIndexing() {

        ResponseEntity <Object> response = indexingService.indexAllSites(indexingService.indexingAll());

        return response;
    }

    @GetMapping("/stopIndexing")
    @Operation(summary = "Stop indexing")
    public ResponseEntity<Object> stopIndexing() {

        ResponseEntity <Object> response = indexingService.indexingStop(indexingService.stopIndexing());

        return response;
    }

    @PostMapping("/indexPage")

    @Operation(summary = "Certain page indexing")
    public ResponseEntity<Object> indexPage(@RequestParam(name = "url") String url) {

        ResponseEntity <Object> response = indexingService.indexSite(url);
        return response;
    }
}

