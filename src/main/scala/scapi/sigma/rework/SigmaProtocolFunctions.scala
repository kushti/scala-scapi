package scapi.sigma.rework

import java.security.SecureRandom

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg

/*
  Abstracting Sigma protocols
  Functionality to get:
  - Interactive Sigma protocols(via actors)
  - Zero-knowledge proof from a Sigma protocol
  - Non-interactive Sigma protocols
  - Commitment from any Sigma protocol
  - Signature from any Sigma protocol
  - Json and ultra-compact binary serialization/deserialization
*/

//todo: implement ring signature protocol of Groth et al.

trait TranscriptMessage {
  def bytes: Array[Byte]
}

trait ProverMessage extends TranscriptMessage

trait VerifierMessage extends TranscriptMessage

trait FirstProverMessage[SP <: SigmaProtocol[SP]] extends ProverMessage

case class Challenge(override val bytes: Array[Byte]) extends VerifierMessage

trait SecondProverMessage[SP <: SigmaProtocol[SP]] extends ProverMessage

trait SigmaProtocol[SP <: SigmaProtocol[SP]] {
  type A <: FirstProverMessage[SP]
  type Z <: SecondProverMessage[SP]
}


trait SigmaProtocolCommonInput[SP <: SigmaProtocol[SP]] {
  val soundness: Int
}

trait SigmaProtocolPrivateInput[SP <: SigmaProtocol[SP]]

/**
  * common interface for both Prover and Verifier
  */
trait Party[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP]] {
  val publicInput: CI
}


trait Prover[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP], PI <: SigmaProtocolPrivateInput[SP]] extends Party[SP, CI] {
  val privateInput: PI
}

trait InteractiveProver[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP], PI <: SigmaProtocolPrivateInput[SP]]
  extends Prover[SP, CI, PI]

trait ActorProver[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP], PI <: SigmaProtocolPrivateInput[SP]]
  extends InteractiveProver[SP, CI, PI]

trait SimulatingProver[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP]] {
  val challenge: Challenge
}


trait ZeroKnowledgeProofOfKnowledge[SP <: SigmaProtocol[SP]]

trait FiatShamir {
  def askOracleInstantiation(ask: Array[Byte]): Array[Byte]
}

trait NonInteractiveProver[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP], PI <: SigmaProtocolPrivateInput[SP]]
  extends Prover[SP, CI, PI] with FiatShamir


trait SigmaSignature[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP]] extends FiatShamir {
  val message: Array[Byte]

  def verify(): Boolean
}

trait Verifier[SP <: SigmaProtocol[SP], CI <: SigmaProtocolCommonInput[SP]] extends Party[SP, CI] {
  def challenge = Challenge({
    require(publicInput.soundness % 8 == 0, "soundness must be fit in bytes")
    val ch = new Array[Byte](publicInput.soundness / 8)
    new SecureRandom().nextBytes(ch) //modifies ch
    ch
  })
}

/**
  * Sigma Protocol transcript enough for verification
  *
  * @tparam SP
  * @tparam CI
  */
trait SigmaProtocolTranscript[
SP <: SigmaProtocol[SP],
CI <: SigmaProtocolCommonInput[SP]] {

  val x: CI

  val a: SP#A
  val e: Challenge
  val z: SP#Z

  def accepted: Boolean
}


object SigmaProtocolFunctions {

  case class FirstMessage(s: SigmaProtocolMsg)

  case class RandomChallenge(challenge: Challenge)

  case class SecondMessage(s: SigmaProtocolMsg)

  case object StartInteraction

  case class Transcript(a: SigmaProtocolMsg, e: Challenge, z: SigmaProtocolMsg, accepted: Boolean)
}