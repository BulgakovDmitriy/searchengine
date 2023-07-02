package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.statisticsDto.BadRequest;
import searchengine.statisticsDto.SearchResults;
import searchengine.statisticsDto.StatisticsSearch;
import searchengine.model.IndexSearch;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SitePage;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.SearchService;
import searchengine.utilites.CleanHtmlCode;
import searchengine.morphology.Morphology;
import searchengine.repository.IndexSearchRepository;
import searchengine.repository.LemmaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final Morphology morphology;
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final IndexSearchRepository indexSearchRepository;
    private final SiteRepository siteRepository;


    @Override
    public SearchResults allSiteSearch(String searchText, int offset, int limit) {
        log.info("Getting results of the search \"" + searchText + "\"");
        List<SitePage> siteList = siteRepository.findAll();
        List<StatisticsSearch> data;
        List<String> textLemmaList = getLemmaFromSearchText(searchText);

        List<Lemma> foundLemmaList = getLemmasFromSiteList(siteList, textLemmaList);

        if (foundLemmaList.isEmpty()) {
            data = getSearchDtoList(foundLemmaList, textLemmaList, offset, limit, searchText);
            return new SearchResults(true, data.size(), data);
        }

        List<StatisticsSearch> searchData = getSearchDtoList(foundLemmaList, textLemmaList, offset, limit, searchText);
        SearchResults search = new SearchResults(true, searchData.size(), searchData);
        log.info("Search done. Got results.");
        return search;
    }

    private List<Lemma> getLemmasFromSiteList(List<SitePage> siteList, List<String> textLemmaList) {
        List<Lemma> foundLemmaList = new ArrayList<>();
        for (SitePage site : siteList) {
            foundLemmaList.addAll(getLemmaListFromSite(textLemmaList, site));
        }
        return foundLemmaList;
    }

    @Override
    public SearchResults siteSearch(String searchText, String url, int offset, int limit) {
        log.info("Searching for \"" + searchText + "\" in - " + url);
        SitePage site = siteRepository.findByUrl(url);
        List<String> textLemmaList = getLemmaFromSearchText(searchText);
        List<Lemma> foundLemmaList = getLemmaListFromSite(textLemmaList, site);
        log.info("Search done. Got results.");
        List<StatisticsSearch> data = getSearchDtoList(foundLemmaList, textLemmaList, offset, limit, searchText);
        return new SearchResults(true, data.size(), data);
    }

    @Override
    public ResponseEntity<Object> search(String searchText, String url, int offset, int limit) {

        SearchResults searchResults;

        if (searchText.isEmpty()) {
            return new ResponseEntity<>(new BadRequest(false, "Empty request"), HttpStatus.BAD_REQUEST);
        } else {
            if (!url.isEmpty()) {
                if (siteRepository.findByUrl(url) == null) {
                    return new ResponseEntity<>(new BadRequest(false, "Required page not found"), HttpStatus.BAD_REQUEST);
                } else {
                    searchResults = siteSearch(searchText, url, offset, limit);
                }
            } else {
                searchResults = allSiteSearch(searchText, offset, limit);
            }
            return new ResponseEntity<>(searchResults, HttpStatus.OK);
        }
    }

    private List<String> getLemmaFromSearchText(String searchText) {
        String[] words = searchText.toLowerCase(Locale.ROOT).split(" ");
        List<String> lemmaList = new ArrayList<>();
        for (String lemma : words) {
            List<String> list = morphology.getLemma(lemma);
            lemmaList.addAll(list);
        }
        return lemmaList;
    }


    private List<Lemma> getLemmaListFromSite(List<String> lemmas, SitePage site) {
        lemmaRepository.flush();
        List<Lemma> lemmaList = new ArrayList<>();
        for (String lemma : lemmas) {
            lemmaList.addAll(lemmaRepository.findByLemmaAndSitePageId(lemma, site));
        }
        lemmaList.sort(Comparator.comparingInt(Lemma::getFrequency));
        return lemmaList;
    }

    private List<StatisticsSearch> getSearchData(Hashtable<Page, Float> pageList, List<String> textLemmaList) {
        List<StatisticsSearch> result = new ArrayList<>();

        for (Page page : pageList.keySet()) {
            String uri = page.getPath();
            String content = page.getContent();
            SitePage pageSite = page.getSiteId();
            String site = pageSite.getUrl();
            String siteName = pageSite.getName();
            Float absRelevance = pageList.get(page);
            StringBuilder clearContent = new StringBuilder();
            String title = CleanHtmlCode.clear(content, "title");
            String body = CleanHtmlCode.clear(content, "body");
            clearContent.append(title).append(" ").append(body);
            String snippet = getSnippet(clearContent.toString(), textLemmaList);

            result.add(new StatisticsSearch(site, siteName, uri, title, snippet, absRelevance));
        }
        return result;
    }

    private String getSnippet(String content, List<String> lemmaList) {
        List<Integer> lemmaIndex = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (String lemma : lemmaList) {
            lemmaIndex.addAll(morphology.findLemmaIndexInText(content, lemma));
        }
        Collections.sort(lemmaIndex);
        List<String> wordsList = getWordsFromContent(content, lemmaIndex);
        for (int i = 0; i < wordsList.size(); i++) {
            result.append(wordsList.get(i)).append("... ");
            if (i > 3) {
                break;
            }
        }
        return result.toString();
    }

    private List<String> getWordsFromContent(String content, List<Integer> lemmaIndex) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < lemmaIndex.size(); i++) {
            int start = lemmaIndex.get(i);
            int end = content.indexOf(" ", start);
            int nextPoint = i + 1;
            while (nextPoint < lemmaIndex.size() && lemmaIndex.get(nextPoint) - end > 0 && lemmaIndex.get(nextPoint) - end < 5) {
                end = content.indexOf(" ", lemmaIndex.get(nextPoint));
                nextPoint += 1;
            }
            i = nextPoint - 1;
            String text = getWordsFromIndex(start, end, content);
            result.add(text);
        }
        result.sort(Comparator.comparingInt(String::length).reversed());
        return result;
    }

    private String getWordsFromIndex(int start, int end, String content) {
        String word = content.substring(start, end);
        int prevPoint;
        int lastPoint;
        if (content.lastIndexOf(" ", start) != -1) {
            prevPoint = content.lastIndexOf(" ", start);
        } else prevPoint = start;
        if (content.indexOf(" ", end + 30) != -1) {
            lastPoint = content.indexOf(" ", end + 30);
        } else lastPoint = content.indexOf(" ", end);
        String text = content.substring(prevPoint, lastPoint);
        try {
            text = text.replaceAll(word, "<b>" + word + "</b>");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return text;
    }

    private List<StatisticsSearch> getSearchDtoList(List<Lemma> lemmaList, List<String> textLemmaList, int offset, int limit, String searchedText) {
        List<StatisticsSearch> result = new ArrayList<>();
        if (lemmaList.size() >= textLemmaList.size()) {
            List<IndexSearch> foundIndexList = findIndexList(lemmaList);
            List<Page> foundPageList = findPageList(foundIndexList);

            Hashtable<Page, Float> sortedPageByAbsRelevance = getPageAbsRelevance(foundPageList, foundIndexList, searchedText);
            List<StatisticsSearch> dataList = getSearchData(sortedPageByAbsRelevance, textLemmaList);

            if (offset > dataList.size()) {
                return new ArrayList<>();
            }
            if (dataList.size() > limit) {
                for (int i = offset; i < limit; i++) {
                    result.add(dataList.get(i));
                }
                return result;
            } else return dataList;
        } else return result;
    }

    private List<IndexSearch> findIndexList(List<Lemma> lemmaList) {
        List<IndexSearch> foundIndexList = new ArrayList<>();
        indexSearchRepository.flush();
        for (Lemma lemma : lemmaList) {
            foundIndexList.addAll(indexSearchRepository.findByLemmaId(lemma.getId()));
        }
        return foundIndexList;
    }

    private List<Page> findPageList(List<IndexSearch> foundIndexList) {
        List<Page> foundPageList = new ArrayList<>();
        pageRepository.flush();
        for (IndexSearch indexSearch : foundIndexList) {
            foundPageList.add(indexSearch.getPage());
        }
        return foundPageList;
    }

    private Hashtable<Page, Float> getPageAbsRelevance(List<Page> pageList, List<IndexSearch> indexList, String searchedText) {
        HashMap<Page, Float> pageWithRelevance = new HashMap<>();
        for (Page page : pageList) {
            float relevant = 0;
            for (IndexSearch index : indexList) {
                if (index.getPage() == page) {
                    relevant += index.getRank();
                }
            }
            pageWithRelevance.put(page, relevant);
        }
        HashMap<Page, Float> pageWithAbsRelevance = new HashMap<>();
        for (Page page : pageWithRelevance.keySet()) {
            int coincidence = 1;
            if (page.getContent().toLowerCase().contains(searchedText.toLowerCase())) {
                coincidence = 1000;
            }

            float absRelevant = pageWithRelevance.get(page) * coincidence / Collections.max(pageWithRelevance.values());
            pageWithAbsRelevance.put(page, absRelevant);
        }
        return sortHashSetByRelevance(pageWithAbsRelevance);
    }

    private Hashtable<Page, Float> sortHashSetByRelevance(HashMap<Page, Float> unsortedHash) {
        return unsortedHash.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, Hashtable::new));
    }
}
