import scala.io.Source

import scala.collection.mutable.Map

import kr.pe.freesearch.jhannanum.comm._

object hannanum_ws {
  println("Welcome to the Scala worksheet")
  val data = Array.ofDim[Double](2, 2)
  val li = data.map(_.sum)
  li.length
 	val ch_idx_file =getClass.getResourceAsStream("/resources/char_idx.txt")
 	//val source = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/char_idx.txt", "UTF-8")
 	val source = Source.fromInputStream(ch_idx_file, "UTF-8")
 	val lineiter = source.getLines()
 	//기본값의 경우 마지막 컬럼에 위치한 기본 확률값을 사용한다.
 	var char_map = Map[String,Int]().withDefaultValue(4791)
 	for(i <- lineiter){
 		val par_li = i.toString().split("\t")
 		//println(par_li(0).toList(0))
		char_map += (par_li(0) -> par_li(1).toInt)
 	}
 	val max_idx = char_map.values.map { x => x.toInt }.max + 1
 	char_map += (" " -> max_idx)
 	//char_map += (par_li(0) -> par_li(1).toInt)
 	char_map.values.max
 	
	
	val emission_file =getClass.getResourceAsStream("/resources/emissionProb.txt")
	val source2 = Source.fromInputStream(emission_file, "UTF-8")
 	//val source2 = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/emissionProb.txt", "UTF-8")
 	val lineiter2 = source2.getLines()
 	
  var B = new ObservationProbabilities(2, 4792)
  var hmmmodel = new HiddenMarkovModel(2, 4792)
  
  
  for(i <- lineiter2){
  	val par_li2 = i.toString().split("\t")
  	hmmmodel.B(par_li2(0).toInt, par_li2(1).toInt) =  par_li2(2).toDouble
  }
  //공백
  hmmmodel.B(1, 4790) =  0.10
  hmmmodel.B(0, 4790) =  0.90
  //기본값
  hmmmodel.B(1, 4791) =  0.50
  hmmmodel.B(0, 4791) =  0.50
  
	hmmmodel.A(0,0) = 0.6646049
	hmmmodel.A(0,1) = 0.33539509
	hmmmodel.A(1,0) = 0.9498057
	hmmmodel.A(1,1) = 0.05019432
	
	
	hmmmodel.Pi(0) = 0.9
	hmmmodel.Pi(1) = 0.1
	
	//PrintStream original = new PrintStream(System.out);
	hmmmodel.prettyPrint(System.out)
	
	
	
 var seq	= Seq[Int]()
	for(i <- "일정한조건에따르면,자유롭게이것을재배포할수가있습니다. 힗러리 "){
	seq = seq :+ char_map(i.toString())

	}
	println(seq)
	
        val observations = seq
        val endState = 0
        val viterbi = new ViterbiAlgorithm(hmmmodel)
       
        
       // val expectedProbability: Double = matches.values.max.toDouble / attempts
        
        val delta: Double = viterbi(seq,seq.size, endState)._1

        val a: Seq[Int] = viterbi(seq,seq.size, 0)._2
        println(a)
        //assertEquals(expectedProbability, delta, tolerance)
}
	