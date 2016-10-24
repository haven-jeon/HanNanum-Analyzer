

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor2

/**
 * @author gogamza
 *
 */
import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor
import java.text.BreakIterator
import  kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor

class SentenceSegmentor2 extends PlainTextProcessor {
  	/** the ID of the document */
	private var documentID:Int = 0
	
	/** the ID of the sentence */
	private var sentenceID:Int = 0
	
	/** the flag to check if there is remaining data in the input buffer */
	var hasRemainingData:Boolean = false
	
	/** the buffer for storing intermediate results */
	private var bufRes:String  = null;
	
	/** the buffer for storing the remaining part after one sentence returned */
	private var bufSents:String = null
	
	/** the index of the buffer for storing the remaining part */
	private var bufEojeolsIdx:Int = 0
	
	/** the flag to check whether current sentence is the end of document */
	private var endOfDocument:Boolean = false
	
	private var sent_iter  = BreakIterator.getSentenceInstance
	
	def doProcess(ps: PlainSentence): PlainSentence = {
	  var sents:String = null;
	  
	  if(bufSents != null){
	    sents = bufSents
	  }else{
	    if (ps == null) {
				return null;
			}

			if (documentID != ps.getDocumentID()) {
				documentID = ps.getDocumentID();
				sentenceID = 0;
			}

			var str:String  = ps.getSentence();
			if (str == null) {
				return null;
			}
			sents = str

			endOfDocument = ps.isEndOfDocument();
	  }
	  
	  
	  sent_iter.setText(sents)
	  var start = sent_iter.first()
	  var end = sent_iter.next()
	  if (end != BreakIterator.DONE) {
	    var raw_sent = sents.substring(start,end).trim()
      var sent = raw_sent.substring(start,raw_sent.length() - 1) + " " + raw_sent.substring(raw_sent.length() - 1, raw_sent.length())
      bufSents = sents.substring(end)
      sentenceID += 1
      if(bufSents.trim() == ""){
        bufSents = null
        hasRemainingData = false
      }else{
        hasRemainingData = true 
      }
	    
      return new PlainSentence(documentID, sentenceID - 1, !hasRemainingData && endOfDocument, sent)
	  }else{
	    bufSents = null
	    hasRemainingData = false
	    return null
	  }
}
	  
	
	def flush(): PlainSentence = {
	  return null
	  }

	
	// Members declared in kr.ac.kaist.swrc.jhannanum.plugin.Plugin 
	
	def initialize(x$1: String,x$2: String): Unit = {}
	
	def shutdown(): Unit = {}
	
}


object test {
 def main(args: Array[String]): Unit = {
    var ceg = new SentenceSegmentor2
    var te= new PlainSentence(1,1,false,
        "아름다운 우리나라 금수강산에서 살자! 그러나 나는 뭐가 중한지 모르겠다.")
    var fst = ceg.doProcess(te)
    while( fst != null){
      println(fst.getSentence)
      println(fst.getDocumentID)
      println(fst.getSentenceID)
      fst = ceg.doProcess(null)
    }
    //var rem = ceg.flush()
    //if(rem != null)
     // println(rem.getSentence)

    //println(cegs.getSentence)
    //println(cegs.getSentence)
   //println(res.toString())
  }
}

