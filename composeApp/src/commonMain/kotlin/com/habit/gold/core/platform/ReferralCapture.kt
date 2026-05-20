package com.habit.gold.core.platform

private val referralQueryKeys = setOf("referralcode", "code")
private val referralRouteSegments = setOf("refer", "referral", "invite")
private val referralCodeRegex = Regex("^[A-Z0-9]{4,32}$")

fun extractReferralCodeFromUrl(rawUrl: String): String? {
    val trimmedUrl = rawUrl.trim()
    if (trimmedUrl.isEmpty()) return null

    extractReferralCodeFromQuery(trimmedUrl)?.let { return it }

    val withoutFragment = trimmedUrl.substringBefore('#')
    val afterScheme = withoutFragment.substringAfter("://", withoutFragment)
    val hostAndPath = afterScheme.substringBefore('?')
    val host = hostAndPath.substringBefore('/', "")
    val path = hostAndPath.substringAfter('/', "")
    val pathSegments = path.split('/')
        .map(String::trim)
        .filter(String::isNotEmpty)

    val routeSegments = buildList {
        host.trim().lowercase().takeIf { it.isNotEmpty() }?.let(::add)
        addAll(pathSegments.map(String::lowercase))
    }
    if (routeSegments.none { it in referralRouteSegments }) return null

    return normalizeReferralCode(pathSegments.lastOrNull())
}

private fun extractReferralCodeFromQuery(rawUrl: String): String? {
    val query = rawUrl.substringAfter('?', "").substringBefore('#')
    if (query.isBlank()) return null
    query.split('&').forEach { pair ->
        val key = pair.substringBefore('=').trim().lowercase()
        if (key !in referralQueryKeys) return@forEach
        val value = pair.substringAfter('=', "")
        normalizeReferralCode(value)?.let { return it }
    }
    return null
}

private fun normalizeReferralCode(rawValue: String?): String? {
    val normalized = rawValue?.trim()?.uppercase()?.takeIf { it.isNotEmpty() } ?: return null
    return normalized.takeIf { referralCodeRegex.matches(it) }
}
