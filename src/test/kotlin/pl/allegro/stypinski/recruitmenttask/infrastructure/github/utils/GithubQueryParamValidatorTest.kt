package pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.GithubQueryParamValidator
import java.util.*
import java.util.stream.Stream

internal class GithubQueryParamValidatorTest {
    companion object {

        @JvmStatic
        fun provideTestCasesForShouldValidateType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("all", Optional.of("all")),
                Arguments.of("owner", Optional.of("owner")),
                Arguments.of("member", Optional.of("member")),
                Arguments.of("not_exists", Optional.empty<String>()),
                Arguments.of(null, Optional.empty<String>())
            )
        }

        @JvmStatic
        fun provideTestCasesForShouldValidateSortBy(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("created", Optional.of("created")),
                Arguments.of("updated", Optional.of("updated")),
                Arguments.of("pushed", Optional.of("pushed")),
                Arguments.of("full_name", Optional.of("full_name")),
                Arguments.of("not_exists", Optional.empty<String>()),
                Arguments.of(null, Optional.empty<String>())
            )
        }

        @JvmStatic
        fun provideTestCasesForShouldValidateSortDirection(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("asc", Optional.of("asc")),
                Arguments.of("desc", Optional.of("desc")),
                Arguments.of("not_exists", Optional.empty<String>()),
                Arguments.of(null, Optional.empty<String>())
            )
        }

        @JvmStatic
        fun provideTestCasesForShouldValidatePerPage(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(-6, GithubQueryParamValidator.DEFAULT_PER_PAGE),
                Arguments.of(0, GithubQueryParamValidator.DEFAULT_PER_PAGE),
                Arguments.of(1, 1),
                Arguments.of(20, 20),
                Arguments.of(54, 54),
                Arguments.of(GithubQueryParamValidator.MAX_PER_PAGE, GithubQueryParamValidator.MAX_PER_PAGE),
                Arguments.of(GithubQueryParamValidator.MAX_PER_PAGE + 1, GithubQueryParamValidator.MAX_PER_PAGE)
            )
        }

        @JvmStatic
        fun provideTestCasesForShouldValidatePage(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(-6, 1),
                Arguments.of(0, 1),
                Arguments.of(1, 1),
                Arguments.of(4,4),
                Arguments.of(126, 126)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldValidateType")
    fun shouldValidateType(type: String?, expected: Optional<String>) {
        // When
        val validatedType = GithubQueryParamValidator.validateType(type)
        // Then
        assertEquals(expected.orElse(null), validatedType.orElse(null))
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldValidateSortBy")
    fun shouldValidateSortBy(sortBy: String?, expected: Optional<String>) {
        // When
        val validatedSortBy = GithubQueryParamValidator.validateSortBy(sortBy)
        // Then
        assertEquals(expected.orElse(null), validatedSortBy.orElse(null))
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldValidateSortDirection")
    fun shouldValidateSortDirection(sortDirection: String?, expected: Optional<String>) {
        // When
        val validatedSortDirection = GithubQueryParamValidator.validateSortDirection(sortDirection)
        // Then
        assertEquals(expected.orElse(null), validatedSortDirection.orElse(null))
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldValidatePerPage")
    fun shouldValidatePerPage(perPage: Int, expected: Int) {
        // When
        val validatedPerPage = GithubQueryParamValidator.validatePerPage(perPage)
        // Then
        assertEquals(expected, validatedPerPage)
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldValidatePage")
    fun shouldValidatePage(page: Int, expected: Int) {
        // When
        val validatedPage = GithubQueryParamValidator.validatePage(page)
        // Then
        assertEquals(expected, validatedPage)
    }
}