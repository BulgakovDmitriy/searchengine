package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.SitePage;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Long> {
  long countBySitePageId(SitePage site);

  List<Lemma> findBySitePageId(SitePage siteId);


  List<Lemma> findByLemma(String lemma);

  List<Lemma> findByLemmaAndSitePageId(String lemma, SitePage site);
}
