// https://adventofcode.com/2021/day/18

import java.io.File

fun input() = File("input/aoc-18.txt").readLines()

sealed class Snail {
    class Regular(var v: Long) : Snail()

    class Pair(var l: Snail, var r: Snail) : Snail()

    override fun toString() = when (this) {
        is Snail.Regular -> v.toString()
        is Snail.Pair -> "[$l,$r]"
    }
}

fun parse(s: String): Snail {
    var p = 0
    s.forEachIndexed { i, c -> 
        when (c) {
            '[' -> p++
            ']' -> p--
            ',' -> if (p == 1) return Snail.Pair(
                parse(s.substring(1, i)), 
                parse(s.substring(i + 1, s.lastIndex))
            )
            else -> if (p == 0) return Snail.Regular(s.toLong())
        }
    }
    error("oops")
}

fun Snail.findDepth(a: List<Snail.Pair>, depth: Int): List<Snail.Pair>? = when (this) {
    is Snail.Regular -> null
    is Snail.Pair -> when {
        depth == 0 -> a + this
        else -> l.findDepth(a + this, depth - 1) ?: r.findDepth(a + this, depth - 1)
    }
}

fun Snail.findValue(p: Snail.Pair, value: Int): Pair<Snail.Pair, Snail.Regular>? = when (this) {
    is Snail.Regular -> if (v >= value) p to this else null
    is Snail.Pair -> l.findValue(this, value) ?: r.findValue(this, value)
}

fun Snail.rightLeaf(): Snail.Regular = when (this) {
    is Snail.Regular -> this
    is Snail.Pair -> r.rightLeaf()
}

fun Snail.leftLeaf(): Snail.Regular = when (this) {
    is Snail.Regular -> this
    is Snail.Pair -> l.leftLeaf()
}

fun Snail.Regular.split() = Snail.Pair(Snail.Regular(v / 2), Snail.Regular(v / 2 + v % 2))

fun Snail.Pair.reduceOnce(): Boolean {
    findDepth(listOf(this), 4)?.let { a ->
        val pairs = a.reversed().windowed(2) { x -> x[1] to x[0] }
        pairs.find { (p, s) -> s == p.r }?.let { (p, _) -> p.l.rightLeaf().v += (a.last().l as Snail.Regular).v }
        pairs.find { (p, s) -> s == p.l }?.let { (p, _) -> p.r.leftLeaf().v += (a.last().r as Snail.Regular).v }

        val s = a.last()
        val p = a[a.lastIndex - 1]
        if (s == p.l) p.l = Snail.Regular(0) else p.r = Snail.Regular(0)
        return true
    }
    findValue(this, 10)?.let { (p, s) ->
        if (p.l == s) p.l = s.split() else p.r = s.split()
        return true
    }
    return false
}

fun Snail.Pair.reduce() = apply { while (reduceOnce()) {} }

fun Snail.magnitude(): Long = when (this) {
    is Snail.Regular -> v
    is Snail.Pair -> 3L * l.magnitude() + 2L * r.magnitude()
}

// Part 1.

fun p1(input: List<String>): Long {
    val snails = input.map { parse(it) as Snail.Pair }
    return snails.reduce { a, b -> Snail.Pair(a, b).reduce() }.magnitude()
}

// Part 2.

fun p2(input: List<String>): Long {
    fun <S, T> product(s: Iterable<S>, t: Iterable<T>) =
        s.asSequence().flatMap { l -> t.map { r -> l to r } }

    return product(input, input)
        .mapNotNull { (a, b) -> if (a != b) parse(a) to parse(b) else null }
        .maxOf { (a, b) -> Snail.Pair(a, b).reduce().magnitude() }
}

with(input()) {
    println(p1(this)) // 4433
    println(p2(this)) // 4559
}
