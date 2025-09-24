package Inheritance

import kotlin.math.*
import kotlin.random.Random

open class Human(
    var name: String = "",
    var surname: String = "",
    var second_name: String = "",
    var speed: Double = 0.0,
    var years: Int = 0
) {
    protected var x = 0.0
    protected var y = 0.0

    //гетеры и сетеры не использую, т.к. мы пользуемся передаем из main напрямую + пользуемся var(по умолчанию)
    open fun move() {
        val angle = Random.nextDouble(0.0, 2 * PI)
        x += speed * cos(angle)
        y += speed * sin(angle)
        println("$name $surname $second_name move to (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }
}

class Driver(
    name: String = "",
    surname: String = "",
    second_name: String = "",
    speed: Double = 0.0,
    years: Int = 0,
    var brand_car: String = ""
) : Human(name, surname, second_name, speed, years){

    val angle = Random.nextDouble(0.0, 2 * PI)

    override fun move(){
        x += speed*2 * cos(angle)
        y += speed*2 * sin(angle)
        println("Driver - $name $surname $second_name on $brand_car move to (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }
}
