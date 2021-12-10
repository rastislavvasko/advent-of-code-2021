// https://adventofcode.com/2021/day/10

import java.io.File
import kotlin.collections.ArrayDeque

fun input() = File("input/aoc-10.txt").readLines()

// Part 1.

fun p1(lines: List<String>): Int {
    val pair = mapOf<Char, Char>(')' to '(', ']' to '[', '}' to '{', '>' to '<')
    val score = mapOf<Char, Int>(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)

    fun check(line: String): Char? {
        val q = ArrayDeque<Char>()
        line.forEach { c ->
            when (c) {
                '(', '[', '{', '<' -> q.addLast(c)
                else -> q.removeLast().let { d -> if (d != pair[c]) return c }
            }
        }
        return null
    }

    return lines.sumOf { l -> check(l)?.let { score[it] } ?: 0 }
}

// Part 2.

fun p2(lines: List<String>): Long {
    val pair = mapOf(')' to '(', ']' to '[', '}' to '{', '>' to '<')
    val revPair = pair.entries.associate { (k, v) -> v to k }
    val score = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

    fun finish(line: String): String? {
        val q = ArrayDeque<Char>()
        line.forEach { c ->
            when (c) {
                '(', '[', '{', '<' -> q.addLast(c)
                else -> q.removeLast().let { d -> if (d != pair[c]) return null }
            }
        }
        return q.map { c -> revPair[c] }.joinToString("").reversed()
    }

    fun count(line: String) = line.fold(0L) { acc, c -> 5L * acc + score.getOrDefault(c, 0) }

    val scores = lines.mapNotNull { l -> finish(l)?.let { count(it) } }
    return scores.sorted()[scores.size / 2]
}

with(input()) {
    println(p1(this)) // 215229
    println(p2(this)) // 1105996483
}
