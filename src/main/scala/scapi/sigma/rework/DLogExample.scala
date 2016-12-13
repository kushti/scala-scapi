package scapi.sigma.rework

import java.math.BigInteger
import java.security.SecureRandom

import edu.biu.scapi.primitives.dlog.{DlogGroup, ECElementSendableData, GroupElement}
import org.bouncycastle.util.BigIntegers


object DLogProtocol {
  class DLogSigmaProtocol extends SigmaProtocol[DLogSigmaProtocol]{
    override type A = FirstDLogProverMessage
    override type Z = SecondDLogProverMessage
  }

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

  class DlogProver(override val publicInput: DlogCommonInput, override val privateInput: DlogProverInput)
    extends Prover[DLogSigmaProtocol, DlogCommonInput, DlogProverInput] {

    lazy val group = publicInput.dlogGroup

    var rOpt: Option[BigInteger] = None

    override def firstMessage: FirstDLogProverMessage = {
      val qMinusOne = group.getOrder.subtract(BigInteger.ONE)
      val r = BigIntegers.createRandomInRange(BigInteger.ZERO, qMinusOne, new SecureRandom)
      rOpt = Some(r)
      val a = group.exponentiate(group.getGenerator, r)
      FirstDLogProverMessage(a)
    }

    override def secondMessage(challenge: Challenge): SecondDLogProverMessage = {
      val q: BigInteger = group.getOrder
      val e: BigInteger = new BigInteger(1, challenge.bytes)
      val ew: BigInteger = e.multiply(privateInput.w).mod(q)
      val z: BigInteger = rOpt.get.add(ew).mod(q)
      rOpt = None
      SecondDLogProverMessage(z)
    }
  }

  case class DLogActorProver(override val publicInput: DlogCommonInput, override val privateInput: DlogProverInput)
    extends DlogProver(publicInput, privateInput) with ActorProver[DLogSigmaProtocol, DlogCommonInput, DlogProverInput]

}
