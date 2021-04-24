package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class GithubLinkHeaderParserTest {

    companion object {
        private val githubBaseUrl = "https://api.github.com";

        @JvmStatic
        fun provideTestCases(): Stream<Arguments> {
            return Stream.of(
                // One url
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=1>; rel=\"prev\"",
                    ParsedLinkHeader(previousPageUrl = "${githubBaseUrl}/users/1234/repos?page=1")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20>; rel=\"next\"",
                    ParsedLinkHeader(nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=1>; rel=\"prev\"",
                    ParsedLinkHeader(previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=1")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20&per_page=2>; rel=\"next\"",
                    ParsedLinkHeader(nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20&per_page=2")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=1?sort=full_name&per_page=100&page=30>; rel=\"last\"",
                    ParsedLinkHeader(lastPageUrl = "${githubBaseUrl}/users/1234/repos?page=1?sort=full_name&per_page=100&page=30")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=1?sort=full_name&page=40&per_page=30>; rel=\"first\"",
                    ParsedLinkHeader(firstPageUrl = "${githubBaseUrl}/users/1234/repos?page=1?sort=full_name&page=40&per_page=30")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=1>; rel=\"prev\"",
                    ParsedLinkHeader(previousPageUrl = "${githubBaseUrl}/users/1234/repos?page=1")
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20>; rel=\"next\"",
                    ParsedLinkHeader(nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20")
                ),
                // Two urls
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=1>; rel=\"prev\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=3>; rel=\"next\"",
                    ParsedLinkHeader(
                        previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=1",
                        nextPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=3; rel=\"next\""
                    )
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20&per_page=5>; rel=\"next\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=3>; rel=\"prev\"",
                    ParsedLinkHeader(
                        nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20&per_page=5",
                        previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=3"
                    )
                ),
                // Three urls
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=1>; rel=\"prev\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=3>; rel=\"next\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=5>; rel=\"last\"",
                    ParsedLinkHeader(
                        previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=1",
                        nextPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=3",
                        lastPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=5"
                    )
                ),
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20&per_page=5>; rel=\"next\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=3>; rel=\"prev\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=1>; rel=\"first\"",
                    ParsedLinkHeader(
                        nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20&per_page=5",
                        previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=3",
                        firstPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=1"
                    )
                ),
                // Four urls
                Arguments.of(
                    "<${githubBaseUrl}/users/1234/repos?page=20&per_page=5>; rel=\"next\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=3>; rel=\"prev\", " +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=1>; rel=\"first\"," +
                    "<${githubBaseUrl}/users/1234/repos?per_page=30&page=5>; rel=\"last\"",
                    ParsedLinkHeader(
                        nextPageUrl = "${githubBaseUrl}/users/1234/repos?page=20&per_page=5",
                        previousPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=3",
                        firstPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=1",
                        lastPageUrl = "${githubBaseUrl}/users/1234/repos?per_page=30&page=5"
                    )
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    fun shouldParseHeader(header: String, expected: ParsedLinkHeader) {
        // When
        val parsedHeader = GithubLinkHeaderParser.parseLinkHeader(header)
        // Then
        assertEquals(expected.firstPageUrl, parsedHeader.firstPageUrl)
    }
}