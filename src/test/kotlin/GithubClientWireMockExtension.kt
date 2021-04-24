import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import pl.allegro.stypinski.recruitmenttask.rest.configuration.GithubConfig
import pl.allegro.stypinski.recruitmenttask.rest.configuration.TokenCredentials
import pl.allegro.stypinski.recruitmenttask.rest.configuration.WebClientConfig

private val wireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

@TestConfiguration
class GithubClientWireMockExtension: BeforeAllCallback, AfterAllCallback {
    private val allegroRepositoriesEndpoint = "/users/allegro/repos"
    private val ejdenRepositoriesEndpoint = "/users/Ejden/repos"
    private val ejdenProfileEndpoint = "/users/Ejden"

    init {
        wireMockServer.start()
    }

    @Bean
    @Primary
    fun githubConfig(): GithubConfig {
        val webClientConfig = WebClientConfig()
        webClientConfig.url = wireMockServer.baseUrl()
        return GithubConfig(
            webClientConfig,
            TokenCredentials()
        )
    }

    override fun beforeAll(context: ExtensionContext?) {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathMatching(allegroRepositoriesEndpoint))
                .withHeader("Accept", WireMock.equalTo("application/vnd.github.v3+json"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/vnd.github.v3+json")
                    .withBodyFile("repositoriesListing/allegroRepositoriesResponse.json")))

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathMatching(ejdenRepositoriesEndpoint))
                .withHeader("Accept", WireMock.equalTo("application/vnd.github.v3+json"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/vnd.github.v3+json")
                    .withBodyFile("repositoriesListing/ejdenRepositoriesListingResponse100perPage.json"))
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathMatching(ejdenProfileEndpoint))
                .withHeader("Accept", WireMock.equalTo("application/vnd.github.v3+json"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/vnd.github.v3+json")
                    .withBodyFile("profile/ejdenProfileResponse.json"))
        )
    }

    override fun afterAll(context: ExtensionContext?) {
        wireMockServer.stop()
    }
}