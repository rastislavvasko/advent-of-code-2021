// https://adventofcode.com/2021/day/1

import java.io.File

val inputs = File("input/aoc-1.txt").readLines().map { it.toInt() }

// Part 1.

fun p1(inputs: List<Int>) =
    inputs.fold(0 to Int.MAX_VALUE) { (sum, prev), cur -> if (prev < cur) (sum+1) to cur else sum to cur }.first

// Part 2.

data class Triple(val a: Int, val b: Int, val c: Int)

fun p2(inputs: List<Int>) =
    inputs.fold(0 to Triple(-1, -1, -1)) { (z, t), d -> 
        val y = when {
            t.a == -1 || t.b == -1 || t.c == -1 -> z
            t.a < d -> z + 1
            else -> z
        }
        y to Triple(t.b, t.c, d)
    }.first

println(p2(inputs))
