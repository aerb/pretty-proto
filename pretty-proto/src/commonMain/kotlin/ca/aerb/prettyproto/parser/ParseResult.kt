package ca.aerb.prettyproto.parser

sealed class ParseResult {
  class Success(val node: Node) : ParseResult()
  class Partial(val error: UnexpectedToken) : ParseResult()
}