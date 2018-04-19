package problems

object Ex2 {
  def main(args: Array[String]): Unit = {
    @volatile var stop = false
    var name = "Fred"
    val t = new Thread(() => {
      println(s"name is $name, stop is $stop")
      println(s"Thread started, name is ${Thread.currentThread().getName}")
      while (! stop ) {

      }
      println(s"Thread finished name is $name")
    })
    t.start()
    println(s"main thread, name is $name stop is $stop...")
    Thread.sleep(1000)
    stop = true
    name = "Jim"
    println(s"main thread, name is $name stop is $stop... main exiting")

  }
}
