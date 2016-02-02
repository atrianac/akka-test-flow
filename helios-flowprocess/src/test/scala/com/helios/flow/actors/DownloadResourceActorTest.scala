package com.helios.flow.actors

import akka.testkit.{ TestKit, TestActorRef }
import akka.actor.ActorSystem
import org.scalatest. {MustMatchers, WordSpecLike}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import akka.testkit.ImplicitSender

class DownloadResourceActorTest extends TestKit(ActorSystem("download-system"))
   with ImplicitSender  
   with MustMatchers
   with WordSpecLike {
  
}