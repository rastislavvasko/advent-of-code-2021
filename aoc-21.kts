// https://adventofcode.com/2021/day/21

import java.io.File
import kotlin.collections.ArrayDeque
import kotlin.math.min
import kotlin.math.max

fun input() = File("input/aoc-21.txt").readLines().map { it.split(": ").last().toInt() }

// Part 1.

fun p1(start: List<Int>): Int {
    data class Player(val pos: Int, val score: Int)

    data class Dice(var state: Int = 0, var rolls: Int = 0) {
        fun roll(): Int {
            rolls++
            state = (state % 100) + 1
            return state
        }
    }

    fun move(pos: Int, roll: Int) = (pos + roll - 1) % 10 + 1

    var dice = Dice()
    var players = start.mapTo(mutableListOf<Player>()) { pos -> Player(pos, 0) }

    fun Player.play(): Player {
        val roll = dice.roll() + dice.roll() + dice.roll()
        val pos = move(pos, roll)
        return Player(pos, score + pos)
    }

    var cur = 0
    while (players.all { it.score < 1000 }) {
        players[cur] = players[cur].play()
        cur = (cur + 1) % players.size
    }
    return players.first { it.score < 1000 }.score * dice.rolls
}

// Part 2.

fun p2(start: List<Int>): Long {
    val rolls = mutableListOf<Int>().apply {
        (1..3).forEach { a -> (1..3).forEach { b -> (1..3).forEach { c -> add(a + b + c) } } }
    }.asSequence()

    data class State(val p1: Int, val s1: Int, val p2: Int, val s2: Int, val first: Boolean)

    data class Wins(val w1: Long, val w2: Long)

    val cache = mutableMapOf<State, Wins>()

    fun move(pos: Int, roll: Int) = (pos + roll - 1) % 10 + 1

    fun State.game(): Wins {
        cache[this]?.let { return it }

        if (s1 >= 21) return Wins(1L, 0L)
        if (s2 >= 21) return Wins(0L, 1L)

        val states = if (first) {
            rolls.map { roll -> 
                val pos = move(p1, roll)
                State(pos, s1 + pos, p2, s2, false)
            }
        } else {
            rolls.map { roll -> 
                val pos = move(p2, roll)
                State(p1, s1, pos, s2 + pos, true)
            }
        }
        val w = states.fold(Wins(0L, 0L)) { acc, state ->
            val w = state.game()
            Wins(acc.w1 + w.w1, acc.w2 + w.w2)
        }

        return w.also { cache[this] = it }
    }

    val wins = State(start[0], 0, start[1], 0, true).game()
    return max(wins.w1, wins.w2)
}   

with(input()) {
    println(p1(this)) // 671580
    println(p2(this)) // 912857726749764
}
