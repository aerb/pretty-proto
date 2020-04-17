package ca.aerb.prettyproto.parser

class UnexpectedToken(
  val unexpected: ProtoTokenizer.Token,
  val innerFailed: Node?,
  val parseContext: ParseContext
) : Exception() {
  data class ParseContext(
    val text: String,
    val localIndex: Int,
    val absoluteIndex: Int
  )
}

fun parseNested(
  block: () -> Unit,
  failure: (failedNode: Node?) -> Node
) {
  try {
    block()
  } catch (e: UnexpectedToken) {
    throw UnexpectedToken(
      unexpected = e.unexpected,
      innerFailed = failure(e.innerFailed),
      parseContext = e.parseContext
    )
  }
}