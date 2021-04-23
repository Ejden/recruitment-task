package pl.allegro.stypinski.recruitmenttask.rest.infrastructure.github.utils

class GithubLinkHeaderParser {
    companion object {
        // Group 1 is a url for page, group 2 is relation of that page
        private const val LINK_REGEX: String = "<(.*?)>;\\s+rel=\"(.*?)\""

        enum class PageRelation(val value: String) {
            FIRST_PAGE("first"),
            LAST_PAGE("last"),
            NEXT_PAGE("next"),
            PREVIOUS_PAGE("prev")
        }

        fun parseLinkHeader(header: String?): ParsedLinkHeader {
            if (header.isNullOrEmpty()) {
                return ParsedLinkHeader()
            }

            val regex = Regex(LINK_REGEX)
            val matches = regex.findAll(header)

            val iterator = matches.iterator()

            var nextPageUrl: String? = null
            var previousPageUrl: String? = null
            var lastPageUrl: String? = null
            var firstPageUrl: String? = null

            while (iterator.hasNext()) {
                val page = iterator.next()

                val url = page.groups[1]!!.value

                when (page.groups[2]!!.value) {
                    PageRelation.FIRST_PAGE.value -> {
                        firstPageUrl = url
                    }
                    PageRelation.LAST_PAGE.value -> {
                        lastPageUrl = url
                    }
                    PageRelation.NEXT_PAGE.value -> {
                        nextPageUrl = url
                    }
                    PageRelation.PREVIOUS_PAGE.value -> {
                        previousPageUrl = url
                    }
                }
            }

            return ParsedLinkHeader(
                nextPageUrl = nextPageUrl,
                previousPageUrl = previousPageUrl,
                lastPageUrl = lastPageUrl,
                firstPageUrl = firstPageUrl,
            )
        }
    }
}

class ParsedLinkHeader (
    val nextPageUrl: String? = null,
    val previousPageUrl: String? = null,
    val lastPageUrl: String? = null,
    val firstPageUrl: String? = null
)
