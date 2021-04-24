package pl.allegro.stypinski.recruitmenttask.infrastructure

import com.netflix.graphql.dgs.*
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubClient
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubRepository
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubUser

@DgsComponent
class GithubRepositoriesDataFetcher(private val githubClient: GithubClient) {

    @DgsData(parentType = "Query", field = "user")
    fun user(@InputArgument username: String): GithubUser? {
        return githubClient.getUser(username)
    }

    @DgsData(parentType = "User", field = "username")
    fun username(dfe: DgsDataFetchingEnvironment): String? {
        return dfe.getSource<GithubUser>().login
    }

    @DgsData(parentType = "User", field = "repositories")
    fun repositories(@InputArgument page: Int?, @InputArgument perPage: Int?, dfe: DgsDataFetchingEnvironment): RepositoriesResponse {
        val username = dfe.getSource<GithubUser>().login
        val userRepositories = githubClient.getRepositories(
            username = username,
            page = page ?: 1,
            perPage = perPage ?: 30
        ).content

        return RepositoriesResponse(username, userRepositories ?: listOf())
    }

    @DgsData(parentType = "Repositories", field = "totalStargazers")
    fun totalStargazers(dfe: DgsDataFetchingEnvironment): Int {
        val username = dfe.getSource<RepositoriesResponse>().username
        return githubClient.getStargazersSum(username).toInt()
    }
}

class RepositoriesResponse (
    val username: String,
    val nodes: List<GithubRepository>
)