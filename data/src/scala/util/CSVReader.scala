package util

import java.io.Serializable

import scala.collection.mutable.ListBuffer

/**
  * Created by caldama on 2/16/17.
  */


class CSVReader extends App {

   def readFile(filename: String, delim: String): ListBuffer[Array[String]] = {
      val bufferedSource = io.Source.fromFile(filename)
     // var row:Array[String] = null
      var lines = new ListBuffer[Array[String]]()
      var i:Int = 0
      for (line <- bufferedSource.getLines) {
        val row = line.split(delim + "(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)").map(_.trim.replaceAll("\"",""))
        //val row = line.split(delim).map(_.trim.replaceAll("\"",""))
        lines += row
          // do whatever you want with the columns here
         i = i+1

      }
      bufferedSource.close
      return lines
   }
}

object CSVReader {

}
