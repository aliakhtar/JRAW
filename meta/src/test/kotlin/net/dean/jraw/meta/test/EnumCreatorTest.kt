package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.meta.EnumCreator
import net.dean.jraw.meta.ParsedEndpoint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class EnumCreatorTest : Spek({
    val endpoints = listOf(
        ParsedEndpoint(
            "GET",
            "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = false
        ),
        ParsedEndpoint(
            "POST",
            "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = true
        )
    )

    it("should generate an enum with unique identifiers") {
        val out = StringBuilder()
        EnumCreator(endpoints).writeTo(out)

        val identifiers = out.toString().split("\n").filter {
            it.trim().matches(Regex("[A-Z_]{3}+.*?[,;]"))
        }.map { it.trim() }

        identifiers.should.have.size(endpoints.size)
        identifiers[0].should.equal("""GET_FOO_BAR("GET", "/api/v1/foo/{bar}", "fooscope"),""")
        identifiers[1].should.equal("""POST_FOO_BAR("POST", "/api/v1/foo/{bar}", "fooscope");""")
    }

    it("should generate compilable code") {
        ensureCompilable { EnumCreator(endpoints).writeTo(it) }
    }
})
