package Inheritance

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun main() = runBlocking {
    val mutex = Mutex()
    val humans = arrayOf(
        Human("Влад", "Погорелов", "Батькович", 9.0),
        Human("Роман", "Петров", "Юрьевич", 9.5),
        Human("Мария", "Иванова", "Сергеевна", 8.2,),
        Driver("Алексей", "Сидоров", "Викторович", 8.0, "Toyota")
    )

    val jobs = humans.mapIndexed { index, human ->
        launch(Dispatchers.Default) {
            for (time in 1..5) {
                mutex.withLock {
                    print("Время: ${time}c ")
                    print("${index + 1}): ")
                    human.move()
                }
                delay(1000)
            }
        }
    }

    println()
}
