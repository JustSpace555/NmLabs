package sem1.kurs

import extensions.read
import extensions.readMatrix
import sem1.lab1.Matrix
import java.util.*

/*
	Курсовая работа
	Выполнил студент группы М8О-305Б-18
	Мошков Михаил
	Тема №1:    Решение систем линейных алгебраических уравнений с симметричными разреженными матрицами большой
				размерности. Метод сопряженных градиентов.
*/

fun main() {

	val scanner = Scanner(System.`in`)

	println("Исходный вид задачи: Ax = b, где A - матрица размером n x n, b - вектор ответов системы уравнений.")

	val n = read<Int>("Введите размерность матрицы A")
	val matrixA = readMatrix(n, n, "Введите матрицу А, в которой числа идут через пробел")
	val b = readMatrix(1, n, "Введите вектор ответов b, в котором числа идут через пробел").transposed()

	println("Введите вектор начальных данных для x (x0), либо нажмите Enter и будут подставлены нули")
	val input = scanner.nextLine()
	val x = mutableListOf(
		if (input.isBlank()) {
			val average = b.transposed()[0].average()
			val newRow = mutableListOf<Double>()
			for (i in 0 until n)
				newRow.add(Math.random() * average)
			Matrix.of(mutableListOf(newRow)).transposed()
		} else
			Matrix.of(mutableListOf(MutableList(n) { 0.0 })).transposed()
	)

	val r = mutableListOf(b - matrixA * x[0])
	val p = mutableListOf(r[0].copy())


	val calculateNewAlpha: (i: Int) -> Double = { i ->
		(r[i].transposed() * r[i])[0, 0] / (p[i].transposed() * matrixA * p[i])[0, 0]
	}
	val alpha = mutableListOf<Double>()


	val calculateNewX: (i: Int) -> Matrix = { i -> x[i - 1] + p[i - 1] * alpha[i - 1] }
	val calculateNewR: (i: Int) -> Matrix = { i -> r[i - 1] -  matrixA * alpha[i - 1] * p[i - 1] }

	val calculateNewBeta: (i: Int) -> Double = { i ->
		(r[i + 1].transposed() * r[i + 1])[0, 0] / (r[i].transposed() * r[i])[0, 0]
	}
	val beta = mutableListOf<Double>()

	val calculateNewP: (i : Int) -> Matrix = { i -> r[i] +  p[i - 1] * beta[i - 1] }

	val eps = read<Double>(message = "Введите точность расчетов")

	var i = 0
	while (r.last().norm > eps) {
		alpha.add(calculateNewAlpha(i))
		x.add(calculateNewX(i + 1))
		r.add(calculateNewR(i + 1))
		beta.add(calculateNewBeta(i))
		p.add(calculateNewP(i + 1))
		i++
	}

	println("Результат выполнения алгоритма:")
	println(x.last().toPrint())
	println("Итераций совершено: $i")
}