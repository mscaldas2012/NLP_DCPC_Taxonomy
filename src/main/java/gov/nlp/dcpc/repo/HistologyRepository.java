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
    public List<Histology> findByHistology(@Param("hist") Long code);
    public List<Histology> findByNameContainingIgnoreCase(@Param("name") String name);
}
