package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.Node
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
import kotlin.browser.document
import kotlin.browser.window

external fun decodeURIComponent(encodedURI: String): String
external fun encodeURIComponent(text: String): String

interface RawTextInputProps : RProps {
  var onParsed: (Node) -> Unit
}

interface RawTextInputState : RState {
  var rawText: String
}

class RawTextInput(
  props: RawTextInputProps
) : RComponent<RawTextInputProps, RawTextInputState>(props) {

  override fun RawTextInputState.init(props: RawTextInputProps) {
    rawText = decodeURIComponent(window.location.search.trimStart('?'))
    try {
      if (rawText.isNotEmpty()) {
        props.onParsed(ProtoParser(rawText).parseRoot())
      }
    } catch (e: Exception) {
      println("Failed to parse with $e")
    }
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
    val encoded = encodeURIComponent(text)
    window.history.replaceState(encoded, document.title, "?${encoded}")
    props.onParsed(ProtoParser(text).parseRoot())
  }
}

fun RBuilder.rawTextInput(onParsed: (Node) -> Unit): ReactElement =
    child(RawTextInput::class) {
      attrs.onParsed = onParsed
    }
