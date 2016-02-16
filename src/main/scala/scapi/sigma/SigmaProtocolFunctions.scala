package scapi.sigma

import java.security.SecureRandom

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg
import scapi.sigma.SigmaProtocolFunctions.Challenge

trait SigmaProtocolTranscript[FM, SM] {
  val a: FM
  val e: Challenge
  val z: SM

  val accepted: Boolean
}

trait SigmaProtocolFunctions[FM, SM] {
  val soundness: Int

  def firstMessage(): FM

  def secondMessage(): FM

  lazy val challenge = {
    require(soundness % 8 == 0, "soundness must be fit in bytes")
    val ch = new Array[Byte](soundness / 8)
    new SecureRandom().nextBytes(ch) //modifies challenge
    ch
  }

  def simulate(): SigmaProtocolTranscript[FM, SM]
}

object SigmaProtocolFunctions {
  type Challenge = Array[Byte]

  case class FirstMessage(s: SigmaProtocolMsg)

  case class RandomChallenge(challenge: Array[Byte])

  case class SecondMessage(s: SigmaProtocolMsg)

  case object StartInteraction

  case class Transcript(a: SigmaProtocolMsg, e: Challenge, z: SigmaProtocolMsg, accepted: Boolean)
}
