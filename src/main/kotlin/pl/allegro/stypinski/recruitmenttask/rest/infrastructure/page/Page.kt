package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.page

import com.fasterxml.jackson.annotation.JsonProperty

class Page<T> (
    val content: T,
    @JsonProperty("page")
    val pageInfo: PageInfo
)

class PageInfo (
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val perPage: Int,
    val sortBy: String?,
    val sortDirection: String?
)