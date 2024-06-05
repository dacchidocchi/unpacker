package moe.nero.unpacker

import kotlin.math.pow

/**
 * Functor for a given base. Will convert strings to natural numbers.
 */
class Unbaser(private val base: Int) {
    private val alphabet =
        mutableMapOf(
            62 to "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            95 to " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~",
        )

    private val dictionary: Map<Char, Int> by lazy {
        alphabet[base]?.mapIndexed { index, ciper ->
            ciper to index
        }?.toMap() ?: throw IllegalArgumentException("Unsupported base encoding.")
    }

    private val unbase: (String) -> Int by lazy {
        base.takeIf { it in 37..61 && !alphabet.containsKey(it) }?.let {
            alphabet[it] = alphabet[62]?.substring(0, it) ?: ""
        }

        when (base) {
            in 2..36 -> { string: String -> string.toInt(base) }
            in alphabet.keys -> ::dictUnbaser
            else -> throw IllegalArgumentException("Unsupported base encoding.")
        }
    }

    operator fun invoke(string: String): Int = unbase(string)

    private fun dictUnbaser(string: String): Int {
        return string.reversed().mapIndexed { index, cipher ->
            val value =
                dictionary[cipher]
                    ?: throw IllegalArgumentException("Invalid character in input string.")
            value * base.toDouble().pow(index).toInt()
        }.sum()
    }
}
