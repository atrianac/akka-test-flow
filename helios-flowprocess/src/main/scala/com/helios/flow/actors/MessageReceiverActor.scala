package com.helios.flow.actors

import akka.actor.Actor
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import javax.jms.Session
import javax.jms.Queue
import javax.jms.Message
import com.helios.flow.actors.MessageConversion._
import akka.stream.io.InputStreamSource
import java.io.FileInputStream
import java.io.File
import akka.actor.Props
import javax.jms.TextMessage

case class InitListening(queueName: String)
case class IncomingMessage(message: Message)

class MessageReceiverActor extends Actor {
  
  val downloadResource = context.actorOf(Props[DownloadResourceActor])
  
  def receive = {
    case InitListening(queueName) => {
      
      val sqsSession = createSession
      val queue = createQueue(queueName, sqsSession)
      val messageConsumer = createConsumer(queue, sqsSession)
      
      messageConsumer.setMessageListener((message: Message) => self ! IncomingMessage(message))
    }
    
    case IncomingMessage(message: TextMessage) => {
      downloadResource ! InitDownload(message.getText)
    }
  }
  
  private def createConsumer(queue: Queue, session: Session) = session.createConsumer(queue)
  
  private def createQueue(queueName: String, session: Session) = session.createQueue(queueName)
  
  private def createSession = createConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  
  private def createConnection = createSqsConnection.createConnection()

  lazy val createSqsConnection = SQSConnectionFactory
                                .builder()
                                .withRegion(Region.getRegion(Regions.US_EAST_1))
                                .withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
                                .build()
}