package pl.allegro.stypinski.recruitmenttask.infrastructure.github

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.GithubLinkHeaderParser
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.GithubQueryParamValidator
import pl.allegro.stypinski.recruitmenttask.common.Page
import pl.allegro.stypinski.recruitmenttask.common.PageInfo
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils.ParsedLinkHeader
import java.io.Serializable
import java.lang.RuntimeException
import java.net.URI
import java.util.*
import kotlin.math.ceil

class GithubClient (
    private val webClient: WebClient
) {

    companion object {
        private const val ACCEPT_HEADER: String = "application/vnd.github.v3+json"
    }

    fun getUser(username: String): GithubUser? {
        val response = webClient.get()
            .uri(createUriForUser(username).toString())
            .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
            .retrieve()
            .toEntity(GithubUser::class.java)
            .block()

        return response?.body
    }


    fun getRepositories(username: String, type: String? = null, sort: String? = null, sortDirection: String? = null, perPage: Int, page: Int): Page<List<GithubRepository>> {
        val response = webClient.get()
            .uri(createUriForRepositories(username, type, sort, sortDirection, perPage, page).toString())
            .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
            .retrieve()
            .toEntityList(GithubRepository::class.java)
            .block()

        val linkHeader = response?.headers?.get(HttpHeaders.LINK)?.get(0)
        val parsedLinkHeader = GithubLinkHeaderParser.parseLinkHeader(linkHeader)

        return Page(
            content = response?.body,
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
        val pages = mutableListOf<Int>()
        var sumOfStargazers = 0L
        val threads = mutableListOf<Thread>()

        for (i in 1..requestedPages) {
            pages.add(i)
        }

        for (i in 1..requestedPages) {
            val run = Runnable {
                val response = webClient.get()
                    .uri(createUriForRepositories(username = username, perPage = 100, page = i).toString())
                    .headers { it.accept = Collections.singletonList(MediaType.valueOf(ACCEPT_HEADER)) }
                    .retrieve()
                    .toEntity(typeReference<List<GithubRepository>>())
                    .block()

                synchronized(sumOfStargazers) {
                    response?.body?.forEach { repo -> sumOfStargazers += repo.stargazersCount }
                }
            }

            val thread = Thread(run)
            threads.add(thread)
            thread.start()
        }

        threads.forEach(Thread::join)

        return sumOfStargazers
    }

//    private fun getRepos(username: String, perPage: Int, page: Int): Mono<List<GithubRepository>> {
//        return webClient.get()
//            .uri(createUriForRepositories(username = username, perPage = perPage, page = page).toString())
//            .retrieve()
//            .bodyToMono(typeReference<List<GithubRepository>>())
//    }

    private fun createUriForRepositories(username: String, type: String? = null, sort: String? = null, sortDirection: String? = null, perPage: Int, page: Int): URI {
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

    private fun createUriForUser(username: String): URI {
        return UriComponentsBuilder.newInstance()
            .path("/users/{username}")
            .buildAndExpand(username)
            .toUri()
    }

}

data class GithubUser (
    val id: Long,
    val login: String,
    @JsonProperty("public_repos")
    val publicRepos: Long
)

class GithubRepository (
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Long
): Serializable

inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

class UserNotFoundException(username: String): RuntimeException("User $username not found")
