package helpers

import scala.collection.mutable
import scala.util.matching.Regex

class Parser {

  def parser(Title: String): String = {

    val toRemove: List[String] = List("dicas de segurança"
      , "vendo", "completo", "ou", "vende se", "vender", "logo", "pra", "para", "-"
      , "*", "leia", "um", "vendo", " /", ".", "commos seu carro com dívidas no banco e detran ",
      "commos carro já financiado", "commos", "quitado", "financiado", "carro", "dívidas", "dividas", "carros",
      "este", "lindo", "está", "esta", " a ", "venda", " á ", "melhores", "condições", "condicoes", "impecavel",
      "novo", "nova", "baixa", "semi", "km", "!", "seu", "aluguel", "financiamento", "super", "novidade", " na ", "loja",
      " sem" , " entrada", " descrição", " descricao", "já", " ja ", "Avalio e compro pagovista cubro qualquer oferta",
      "financio"

    )

    var lowerTitle: String = Title.toLowerCase

    lowerTitle =
      lowerTitle
      .replace("dicas de segurança", "")
//      .replace(toRemove(1), "")
//      .replace(toRemove(2), "")
//      .replace(toRemove(3), "")
//      .replace(toRemove(4), "")
//      .replace(toRemove(5), "")
//      .replace(toRemove(6), "")
//      .replace(toRemove(7), "")
//      .replace(toRemove(8), "")
//      .replace(toRemove(9), "")
//      .replace(toRemove(10), "")
//      .replace(toRemove(11), "")
//      .replace(toRemove(12), "")
//      .replace(toRemove(13), "")
//      .replace(toRemove(14), "")
//      .replace(toRemove(15), "")
//      .replace(toRemove(15), "")
//      .replace(toRemove(16), "")
//      .replace(toRemove(17), "")
//      .replace(toRemove(18), "")
//      .replace(toRemove(19), "")
//      .replace(toRemove(20), "")
//      .replace(toRemove(21), "")
//      .replace(toRemove(22), "")
//      .replace(toRemove(23), "")
//      .replace(toRemove(24), "")
//      .replace(toRemove(25), "")
//      .replace(toRemove(26), "")
//      .replace(toRemove(27), "")
//      .replace(toRemove(28), "")
//      .replace(toRemove(29), "")
//      .replace(toRemove(30), "")
//      .replace(toRemove(31), "")
//      .replace(toRemove(32), "")
//      .replace(toRemove(33), "")
//      .replace(toRemove(34), "")
//      .replace(toRemove(35), "")
//      .replace(toRemove(36), "")
//      .replace(toRemove(37), "")
//      .replace(toRemove(38), "")
//      .replace(toRemove(39), "")
//      .replace(toRemove(40), "")
//      .replace(toRemove(41), "")
//      .replace(toRemove(42), "")
//      .replace(toRemove(43), "")
//      .replace(toRemove(44), "")
//      .replace(toRemove(45), "")
//      .replace(toRemove(46), "")
//      .replace(toRemove(47), "")
//      .replace(toRemove(48), "")
//      .replace(toRemove(49), "")
//      .replace(toRemove(50), "")
//      .replace(toRemove(51), "")
//      .replace(toRemove(52), "")
//      .replace(toRemove(53), "")
//      .replace(toRemove(54), "")
//      .replace(toRemove(55), "")
//      .replace(toRemove(56), "")
      .replaceAll(" {2}", " ")
      .trim

//    val regexMatchesOne: Option[String] = "[0-9]{4}".r findFirstIn lowerTitle
//
//    if (regexMatchesOne.isDefined) {
//      val regexResult = regexMatchesOne.toString.replaceAll("[A-z()]", "").trim
//
//      if (regexResult.startsWith("2") || regexResult.startsWith("19")) {
//        return lowerTitle
//          .replace(regexResult, s"($regexResult)")
//          .replaceAll(" {2}", " ")
//          .trim
//          .capitalize
//
//      } else {
//        return lowerTitle
//          .replace(regexResult, s"2${regexResult.takeRight(3)}")
//          .replaceAll(" {2}", " ")
//          .trim
//          .capitalize
//      }
//
//    }
//
//    val regexMatchesTwo: Option[String] = " [0-9]{2}".r findFirstIn lowerTitle
//
//    if (regexMatchesTwo.isDefined) {
//      val regexResult = regexMatchesTwo.toString.replaceAll("[A-z()]", "").trim
//
//      return lowerTitle
//        .replaceAll(regexResult, s"(20$regexResult)")
//        .replaceAll(" {2}", " ")
//        .trim
//        .capitalize
//    }
//
//    val regexMatchesTree: Option[String] = "(\\S[0-9]{4})".r findFirstIn lowerTitle
//    if (regexMatchesTree.isDefined) {
//      val regexResult = regexMatchesTree.toString.replaceAll("[A-z()]", "").trim
//
//      return lowerTitle
//        .replaceAll(regexResult, s"($regexResult)")
//        .replaceAll(" {2}", " ")
//        .trim
//        .capitalize
//    }
//
//    val regexMatchesFour: Option[String] = "[0-9]{2}/[0-9]{2}".r findFirstIn lowerTitle
//    if (regexMatchesFour.isDefined) {
//      val regexResult = regexMatchesFour.toString.replaceAll("[A-z()]", "").trim
//
//      return lowerTitle
//        .replaceAll(regexResult, s"($regexResult)")
//        .replaceAll(" {2}", " ")
//        .trim
//        .capitalize
//    }

    lowerTitle
      .replaceAll(" {2}", " ")
      .trim
      .capitalize
  }
}
