package primes

object PrimeStream {
  def primes: Stream[Long] = {
    def sieve(str: Stream[Long]): Stream[Long] =
      str.head #:: sieve(str.tail.filter(_ % str.head != 0))
    sieve(Stream.iterate(2L)(x => x + 1))
  }

  def main(args: Array[String]): Unit = {
    println(primes take 30 mkString ", ")
  }
}
