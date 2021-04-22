package pl.allegro.stypinski.recruitmenttask.rest.core.repositories

import org.springframework.stereotype.Service
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.GCRepositories
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.GithubClient
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.Repository
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.page.Page

@Service
class RepositoriesService (val githubClient: GithubClient) {

    fun getRepositories(username: String, type: String?, sort: String?, sortDirection: String?, perPage: Int,
                        page: Int): Page<List<Repository>> {
        return githubClient.getRepositories(username, type, sort, sortDirection, perPage, page)
    }
}