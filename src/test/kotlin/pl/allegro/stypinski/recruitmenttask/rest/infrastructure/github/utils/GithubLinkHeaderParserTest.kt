package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class GithubLinkHeaderParserTest {

    companion object {
        fun provideTestCases(): Stream<Arguments> {
            return Stream.of(

            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    fun shouldParseHeader(header: String, expectedHeader: ParsedLinkHeader) {

    }
}