package gov.nlp.dcpc.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by marcelo on 2/18/17.
 */
@NodeEntity(label="SITE")
@Data
public class Site {
    @GraphId
    private Long id;

    private String code;
    private String maincode;
    private String subcode;
    private String name;
}
