

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

			var str:String  = null;
			if ((str = ps.getSentence()) == null) {
				return null;
			}
			sents = str

			endOfDocument = ps.isEndOfDocument();
	  }
	  
	  
	  sent_iter.setText(sents)
	  var start = sent_iter.first()
	  var end = sent_iter.next()
	  if (end != BreakIterator.DONE) {
      var sent = sents.substring(start,end)
      bufSents = sents.substring(end)
      sentenceID += 1
      hasRemainingData = true
      return new PlainSentence(documentID, sentenceID - 1, !hasRemainingData && endOfDocument, sent)
	  }else{
	    hasRemainingData = false
	    return null
	  }
}
	  
	
	def flush(): PlainSentence = {
	  return null
	  }

	
	// Members declared in kr.ac.kaist.swrc.jhannanum.plugin.Plugin 
	
	def initialize(x$1: String,x$2: String): Unit = ??? 
	
	def shutdown(): Unit = ??? 
	
}

/*

object test {
 def main(args: Array[String]): Unit = {
    var ceg = new SentenceSegmentor
    var te= new PlainSentence(1,1,false, "'인터넷 소설이 등장하면서' 소설을 쓰는 사람들이 늘어나긴 했지만, 소설을 읽는 사람이 줄어들면서 그들만의 세계가 되어 버렸다. 그러나 이후 국내 소설계에서 무시할 수 없는 비중을 차지하게 된 양판소와 귀여니류 연애소설은 불쏘시개 취급 받으며 시간때우기에 불과하다는 평가를 자주 받곤 하지만, 애초에 시간때우기 용이라는 말은 바꿔 말하면 시간을 때울 정도는 된다는 이야기다. 결국 아무리 까여도 보는 사람이 있기 때문에 쓰고 그것이 출판으로 이어지는 것이다. 특히 귀여니의 소설들은 인터넷 소설이 본격적으로 텍스트화, 즉 출판이 되는 시발점이 되었다는 점에서 여러모로 의의가 있다고 할 수 있다. 사실 문학계에서 온라인의 글이 이모티콘과 맞춤법.을 안 지키고 그대로. 활자화 된 것은 엄청난 혁명이라고 말할 수 있다. 까는거야 까여야 하는 거지만 일단 이런 의의가 있다는건 알아두자.  U.S. A. Introduction. I'm fine... 12.42")
    var fst = ceg.doProcess(te)
    while( fst != null){
      println(fst.getSentence)
      fst = ceg.doProcess(te)
    }
    var rem = ceg.flush()
    if(rem != null)
      println(rem.getSentence)

    //println(cegs.getSentence)
    //println(cegs.getSentence)
   //println(res.toString())
  }
}

*/

