package org.srtingres.helper.model

import kotlinx.serialization.json.Json


fun compareResources(
    modified: List<StringResource>,
    reference: List<StringResource>
): List<ComparisonItem> {
    val result = mutableListOf<ComparisonItem>()

    // 建立參考資源的映射表，相同 value 對應到多個資源
    val referenceMap = mutableMapOf<String, MutableList<StringResource>>()

    // 將所有參考資源按照 value 分組
    for (ref in reference) {
        referenceMap.getOrPut(ref.value) { mutableListOf() }.add(ref)
    }

    for (mod in modified) {
        // 查找所有相同 value 的參考資源
        val refs = referenceMap[mod.value]

        // 過濾出 key 不同的參考資源
        if (refs != null) {
            val isAlreadyHaveKey = refs.any { it.key == mod.key && it.value == mod.value }
            if (!isAlreadyHaveKey) {
                val differentKeyRefs = refs.filter { it.key != mod.key }.toMutableList()
                if (differentKeyRefs.isNotEmpty()) {
                    result.add(ComparisonItem(modified = mod, reference = differentKeyRefs))
                }
            }
        }
    }

    return result.sortedBy { it.modified.atLine }
}

fun parseStringResources(
    input: String,
    shouldCheckFormat: Boolean,
    filterPrefix: String? = null
): List<StringResource> {
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
    val regex = if (shouldCheckFormat) {
        """<string\s+name="([a-z0-9_]+)">([^<]+)</string>""".toRegex()
    } else {
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


fun parseIosStringResources(input: String, filterPrefix: String? = null): List<StringResource> {
    val format = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    val lineRow = input.lines()
    try {
        val resourceFile = format.decodeFromString<StringResourceFile>(input)
        return resourceFile.strings.mapNotNull { (key, entry) ->
            val enValue = entry.localizations?.get("en")?.stringUnit?.value
            if (key.isNotEmpty() && !enValue.isNullOrEmpty() && (filterPrefix.isNullOrEmpty() || key.startsWith(
                    filterPrefix
                ))
            ) {
                StringResource(key, enValue, lineRow.indexOfFirst { it.contains(key) } + 1)
            } else null
        }
    } catch (e: Exception) {
        println("解析錯誤: ${e.message}")
        return emptyList()
    }
}
