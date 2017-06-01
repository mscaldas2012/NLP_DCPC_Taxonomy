package util

import java.io.{File, FileWriter, PrintWriter}

import scala.collection.mutable.ListBuffer


/**
  * Created by caldama on 6/1/17.
  */

class HL7MessageSplitter {

  def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

  def writeToFile(fileName: String, data: String) =
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }

  def appendToFile(fileName: String, textData: String) =
    using(new FileWriter(fileName, true)) {
      fileWriter =>
        using(new PrintWriter(fileWriter)) {
          printWriter => printWriter.println(textData)
        }
    }

  def generateFileName(outputDir: String, prefix: String , counter: Int, extension: String = ".txt"): String = {
    var newFile = outputDir
    //Make sure output directory ends in a slash to separate frm the filename
    if (!outputDir.endsWith("/")) {
      newFile +=  "/"
    }
    //make sure extension has a dot
    var newExtension = extension
    if (newExtension.startsWith(".")) {
      newExtension = "." + extension
    }
    newFile += prefix +  "_" + "%05d".format(counter) + newExtension
    newFile
  }

  def splitMessages(filename: String, outputDir: String, outputFileNamePrefix: String): Unit = {
    val MSH = "MSH"

    val bufferedSource = io.Source.fromFile(filename)
    // var row:Array[String] = null
    var lines = new ListBuffer[Array[String]]()
    var newFile = 0
    var newMessage = ""
    for (line <- bufferedSource.getLines) {
      if (line.startsWith(MSH)) {
        //persiste previous message:
        if (newMessage != null && newMessage.trim().length() > 0) {
          //not first message:
          //implicit val codec = scalax.io.Codec.UTF8
          writeToFile(generateFileName(outputDir , outputFileNamePrefix , newFile ), newMessage)


        }
        //New message:
        newMessage = line + "\n" //init message
        newFile += 1
      } else {
        newMessage += line + "\n" //conitnue building message.
      }
    }
    //Save Last Message:
    writeToFile(generateFileName(outputDir , outputFileNamePrefix , newFile ), newMessage)
    bufferedSource.close

  }
}

object HL7MessageSplitter {}

object HL7MsgSplitterApp extends App {
  var msgSplitter = new HL7MessageSplitter()
  msgSplitter.splitMessages("data/src/resources/Cancer.txt", "/Users/caldama/temp", "cancerMSG")

}