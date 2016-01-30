package scapi.sigma

import java.math.BigInteger
import java.security.SecureRandom

import akka.actor.{ActorRef, Actor}
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.{SigmaDlogProverInput, SigmaDlogProverComputation}
import edu.biu.scapi.primitives.dlog.GroupElement
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m
import org.bouncycastle.util.BigIntegers
import scapi.sigma.SigmaProtocolMessages.{FirstMessage, SecondMessage, RandomChallenge, StartInteraction}


class SigmaProverActor(t:Int, h:GroupElement, verifierActor: ActorRef) extends Actor {
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
