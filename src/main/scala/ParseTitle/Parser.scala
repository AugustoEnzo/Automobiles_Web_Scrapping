package ParseTitle

class Parser {

  def parser(Title: String): String = {

    val toRemove: List[String] = List("dicas de segurança"
      , "vendo", "completo", "ou", "vende se", "r$", "vender", "logo", "pra", "para", "-"
      , "*", "leia", "um", "\"", "vendo"

    )
    val lowerTitle: String = Title.toLowerCase

    lowerTitle
      .replace("dicas de segurança", "")
      .replace(toRemove(1), "")
      .replace(toRemove(2), "")
      .replace(toRemove(3), "")
      .replace(toRemove(4), "")
      .replace(toRemove(5), "")
      .replace(toRemove(6), "")
      .replace(toRemove(7), "")
      .replace(toRemove(8), "")
      .replace(toRemove(9), "")
      .replace(toRemove(10), "")
      .replace(toRemove(11), "")
      .replace(toRemove(12), "")
      .replace(toRemove(13), "")
      .replace(toRemove(14), "")
      .replace(toRemove(15), "")
      .stripTrailing
      .stripLeading
      .capitalize

    }
}
