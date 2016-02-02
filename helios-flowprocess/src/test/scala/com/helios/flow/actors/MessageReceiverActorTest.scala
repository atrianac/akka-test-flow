package com.helios.flow.actors

import akka.testkit.{ TestKit, TestActorRef }
import akka.actor.ActorSystem
import org.scalatest. {MustMatchers, WordSpecLike}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import akka.testkit.ImplicitSender

class MessageReceiverActorTest extends TestKit(ActorSystem("message-system"))
   with ImplicitSender  
   with MustMatchers
   with WordSpecLike {
  
  import MessageReceiverActor._
  
  "message receive actor" must {
    val messageActor = TestActorRef[MessageReceiverActor]
    
    "init listening" in {
      messageActor ! InitListening("helios-test")
      expectMsg(FiniteDuration(20, TimeUnit.SECONDS), Acknowledge(MessageStatus.Sucess))
    }
    
  }

}