package pl.allegro.stypinski.recruitmenttask.infrastructure.graphql

import com.netflix.graphql.dgs.*
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubRepository
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubUser
import pl.allegro.stypinski.recruitmenttask.repositories.RepositoriesService
import pl.allegro.stypinski.recruitmenttask.repositories.UsersService

@DgsComponent
class GithubRepositoriesDataFetcher(
    private val repositoriesService: RepositoriesService,
    private val usersService: UsersService
) {

    @DgsData(parentType = "Query", field = "user")
    fun user(@InputArgument username: String): GithubUser? {
        return usersService.getUser(username)
    }

    @DgsData(parentType = "User", field = "username")
    fun username(dfe: DgsDataFetchingEnvironment): String? {
        return dfe.getSource<GithubUser>().login
    }

    @DgsData(parentType = "User", field = "repositories")
    fun repositories(@InputArgument page: Int?,
                     @InputArgument perPage: Int?,
                     @InputArgument type: String?,
                     @InputArgument sort: String?,
                     @InputArgument direction: String?,
                     dfe: DgsDataFetchingEnvironment): RepositoriesResponse {
        val username = dfe.getSource<GithubUser>().login
        val userRepositories = repositoriesService.getRepositories(
            username = username,
            page = page ?: 1,
            perPage = perPage ?: 30,
            type = type,
            sort = sort,
            sortDirection = direction
        ).content

        return RepositoriesResponse(username, userRepositories ?: listOf())
    }

    @DgsData(parentType = "Repositories", field = "totalStargazers")
    fun totalStargazers(dfe: DgsDataFetchingEnvironment): Int {
        val username = dfe.getSource<RepositoriesResponse>().username
        return repositoriesService.getStargazersSum(username).toInt()
    }
}

class RepositoriesResponse (
    val username: String,
    val nodes: List<GithubRepository>
)
