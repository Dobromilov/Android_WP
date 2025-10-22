package Inheritance

import kotlin.math.*
import kotlin.random.Random

open class Human(
    _name: String,
    _surname: String,
    _second_name: String,
    _speed: Double,
) : Movable{
    var name: String = _name
    var surname: String = _surname
    var second_name: String = _second_name

    override var speed: Double = _speed
    override var x = 0.0
    override var y = 0.0

    //гетеры и сетеры не использую, т.к. мы передаем из main напрямую + пользуемся var(по умолчанию)
    override fun move() {
        val angle = Random.nextDouble(0.0, 2 * PI)
        x += speed * cos(angle)
        y += speed * sin(angle)
        println("$name $surname $second_name move to (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }
}

