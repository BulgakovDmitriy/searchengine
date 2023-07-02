package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface IndexingService {
    boolean urlIndexing(String url);
    boolean indexingAll();
    boolean stopIndexing();

    ResponseEntity<Object> indexSite (String url);
}
