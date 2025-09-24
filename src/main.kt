package Inheritance

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun main() = runBlocking {
    val mutex = Mutex()
    val humans = listOf(
        Human("Влад", "Погорелов", "Батькович", 9.0, 30),
        Human("Роман", "Петров", "Юрьевич", 9.5, 25),
        Human("Мария", "Иванова", "Сергеевна", 8.2, 28),
        Driver("Алексей", "Сидоров", "Викторович", 8.0, 35, "Toyota")
    )

    val jobs = humans.mapIndexed { index, human ->
        launch(Dispatchers.Default) {
            for (time in 1..5) {
                print("Время: ${time}c")
                mutex.withLock {
                    print("${index + 1}): ")
                    human.move()
                }
                delay(1000)
            }
        }
    }

    jobs.joinAll()
    println()
}
