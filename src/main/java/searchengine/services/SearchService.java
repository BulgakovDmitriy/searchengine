package searchengine.services;

import searchengine.statisticsDto.SearchResults;



public interface SearchService {
    SearchResults allSiteSearch(String text, int offset, int limit);
    SearchResults siteSearch(String searchText, String url, int offset, int limit);

}
