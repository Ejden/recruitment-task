package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class Repository (
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Long
): Serializable
