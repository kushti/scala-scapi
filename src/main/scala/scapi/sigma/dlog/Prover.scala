package scapi.sigma.dlog

import java.math.BigInteger
import java.security.SecureRandom

import akka.actor.{Actor, ActorRef}
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.{SigmaDlogProverComputation, SigmaDlogProverInput}
import edu.biu.scapi.primitives.dlog.GroupElement
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m
import org.bouncycastle.util.BigIntegers
import scapi.sigma.SigmaProtocolMessages
import SigmaProtocolMessages.{FirstMessage, RandomChallenge, SecondMessage, StartInteraction}


class Prover(t: Int, h: GroupElement, verifierActor: ActorRef) extends Actor {
  val dlog = new MiraclDlogECF2m("K-233")

  val proverComputation = new SigmaDlogProverComputation(dlog, t, new SecureRandom())

  override def receive = {
    case StartInteraction =>
      val qMinusOne = dlog.getOrder.subtract(BigInteger.ONE)
      val random = new SecureRandom()
      val w = BigIntegers.createRandomInRange(BigInteger.ZERO, qMinusOne, random)
      val input = new SigmaDlogProverInput(h, w)
      val a = proverComputation.computeFirstMsg(input)
      verifierActor ! FirstMessage(a)

    case RandomChallenge(challenge) =>
      val z = this.proverComputation.computeSecondMsg(challenge)
      verifierActor ! SecondMessage(z)
  }
}
