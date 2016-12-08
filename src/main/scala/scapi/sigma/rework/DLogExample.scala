package scapi.sigma.rework

import java.math.BigInteger

import edu.biu.scapi.primitives.dlog.{DlogGroup, ECElementSendableData, GroupElement}


object DLogProtocol {
  class DLogSigmaProtocol extends SigmaProtocol[DLogSigmaProtocol]

  case class DlogCommonInput(dlogGroup: DlogGroup, h: GroupElement, override val soundness: Int)
    extends SigmaProtocolCommonInput[DLogSigmaProtocol]

  case class DlogProverInput(w: BigInteger) extends SigmaProtocolPrivateInput[DLogSigmaProtocol]

  case class FirstDLogProverMessage(a: GroupElement) extends FirstProverMessage[DLogSigmaProtocol] {
    override def bytes: Array[Byte] = a.generateSendableData() match {
      case ed: ECElementSendableData =>
        val x = ed.getX.toByteArray
        val y = ed.getY.toByteArray

        Array(x.size.toByte, y.size.toByte) ++  x ++ y
      case _ => ???
    }
  }

  case class SecondDLogProverMessage(z: BigInt) extends SecondProverMessage[DLogSigmaProtocol] {
    override def bytes: Array[Byte] = z.toByteArray
  }

}
