package scapi.ot

import edu.biu.scapi.interactiveMidProtocols.ot.{OTSMsg, OTRGroupElementPairMsg}


object ObliviousTransferProtocolMessages {
   case class Start(sigma:Byte)
   case class ComputedTuple(msg:OTRGroupElementPairMsg)
   case class GroupElements(msg:OTSMsg)
 }
