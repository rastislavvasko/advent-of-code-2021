// https://adventofcode.com/2021/day/24

import java.io.File
import kotlin.collections.ArrayDeque

fun input() = File("input/aoc-24.txt").readLines()

sealed class Ins(val reg: Int) {
    fun exec(mem: Memory) {
        mem[reg] = eval(mem)
    }

    abstract fun eval(mem: Memory): Set<Int>

    fun product(a: Set<Int>, b: Set<Int>, op: (Int, Int) -> Int): Set<Int> {
        val res = mutableSetOf<Int>()
        for (i in a) { 
            for (j in b) { res += op(i, j) }
        }
        return res
    }

    fun map(a: Set<Int>, op: (Int) -> Int): Set<Int> = a.mapTo(mutableSetOf()) { op(it) }
}
data class Inp(val a: Int, val input: () -> Set<Int>) : Ins(a) {
    override fun eval(mem: Memory) = input()
}
data class AddVar(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when {
        mem[b] == setOf(0) -> mem[a] // Noop.
        else -> product(mem[a], mem[b]) { i, j -> i + j }
    }
}
data class AddNum(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when {
        b == 0 -> mem[a] // Noop.
        else -> map(mem[a]) { i -> i + b }
    }
}
data class MulVar(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when { 
        mem[b] == setOf(1) -> mem[a] // Noop.
        else -> product(mem[a], mem[b]) { i, j -> i * j }
    }
}
data class MulNum(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when {
        b == 0 -> setOf(0)
        else -> map(mem[a]) { i -> i * b }
    }
}
data class DivVar(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when {
        mem[b] == setOf(1) -> mem[a] // Noop.
        else -> product(mem[a], mem[b]) { i, j -> i / j }
    }
}
data class DivNum(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = when {
        b == 1 -> mem[a] // Noop.
        else -> map(mem[a]) { i -> i / b }
    }
}
data class ModVar(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = product(mem[a], mem[b]) { i, j -> i % j }
}
data class ModNum(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = map(mem[a]) { i -> i % b }
}
data class EqlVar(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = product(mem[a], mem[b]) { i, j -> if (i == j) 1 else 0 }
}
data class EqlNum(val a: Int, val b: Int) : Ins(a) {
    override fun eval(mem: Memory) = map(mem[a]) { i -> if (i == b) 1 else 0 }
}

class Memory {
    private var data = Array<Set<Int>>(4) { setOf(0) }

    operator fun get(i: Int) = data[i]

    operator fun set(i: Int, v: Set<Int>) { data[i] = v }
}

val Char.index get() = when (this) {
    'x' -> 0
    'y' -> 1
    'z' -> 2
    'w' -> 3
    else -> error("")
}

fun process(lines: List<String>, input: () -> Set<Int>): List<Ins> = lines.map { line -> 
    val ins = line.substring(0, 3)
    val a = line[4].index
    val (b, isVar) = when (line.getOrElse(6) { ' ' }) {
        ' ' -> 0 to false
        'x', 'y', 'z', 'w' -> line[6].index to true
        else -> line.substring(6).toInt() to false
    }
    when (ins) {
        "inp" -> Inp(a, input)
        "add" -> if (isVar) AddVar(a, b) else AddNum(a, b)
        "mul" -> if (isVar) MulVar(a, b) else MulNum(a, b)
        "div" -> if (isVar) DivVar(a, b) else DivNum(a, b)
        "mod" -> if (isVar) ModVar(a, b) else ModNum(a, b)
        "eql" -> if (isVar) EqlVar(a, b) else EqlNum(a, b)
        else -> error("")
    }
}

fun check(monad: List<Ins>): Boolean {
    val mem = Memory()
    monad.forEach { ins -> ins.exec(mem) }
    return 0 in mem[2] 
}

data class State(val digits: List<Set<Int>>) {
    override fun toString() = digits.joinToString("") { if (it.size == 1) it.first().toString() else "[${it.joinToString("")}]" }
}

// Part 1.

fun p1(input: List<String>): Long {
    var state = State(mutableListOf<Set<Int>>().apply { repeat(14) { add((9 downTo 1).toSet()) } })
    val queue = ArrayDeque<State>().apply { add(state) }

    var inp = 0
    val monad = process(input) { state.digits[inp].also { inp = (inp + 1) % 14 } }

    while (queue.isNotEmpty()) {
        state = queue.removeLast()
        val index = state.digits.indexOfFirst { it.size > 1 }

        if (!check(monad)) continue
        if (index == -1) break

        state.digits[index].sorted().forEach { digit ->
            val digits = state.digits.toMutableList().apply { set(index, setOf(digit)) }
            queue.add(State(digits))
        }
    }

    return state.digits.fold(0L) { acc, digit -> acc * 10L + digit.first() }
}

// Part 2.

fun p2(input: List<String>): Long {
    var state = State(mutableListOf<Set<Int>>().apply { repeat(14) { add((1..9).toSet()) } })
    val queue = ArrayDeque<State>().apply { add(state) }

    var inp = 0
    val monad = process(input) { state.digits[inp].also { inp = (inp + 1) % 14 } }

    while (queue.isNotEmpty()) {
        state = queue.removeLast()
        val index = state.digits.indexOfFirst { it.size > 1 }

        if (!check(monad)) continue
        if (index == -1) break

        state.digits[index].sortedDescending().forEach { digit ->
            val digits = state.digits.toMutableList().apply { set(index, setOf(digit)) }
            queue.add(State(digits))
        }
    }

    return state.digits.fold(0L) { acc, digit -> acc * 10L + digit.first() }
}   

with(input()) {
    println(p1(this)) // 12934998949199
    println(p2(this)) // 11711691612189
}
