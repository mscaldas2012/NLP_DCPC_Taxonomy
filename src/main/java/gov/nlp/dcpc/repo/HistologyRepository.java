package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by marcelo on 2/18/17.
 */
@RepositoryRestResource(collectionResourceRel = "HISTOLOGY", path = "histology")
public interface HistologyRepository extends GraphRepository<Histology>, HistologyRepositoryCustom {
    public Histology findByCode(@Param("code") String code);
    public List<Histology> findByHistology(@Param("hist") int code);
    public List<Histology> findByNameContaining(@Param("name") String name);

    public List<Histology> findByFamily(@Param("family") String family);

    //method to find histology by site
    @Query("match(H:HISTOLOGY)-[r:OCCURS_IN]->(s) where s.name = $site return H ")
    public List<Histology> findBySitesName(@Param("site") String site);
    @Query("match(H:HISTOLOGY)-[r:OCCURS_IN]->(s) where s.code = $code return H ")
    public List<Histology> findBySitesCode(@Param("code") String code);


    //TODO::method to find all histologies by name or synonym (regExp) - Retrieve Histology only, not synonyms.
    @Query("MATCH(h:HISTOLOGY) WHERE(h.name CONTAINS $text) return h union  OPTIONAL MATCH (h)<-[r:SYNONYM_OF]-(s:SYNONYM) WHERE(s.name CONTAINS $text) return h")
    public List<Histology> findByNameContainingOrSynonymsNameContaining(@Param("text") String text);


    //match (h) where  h.name =~ '(?i).*BREAST.*' and h.name =~ '.*DISEASE.*' and h.name =~ '.*PAGET.*' return h
}
