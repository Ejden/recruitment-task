package pl.allegro.stypinski.recruitmenttask.configuration

import org.springframework.context.annotation.Configuration

@Configuration
class TokenCredentials {
    var prefix: String = "Bearer"
    var token: String = ""
}
