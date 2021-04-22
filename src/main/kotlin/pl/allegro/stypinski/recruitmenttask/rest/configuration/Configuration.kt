package pl.allegro.stypinski.recruitmenttask.rest.configuration

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient

import reactor.netty.http.client.HttpClient

import pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.GithubClient
import java.util.concurrent.TimeUnit

@Configuration
class Configuration {

    @Bean
    fun createGithubClient(props: GithubConfig): GithubClient {
        return GithubClient(
            WebClient.builder()
                .baseUrl(props.webClient.url)
                .clientConnector(ReactorClientHttpConnector(createHttpClientForGithub(props.webClient.timeoutMillis)))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "${props.credentials.prefix} ${props.credentials.token}")
                .codecs { it.defaultCodecs().maxInMemorySize(props.webClient.maxInMemorySizeInBytes) }
                .build()
        )
    }

    private fun createHttpClientForGithub(timeoutMillis: Int): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMillis)
            .responseTimeout(Duration.ofMillis(timeoutMillis.toLong()))
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
                connection.addHandlerLast(WriteTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
            }
    }
}

@Configuration
@ConfigurationProperties("github")
class GithubConfig(
    val webClient: WebClientConfig,
    val credentials: TokenCredentials
)
