package ca.aerb.prettyproto

import ca.aerb.prettyproto.parser.Node.Object
import ca.aerb.prettyproto.parser.Node.Single
import ca.aerb.prettyproto.parser.ProtoParser
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun justAnEmptyObject() {
        assertEquals(
            expected = Object(
                name = "A",
                fields = emptyMap()
            ),
            actual = ProtoParser("A{}").parseRoot()
        )
    }

    @Test
    fun objectWithField() {
        assertEquals(
            expected = Object(
                name = "A",
                fields = mapOf("b" to Single("wat"))
            ),
            actual = ProtoParser("A{b=wat}").parseRoot()
        )
    }

    @Test
    fun objectWithMultipleFields() {
        assertEquals(
            expected = Object(
                name = "A",
                fields = mapOf("b" to Single("wat"), "c" to Single("test"))
            ),
            actual = ProtoParser("A{b=wat, c=test}").parseRoot()
        )
    }

    @Test
    fun objectWithNestedObject() {
        assertEquals(
            expected = Object(
                name = "A",
                fields = mapOf(
                    "b" to Object(
                        name = "B",
                        fields = mapOf("c" to Single("c"))
                    )
                )
            ),
            actual = ProtoParser("A{b=B{c=c}}").parseRoot()
        )
    }

    @Test
    fun escapedCharacters() {
        assertEquals(
            expected = Object(
                name = "A",
                fields = mapOf("b" to Single(""",{}[]\"""))
            ),
            actual = ProtoParser("""A{b=\,\{\}\[\]\\}""").parseRoot()
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
}