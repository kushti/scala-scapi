package scapi.sigma.rework

import edu.biu.scapi.primitives.dlog.GroupElement


object DLogProtocol {
  class DLogSigmaProtocol extends SigmaProtocol[DLogSigmaProtocol]

  case class FirstDLogProverMessage(a: GroupElement) extends FirstProverMessage[DLogSigmaProtocol] {
    override def bytes: Array[Byte] = ???
  }

  case class SecondDLogProverMessage(z: BigInt) extends SecondProverMessage[DLogSigmaProtocol] {
    override def bytes: Array[Byte] = ???
  }

}
