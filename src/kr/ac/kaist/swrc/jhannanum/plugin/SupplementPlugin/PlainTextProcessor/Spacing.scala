package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence
import scala.io.Source
import kr.pe.freesearch.jhannanum.comm._


class Spacing extends PlainTextProcessor {
  /** As seen from class Spacing, the missing signatures are as follows.
 *  For convenience, these are usable as stub implementations.
 */
   val numState = 2
   val numChars = 4792
  
   var hmm =  new HiddenMarkovModel(numState, numChars)
   var char_map = Map[String,Int]().withDefaultValue(numChars - 1)
   var viterbi:ViterbiAlgorithm = null
   
  // Members declared in kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor
  def doProcess(ps: PlainSentence): PlainSentence = {
    val chss = ps.getSentence.toArray	
    var seq	= Seq[Int]()
    //char to seq idx
  	for(i <- ps.getSentence){
  	  seq = seq :+ char_map(i.toString()) 
  	}
    val buf = Array.ofDim[Char](seq.length * 2)
    var space_seq = viterbi(seq, seq.length, 0)._2
    
    var spacingidx = 0
    for((x, i) <- space_seq.zipWithIndex){
      if(x == 1 ) {
        spacingidx += 1
        buf(spacingidx) = ' '
      }
      buf(spacingidx) = chss(i)
      spacingidx += 1
    } 
    val strbuf =buf.slice(0, spacingidx).mkString
    ps.setSentence(strbuf.replaceAll("""\s+""", " "))
    return ps
  }
 
  def flush(): PlainSentence = null
  def hasRemainingData(): Boolean = false
  
  // Members declared in kr.ac.kaist.swrc.jhannanum.plugin.Plugin
  def initialize(x$1: String,x$2: String): Unit = {
    val ch_idx_file =getClass.getResourceAsStream("/resources/char_idx.txt")                           
 	  val source = Source.fromInputStream(ch_idx_file, "UTF-8")
 	  val lineiter = source.getLines()          
 	  //기본값의 경우 마지막 컬럼에 위치한 기본 확률값을 사용한다.0.5:0.5
 	  //var char_map = Map[String,Int]().withDefaultValue(numChars - 1)
       
 	  for(i <- lineiter){
 		  val par_li = i.toString().split("\t")
		  char_map += (par_li(0) -> par_li(1).toInt)
 	   }
    //공백은 마지막 인덱스로 ...
 	  //char_map += (" " -> 4790)
 	  val max_idx = char_map.values.map { x => x.toInt }.max + 1
 	  char_map += (" " -> max_idx)
 	  
 	  val emission_file =getClass.getResourceAsStream("/resources/emissionProb.txt")
  	val source2 = Source.fromInputStream(emission_file, "UTF-8")
   	val lineiter2 = source2.getLines()
   	
    //hmm = new HiddenMarkovModel(2, 4792)
    
    
    for(i <- lineiter2){
    	val par_li2 = i.toString().split("\t")
    	hmm.B(par_li2(0).toInt, par_li2(1).toInt) =  par_li2(2).toDouble
    }
    //공백의 다음엔 띄어쓰기가 올 가능성이 희박하다. 
    hmm.B(1, 4790) =  0.10
    hmm.B(0, 4790) =  0.90
    //기본 발현 확률 
    hmm.B(1, 4791) =  0.60
    hmm.B(0, 4791) =  0.40
    
    //코퍼스로 부터 계산된 전이확률
  	hmm.A(0,0) = 0.6646049
  	hmm.A(0,1) = 0.33539509
  	hmm.A(1,0) = 0.9498057
  	hmm.A(1,1) = 0.05019432
  	
  	//첫 state 확률
  	hmm.Pi(0) = 0.6
  	hmm.Pi(1) = 0.4
  
 	  viterbi = new ViterbiAlgorithm(hmm)
    }
  
  def shutdown(): Unit = {}

}




object test {
 def main(args: Array[String]): Unit = {
    var ceg = new Spacing
    var te= new PlainSentence(1,1,false,
        "일정한조건에")
    //println(te.getSentence)
    ceg.initialize("", "")
    var fst = ceg.doProcess(te)
      println(fst.getSentence)
      println(fst.getDocumentID)
      println(fst.getSentenceID)
     // fst = ceg.doProcess(null)
    
    //var rem = ceg.flush()
    //if(rem != null)
     // println(rem.getSentence)

    //println(cegs.getSentence)
    //println(cegs.getSentence)
   //println(res.toString())
  }
}
