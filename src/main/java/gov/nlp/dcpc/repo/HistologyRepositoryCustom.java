package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;

import java.util.List;

/**
 * Created by marcelo on 2/24/17.
 */
public interface HistologyRepositoryCustom {
    public List<Histology> findMatchingHistologies(String... values);
}
