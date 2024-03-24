package moe.nero.unpacker

/**
 * Unpacker for [Dean Edward's p.a.c.k.e.r](http://dean.edwards.name/packer/).
 */
object Unpacker {
    private val packedRegex = Regex("""eval\s*\(\s*function\s*\(\s*p\s*,\s*a\s*,\s*c\s*,\s*k\s*,\s*e\s*,\s*""")

    /**
     * Detects whether [source] is P.A.C.K.E.R. coded.
     * @param source The source string to detect.
     */
    fun detect(source: String): Boolean {
        return source.contains(packedRegex)
    }

    /**
     * Unpacks P.A.C.K.E.R. packed js code.
     * @param source The source string to unpack.
     */
    fun unpack(source: String): String {
        require(detect(source)) { "Invalid p.a.c.k.e.r data." }

        val matcher = packedRegex.find(source)!!
        val beginOffset = matcher.range.first
        val beginString = source.substring(0, beginOffset)
        val sourceEnd = source.substring(beginOffset)

        val endString = sourceEnd.split("')))", limit = 2).getOrElse(1) {
            sourceEnd.split("}))", limit = 2).getOrNull(1) ?: ""
        }

        val (payload, symtab, radix, count) = filterArgs(source)
        require(count == symtab.size) { "Malformed p.a.c.k.e.r. symtab. ($count != ${symtab.size})" }

        val unbase = Unbaser(radix)
        val lookup: (String) -> String = { word -> symtab[unbase(word)].ifEmpty { word } }

        val newSource = payload
            .replace("\\\\", "\\")
            .replace("\\'", "'")
            .replace(Regex("\\b\\w+\\b")) { match -> lookup(match.value) }

        return replaceStrings(beginString, newSource, endString)
    }

    private fun filterArgs(source: String): Quadruple<String, List<String>, Int, Int> {
        val juicers = listOf(
            Regex("""}\('(.*)', *(\d+), *(\d+), *'(.*)'\.split\('\|'\), *(\d+), *(.*)\)\)"""),
            Regex("""}\('(.*)', *(\d+), *(\d+), *'(.*)'\.split\('\|'\)"""),
        )

        for (juicer in juicers) {
            val match = juicer.find(source) ?: continue

            return Quadruple(
                match.groupValues[1],
                match.groupValues[4].split("|"),
                match.groupValues[2].toInt(),
                match.groupValues[3].toInt()
            )
        }

        throw IllegalArgumentException("Could not make sense of p.a.c.k.e.r data (unexpected code structure)")
    }

    private fun replaceStrings(begin: String, source: String, end: String): String {
        val match = Regex("var *(_\\w+)=\"(.*?)\";").find(source)

        if (match != null) {
            val (varName, strings) = match.destructured
            val startPoint = match.range.last + 1
            val lookup = strings.split("\",\"")
            val variable = "%s[%d]".format(varName)
            var modifiedSource = source
            for ((index, value) in lookup.withIndex()) {
                modifiedSource = modifiedSource.replace(variable.format(index), "\"$value\"")
            }
            return modifiedSource.substring(startPoint)
        }
        return begin + source + end
    }
}
