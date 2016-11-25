package scapi.sigma

import java.security.SecureRandom

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProtocolMsg
import scapi.sigma.SigmaProtocolFunctions.Challenge

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

trait SigmaProtocol

trait ZeroKnowledgeProofOfKnowledge[SP <: SigmaProtocol]

trait SigmaProtocolCommonInput[SP <: SigmaProtocol] {
  val soundness: Int
}

trait SigmaProtocolPrivateInput[SP <: SigmaProtocol]


trait Party[SP <: SigmaProtocol, CI <: SigmaProtocolCommonInput[SP]] {
  val publicInput: CI
}


trait Prover[SP <: SigmaProtocol, CI <: SigmaProtocolCommonInput[SP], PI <: SigmaProtocolPrivateInput[SP]] extends Party[SP, CI] {
  val privateInput: PI
}

trait Verifier[SP <: SigmaProtocol, CI <: SigmaProtocolCommonInput[SP]] extends Party[SP, CI]


trait SigmaProtocolTranscript[SP <: SigmaProtocol, CI <: SigmaProtocolCommonInput[SP], FM, SM] {
  val x: CI

  val a: FM
  val e: Challenge
  val z: SM

  def accepted: Boolean
}

/*
trait SigmaProtocolFunctions[CI <: SigmaProtocolCommonInput] {
  type FM
  type SM

  def firstMessage(): FM

  def secondMessage(): FM

  def challenge(commonInput: CI) = {
    require(commonInput.soundness % 8 == 0, "soundness must be fit in bytes")
    val ch = new Array[Byte](commonInput.soundness / 8)
    new SecureRandom().nextBytes(ch) //modifies challenge
    ch
  }

  def simulate(): SigmaProtocolTranscript[CI, FM, SM]
}*/



object SigmaProtocolFunctions {
  type Challenge = Array[Byte]

  case class FirstMessage(s: SigmaProtocolMsg)

  case class RandomChallenge(challenge: Challenge)

  case class SecondMessage(s: SigmaProtocolMsg)

  case object StartInteraction

  case class Transcript(a: SigmaProtocolMsg, e: Challenge, z: SigmaProtocolMsg, accepted: Boolean)
}
