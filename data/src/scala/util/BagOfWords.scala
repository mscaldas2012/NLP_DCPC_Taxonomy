package util

import scala.collection.immutable.ListMap
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

/**
  * Created by marcelo on 2/23/17.
  */
class BagOfWords {
    val siteListing = "\\(?(C[0-9]{2}\\.[_[0-9]])\\)?".r
    val stopWords = "WITH|OF|THE|IN|OR|A|AND|ALSO| ".r
    val histCode = "[0-9]{4}/[0-9]".r


    def createBagOfWords(filename: String): ListMap[String, Int] = {
        val bufferedSource = io.Source.fromFile(filename)
        var lines = new ListBuffer[Array[String]]()
        var i: Int = 0
        var bagResult = scala.collection.mutable.HashMap.empty[String,Int]
        for (line <- bufferedSource.getLines) {
            val row = line.split("[|, ]").map(_.trim.replaceAll("\"",""))
            for (w <- row) {
                if (!w.isEmpty) {
                    val uw = w.toUpperCase()
                    uw match {
                        case siteListing(_*) => {}
                        case stopWords(_*) => {}
                        case histCode(_*) => {}
                        case _ => {
                            if (bagResult.contains(uw)) {
                                bagResult(uw) = bagResult(uw) + 1
                            } else {
                                bagResult(uw) = 1
                            }
                        }
                    }
                }


            }
        }
        ListMap(bagResult.toSeq.sortWith(_._2 > _._2):_*)
    }

}

object BagOfWords {

}

object BagOfWordsApp extends App {
    var bow = new BagOfWords()
    var bag = bow.createBagOfWords("data/src/resources/histology.txt")
    var totalWords = 0
    for ((k,v) <- bag)
        totalWords += v
    println(s"total $totalWords")
    println(s"entries ${bag.size}")
    for ((k,v) <- bag) {
        if (v > 0) {
            var p  =f"${v.toFloat / totalWords * 100f}%.2f"
            println(s"$k -> $v ($p%)")
        }
    }

}
