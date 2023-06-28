package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexSearch;
import java.util.List;

@Repository
public interface IndexSearchRepository extends JpaRepository<IndexSearch, Long> {


    List<IndexSearch> findByLemmaId (long lemmaId);

    List<IndexSearch> findByPageId (long pageId);
    IndexSearch findByLemmaIdAndPageId (long lemmaId, long pageId);

}
