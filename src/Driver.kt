package Inheritance

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Driver(
    _name: String,
    _surname: String,
    _second_name: String,
    _speed: Double,
    _brand_car: String
) : Movable{
    var name: String = _name
    var surname: String = _surname
    var second_name: String = _second_name
    var brand_car: String = _brand_car

    override var speed: Double = _speed
    override var x = 0.0
    override var y = 0.0
    val angle = Random.nextDouble(0.0, 2 * PI)

    override fun move(){
        x += speed*2 * cos(angle)
        y += speed*2 * sin(angle)
        println("Driver - $name $surname $second_name on $brand_car move to (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }
}
