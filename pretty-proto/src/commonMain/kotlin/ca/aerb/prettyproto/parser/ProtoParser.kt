package ca.aerb.prettyproto.parser

import ca.aerb.prettyproto.parser.Node.Array
import ca.aerb.prettyproto.parser.Node.Single
import ca.aerb.prettyproto.parser.ProtoTokenizer.FieldContent
import ca.aerb.prettyproto.parser.ProtoTokenizer.Token

class ProtoParser(text: String) {

  private val tokenizer = ProtoTokenizer(text)

  fun parseRoot(): Node {
    val topLevel = expectNext<Token.Symbol>()
    return parseObject(topLevel.value)
  }

  private inline fun <reified T : Token> expectNext(): T {
    val next = tokenizer.next()
    if (next !is T) {
      throw IllegalStateException("Expected token ${T::class} but got $next")
    }
    return next
  }

  private fun unexpectedToken(token: Token): Nothing =
    throw IllegalStateException("Unexpected token $token")

  private fun parseField(): Node {
    expectNext<Token.Assign>()
    return parseValue()
  }

  private fun parseValue(): Node {
    val content = tokenizer.readFieldContent()
    return when (content.hint) {
      FieldContent.UpcomingHint.FieldEnd,
      FieldContent.UpcomingHint.ObjectEnd,
      FieldContent.UpcomingHint.ArrayEnd -> Single(content.value)
      FieldContent.UpcomingHint.ObjectStart -> parseObject(content.value)
      FieldContent.UpcomingHint.ArrayStart -> parseArray()
    }
  }

  private fun parseArray(): Array {
    expectNext<Token.OpenArray>()
    val items = ArrayList<Node>()
    var first = true
    while (true) {
      if (first) {
        items += parseValue()
        first = false
      } else {
        when (val next = tokenizer.next()) {
          Token.Comma -> items += parseValue()
          Token.CloseArray -> return Array(items)
          else -> unexpectedToken(next)
        }
      }
    }
  }

  private fun parseObject(name: String): Node {
    expectNext<Token.OpenObject>()
    val fields = LinkedHashMap<String, Node>()
    var first = true
    while (true) {
      when (val next = tokenizer.next()) {
        is Token.Symbol -> {
          check(first)
          fields[next.value] = parseField()
        }
        is Token.Comma -> {
          check(!first)
          val fieldName = expectNext<Token.Symbol>()
          fields[fieldName.value] = parseField()
        }
        is Token.CloseObject -> {
          return Node.Object(name, fields)
        }
        else -> unexpectedToken(next)
      }
      first = false
    }
  }
}