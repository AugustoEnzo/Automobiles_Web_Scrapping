import scala.collection.{immutable, mutable}
import scala.io.Source
import com.ibm.cloud.cloudant.v1.model.AllDocsResult

package object JSIMPLE {
  var stringMap: mutable.Map[String, String] = mutable.Map[String, String]()

  def parse(file_path: String): immutable.Map[String, String] = {

    val fileSource: Source = Source.fromFile(file_path)

    for (line <- fileSource.getLines) {
      val s_line: Array[String] = line.split(": ")
      stringMap +=
        s_line(0).replaceAll("\"", "")
          .replaceAll(",", "").trim -> s_line(1).replaceAll("\"", "")
          .replaceAll(",", "")
    }

    fileSource.close

    val finalMap: immutable.Map[String, String] = stringMap.toMap
    finalMap
  }

  def parseDocResult(response: AllDocsResult): String = {
    var result: String = ""
    response.getRows.forEach(row => result = row.getKey)
    result
  }
}
