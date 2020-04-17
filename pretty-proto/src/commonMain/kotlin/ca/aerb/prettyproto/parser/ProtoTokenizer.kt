package ca.aerb.prettyproto.parser

import ca.aerb.prettyproto.parser.ProtoTokenizer.FieldContent.UpcomingHint

class ProtoTokenizer(private val text: String) {

  var index: Int = 0
    private set

  private fun peek(ahead: Int = 0): Char? {
    val i = index + ahead
    return if (i >= text.length) null
    else text[i]
  }

  private fun advance(by: Int = 1) {
    index += by
  }

  private fun readSymbol(): Token.Symbol {
    val sb = StringBuilder()
    while (true) {
      val curr = peek()
      if (curr.isSymbolChar()) {
        sb.append(curr)
        advance()
      } else {
        check(sb.isNotEmpty()) { "Unexpected empty symbol" }
        return Token.Symbol(sb.toString())
      }
    }
  }

  fun readFieldContent(): FieldContent {
    val sb = StringBuilder()
    if (peek() == '[' || (peek() == ' ' && peek(1) == '[')) {
      return FieldContent(UpcomingHint.ArrayStart)
    }

    while (true) {
      val curr = peek()
      val endOfField = when (curr) {
        '{' -> FieldContent(
          value = sb.toString().trim(),
          hint = UpcomingHint.ObjectStart
        )
        ',', '}', ']', null -> FieldContent(
          value = sb.toString(),
          hint = UpcomingHint.EndOfContent
        )
        else -> null
      }
      if (endOfField != null) {
        return endOfField
      }

      if (curr == '\\' && peek(1) in EscapedCharacter) {
        sb.append(peek(1))
        advance(by = 2)
      } else {
        sb.append(curr)
        advance()
      }
    }
  }

  fun next(): Token {
    var curr: Char
    while (true) {
      curr = peek() ?: return Token.EOF
      if (!curr.isWhitespace()) break
      else advance()
    }

    return if (curr.isSymbolChar()) {
      readSymbol()
    } else {
      advance()
      when(curr) {
        '{' -> Token.OpenObject
        '}' -> Token.CloseObject
        '=' -> Token.Assign
        ',' -> Token.Comma
        '[' -> Token.OpenArray
        ']' -> Token.CloseArray
        else -> throw RuntimeException("Unexpected character '$curr'(${curr.toInt()})")
      }
    }
  }

  data class FieldContent(val hint: UpcomingHint, val value: String = "") {
    enum class UpcomingHint {
      ObjectStart,
      ArrayStart,
      EndOfContent
    }
  }

  sealed class Token(private val debugText: String? = null) {
    data class Symbol(val value: String) : Token()
    object OpenObject : Token("OpenObject")
    object CloseObject : Token("CloseObject")
    object Assign : Token("Assign")
    object Comma: Token("Comma")
    object EOF: Token("EOF")
    object OpenArray : Token("OpenArray")
    object CloseArray : Token("CloseArray")

    override fun toString(): String = debugText ?: super.toString()
  }
}

private val EscapedCharacter = setOf(',', '{', '}', '[', ']', '\\')

private fun Char?.isSymbolChar(): Boolean =
    this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9' || this == '_'