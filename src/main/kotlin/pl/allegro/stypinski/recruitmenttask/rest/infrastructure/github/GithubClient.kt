package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils.GithubLinkHeaderParser
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils.GithubQueryParamValidator
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils.ParsedLinkHeader
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.page.Page
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.page.PageInfo
import java.net.URI
import java.util.*

class GithubClient (
    private val webClient: WebClient
) {

    companion object {
        private const val ACCEPT_HEADER: String = "application/vnd.github.v3+json"
    }

    fun getRepositories(username: String, type: String?, sort: String?, sortDirection: String?, perPage: Int, page: Int): Page<List<Repository>> {
        val response = webClient.get()
            .uri(createUriForRepositories(username, type, sort, sortDirection, perPage, page).toString())
            .headers { h ->
                h.set(HttpHeaders.AUTHORIZATION, "Bearer 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")
                h.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER))
            }
            .retrieve()
            .toEntity(typeReference<List<Repository>>())
            .block()

        val repos = parseResponseToGCRepositories(response!!, page)

        return Page(
            content = repos.repositories,
            pageInfo = PageInfo(
                currentPage = GithubQueryParamValidator.validatePage(page),
                totalPages = repos.linkHeader.totalPages,
                perPage = GithubQueryParamValidator.validatePerPage(perPage),
                sortBy = GithubQueryParamValidator.validateSortBy(sort).orElse(null),
                sortDirection = GithubQueryParamValidator.validateSortDirection(sortDirection).orElse(null)
            )
        )
    }

    private fun parseResponseToGCRepositories(responseEntity: ResponseEntity<List<Repository>>, currentPage: Int): GCRepositories {
        val headers = responseEntity.headers

        val linkHeader = headers[HttpHeaders.LINK]?.get(0)

        return GCRepositories(
            repositories = responseEntity.body!!,
            linkHeader = GithubLinkHeaderParser.parseLinkHeader(linkHeader, currentPage)
        )
    }

    private fun createUriForRepositories(username: String, type: String?, sort: String?, sortDirection: String?, perPage: Int, page: Int): URI {
        return UriComponentsBuilder.newInstance()
            .path("/users/{username}/repos")
            .queryParamIfPresent("type", GithubQueryParamValidator.validateType(type))
            .queryParamIfPresent("sort", GithubQueryParamValidator.validateSortBy(sort))
            .queryParamIfPresent("direction", GithubQueryParamValidator.validateSortDirection(sortDirection))
            .queryParam("per_page", GithubQueryParamValidator.validatePerPage(perPage))
            .queryParam("page", GithubQueryParamValidator.validatePage(page))
            .buildAndExpand(username)
            .toUri()
    }
}

class GCRepositories (
    val repositories: List<Repository>,
    val linkHeader: ParsedLinkHeader
)

inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}
