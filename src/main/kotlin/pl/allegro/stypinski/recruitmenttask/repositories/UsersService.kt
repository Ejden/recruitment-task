package pl.allegro.stypinski.recruitmenttask.repositories

import org.springframework.stereotype.Service
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubClient
import pl.allegro.stypinski.recruitmenttask.infrastructure.github.GithubUser

@Service
class UsersService(private val githubClient: GithubClient) {

    fun getUser(username: String): GithubUser? {
        return githubClient.getUser(username)
    }
}