package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.SitePage;



@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
   long countBySiteId(SitePage siteId);
   Iterable<Page> findBySiteId(SitePage site);


}
