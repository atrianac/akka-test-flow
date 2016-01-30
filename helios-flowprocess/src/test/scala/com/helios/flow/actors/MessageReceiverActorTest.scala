package com.helios.flow.actors

import akka.testkit.{ TestKit, TestActorRef }
import akka.actor.ActorSystem
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike

class MessageReceiverActorTest extends TestKit(ActorSystem("message-system"))
   with MustMatchers
   with WordSpecLike {
  
  "message receive actor" must {
    val messageActor = TestActorRef[MessageReceiverActor]
    
    "init listening" in {
      messageActor ! InitListening("helios-test")
    }
    
  }

}