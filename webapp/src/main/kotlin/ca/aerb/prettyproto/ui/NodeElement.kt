package ca.aerb.prettyproto.ui

import ca.aerb.prettyproto.parser.Node
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.table
import react.dom.td
import react.dom.tr

interface NodeProps : RProps {
  var node: Node
}

interface NodeState : RState

class NodeElement(props: NodeProps) : RComponent<NodeProps, NodeState>(props) {
  override fun RBuilder.render() {
    when (val node = props.node) {
      is Node.Object -> {
        div("rounded-lg bg-light font-weight-bold") {
          +node.name
          table {
            node.fields.forEach { (name, value) ->
              tr {
                td {
                  attrs.set("valign", "top")
                  +"${name}="
                }
                td {
                  attrs.set("valign", "top")
                  nodeElement(value)
                }
              }
            }
          }
        }
      }
      is Node.Array -> {
        table {
          node.items.forEachIndexed { index, child ->
            tr {
              td {
                attrs.set("valign", "top")
                +"$index="
              }
              td {
                attrs.set("valign", "top")
                nodeElement(child)
              }
            }
          }
        }
      }
      is Node.Single -> {
        div("single-node") {
          +node.value
        }
      }
    }
  }
}

fun RBuilder.nodeElement(node: Node?) {
  node ?: return

  child(NodeElement::class) {
    attrs.node = node
  }
}