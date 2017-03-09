package scripts

import java.io.{File, PrintWriter}

import util.CSVReader

/**
  * Created by marcelo on 2/17/17.
  * Codes from http://codes.iarc.fr/abouticdo.php
  * Generates Neo4J Script files to load data into graphs
  *
  * Scripts will create nodes for:
  *    - Histology code (HISTOLOGY_CODE)
  *    - its synonyms (SYNONYM)
  *    - sites (SITE).
  * Relationships will be created for:
  *    - sites and subsites (PART_OF)
  *    - Histology and its synonyms (SYNONYM_OF) and
  *    - Histology and sites where it can occur (OCCURS_IN)
  */

class ScriptGenerator {

    def generateNeo4JScriptForSites(filename: String): Unit = {
        var reader: CSVReader = new CSVReader()
        var lines = reader.readFile(filename, "\\|")
        var previouscodeline: String = null

        val pw = new PrintWriter(new File("neo4J_sites.txt"))

        for (row <- lines) {
            //CREATE (C00:SITE {code: 'C00', name:'Lip'})
            var code = row(0)
            var codeName = code.replaceAllLiterally(".", "")
            var name = row(1).toUpperCase()
            if (row(0).indexOf('.') > 0) {
                //Not true for first line.. so we're good on not checking previousline
                val subcodes = row(0).split('.').map(_.trim)
                var maincode = subcodes(0)
                var subcode = subcodes(1)
                pw.write(s"CREATE ($codeName:SITE { code: '$code', maincode: '$maincode', subcode:'$subcode', name: '$name'});\n\n")
                pw.write(s"MATCH(c:SITE {code:'$code'}), (p:SITE {code:'$previouscodeline'}) CREATE (c)-[:PART_OF]->(p);\n\n")
            }
            else {
                pw.write(s"CREATE ($codeName:SITE { code: '$code', maincode: '$code', name: '$name'});\n\n")
                previouscodeline = row(0)
            }
        }
        pw.close
    }

    // Create Relationship between "see also" histologies: Reg Ex: see also M\-([0-9]{4}/[0-9]
    // Separate Name and Descirption on Histologies - clean up the names.
    //  Get rid of sites; \((C[0-9]{2}\.[_[0-9]](, *)?)*\)
    //  Get rid of see also

    def generateNeo4JScriptsForHistology(filename: String): Unit = {
        var reader: CSVReader = new CSVReader()
        var lines = reader.readFile(filename, "\\|")
        var index = 1
        var previousRow: Array[String] = null
        val pw = new PrintWriter(new File("neo4j_HistologyCodes.txt"))

        val seeAlso = "SEE ALSO M-(\\d{4}/\\d)"
        val seeAlsoReg = (".*" + seeAlso + ".*").r

        val siteListing = "\\((C[0-9]{2}\\.[_[0-9]](, *)?)*\\)"
        var seeAlsoRelationships = scala.collection.mutable.HashMap.empty[String,String]
        for (row <- lines) {
            if (row.length == 2) {
                previousRow = row
                val code = row(0)
                val codename = getNeo4jNodeNameForHistology(code)
                val hist = code.substring(0, code.indexOf("/"))
                val family = code.substring(0,3)
                val behavior = code.substring(code.indexOf("/") + 1)
                val description = row(1)

                var name = row(1).toUpperCase().replaceAll(siteListing,"").trim() //Remove SITE parenthesis.
                name match {
                    case seeAlsoReg(histCode) => {  seeAlsoRelationships += (getNeo4jNodeNameForHistology(histCode) -> codename)}
                    case _ => {}
                }
                name = name.replaceAll(seeAlso, "").replaceAll("\\(\\)", "").replaceAll(", \\)", ")")

                pw.write(s"CREATE ($codename:HISTOLOGY { code: '$code', family: $family, histology: $hist, behavior: $behavior, name: '$name', description: '$description'})\n")

            } else {
                val code = previousRow(0)
                val codename = getNeo4jNodeNameForHistology(code)
                val hist = code.substring(0, code.indexOf("/"))
                val family = code.substring(0,3)
                val behavior = code.substring(code.indexOf("/") + 1)
                val description = row(0)

                val paddedVal = f"${index}%09d"
                val synCode = s"SYN_$paddedVal"

                var name = row(0).toUpperCase().replaceAll(siteListing, "").trim()
                name = name.replaceAll(seeAlso, "").replaceAll("\\(\\)", "").replaceAll(", \\)", ")")

                pw.write(s"CREATE ($synCode:SYNONYM { code: '$code', family: $family, histology: $hist, behavior: $behavior, name: '$name', description: '$description'})\n")
                pw.write(s"CREATE ($synCode)-[:SYNONYM_OF]->($codename)\n")
                index += 1
            }
        }
        //add see also relationships:
        for ((k,v) <- seeAlsoRelationships) pw.write(s"CREATE ($k)-[:SEE_ALSO]->($v)\n")

        pw.close
    }

    private def getNeo4jNodeNameForHistology(code: String) = {
        "H" + code.replaceAllLiterally("/", "_")
    }

    def generateNeo4JScriptsForHistSiteRelationship(filename: String): Unit = {
        var reader: CSVReader = new CSVReader()
        var lines = reader.readFile(filename, ",")
        var index = 1
        var previousRow: Array[String] = null

        val pw = new PrintWriter(new File("neo4j_hist_site_rel.txt"))
        for (row <- lines) {
            val sites = row(0).split(",").map(_.trim)
            for (elem <- sites) {
                val histCode = row(1)
                pw.write(s"MATCH(h:HISTOLOGY {code:'$histCode'}), (s:SITE {code:'$elem'}) CREATE(h)-[:OCCURS_IN]->(s) ;\n\n")
            }
        }
        pw.close
    }

}

object ScriptGenerator {

}

object ScriptGeneratorApp extends App {
    var sg: ScriptGenerator = new ScriptGenerator()
    sg.generateNeo4JScriptForSites("data/src/resources/sites.txt")
    sg.generateNeo4JScriptsForHistology("data/src/resources/histology.txt")
    sg.generateNeo4JScriptsForHistSiteRelationship("data/src/resources/histology_site_rel.txt")
}

object TestRegExp extends App {
    val seeAlso = ".*SEE ALSO M-(\\d{4}/\\d).*"
    val seeAlsoReg = seeAlso.r

    val name ="  9930/3|Myeloid sarcoma (SEE ALSO M-9861/3)"
    name match {
        case seeAlsoReg( gp, _*) => { println(s"$gp is the code i'm looking for ")}
        case _ => {}
    }
    val date = """(\d\d\d\d)-(\d\d)-(\d\d)""".r
    "2004-01-20" match {
        case date(year, month, day) => println(s"$year was a good year for PLs.")
    }



}


