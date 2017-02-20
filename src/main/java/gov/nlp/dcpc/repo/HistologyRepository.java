package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by marcelo on 2/18/17.
 */
@RepositoryRestResource(collectionResourceRel = "MORTH_CODE", path = "histology")
public interface HistologyRepository extends PagingAndSortingRepository<Histology, Long> {
    public Histology findByCode(@Param("code") String code);
    public List<Histology> findByHistology(@Param("hist") int code);
    public List<Histology> findByNameContaining(@Param("name") String name);

    public List<Histology> findByFamily(@Param("family") String family);

    //method to find histology by site
    public List<Histology> findBySitesName(@Param("site") String site);
    public List<Histology> findBySitesCode(@Param("code") String code);


    //TODO::method to find all histologies by name or synonym (regExp) - Retrieve Histology only, not synonyms.
    @Query("MATCH(h:MORPH_CODE) WHERE(h.name =~ '.*Round.*') return h union  OPTIONAL MATCH (h)<-[r:SYNONYM_OF]-(s:SYNONYM) WHERE(s.name =~ '.*Round.*') return h")
    public List<Histology> findByNameContainingOrSynonymsNameContaining(@Param("synonym") String synonym);

}
