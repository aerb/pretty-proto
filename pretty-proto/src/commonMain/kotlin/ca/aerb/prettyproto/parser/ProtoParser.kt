package ca.aerb.prettyproto.parser

import ca.aerb.prettyproto.parser.Node.Array
import ca.aerb.prettyproto.parser.Node.Single
import ca.aerb.prettyproto.parser.ProtoTokenizer.FieldContent.UpcomingHint
import ca.aerb.prettyproto.parser.ProtoTokenizer.Token

class ProtoParser(private val text: String) {

  private val tokenizer = ProtoTokenizer(text)

  fun parseRoot(): ParseResult {
    return try {
      ParseResult.Success(parseValue())
    } catch (e: UnexpectedToken) {
      ParseResult.Partial(e)
    }
  }

  private inline fun <reified T : Token> expectNext(): T {
    val next = tokenizer.next()
    if (next !is T) {
      throwUnexpected(next)
    }
    return next
  }

  private fun throwUnexpected(token: Token, currentNode: Node? = null): Nothing {
    val curr = tokenizer.index
    val range = 10
    val startIndex = (curr - range).coerceAtLeast(0)
    val endIndex = (curr + range).coerceAtMost(text.length)
    throw UnexpectedToken(
      unexpected = token,
      innerFailed = currentNode,
      parseContext = UnexpectedToken.ParseContext(
        text = text.substring(startIndex, endIndex),
        localIndex = curr - startIndex,
        absoluteIndex = curr
      )
    )
  }


  private fun parseField(): Node {
    expectNext<Token.Assign>()
    return parseValue()
  }

  private fun parseValue(): Node {
    val content = tokenizer.readFieldContent()
    return when (content.hint) {
      UpcomingHint.EndOfContent -> Single(content.value)
      UpcomingHint.ObjectStart -> parseObject(content.value)
      UpcomingHint.ArrayStart -> parseArray()
    }
  }

  private fun parseArray(): Array {
    expectNext<Token.OpenArray>()
    val items = ArrayList<Node>()

    fun parseItem() {
      parseNested(
        block = { items += parseValue() },
        failure = { failedNode ->
          if (failedNode != null) items += failedNode
          Array(items)
        }
      )
    }

    var first = true
    while (true) {
      if (first) {
        parseItem()
        first = false
      } else {
        when (val next = tokenizer.next()) {
          Token.Comma -> parseItem()
          Token.CloseArray -> return Array(items)
          else -> throwUnexpected(next, Array(items))
        }
      }
    }
  }

  private fun parseObject(name: String): Node {
    expectNext<Token.OpenObject>()
    val fields = LinkedHashMap<String, Node>()

    fun parseField(fieldName: String) {
      check(fieldName !in fields) { "Field $fieldName already present." }
      parseNested(
        block = { fields[fieldName] = parseField() },
        failure = { failedNode ->
          if (failedNode != null) fields[fieldName] = failedNode
          Node.Object(name, fields)
        }
      )
    }

    var first = true
    while (true) {
      when (val next = tokenizer.next()) {
        is Token.Symbol -> {
          check(first)
          parseField(next.value)
        }
        is Token.Comma -> {
          check(!first)
          val fieldName = expectNext<Token.Symbol>()
          parseField(fieldName.value)
        }
        is Token.CloseObject -> {
          return Node.Object(name, fields)
        }
        else -> throwUnexpected(next, Node.Object(name, fields))
      }
      first = false
    }
  }
}