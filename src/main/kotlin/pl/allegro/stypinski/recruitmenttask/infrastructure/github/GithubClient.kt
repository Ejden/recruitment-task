package pl.allegro.stypinski.recruitmenttask.infrastructure.github

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.GithubLinkHeaderParser
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.GithubQueryParamValidator
import pl.allegro.stypinski.recruitmenttask.common.Page
import pl.allegro.stypinski.recruitmenttask.common.PageInfo
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.ParsedLinkHeader
import java.lang.RuntimeException
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.ceil
import kotlin.streams.toList

class GithubClient (
    private val webClient: WebClient,
    private val executor: Executor
) {

    companion object {
        private const val ACCEPT_HEADER: String = "application/vnd.github.v3+json"
    }

    fun getUser(username: String): GithubUser? {
        val response = webClient.get()
            .uri(createUriForUser(username))
            .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
            .retrieve()
            .toEntity(GithubUserResponse::class.java)
            .block()

        return response?.body?.toGithubUser()
    }


    fun getRepositories(username: String, type: String? = null, sort: String? = null, sortDirection: String? = null, perPage: Int, page: Int): Page<List<GithubRepository>> {
        val response = webClient.get()
            .uri(createUriForRepositories(username, type, sort, sortDirection, perPage, page))
            .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
            .retrieve()
            .toEntityList(GithubRepositoryResponse::class.java)
            .block()

        val repositories = response?.body?.map { it.toGithubRepository() }
        val linkHeader = response?.headers?.get(HttpHeaders.LINK)?.get(0)
        val parsedLinkHeader = GithubLinkHeaderParser.parseLinkHeader(linkHeader)

        return Page(
            content = repositories,
            pageInfo = PageInfo(
                currentPage = GithubQueryParamValidator.validatePage(page),
                totalPages = countTotalPages(parsedLinkHeader),
                perPage = GithubQueryParamValidator.validatePerPage(perPage),
                sortBy = GithubQueryParamValidator.validateSortBy(sort).orElse(null),
                sortDirection = GithubQueryParamValidator.validateSortDirection(sortDirection).orElse(null)
            )
        )
    }

    private fun countTotalPages(linkHeader: ParsedLinkHeader): Int {
        val pageRegex = "[&?]page=(\\d+)"
        val lastPageRegex = Regex(pageRegex)

        if (linkHeader.lastPageUrl != null) {
            // I'm not checking for nulls, because I know that's a http standard for providing cursor for pages
            return lastPageRegex.find(linkHeader.lastPageUrl)!!.groups[1]!!.value.toInt()
        }

        if (linkHeader.firstPageUrl != null) {
            // I'm not checking for nulls, because I know that's a http standard for providing cursor for pages
            // If i'm on the last page I know that this page is the previous page + 1
            return lastPageRegex.find(linkHeader.previousPageUrl!!)!!.groups[1]!!.value.toInt() + 1
        }

        // If there is no firstPage or lastPage header in github response I know that there is just 1 page
        return 1
    }

    fun getStargazersSum(username: String): Long {
        val user = getUser(username) ?: throw UserNotFoundException(username)

        // Count how many pages are required to get all user repositories and round this number up
        val requestedPages = ceil(user.publicRepos.toDouble() / GithubQueryParamValidator.MAX_PER_PAGE).toInt()

        val repositoryFutures = (1..requestedPages)
            .map { createUriForRepositories(username = username, perPage = 100, page = it).toString() }
            .map { CompletableFuture.supplyAsync({ getPageOfRepositories(it) }, executor) }

        return repositoryFutures
            .map { it.join() }
            .flatten()
            .map { it.toGithubRepository() }
            .sumOf { it.stargazersCount }
    }

    private fun getPageOfRepositories(url: String): MutableList<GithubRepositoryResponse> {
        return webClient.get()
            .uri(url)
            .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
            .retrieve()
            .toEntityList(GithubRepositoryResponse::class.java)
            .block()?.body!!
    }

    private fun createUriForRepositories(username: String, type: String? = null, sort: String? = null, sortDirection: String? = null, perPage: Int, page: Int): String {
        return UriComponentsBuilder.newInstance()
            .path("/users/{username}/repos")
            .queryParamIfPresent("type", GithubQueryParamValidator.validateType(type))
            .queryParamIfPresent("sort", GithubQueryParamValidator.validateSortBy(sort))
            .queryParamIfPresent("direction", GithubQueryParamValidator.validateSortDirection(sortDirection))
            .queryParam("per_page", GithubQueryParamValidator.validatePerPage(perPage))
            .queryParam("page", GithubQueryParamValidator.validatePage(page))
            .buildAndExpand(username)
            .toUri()
            .toString()
    }

    private fun createUriForUser(username: String): String {
        return UriComponentsBuilder.newInstance()
            .path("/users/{username}")
            .buildAndExpand(username)
            .toUri()
            .toString()
    }

}

data class GithubUser (
    val id: Long,
    val login: String,
    val publicRepos: Long
)

// Wrapper for github snake case
private data class GithubUserResponse (
    val id: Long,
    val login: String,
    @JsonProperty("public_repos")
    val publicRepos: Long
) {
    fun toGithubUser(): GithubUser {
        return GithubUser(id, login, publicRepos)
    }
}

data class GithubRepository (
    val name: String,
    val stargazersCount: Long
)

// Wrapper for github snake case
private data class GithubRepositoryResponse (
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Long
) {
    fun toGithubRepository(): GithubRepository {
        return GithubRepository(name, stargazersCount)
    }
}

class UserNotFoundException(username: String): RuntimeException("User $username not found")
