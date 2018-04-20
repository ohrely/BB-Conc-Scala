package implicitstuff

class Thing(val name: String) {
  def ## (x: String): Unit = println(s"$name plus $x")
  def ## (x: Thing): Unit = println(s"$name plus ${x.name}")

  object Implicits {
    implicit def xxx(x: String): Thing = new Thing(x)
  }

}

object MyLib {

  implicit class StuffArg(val s: String)
  def doStuff(implicit name : StuffArg) : Unit = println(s"name is ${name.s}")
  implicit val x: StuffArg = new StuffArg("Banana")
}

object MyLibAlternate {
  implicit val y: MyLib.StuffArg = new MyLib.StuffArg("Orange")
}

object Ex1 {

  def main(args: Array[String]): Unit = {
//    import MyLib.doStuff

    MyLib.doStuff("Fred")

    {
      import MyLib.x
      MyLib.doStuff
    }
    {
      import MyLibAlternate.y
      MyLib.doStuff
    }

    val t = new Thing("Fred")
    t ## "Jones"
    import t.Implicits.xxx
    "Jones" ## t

    def useThing(t: Thing): Unit = println(s"Thing has name ${t.name}")

    useThing(t)
    useThing("Albert")

  }
}
