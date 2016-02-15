package scapi.sigma

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg


object SigmaProtocolMessages {

  case class FirstMessage(s: SigmaProtocolMsg)

  case class RandomChallenge(challenge: Array[Byte])

  case class SecondMessage(s: SigmaProtocolMsg)

  case object StartInteraction

}
