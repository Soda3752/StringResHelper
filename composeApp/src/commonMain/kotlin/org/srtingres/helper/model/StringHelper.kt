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

    return result.sortedBy { it.modified.atLine }
}

fun parseStringResources(input: String, shouldCheckFormat: Boolean, filterPrefix: String?=null): List<StringResource> {
    if (input.trim().isEmpty()) return emptyList()

    val resources = mutableListOf<StringResource>()
    val lines = input.lines()

    // 計算每行起始位置的索引
    val lineStartIndices = ArrayList<Int>()
    var currentIndex = 0
    lines.forEach { line ->
        lineStartIndices.add(currentIndex)
        currentIndex += line.length + 1 // +1 是換行符的長度
    }

    // 匹配格式為 <string name="key">value</string> 的字串資源
    val regex = if (shouldCheckFormat){
        """<string\s+name="([a-z0-9_]+)">([^<]+)</string>""".toRegex()
    }else{
        """<string\s+name="([^"]+)">([^<]+)</string>""".toRegex()
    }

    regex.findAll(input).forEach { matchResult ->
        val (key, value) = matchResult.destructured
        val startPosition = matchResult.range.first

        // 二分搜尋找到對應的行號
        val lineIndex = lineStartIndices.binarySearch {
            it.compareTo(startPosition)
        }.let {
            if (it >= 0) it else -it - 2
        }

        resources.add(StringResource(key, value, lineIndex + 1)) // 行號從1開始
    }

    return resources.filter {
        filterPrefix.isNullOrEmpty() || it.key.startsWith(filterPrefix)
    }
}