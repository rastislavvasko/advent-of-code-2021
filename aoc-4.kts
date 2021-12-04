// https://adventofcode.com/2021/day/4

import java.io.File

val inputs = File("input/aoc-4.txt").readLines()
val draws = inputs[0].split(',').map { it.toInt() }

data class Matrix(private val size: Int = 5) {
    private val data = Array(size) { Array<Int>(size) { 0 } }
    private val indices = data.indices

    operator fun get(x: Int, y: Int): Int = data[y][x]

    operator fun set(x: Int, y: Int, z: Int) { data[y][x] = z }

    fun find(z: Int): Pair<Int, Int>? {
        for (y in indices) { 
            val x = data[y].indexOf(z)
            if (x != -1) return x to y
        }
        return null
    }

    fun check(): Boolean = indices.any { i -> 
        indices.all { data[i][it] != 0 } || indices.all { data[it][i] != 0 } 
    }

    fun sum(filter: (Int, Int) -> Boolean): Int {
        var sum = 0
        for (y in indices) {
            for (x in indices) {
                if (filter(x, y)) sum += data[y][x]
            }
        }
        return sum
    }
}

val n = (1+inputs.size) / 6
val bingos = Array<Matrix>(n) { Matrix() }
for (i in 0 until n) {
    for (r in 0 until 5) {
        inputs[1 + 6*i + r + 1].trim().split("\\s+".toRegex()).forEachIndexed { c, x -> bingos[i][c, r] = x.toInt() }
    }
}

// Part 1.

fun p1(draws: List<Int>, bingos: Array<Matrix>): Int {
    val marked = Array<Matrix>(bingos.size) { Matrix() }

    draws.forEach { draw -> 
        bingos.forEachIndexed { i, bingo -> 
            bingo.find(draw)?.let { (x, y) -> 
                marked[i][x, y] = 1 
                if (marked[i].check()) return draw * bingo.sum { dx, dy -> marked[i][dx, dy] == 0 }
            }
        }
    }
    return -1
}

// Part 2.

fun p2(draws: List<Int>, bingos: Array<Matrix>): Int {
    val marked = Array<Matrix>(bingos.size) { Matrix() }
    val done = bingos.indices.toMutableSet()

    draws.forEach { draw -> 
        bingos.forEachIndexed { i, bingo -> 
            if (i !in done) return@forEachIndexed

            bingo.find(draw)?.let { (x, y) -> 
                marked[i][x, y] = 1 
                if (marked[i].check()) {
                    done -= i
                    if (done.isEmpty()) {
                        return draw * bingos[i].sum { dx, dy -> marked[i][dx, dy] == 0 }
                    }
                }
            }
        }
    }
    return -1
}

println(p2(draws, bingos))
