package pl.allegro.stypinski.recruitmenttask.repositories

import org.springframework.stereotype.Service
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubClient
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubRepository
import pl.allegro.stypinski.recruitmenttask.common.Page

@Service
class RepositoriesService (val githubClient: GithubClient) {

    fun getRepositories(username: String, type: String?, sort: String?, sortDirection: String?, perPage: Int,
                        page: Int): Page<List<GithubRepository>> {
        return githubClient.getRepositories(username, type, sort, sortDirection, perPage, page)
    }

    fun getStargazersSum(username: String): Long {
        return githubClient.getStargazersSum(username)
    }
}