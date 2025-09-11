class Human {
    var name: String = ""
    var surname: String = ""
    var second_name: String = ""
    var group_number: Int = -1

    constructor(_name: String, _age: Int, _second_name: String, _gn: Int) {
        name = _name
        age = _age
        second_name = _second_name
        group_number = _gn
        print("We create the Human ibject with name: $name")
    }
}

fun main() {
    val petya: Human("Petya", "Ivanov", "Petrovich", 444)

    var counter: Int = 10
    val name: String = "Tom"

    println("Hello, World!")
}