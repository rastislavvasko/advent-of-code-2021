// https://adventofcode.com/2021/day/2

import java.io.File

val inputs = File("input/aoc-2.txt").readLines().map { it.split(' ') }

// Part 1.

fun p1(inputs: List<List<String>>) =
    inputs.fold(0 to 0) { (x, y), step -> 
        val num = step[1].toInt()
        when (step[0]) {
            "up" -> x to (y-num)
            "down" -> x to (y+num)
            else -> (x+num) to y
        }
    }.let { (x, y) -> x * y}

// Part 2.

data class Triple(val x: Int, val y: Int, val aim: Int)

fun p2(inputs: List<List<String>>) =
    inputs.fold(Triple(0, 0, 0)) { acc, step -> 
        val num = step[1].toInt()
        when (step[0]) {
            "up" -> acc.copy(aim = acc.aim - num)
            "down" -> acc.copy(aim = acc.aim + num)
            else -> acc.copy(x = acc.x + num, y = acc.y + acc.aim * num)
        }
    }.run { x * y }

println(p2(inputs))
