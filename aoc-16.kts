// https://adventofcode.com/2021/day/16

import java.io.File

fun input() = File("input/aoc-16.txt").readLines().first()

fun Int.bit(pos: Int): Boolean = ((this shr pos) and 1) == 1

fun String.toBits(): Bits {
    val arr = BooleanArray(length * 4)
    forEachIndexed { i, c -> 
        val hex = if (c >= '0' && c <= '9') c - '0' else c - 'A' + 10
        (0..3).forEach { j -> arr[4 * i + j] = hex.bit(3 - j) }
    }
    return Bits(arr)
}

data class Bits(val arr: BooleanArray) {
    operator fun get(i: Int) = if (arr[i]) 1 else 0

    operator fun get(r: IntRange) = r.fold(0) { acc, i -> (acc shl 1) + get(i) }

    fun sub(i: Int, j: Int = arr.lastIndex) = Bits(BooleanArray(j - i + 1) { k -> arr[i + k] })
}

data class Packet(val bits: Bits) {
    val version = bits[0..2]
    val type = bits[3..5]

    var size: Int = 0
    var literal: Long = 0
    var subpackets: List<Packet> = emptyList()

    init {
        if (type == 4) {
            val (l, s) = literal()
            literal = l
            size = s
        } else {
            val (l, s) = operator()
            subpackets = l
            size = s
        }
    }

    private fun literal(): Pair<Long, Int> {
        var l = 0L
        var s = 0
        for (i in 6 until bits.arr.size step 5) {
            for (j in i + 1 until i + 5) l = 2L * l + bits[j].toLong()
            s += 5
            if (bits[i] == 0) break
        }
        return l to (s + 6)
    }

    private fun operator(): Pair<List<Packet>, Int> {
        val lengthTypeId = bits[6]
        val list = mutableListOf<Packet>()
        when (lengthTypeId) {
            0 -> {
                val length = bits[7..21]
                var i = 22
                while (i < 22 + length) {
                    val p = Packet(bits.sub(i)).also { list += it }
                    i += p.size
                }
            }
            1 -> {
                val number = bits[7..17]
                var i = 18
                repeat(number) {
                    val p = Packet(bits.sub(i)).also { list += it }
                    i += p.size
                }
            }
        }
        return list to (list.sumOf { it.size } + 6 + 1 + (if (lengthTypeId == 0) 15 else 11))
    }
}

// Part 1.

fun p1(input: String): Int {
    val bits = input.toBits()

    fun Packet.sum(): Int = version + subpackets.sumOf { p -> p.sum() }

    return Packet(bits).sum()
}

// Part 2.

fun p2(input: String): Long {
    val bits = input.toBits()

    fun Packet.eval(): Long = when (type) {
        0 -> subpackets.sumOf { p -> p.eval() }
        1 -> subpackets.fold(1) { acc, p -> acc * p.eval() }
        2 -> subpackets.minOf { p -> p.eval() }
        3 -> subpackets.maxOf { p -> p.eval() }
        5 -> if (subpackets[0].eval() > subpackets[1].eval()) 1L else 0L
        6 -> if (subpackets[0].eval() < subpackets[1].eval()) 1L else 0L
        7 -> if (subpackets[0].eval() == subpackets[1].eval()) 1L else 0L
        else -> literal.toLong()
    }

    return Packet(bits).eval()
}

with(input()) {
    println(p1(this)) // 871
    println(p2(this)) // 68703010504
}
