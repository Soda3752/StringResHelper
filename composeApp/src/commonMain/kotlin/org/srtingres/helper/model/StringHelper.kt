package org.srtingres.helper.model

fun compareResources(
    modified: List<StringResource>,
    reference: List<StringResource>
): List<ComparisonItem> {
    val result = mutableListOf<ComparisonItem>()

    // 建立參考資源的映射表，便於查詢
    val referenceMap = reference.associateBy { it.value }

    for (mod in modified) {
        // 查找相同value的參考資源
        val ref = referenceMap[mod.value]

        // 只有當value相同但key不同時，才添加到結果列表
        if (ref != null && mod.key != ref.key) {
            result.add(ComparisonItem(modified = mod, reference = ref))
        }
    }

    return result.sortedBy { it.modified.key }
}

fun parseStringResources(input: String): List<StringResource> {
    if (input.trim().isEmpty()) return emptyList()

    val resources = mutableListOf<StringResource>()
    // 匹配格式為 <string name="key">value</string> 的字串資源
    val regex = """<string\s+name="([^".]+)">([^<]+)</string>""".toRegex()

    regex.findAll(input).forEach { matchResult ->
        val (key, value) = matchResult.destructured
        resources.add(StringResource(key, value))
    }

    return resources
}