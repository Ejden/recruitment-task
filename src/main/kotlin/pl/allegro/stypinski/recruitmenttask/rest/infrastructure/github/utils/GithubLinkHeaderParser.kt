package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils

class GithubLinkHeaderParser {
    companion object {
        // Group 1 is a url for page, group 2 is relation of that page
        private const val LINK_REGEX: String = "<(.*?)>;\\s+rel=\"(.*?)\""
        private const val PAGE_REGEX: String = "[&?]page=(\\d+)"

        enum class PageRelation(val value: String) {
            FIRST_PAGE("first"),
            LAST_PAGE("last"),
            NEXT_PAGE("next"),
            PREVIOUS_PAGE("prev")
        }

        fun parseLinkHeader(header: String?, currentPage: Int): ParsedLinkHeader {
            if (header.isNullOrEmpty()) {
                return ParsedLinkHeader(
                    hasPages = false,
                    hasNextPage = false,
                    hasPreviousPage = false
                )
            }

            val regex = Regex(LINK_REGEX)
            val matches = regex.findAll(header)

            val iterator = matches.iterator()

            var hasNextPage: Boolean = false
            var hasPreviousPage: Boolean = false
            var nextPageUrl: String? = null
            var previousPageUrl: String? = null
            var lastPageUrl: String? = null
            var firstPageUrl: String? = null
            var totalPages: Int = 1

            while (iterator.hasNext()) {
                val page = iterator.next()

                val url = page.groups[1]!!.value

                when (page.groups[2]!!.value) {
                    PageRelation.FIRST_PAGE.value -> {
                        firstPageUrl = url
                    }
                    PageRelation.LAST_PAGE.value -> {
                        lastPageUrl = url

                        val lastPageRegex = Regex(PAGE_REGEX)
                        totalPages = lastPageRegex.find(url)!!.groups[1]!!.value.toInt()
                    }
                    PageRelation.NEXT_PAGE.value -> {
                        hasNextPage = true
                        nextPageUrl = url
                    }
                    PageRelation.PREVIOUS_PAGE.value -> {
                        hasPreviousPage = true
                        previousPageUrl = url
                    }
                }

                if (lastPageUrl == null) {
                    // It means, there is no url for last page. So, the last page is current page. It means that totalPages
                    // is equal to number of current page
                    totalPages = currentPage
                }
            }

            return ParsedLinkHeader(
                hasPages = true,
                hasNextPage = hasNextPage,
                hasPreviousPage = hasPreviousPage,
                nextPageUrl = nextPageUrl,
                previousPageUrl = previousPageUrl,
                lastPageUrl = lastPageUrl,
                firstPageUrl = firstPageUrl,
                currentPage = currentPage,
                totalPages = totalPages
            )
        }
    }
}

class ParsedLinkHeader (
    val hasPages: Boolean,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextPageUrl: String? = null,
    val previousPageUrl: String? = null,
    val lastPageUrl: String? = null,
    val firstPageUrl: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)
