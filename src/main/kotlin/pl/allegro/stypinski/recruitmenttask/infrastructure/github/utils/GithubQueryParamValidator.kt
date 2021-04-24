package pl.allegro.stypinski.recruitmenttask.infrastructure.github.utils

import java.util.*

class GithubQueryParamValidator {
    companion object {
        const val DEFAULT_PER_PAGE: Int = 30
        const val MAX_PER_PAGE: Int = 100

        fun validateType(type: String?): Optional<String> {
            return when (type) {
                "all", "owner", "member" -> Optional.of(type)
                else -> Optional.empty<String>()
            }
        }

        fun validateSortBy(sortBy: String?): Optional<String> {
            return when (sortBy) {
                null -> Optional.empty<String>()
                "created", "updated", "pushed", "full_name" -> Optional.of(sortBy)
                else -> Optional.empty<String>()
            }
        }

        fun validateSortDirection(sortDirection: String?): Optional<String> {
            return when (sortDirection) {
                null -> Optional.empty<String>()
                "asc", "desc" -> Optional.of(sortDirection)
                else -> Optional.empty<String>()
            }
        }

        fun validatePerPage(perPage: Int): Int {
            if (perPage <= 0) return DEFAULT_PER_PAGE
            if (perPage >= 100) return MAX_PER_PAGE
            return perPage
        }

        fun validatePage(page: Int): Int {
            if (page <= 0) return 1
            return page
        }
    }
}