package scapi.sigma

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg


object SigmaProtocol {
  type Challenge = Array[Byte]

  case class FirstMessage(s: SigmaProtocolMsg)

  case class RandomChallenge(challenge: Array[Byte])

  case class SecondMessage(s: SigmaProtocolMsg)

  case object StartInteraction

  case class Transcript(a: SigmaProtocolMsg, e: Challenge, z: SigmaProtocolMsg, accepted: Boolean)
}
