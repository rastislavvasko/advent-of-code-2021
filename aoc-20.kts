// https://adventofcode.com/2021/day/20

import java.io.File

fun input() = File("input/aoc-20.txt").readLines()

fun process(lines: List<String>) = lines[0] to Matrix(lines.subList(2, lines.size))

data class Point(val x: Int, val y: Int)

data class Matrix(val m: Int, val n: Int, private val def: Char = '.') : Iterable<Point> {
    private val data = Array(m) { CharArray(n) { def } }

    operator fun get(p: Point) = data[p.y][p.x]

    operator fun set(p: Point, z: Char) { data[p.y][p.x] = z }

    fun adj(p: Point) = sequenceOf(
        Point(p.x - 1, p.y - 1),
        Point(p.x, p.y - 1),
        Point(p.x + 1, p.y - 1),
        Point(p.x -1, p.y),
        Point(p.x, p.y),
        Point(p.x + 1, p.y),
        Point(p.x - 1, p.y + 1),
        Point(p.x, p.y + 1),
        Point(p.x + 1, p.y + 1),
    ).map { q -> if (q.x >= 0 && q.x < n && q.y >= 0 && q.y < n) get(q) else def }

    override fun iterator(): Iterator<Point> = object : Iterator<Point> {
        private var i = 0

        override fun next() = Point(i % n, i / n).also { i++ }

        override fun hasNext() = i < m*n
    }

    override fun toString() = data.map { r -> r.joinToString("") }.joinToString("\n")
}

fun Matrix(lines: List<String>) = Matrix(lines.size, lines.first().length).apply {
    lines.forEachIndexed { y, l -> l.forEachIndexed { x, c -> this[Point(x, y)] = c } }
}

fun Matrix.expand(i: Int) = Matrix(m + 2 * i, n + 2 * i, '.').also { image ->
    forEach { p -> if (get(p) == '#') image[Point(p.x + i, p.y + i)] = '#' }
}

fun Matrix.shrink(i: Int) = Matrix(m - 2 * i, n - 2 * i, '.').also { image ->
    image.forEach { p -> if (get(Point(p.x + i, p.y + i)) == '#') image[p] = '#' }
}

fun Matrix.enhance(p: Point, algo: String): Char {
    val dec = adj(p).fold(0) { acc, c -> (acc shl 1) + (if (c == '#') 1 else 0) }
    return algo[dec]
}

fun Matrix.enhance(algo: String): Matrix {
    return Matrix(m, n, '.').also { res -> forEach { p -> res[p] = enhance(p, algo) } }
}

// Part 1.

fun p1(input: List<String>): Int {
    val (algo, image) = process(input)

    val res = image.expand(4).enhance(algo).enhance(algo).shrink(2)
    return res.count { p -> res[p] == '#' }
}

// Part 2.

fun p2(input: List<String>): Int {
    val (algo, image) = process(input)

    val rep = 50
    val res = (0 until rep).fold(image.expand(2 * rep)) { acc, _ -> acc.enhance(algo) }.shrink(rep)
    return res.count { p -> res[p] == '#' }
}

with(input()) {
    println(p1(this)) // 5583
    println(p2(this)) // 19592
}
