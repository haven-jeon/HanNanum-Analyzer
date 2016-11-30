import scala.io.Source

import scala.collection.mutable.Map

import kr.pe.freesearch.jhannanum.comm._

object hannanum_ws {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(167); 
  println("Welcome to the Scala worksheet");$skip(39); 
  val data = Array.ofDim[Double](2, 2);System.out.println("""data  : Array[Array[Double]] = """ + $show(data ));$skip(27); 
  val li = data.map(_.sum);System.out.println("""li  : Array[Double] = """ + $show(li ));$skip(12); val res$0 = 
  li.length;System.out.println("""res0: Int = """ + $show(res$0));$skip(75); 
 	val ch_idx_file =getClass.getResourceAsStream("/resources/char_idx.txt");System.out.println("""ch_idx_file  : java.io.InputStream = """ + $show(ch_idx_file ));$skip(158); 
 	//val source = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/char_idx.txt", "UTF-8")
 	val source = Source.fromInputStream(ch_idx_file, "UTF-8");System.out.println("""source  : scala.io.BufferedSource = """ + $show(source ));$skip(35); 
 	val lineiter = source.getLines();System.out.println("""lineiter  : Iterator[String] = """ + $show(lineiter ));$skip(96); 
 	//기본값의 경우 마지막 컬럼에 위치한 기본 확률값을 사용한다.
 	var char_map = Map[String,Int]().withDefaultValue(4791);System.out.println("""char_map  : scala.collection.mutable.Map[String,Int] = """ + $show(char_map ));$skip(146); 
 	for(i <- lineiter){
 		val par_li = i.toString().split("\t")
 		//println(par_li(0).toList(0))
		char_map += (par_li(0) -> par_li(1).toInt)
 	};$skip(61); 
 	val max_idx = char_map.values.map { x => x.toInt }.max + 1;System.out.println("""max_idx  : Int = """ + $show(max_idx ));$skip(31); val res$1 = 
 	char_map += (" " -> max_idx);System.out.println("""res1: scala.collection.mutable.Map[String,Int] = """ + $show(res$1));$skip(69); val res$2 = 
 	//char_map += (par_li(0) -> par_li(1).toInt)
 	char_map.values.max;System.out.println("""res2: Int = """ + $show(res$2));$skip(85); 
 	
	
	val emission_file =getClass.getResourceAsStream("/resources/emissionProb.txt");System.out.println("""emission_file  : java.io.InputStream = """ + $show(emission_file ));$skip(62); 
	val source2 = Source.fromInputStream(emission_file, "UTF-8");System.out.println("""source2  : scala.io.BufferedSource = """ + $show(source2 ));$skip(140); 
 	//val source2 = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/emissionProb.txt", "UTF-8")
 	val lineiter2 = source2.getLines();System.out.println("""lineiter2  : Iterator[String] = """ + $show(lineiter2 ));$skip(51); 
 	
  var B = new ObservationProbabilities(2, 4792);System.out.println("""B  : kr.pe.freesearch.jhannanum.comm.ObservationProbabilities = """ + $show(B ));$skip(48); 
  var hmmmodel = new HiddenMarkovModel(2, 4792);System.out.println("""hmmmodel  : kr.pe.freesearch.jhannanum.comm.HiddenMarkovModel = """ + $show(hmmmodel ));$skip(149); 
  
  
  for(i <- lineiter2){
  	val par_li2 = i.toString().split("\t")
  	hmmmodel.B(par_li2(0).toInt, par_li2(1).toInt) =  par_li2(2).toDouble
  };$skip(37); 
  //공백
  hmmmodel.B(1, 4790) =  0.10;$skip(30); 
  hmmmodel.B(0, 4790) =  0.90;$skip(38); 
  //기본값
  hmmmodel.B(1, 4791) =  0.50;$skip(30); 
  hmmmodel.B(0, 4791) =  0.50;$skip(32); 
  
	hmmmodel.A(0,0) = 0.6646049;$skip(30); 
	hmmmodel.A(0,1) = 0.33539509;$skip(29); 
	hmmmodel.A(1,0) = 0.9498057;$skip(30); 
	hmmmodel.A(1,1) = 0.05019432;$skip(26); 
	
	
	hmmmodel.Pi(0) = 0.9;$skip(22); 
	hmmmodel.Pi(1) = 0.1;$skip(91); 
	
	//PrintStream original = new PrintStream(System.out);
	hmmmodel.prettyPrint(System.out);$skip(28); 
	
	
	
 var seq	= Seq[Int]();System.out.println("""seq  : Seq[Int] = """ + $show(seq ));$skip(85); 
	for(i <- "일정한조건에따르면,자유롭게이것을재배포할수가있습니다. 힗러리 "){
	seq = seq :+ char_map(i.toString())

	};$skip(18); 
	println(seq);$skip(33); 
	
        val observations = seq;System.out.println("""observations  : Seq[Int] = """ + $show(observations ));$skip(25); 
        val endState = 0;System.out.println("""endState  : Int = """ + $show(endState ));$skip(53); 
        val viterbi = new ViterbiAlgorithm(hmmmodel);System.out.println("""viterbi  : kr.pe.freesearch.jhannanum.comm.ViterbiAlgorithm = """ + $show(viterbi ));$skip(172); 
       
        
       // val expectedProbability: Double = matches.values.max.toDouble / attempts
        
        val delta: Double = viterbi(seq,seq.size, endState)._1;System.out.println("""delta  : Double = """ + $show(delta ));$skip(55); 

        val a: Seq[Int] = viterbi(seq,seq.size, 0)._2;System.out.println("""a  : Seq[Int] = """ + $show(a ));$skip(19); 
        println(a)}
        //assertEquals(expectedProbability, delta, tolerance)
}
	