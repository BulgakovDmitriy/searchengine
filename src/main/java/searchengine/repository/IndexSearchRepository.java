package searchengine.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexSearch;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface IndexSearchRepository extends JpaRepository<IndexSearch, Long> {


    List<IndexSearch> findByLemmaId (long lemmaId);
    List<IndexSearch> findByPageId (long pageId);
    IndexSearch findByLemmaIdAndPageId (long lemmaId, long pageId);

    @Cacheable("indexesByLemmasAndPages")
    default List<IndexSearch> findByPagesAndLemmas(List<Lemma> lemmaList, List<Page> pageList) {
        Set<Long> lemmaIds = lemmaList.stream().map(Lemma::getId).collect(Collectors.toSet());
        Set<Long> pageIds = pageList.stream().map(Page::getId).collect(Collectors.toSet());

        return findAllByLemmaIdInAndPageIdIn(lemmaIds, pageIds);
    }

    List<IndexSearch> findAllByLemmaIdInAndPageIdIn(Set<Long> lemmaIds, Set<Long> pageIds);

}
