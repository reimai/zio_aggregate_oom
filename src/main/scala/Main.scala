import zio._
import zio.console.putStrLn
import zio.duration._
import zio.stream.{Transducer, ZStream}

object Main extends App {

  private val ones = Array.fill[Byte](256 * 1024)(1)
  case class InboxMessage(bytes: Array[Byte] = ones.clone())

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    ZStream
      // here I would expect at most 2 InboxMessages in the heap, one in handoff and one in transducer's fold
      .fromIterator(Iterator.continually(InboxMessage()))
      .aggregateAsyncWithin(
        // a batch of 1024 InboxMessages would be ~250mb
        Transducer.fold[InboxMessage, Int](0)(_ < 1024) { case (acc, _) =>
          acc + 1
        },
        // but I'm seeing multiple of 250mb batches in a heap, retained by race-losing schedules' fibers observers
        Schedule.spaced(1.hour)
      )
      .mapM { batch =>
        // oom after 5 batches with a 2gb heap
        putStrLn(s"got $batch")
      }
      .runDrain
      .exitCode
}
