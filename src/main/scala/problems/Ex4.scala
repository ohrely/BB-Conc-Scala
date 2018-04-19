package problems

import java.util.concurrent.atomic.AtomicInteger

object Ex4 {
  def main(args: Array[String]): Unit = {
    var count = new AtomicInteger(0)
//    val job = () => (0 to 10000).foreach(_ => count += 1)
    val job:Runnable  = () => (0 until 10000).foreach(_ => count.incrementAndGet())
//    new Thread(() => (0 to 10000).foreach(_ => count += 1)).start()
    val t1 = new Thread(job)
    val t2 = new Thread(job)
    t1.start()
    t2.start()
    t1.join
    t2.join
    println(s"count value is $count")
  }
}
