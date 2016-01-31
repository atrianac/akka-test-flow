package com.helios.flow.actors

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.helios.flow.actors.MessageConversion.convertMessage

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import javax.jms.Message
import javax.jms.Queue
import javax.jms.Session
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
                                .builder
                                .withRegion(Region.getRegion(Regions.US_WEST_2))
                                .withAWSCredentialsProvider(new ClasspathPropertiesFileCredentialsProvider)
                                .build
}