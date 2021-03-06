package pl.allegro.stypinski.recruitmenttask.configuration

import org.springframework.context.annotation.Configuration

@Configuration
class WebClientConfig {
    var url: String = ""
    var timeoutMillis: Int = 500
    var maxInMemorySizeInBytes: Int = 24 * 1024
}