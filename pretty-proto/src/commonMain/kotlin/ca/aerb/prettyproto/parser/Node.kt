package ca.aerb.prettyproto.parser

sealed class Node {
  data class Object(val name: String, val fields: Map<String, Node>) : Node()
  data class Single(val value: String) : Node()
  data class Array(val items: List<Node>) : Node()

  fun toPrettyString(): String = toPrettyString(this, 0)
}

fun indent(spaces: Int): String = " ".repeat(spaces)

private fun toPrettyString(node: Node, depth: Int): String {
  return when (node) {
    is Node.Object -> {
      buildString {
        append(node.name).append("{\n")
        val nextDepth = depth + 2
        val childIndent = indent(nextDepth)
        val lastIndex = node.fields.size - 1
        node.fields.toList().forEachIndexed { index, (name, value) ->
          append(childIndent).append(name).append("=").append(
            toPrettyString(value, nextDepth)
          )
          if (index == lastIndex) {
            append("\n")
          } else {
            append(",\n")
          }
        }
        append(indent(depth)).append("}")
      }
    }
    is Node.Single -> node.value
    is Node.Array -> {
      buildString {
        append("[\n")
        val nextDepth = depth + 2
        val childIndent = indent(nextDepth)
        val lastIndex = node.items.size - 1
        node.items.forEachIndexed { index, child ->
          append(childIndent).append(toPrettyString(child, nextDepth))
          if (index == lastIndex) {
            append("\n")
          } else {
            append(",\n")
          }
        }
        append(indent(depth)).append("]")
      }
    }
  }
}