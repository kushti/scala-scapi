package scapi.sigma.dlog

import java.math.BigInteger
import java.security.SecureRandom

import akka.actor.{Actor, ActorRef}
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.{SigmaBIMsg, SigmaGroupElementMsg}
import edu.biu.scapi.primitives.dlog.GroupElement
import org.bouncycastle.util.BigIntegers
import scapi.sigma.SigmaProtocolMessages
import SigmaProtocolMessages.{FirstMessage, RandomChallenge, SecondMessage, StartInteraction}


class Prover(commonInput: CommonInput,
             proverInput: ProverInput,
             verifierActor: ActorRef) extends Actor {

  val dlog = commonInput.dlogGroup
  val w = proverInput.w
  val random = new SecureRandom()

  def beforeFirstMessage:Receive = {
    case StartInteraction =>
      val qMinusOne = dlog.getOrder.subtract(BigInteger.ONE)

      val r = BigIntegers.createRandomInRange(BigInteger.ZERO, qMinusOne, random)

      //Compute a = g^r.
      val a: GroupElement = dlog.exponentiate(dlog.getGenerator, r)

      val sigmaProtocolMsg = new SigmaGroupElementMsg(a.generateSendableData)
      verifierActor ! FirstMessage(sigmaProtocolMsg)
      context become beforeSecondMessage(r)
  }

  def beforeSecondMessage(r:BigInteger): Receive  = {
    case RandomChallenge(challenge) =>
      //todo: check challenge length
      //Compute z = (r+ew) mod q
      val q: BigInteger = dlog.getOrder
      val e: BigInteger = new BigInteger(1, challenge)
      val ew: BigInteger = e.multiply(w).mod(q)
      val z: BigInteger = r.add(ew).mod(q)
      verifierActor ! SecondMessage(new SigmaBIMsg(z))
  }

  override def receive = beforeFirstMessage
}
