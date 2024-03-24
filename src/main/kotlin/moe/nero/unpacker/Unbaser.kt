package moe.nero.unpacker

import kotlin.math.pow

/**
 * Functor for a given base. Will convert strings to natural numbers.
 */
class Unbaser(private val base: Int) {
    private val alphabet = mutableMapOf(
        62 to "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
        95 to " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
    )

    private lateinit var dictionary: Map<Char, Int>
    private val unbase: (String) -> Int

    init {
        if (base in 37..61) {
            if (!alphabet.containsKey(base)) {
                alphabet[base] = alphabet[62]?.substring(0, base) ?: ""
            }
        }

        unbase = if (base in 2..36) {
            { string: String -> string.toInt(base) }
        } else {
            alphabet[base]?.let { alphabet ->
                dictionary = alphabet.mapIndexed { index, cipher -> cipher to index }.toMap()
                ::dictunbaser
            } ?: throw IllegalArgumentException("Unsupported base encoding.")
        }
    }

    operator fun invoke(string: String): Int {
        return unbase(string)
    }

    private fun dictunbaser(string: String): Int {
        var ret = 0
        string.reversed().forEachIndexed { index, cipher ->
            ret += (base.toDouble().pow(index) * dictionary[cipher]!!).toInt()
        }
        return ret
    }
}
