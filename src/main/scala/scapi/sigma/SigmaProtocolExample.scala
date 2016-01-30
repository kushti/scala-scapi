package scapi.sigma

import java.math.BigInteger
import java.security.SecureRandom

import akka.actor.{ActorSystem, Props}
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m
import org.bouncycastle.util.BigIntegers
import scapi.sigma.SigmaProtocolMessages.StartInteraction

/**
 * Sigma Protocols are a basic building block for Zero-knowledge proofs,
 * Zero-Knowledge Proofs Of Knowledge and more. A sigma protocol is a 3-round proof,
 * comprised of:

    1. A first message from the prover to the verifier
    2. A random challenge from the verifier
    3. A second message from the prover.
 */

object SigmaProtocolExample extends App {
  val sys = ActorSystem("SigmaProtocolExample")

  val dlog = new MiraclDlogECF2m("K-233")
  val qMinusOne = dlog.getOrder().subtract(BigInteger.ONE)
  val random = new SecureRandom()
  val r = BigIntegers.createRandomInRange(BigInteger.ZERO, qMinusOne, random)
  val h = dlog.exponentiate(dlog.getGenerator(), r)

  val t = 4

  val verifier = sys.actorOf(Props(classOf[SigmaVerifierActor], t, h))
  val prover = sys.actorOf(Props(classOf[SigmaProverActor], t, h, verifier))

  prover ! StartInteraction
}
