package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by marcelo on 2/23/17.
 */

public class HistologyRepositoryImpl implements HistologyRepositoryCustom {

    @Autowired
    Neo4jOperations neo4jTemplate;


    /**
     *
     * Method that retreives any Histology that it's name has all the values passed or any of its Synonyms also have all
     * the values passed.
     *
     * match (h:HISTOLOGY) where  (h.name=~ '.*\\bBREAST\\b.*' AND h.name =~ '.*\\bPAGET\\b.*') return h
     * UNION
     * match (h:HISTOLOGY)<-[r:SYNONYM_OF]-(s:SYNONYM) where  (s.name=~ '.*BREAST.*' AND s.name =~ '.*PAGET.*')  return h
     *
     * @param values Strings we want to match on Histology and synonims - all of them on a single entry.
     * @return a list of Histologies that matches values passed.
     */

    public List<Histology> findMatchingHistologies(String... values) {
        String query1 = " MATCH (h:HISTOLOGY) WHERE (";
        String query2 = " MATCH (h:HISTOLOGY)<-[r:SYNONYM_OF]-(s:SYNONYM) WHERE (";
        for (int i = 0; i < values.length; i++) {
            String s = values[i];
            query1 += " h.name =~ '.*\\\\b" + s.toUpperCase() + "\\\\b.*' ";
            query2 += " s.name =~ '.*\\\\b" + s.toUpperCase() + "\\\\b.*' ";
            if (i < values.length - 1) {
                query1 += " and ";
                query2 += " and ";
            }
        }
        query1 += ") return h";
        query2 += ") return h";

        Result nodes = neo4jTemplate.query(query1 + " UNION " + query2, new HashMap<String, Long>());
        List<Histology> result = new ArrayList<>() ;

        nodes.queryResults().forEach((m) ->
                m.keySet().forEach(kk ->
                        result.add((Histology) m.get(kk))) );

        return result;
    }
}
