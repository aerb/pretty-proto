package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.Node
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
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

      div("mb-3") {
        +"A utility to format the toString() output of "
        a("https://square.github.io/wire/") { +"Wire" }
        +" generated Protos."
      }

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
      div {
        +"© 2019 Adam Erb. Open source on "
        a("https://github.com/aerb/pretty-proto") { +"Github" }
        +"."
      }
    }
  }
}

fun RBuilder.app() = child(App::class) {}
