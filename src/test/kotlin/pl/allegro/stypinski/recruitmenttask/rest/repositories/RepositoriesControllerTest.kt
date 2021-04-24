package pl.allegro.stypinski.recruitmenttask.rest.repositories

import GithubClientWireMockExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.io.File

@SpringBootTest
@AutoConfigureMockMvc
@Import(GithubClientWireMockExtension::class)
@ExtendWith(GithubClientWireMockExtension::class)
internal class RepositoriesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Test
    fun `Should get repositories listing for user`() {
        // Given
        val expectedBody = loadResourceAsString("repositoriesListing/repositoriesExpectedResponse.json")

        // When
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/allegro/repositories")
            .header("Accept", "application/vnd.github.v3+json"))
            .andReturn()
        // Then
        assertEquals(200, result.response.status)
        assertEquals(expectedBody, result.response.contentAsString)
    }

    @Test
    fun `Should get sum of stargazers for one page of repositories`() {
        // Given
        val expectedBody = loadResourceAsString("stargazersSum/ejdenStargazersSumExpectedResponse.json")

        // When
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/Ejden/stargazers")
            .header("Accept", "application/vnd.github.v3+json"))
            .andReturn()

        // Then
        assertEquals(200, result.response.status)
        assertEquals(expectedBody, result.response.contentAsString)
    }

    /**
     * file should be place in resources/__files folder. filePath is relative to __files folder
     */
    private fun loadResourceAsString(filePath: String): String {
        return File("src/test/resources/__files/$filePath").readText(Charsets.UTF_8).replace("\\s+".toRegex(), "")
    }
}
