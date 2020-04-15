package ca.aerb.prettyproto

import ca.aerb.prettyproto.parser.Node
import ca.aerb.prettyproto.parser.ProtoParser
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun justAnEmptyObject() {
        assertEquals(
            expected = Node.Object(
                name = "A",
                fields = emptyMap()
            ),
            actual = ProtoParser("A{}").parseRoot()
        )
    }

    @Test
    fun objectWithField() {
        assertEquals(
            expected = Node.Object(
                name = "A",
                fields = mapOf("b" to Node.Single("wat"))
            ),
            actual = ProtoParser("A{b=wat}").parseRoot()
        )
    }

    @Test
    fun objectWithMultipleFields() {
        assertEquals(
            expected = Node.Object(
                name = "A",
                fields = mapOf("b" to Node.Single("wat"), "c" to Node.Single("test"))
            ),
            actual = ProtoParser("A{b=wat, c=test}").parseRoot()
        )
    }

    @Test
    fun objectWithNestedObject() {
        assertEquals(
            expected = Node.Object(
                name = "A",
                fields = mapOf(
                    "b" to Node.Object(
                        name = "B",
                        fields = mapOf("c" to Node.Single("c"))
                    )
                )
            ),
            actual = ProtoParser("A{b=B{c=c}}").parseRoot()
        )
    }

    @Test
    fun escapedCharacters() {
        assertEquals(
            expected = Node.Object(
                name = "A",
                fields = mapOf("b" to Node.Single(""",{}[]\"""))
            ),
            actual = ProtoParser("""A{b=\,\{\}\[\]\\}""").parseRoot()
        )
    }

    @Test
    fun nestedArrays() {
        assertEquals(
            expected = Node.Object(
                name = "NestedArray",
                fields = mapOf(
                    "my_list" to nodeArrayOf(
                        nodeArrayOf(Node.Single("a")),
                        nodeArrayOf(nodeArrayOf(Node.Single("b")))
                    )
                )
            ),
            actual = ProtoParser("NestedArray{my_list=[[a], [[b]]]}").parseRoot()
        )
    }

    @Test
    fun bigOlBlobLetsJustNotCrashForNow() {
        ProtoParser("MyNestedProto{documents=[Document{name=Sept 30\\, 2019 Statement, key=0, " +
            "url=https://test.biz&file_key=2019101601, type=STONKS, " +
            "document_date=1569888000000, email_forwardable=false}, Document{name=Oct 31\\, " +
            "2019 Statement, key=2019110501, url=https://test.biz/login?token=:~), type=STONKS, " +
            "document_date=1572566400000, email_forwardable=false}]}").parseRoot()
    }

    private fun nodeArrayOf(vararg node: Node) = Node.Array(listOf(*node))
}