package scripts

import scala.io.Source

/**
  * Created by marcelo on 3/7/17.
  * Generate ICD-9 Codes from http://icd9data.com
  * for 2015 NeoPlasms - 140 - 239
  *
  * For future reference, Seer has a spreadsheet to convert ICD-o-3 to ICD-9-CM to ICD-10 and ICD-10-CM
  * https://seer.cancer.gov/tools/conversion/
  */
class ICD9CodeGenerator {

    def crawlICD9WebStie(): Unit = {
        val rootURL = "http://www.icd9data.com/2015/Volume1/140-239/default.htm"
        val regextractor = "<a href=\"(.*)htm\".*>(.*)</a>(.*)</li>".r
        //Point to the root ( this case Neoplasms )
        val html = Source.fromURL(rootURL)
        val s = html.mkString
        regextractor.findAllIn(s).matchData foreach {
            m => println(m.group(1) + "htm;" + m.group(2) + ";"+m.group(3) +"\n")
        }
        //Gather content

        //crawl each link
    }
}

object ICD9CodeGenerator {}

object ICD9App extends App {
    var codegen:ICD9CodeGenerator = new ICD9CodeGenerator()
    codegen.crawlICD9WebStie()

}
