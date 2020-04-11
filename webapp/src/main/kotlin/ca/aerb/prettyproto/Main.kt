package ca.aerb.prettyproto

import ca.aerb.prettyproto.ui.app
import react.dom.render
import kotlin.browser.document

fun main() {
  render(document.getElementById("root")) { app() }
}
