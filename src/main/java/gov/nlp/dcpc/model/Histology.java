package gov.nlp.dcpc.model;

import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

/**
 * Created by marcelo on 2/18/17.
 */
@Data
@NodeEntity(label="HISTOLOGY")
@ToString(callSuper=true, exclude="synonyms")
public class Histology extends Synonym {

    @Relationship(type="OCCURS_IN", direction=Relationship.OUTGOING)
    private List<Site> sites;

    @Relationship(type="SYNONYM_OF", direction=Relationship.INCOMING)
    private List<Synonym> synonyms;

//    public String toString() {
//        return super.toString();
//    }
}
