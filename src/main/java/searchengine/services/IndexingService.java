package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface IndexingService {
    boolean urlIndexing(String url);
    boolean indexingAll();
    boolean stopIndexing();

    ResponseEntity<Object> indexingStop(boolean stopIndexing);

    ResponseEntity<Object> indexAllSites(boolean indexingAll);

    ResponseEntity<Object> indexSite (String url);
}
