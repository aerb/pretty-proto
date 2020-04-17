package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.Node
import ca.aerb.prettyproto.parser.ParseResult
import ca.aerb.prettyproto.parser.ProtoParser
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.div
import react.dom.input
import react.dom.span
import react.setState

interface RawTextInputProps : RProps {
  var onParsed: (ParseResult) -> Unit
}

interface RawTextInputState : RState {
  var rawText: String
}

class RawTextInput(
  props: RawTextInputProps
) : RComponent<RawTextInputProps, RawTextInputState>(props) {

  override fun RawTextInputState.init(props: RawTextInputProps) {
    rawText = "MyExampleProto{documents=[Document{name=Sandy Statement, key=0, " +
        "url=https://test.biz&file_key=sandy.png, type=STONKS, document_date=1970, " +
        "email_forwardable=false}, Document{name=Randy Statement, key=42, " +
        "url=https://test.biz/login?token=:~), type=STONKS, document_date=1971, " +
        "email_forwardable=false}]}"
    props.onParsed(ProtoParser(rawText).parseRoot())
  }

  override fun RBuilder.render() {
    div("input-group") {
      div("input-group-prepend") {
        span("input-group-text") { +"Proto Dump" }
      }
      input(type = InputType.text, classes = "form-control") {
        attrs {
          value = state.rawText
          placeholder = "The output of your proto's toString() here"
          onChangeFunction = ::onInputChanged

        }
      }
    }
  }

  private fun onInputChanged(event: Event) {
    val text = (event.target as HTMLInputElement).value
    setState {
      rawText = text
    }
    props.onParsed(ProtoParser(text).parseRoot())
  }
}

fun RBuilder.rawTextInput(onParsed: (ParseResult) -> Unit): ReactElement =
    child(RawTextInput::class) {
      attrs.onParsed = onParsed
    }
