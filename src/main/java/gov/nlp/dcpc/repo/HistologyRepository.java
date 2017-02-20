package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
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

    //TODO::method to find histology by site
    //TODO::method to find all synonyms of a histology
    //TODO::method to find all histologies by name or synonym (regExp) - Retrieve Histology only, not synonyms.
}
