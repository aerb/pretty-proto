package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.Node
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.br
import react.dom.code
import react.dom.div
import react.dom.h1
import react.dom.pre
import react.setState
import kotlin.browser.window

interface AppState : RState {
  var node: Node?
}

class App : RComponent<RProps, AppState>() {

  override fun RBuilder.render() {
    div("container") {
      br {  }
      h1 { +"Pretty Proto" }
      br {  }
      rawTextInput { parsedNode ->
        setState {
          node = parsedNode
        }
      }
      br {  }
      state.node?.let {
        pre("rounded-lg bg-light pt-3 pb-3 pl-3 pr-3") {
          code {
            +it.toPrettyString()
          }
        }
      }
    }
  }
}

fun RBuilder.app() = child(App::class) {}
