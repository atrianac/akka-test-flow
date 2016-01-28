package com.helios.actors

import akka.actor.Actor
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import javax.jms.Session

case class InitListening(queueName: String)
case class IncomingMessage(message: Object)

class MessageReceiverActor extends Actor {
  
 
  
  def receive = {
    case InitListening(queueNaeme) => {
      val sqsSession = createConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)
      sqsSession.createQueue(queueNaeme)
    }
  }
  
  def createConnection = createSqsConnection.createConnection()

  lazy val createSqsConnection = SQSConnectionFactory
                                .builder()
                                .withRegion(Region.getRegion(Regions.US_EAST_1))
                                .withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
                                .build()
}