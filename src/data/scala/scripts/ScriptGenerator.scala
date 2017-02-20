package scripts

import java.io.{File, PrintWriter}

import util.CSVReader

/**
  * Created by marcelo on 2/17/17.
  * Codes from http://codes.iarc.fr/abouticdo.php
  * Generates Neo4J Script files to load data into graphs
  *
  * Scripts will create nodes for:
  *    - Morphology code (MORTH_CODE)
  *    - its synonyms (SYNONYM)
  *    - sites (SITE).
  * Relationships will be created for:
  *    - sites and subsites (PART_OF)
  *    - Morphology and its synonyms (SYNONYM_OF) and
  *    - morphology and sites where it can occur (OCCURS_IN)
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
                pw.write(s"CREATE ($codeName:SITE { code: '$code', maincode: '$maincode', subcode:'$subcode', name: '$name'})\n")
                pw.write(s"CREATE ($codeName)-[:PART_OF]->($previouscodeline)\n")
            }
            else {
                pw.write(s"CREATE ($codeName:SITE { code: '$code', maincode: '$code', name: '$name'})\n")
                previouscodeline = row(0)
            }
        }
        pw.close
    }

    def generateNeo4JScriptsForMorphology(filename: String): Unit = {
        var reader: CSVReader = new CSVReader()
        var lines = reader.readFile(filename, "\\|")
        var index = 1
        var previousRow: Array[String] = null
        val pw = new PrintWriter(new File("neo4j_morphologyCodes.txt"))
        for (row <- lines) {
            if (row.length == 2) {
                previousRow = row
                val code = row(0)
                val codename = "H" + code.replaceAllLiterally("/", "_")
                val hist = code.substring(0, code.indexOf("/"))
                val family = code.substring(0,3)
                val behavior = code.substring(code.indexOf("/") + 1)
                val name = row(1).toUpperCase()
                pw.write(s"CREATE ($codename:MORPH_CODE { code: '$code', family: $family, histology: $hist, behavior: $behavior, name: '$name'})\n")
            } else {
                val name = row(0).toUpperCase()
                val paddedVal = f"${index}%09d"
                val code = previousRow(0)
                val codename = "H" + code.replaceAllLiterally("/", "_")
                val hist = code.substring(0, code.indexOf("/"))
                val family = code.substring(0,3)
                val behavior = code.substring(code.indexOf("/") + 1)
                val synCode = s"SYN_$paddedVal"
                pw.write(s"CREATE ($synCode:SYNONYM { code: '$code', family: $family, histology: $hist, behavior: $behavior, name: '$name'})\n")
                pw.write(s"CREATE ($synCode)-[:SYNONYM_OF]->($codename)\n")
                index += 1
            }
        }
        pw.close
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
                pw.write(s"MATCH(h:MORPH_CODE {code:'$histCode'}), (s:SITE {code:'$elem'}) CREATE(h)-[:OCCURS_IN]->(s) ;\n\n")
            }
        }
        pw.close
    }

}

object ScriptGenerator {

}

object ScriptGeneratorApp extends App {
    var sg: ScriptGenerator = new ScriptGenerator()
    sg.generateNeo4JScriptForSites("src/data/resources/sites.txt")
    sg.generateNeo4JScriptsForMorphology("src/data/resources/morphology.txt")
    sg.generateNeo4JScriptsForHistSiteRelationship("src/data/resources/histology_site_rel.txt")
}
