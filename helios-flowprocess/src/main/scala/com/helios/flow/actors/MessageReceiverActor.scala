package com.helios.flow.actors

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.helios.flow.actors.MessageConversion.convertMessage
import akka.actor.{Actor, Props, actorRef2Scala}
import javax.jms.{Message, Queue, Session, TextMessage}
import akka.actor.ActorLogging


object MessageStatus extends Enumeration {
  val Sucess, Error = Value
}

object MessageReceiverActor {
  case class InitListening(queueName: String)
  case class IncomingMessage(message: Message)
  case class Acknowledge(status: MessageStatus.Value)  
}


class MessageReceiverActor extends Actor with ActorLogging {
  
  import MessageReceiverActor._
  import DownloadResourceActor._
  
  val downloadResource = context.actorOf(Props[DownloadResourceActor])
  
  def receive = {
    case InitListening(queueName) => {
      
      val sqsSession = createSession
      val queue = createQueue(queueName, sqsSession)
      val messageConsumer = createConsumer(queue, sqsSession)
      
      messageConsumer.setMessageListener((message: Message) => self ! IncomingMessage(message))
      sender() ! Acknowledge(MessageStatus.Sucess)
    }
    
    case IncomingMessage(message: TextMessage) => {
      downloadResource ! InitDownload(new S3Message("", ""))
    }
    
    case _ => log.debug("Message not supported")
  }
  
  def getS3Message(message: TextMessage) = message.getText
  
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