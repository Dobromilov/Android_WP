import kotlin.math.*
import kotlin.random.Random

class Human(
    private var fullName: String,
    private var age: Int,
    private var currentSpeed: Double
) {
    private var x: Double = 0.0
    private var y: Double = 0.0

    fun move() {
        val angle = Random.nextDouble(0.0, 2 * PI)
        x += currentSpeed * cos(angle)
        y += currentSpeed * sin(angle)
    }

    fun getFullName() = fullName
    fun setFullName(name: String) { fullName = name }
    
    fun getAge() = age
    fun setAge(newAge: Int) { age = newAge }
    
    fun getCurrentSpeed() = currentSpeed
    fun setCurrentSpeed(speed: Double) { currentSpeed = speed }
    
    fun getPosition() = "(${String.format("%.2f", x)}, ${String.format("%.2f", y)})"
}

fun main() {
    val humans = listOf(
        Human("Погорелов Влад Батькович", 30, 12.0),
        Human("Петров Роман Юрьевич", 25, 9.5),
        Human("Иванова Мария Сергеевна", 28, 8.2),
        Human("Сидоров Алексей Викторович", 35, 10.1),
        Human("Козлова Анна Дмитриевна", 22, 7.8),
        Human("Николаев Иван Петрович", 40, 11.5)
    )

    val simulationTime = 5 
    val timeStep = 1.0 

    repeat(simulationTime) { step ->
        println("\nСекунда ${step + 1}:")
        humans.forEach { human ->
            human.move()
            println("${human.getFullName()} переместился в позицию ${human.getPosition()}")
        }
    }
}
