import scala.collection.{immutable, mutable}
import scala.io.Source

package object JSIMPLE {
  var stringMap: mutable.Map[String, String] = mutable.Map[String, String]()

  def parse(file_path: String): immutable.Map[String, String] = {

    val fileSource: Source = Source.fromFile(file_path)

    for (line <- fileSource.getLines) {
      try {
        val s_line: Array[String] = line.split(": ")
        stringMap +=
          s_line(0).replaceAll("\"", "")
            .replaceAll(",", "").trim -> s_line(1).replaceAll("\"", "")
            .replaceAll(",", "")
      } catch {
        case _: Throwable => null
      }
    }

    fileSource.close

    val finalMap: immutable.Map[String, String] = stringMap.toMap
    finalMap
  }
}
