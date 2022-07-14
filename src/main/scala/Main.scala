import zio.Console.ConsoleLive
import zio._
import zio.stream.{ZSink, ZStream}

object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    ZStream
      // here I would expect at most 2 InboxMessages in the heap, one in handoff and one in transducer's fold
      .fromIterator(Iterator.continually(InboxMessage()))
      .aggregateAsyncWithin(
        // a batch of 1024 InboxMessages would be ~250mb
        ZSink.fold[InboxMessage, Int](0)(_ < 1024) { case (acc, _) =>
          acc + 1
        },
        // but I'm seeing multiple of 250mb batches in a heap, retained by race-losing schedules' fibers observers
        Schedule.spaced(1.hour)
      )
      .mapZIO { batch =>
        // oom after 5 batches with a 2gb heap
        ConsoleLive.printLine(s"got $batch")
      }
      .runDrain
      .exitCode
}
