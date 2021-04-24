package pl.allegro.stypinski.recruitmenttask.rest.repositories

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.GithubRepository
import pl.allegro.stypinski.recruitmenttask.common.Page

@Controller
@RequestMapping("api/users")
class RepositoriesController(val repositoriesService: RepositoriesService) {

    @GetMapping("/{username}/repositories")
    fun getRepositoriesListing(
        @PathVariable username: String,
        @RequestParam type: String?,
        @RequestParam sort: String?,
        @RequestParam direction: String?,
        @RequestParam(defaultValue = "30", name = "per_page") perPage: Int,
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<Page<List<GithubRepository>>> {
        val response = repositoriesService.getRepositories(username, type, sort, direction, perPage, page)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{username}/stargazers")
    fun getStargazersSum(@PathVariable username: String): ResponseEntity<StargazersResponse> {
        return ResponseEntity.ok(StargazersResponse(repositoriesService.getStargazersSum(username)))
    }

}

data class StargazersResponse (
    val stargazersSum: Long
)
