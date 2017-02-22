package gov.nlp.dcpc.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by marcelo on 2/18/17.
 */
@NodeEntity(label="SYNONYM")
@Data
public class Synonym {
    @GraphId
    private Long id;

    private int histology;
    private int family;
    private String code;
    private String name;
    private String description;
    private int behavior;
}
