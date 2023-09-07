package helpers

import ujson.Value.Value

class Simple {
  def verifyIfImageLinkExists(iteratorElement: Value): Boolean = {
    try {
      iteratorElement("original")

      true
    } catch {
      case _: NoSuchElementException => false
    }
  }
}
