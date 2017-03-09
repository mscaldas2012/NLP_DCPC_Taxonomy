package scripts

import java.io.{File, PrintWriter}

import scala.io.Source
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.{Element, ElementNode}
import org.jsoup.nodes.Node

/**
  * Created by marcelo on 3/7/17.
  * Generate ICD-9 Codes from http://icd9data.com
  * for 2015 NeoPlasms - 140 - 239
  *
  * For future reference, Seer has a spreadsheet to convert ICD-o-3 to ICD-9-CM to ICD-10 and ICD-10-CM
  * https://seer.cancer.gov/tools/conversion/
  */
class ICD9CodeGenerator {
    val rootURL = "http://www.icd9data.com"

    def crawSubPage2(url: String, pw: PrintWriter): Unit = {
        val regextractor = "([0-9]{3}\\.[0-9]{1,2}) (.*)".r
        val browser = JsoupBrowser()
        val doc = browser.get(url)
        //<div class="definitionList">
        //if (doc.body.)
        val items: List[Element] = doc >> elementList("div .codeHierarchyInnerWrapper li")
        println("========")
        for (item <- items) {
            println("item text: " + item.text)
            var code = ""
            var name = ""
            var codeName = ""
            regextractor.findAllIn(item.text).matchData foreach {
                m => {
                    println(m.group(1) + " -> " + m.group(2) + "\n")
                    code = m.group(1)
                    name = m.group(2).replaceAll("convert [0-9]{3}\\.[0-9]{1,2} to ICD-10-CM", "")
                    codeName = "ICD9_" + code.replaceAll("-", "_")
                    codeName = codeName.replaceAll("\\.","_")
                    pw.write(s"CREATE ($codeName:ICD_9 { code: '$code',  name: '$name'});\n\n")
                }
            }
//            for (c <- item.childNodes if c.isInstanceOf[ElementNode] && c.asInstanceOf[ElementNode].element.hasAttr("href")) {
//                val url = c.asInstanceOf[ElementNode].element.attr("href")
//                println("crawling " + url)
//                crawSubPage2(rootURL + url, pw)
//            }

        }

    }

    def crawSubPage(url: String, pw: PrintWriter): Unit = {
        val regextractor = "([0-9]{3}) (.*)".r
        val browser = JsoupBrowser()
        val doc = browser.get(url)
        //<div class="definitionList">
        //if (doc.body.)
        val items: List[Element] = doc >> elementList("div .definitionList li")
        println("========")
        for (item <- items) {
            //println("item text: " + item.text)
            var code = ""
            var name = ""
            var codeName = ""
            regextractor.findAllIn(item.text).matchData foreach {
                m => {
                    println(m.group(1) + " -> " + m.group(2) + "\n")
                    code = m.group(1)
                    name = m.group(2)
                    codeName = "ICD9_" + code.replaceAll("-", "_")
                    pw.write(s"CREATE ($codeName:ICD_9 { code: '$code',  name: '$name'});\n\n")
                }
            }
            for (c <- item.childNodes if c.isInstanceOf[ElementNode] && c.asInstanceOf[ElementNode].element.hasAttr("href")) {
                val url = c.asInstanceOf[ElementNode].element.attr("href")
                println("crawling " + url)
                crawSubPage2(rootURL + url, pw)
            }

        }
//        println("trying second level")
//        val items2: List[Element] = doc >> elementList("ul .codeHierarchyUL ")
//        for (item <- items2) {
//            println("second level: " + item)
//        }
    }

    def crawlICD9WebSite(): Unit = {
        val pw = new PrintWriter(new File("icd9_codes.txt"))

        val neoplasmURL = rootURL + "/2015/Volume1/140-239/default.htm"
        val regextractor = "([0-9]{3}-[0-9]{3}) (.*)".r
        //Point to the root ( this case Neoplasms )
        val browser = JsoupBrowser()
        val doc = browser.get(neoplasmURL)
        //<div class="definitionList">
        val items: List[Element] = doc >> elementList("div .definitionList li")
        println("========")
        for (item <- items) {
            //println("item text: " + item.text)
            var code = ""
            var name = ""
            var codeName = ""
            regextractor.findAllIn(item.text).matchData foreach {
                m => {
                    println(m.group(1) + " -> " + m.group(2) + "\n")
                    code = m.group(1)
                    name = m.group(2)
                    codeName = "ICD9_" + code.replaceAll("-","_")
                    pw.write(s"CREATE ($codeName:ICD_9_GROUP { group_code: '$code',  name: '$name'});\n\n")
                }
            }
            for (c <- item.childNodes if c.isInstanceOf[ElementNode] && c.asInstanceOf[ElementNode].element.hasAttr("href") ) {
                val url = c.asInstanceOf[ElementNode].element.attr("href")
                crawSubPage(rootURL + url, pw)
            }

        }
        pw.close()



//        val html = Source.fromURL(rootURL)
//        val s = html.mkString
//        regextractor.findAllIn(s).matchData foreach {
//            m => println(m.group(1) + "htm;" + m.group(2) + ";"+m.group(3) +"\n")
//        }
        //Gather content

        //crawl each link
    }
}

object ICD9CodeGenerator {}

object ICD9App extends App {
    var codegen:ICD9CodeGenerator = new ICD9CodeGenerator()
    codegen.crawlICD9WebSite()

}
