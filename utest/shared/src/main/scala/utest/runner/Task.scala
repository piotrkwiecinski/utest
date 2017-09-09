package utest
package runner
//import acyclic.file
import sbt.testing._
import sbt.testing

import scala.concurrent.{Await, Future}
import concurrent.duration._
import utest.framework.{ExecutionContext, Tree}

class Task(val taskDef: TaskDef,
           runUTestTask: (Seq[Logger], EventHandler) => Future[Unit])
           extends sbt.testing.Task{



  def tags(): Array[String] = Array()

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[testing.Task] = {
    Await.result(executeInternal(eventHandler, loggers), Duration.Inf)
    Array()
  }

  def execute(eventHandler: EventHandler,
              loggers: Array[Logger],
              continuation: Array[testing.Task] => Unit) = {

    implicit val ec = ExecutionContext.RunNow

    executeInternal(eventHandler, loggers).recover { case t =>
      loggers.foreach(_.trace(t))
    }.onComplete{_ =>
      continuation(Array())
    }
  }

  private def executeInternal(eventHandler: EventHandler, loggers: Array[Logger]) = {
    runUTestTask(loggers, eventHandler)

  }
}
