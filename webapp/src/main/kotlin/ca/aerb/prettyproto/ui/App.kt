package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.ParseResult
import ca.aerb.prettyproto.parser.ProtoTokenizer.Token
import ca.aerb.prettyproto.parser.indent
import kotlinx.css.UserSelect
import kotlinx.css.userSelect
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.br
import react.dom.div
import react.dom.h1
import react.dom.pre
import react.setState
import styled.css
import styled.styledCode

interface AppState : RState {
  var node: ParseResult?
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

      rawTextInput { parseResult ->
        setState {
          node = parseResult
        }
      }
      br {  }
      state.node?.let {
        pre("rounded-lg bg-light pt-3 pb-3 pl-3 pr-3") {
          styledCode {
            css { userSelect = UserSelect.all }
            when (it) {
              is ParseResult.Success -> +it.node.toPrettyString()
              is ParseResult.Partial -> {
                val parseContext = it.error.parseContext
                if (it.error.unexpected is Token.ETX) {
                  +"Unexpected end of string at position ${parseContext.absoluteIndex}\n"
                  +"Near: ${parseContext.text}\n"
                  +"      ${indent(parseContext.text.length)}^\n"
                } else {
                  +"Unexpected token ${it.error.unexpected} at position ${parseContext.absoluteIndex}\n"
                  +"Near: ${parseContext.text}\n"
                  +"      ${indent((parseContext.localIndex - 1).coerceAtLeast(0))}^\n"
                }

                it.error.innerFailed?.let {
                  +"\nBest Effort:\n\n"
                  +it.toPrettyString()
                }
              }
            }
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
