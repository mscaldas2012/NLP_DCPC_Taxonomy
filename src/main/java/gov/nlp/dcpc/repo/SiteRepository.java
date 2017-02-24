package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Site;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by marcelo on 2/18/17.
 */
@RepositoryRestResource(collectionResourceRel = "SITE", path = "site")
public interface SiteRepository extends GraphRepository<Site> {
//    public List<Site> findMainSites() {
//        return findBySubcodeIsNull();
//    }

    public Site findByCode(@Param("code") String code);
    //Retrieves a whole family (Site and Subsites)
    public List<Site> findByMaincode(@Param("maincode") String mainCode);
    public Site findByName(@Param("name") String name);
    //Retrieve primary sites...
    public List<Site> findBySubcodeIsNull();

    //Similar to findByMaincode, but this one leaves the parent out of the results.
    @Query("MATCH (s:SITE) WHERE (s.maincode CONTAINS $parentCode AND s.subcode is not null)  RETURN s")
    public List<Site> findChildren(@Param("parentCode") String parentCode);



}
