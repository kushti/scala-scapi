package scapi.sigma.dlog

import java.security.SecureRandom

import akka.actor.Actor
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.{SigmaDlogCommonInput, SigmaDlogVerifierComputation}
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg
import edu.biu.scapi.primitives.dlog.GroupElement
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m
import scapi.sigma.SigmaProtocolMessages
import SigmaProtocolMessages.{FirstMessage, RandomChallenge, SecondMessage}


class SigmaVerifierActor(t: Int, h: GroupElement) extends Actor {
  val dlog = new MiraclDlogECF2m("K-233")

  val verifierComputation = new SigmaDlogVerifierComputation(dlog, t, new SecureRandom())
  val input = new SigmaDlogCommonInput(h)

  var a: SigmaProtocolMsg = _

  override def receive = {
    case FirstMessage(am) =>
      a = am
      verifierComputation.sampleChallenge()
      val sampleChallenge = verifierComputation.getChallenge
      sender() ! RandomChallenge(sampleChallenge)

    case SecondMessage(z) =>
      val result = verifierComputation.verify(input, a, z)
      println("Verification result: " + result)
  }
}
