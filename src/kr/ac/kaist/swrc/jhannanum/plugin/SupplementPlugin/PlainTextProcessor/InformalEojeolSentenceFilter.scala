package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor2.SentenceSegmentor2

/**
 * This module prevent to process long Eojole before Morphlogical analysis by adding space between long Eojole.
 *  
 */


class InformalEojeolSentenceFilter extends PlainTextProcessor{
  /** As seen from class InformalEojeolSentenceFilter, the missing signatures are as follows.
 *  For convenience, these are usable as stub implementations.
 */
  // Members declared in kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor
  var long_eojole_length = 20
  var smallest_eojole_len = 10
  
  def doProcess(ps: PlainSentence): PlainSentence = {
    var buf = new StringBuilder
    var word = new String
    var tokens = ps.getSentence().split("\\s+")
    for(token <- tokens){
      if(token.length() >= long_eojole_length){
        for(i <- 0 until token.length()/smallest_eojole_len + 1){
          buf.append(token.slice(0 + i * smallest_eojole_len, smallest_eojole_len + i * smallest_eojole_len))
          buf.append(" ")
        }
      }else{
        buf.append(token)
        buf.append(" ")
      }
    }
    ps.setSentence(buf.toString())
    return ps
  }
  
  def flush(): PlainSentence = null
  def hasRemainingData(): Boolean = false
  
  // Members declared in kr.ac.kaist.swrc.jhannanum.plugin.Plugin
  def initialize(x$1: String,x$2: String): Unit = {}
  def shutdown(): Unit = {}
  
}



