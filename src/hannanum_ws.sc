import scala.io.Source

import scala.collection.mutable.Map

import kr.pe.freesearch.jhannanum.comm._

object hannanum_ws {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val data = Array.ofDim[Double](2, 2)            //> data  : Array[Array[Double]] = Array(Array(0.0, 0.0), Array(0.0, 0.0))
  val li = data.map(_.sum)                        //> li  : Array[Double] = Array(0.0, 0.0)
  li.length                                       //> res0: Int = 2
 	val ch_idx_file =getClass.getResourceAsStream("/resources/char_idx.txt")
                                                  //> ch_idx_file  : java.io.InputStream = java.io.BufferedInputStream@1d3c468a
 	//val source = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/char_idx.txt", "UTF-8")
 	val source = Source.fromInputStream(ch_idx_file, "UTF-8")
                                                  //> source  : scala.io.BufferedSource = non-empty iterator
 	val lineiter = source.getLines()          //> lineiter  : Iterator[String] = non-empty iterator
 	//기본값의 경우 마지막 컬럼에 위치한 기본 확률값을 사용한다.
 	var char_map = Map[String,Int]().withDefaultValue(4791)
                                                  //> char_map  : scala.collection.mutable.Map[String,Int] = Map()
 	for(i <- lineiter){
 		val par_li = i.toString().split("\t")
 		//println(par_li(0).toList(0))
		char_map += (par_li(0) -> par_li(1).toInt)
 	}
 	val max_idx = char_map.values.map { x => x.toInt }.max + 1
                                                  //> max_idx  : Int = 4790
 	char_map += (" " -> max_idx)              //> res1: scala.collection.mutable.Map[String,Int] = Map(교 -> 359, 組 -> 2249
                                                  //| , 兩 -> 4638, 誌 -> 1364, 錫 -> 1643, 左 -> 1884, 츌 -> 4689, 쨍 -> 14
                                                  //| 73, 논 -> 680, 퐁 -> 1861, 튬 -> 1243, 꿨 -> 1193, 臭 -> 3589, 改 -> 2
                                                  //| 324, 잇 -> 141, 後 -> 1695, 盈 -> 4478, 莊 -> 3606, 蓄 -> 3417, 훼 -> 
                                                  //| 1179, 초 -> 243, 暻 -> 1388, 끔 -> 902, 자 -> 20, 敦 -> 2533, 옻 -> 19
                                                  //| 58, 팡 -> 1004, 톱 -> 1475, 넙 -> 2868, 艇 -> 4639, 蜂 -> 3419, 湖 -> 
                                                  //| 2589, 톺 -> 4074, 땅 -> 627, ㎙ -> 4333, 뉜 -> 1854, 꿍 -> 2150, 閨 ->
                                                  //|  4040, 紗 -> 3973, 津 -> 2962, 탈 -> 497, 聽 -> 1576, 層 -> 3669, 쭐 -
                                                  //| > 2506, 企 -> 2371, ㎢ -> 702, 袍 -> 4177, 究 -> 3625, 楊 -> 3258, 柵 
                                                  //| -> 3277, 媒 -> 3364, 니 -> 148, 燎 -> 3940, 월 -> 404, 벡 -> 2428, 맷 
                                                  //| -> 2172, 띨 -> 2861, 蘇 -> 1753, 楓 -> 4000, 쾅 -> 1788, \ -> 4574, 丘 
                                                  //| -> 2973, 淳 -> 4307, 』 -> 2033, 띱 -> 4787, 鈞 -> 1805, 般 -> 2368, �
                                                  //| � -> 2311, 誰 -> 2924, e -> 1089, 틴 -> 1085, 준 -> 305, ㅒ -> 3803, 쁘
                                                  //|  -> 954, 똥 -> 1703, 듐 -> 2637, 西 -> 1778, 曺 -> 3323, ­ -> 3654, 斥
                                                  //|  -> 3311, 팼 -> 3
                                                  //| Output exceeds cutoff limit.
 	//char_map += (par_li(0) -> par_li(1).toInt)
 	char_map.values.max                       //> res2: Int = 4790
 	
	
	val emission_file =getClass.getResourceAsStream("/resources/emissionProb.txt")
                                                  //> emission_file  : java.io.InputStream = java.io.BufferedInputStream@3abc8e1e
                                                  //| 
	val source2 = Source.fromInputStream(emission_file, "UTF-8")
                                                  //> source2  : scala.io.BufferedSource = non-empty iterator
 	//val source2 = Source.fromFile("C:/Users/gogamza/Documents/work/spacing/emissionProb.txt", "UTF-8")
 	val lineiter2 = source2.getLines()        //> lineiter2  : Iterator[String] = non-empty iterator
 	
  var B = new ObservationProbabilities(2, 4792)   //> B  : kr.pe.freesearch.jhannanum.comm.ObservationProbabilities = kr.pe.frees
                                                  //| earch.jhannanum.comm.ObservationProbabilities@7d2452e8
  var hmmmodel = new HiddenMarkovModel(2, 4792)   //> hmmmodel  : kr.pe.freesearch.jhannanum.comm.HiddenMarkovModel = kr.pe.frees
                                                  //| earch.jhannanum.comm.HiddenMarkovModel@5f70bea5
  
  
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
	hmmmodel.prettyPrint(System.out)          //> A: [ [0.6646049 0.33539509]
                                                  //|      [0.9498057 0.05019432] ]
                                                  //| B: [ [0.816195 0.023429 0.751253 0.820822 0.798161 0.763444 0.900033 0.9258
                                                  //| 75 0.981283 0.077493 0.950716 0.946827 0.819966 0.898415 0.861392 0.931545 
                                                  //| 0.476701 0.951687 0.856591 0.88785 0.86618 0.633088 0.96721 0.114453 0.8331
                                                  //| 33 0.883177 0.869485 0.951851 0.870678 0.076512 0.96023 0.8796 0.982132 0.9
                                                  //| 41315 0.743463 0.975027 0.585247 0.279934 0.878386 0.868813 0.18265 0.91062
                                                  //| 3 0.931298 0.944878 0.14907 1.0 0.936881 0.12915 0.792905 0.503755 0.862318
                                                  //|  0.840927 0.965826 0.748238 0.964136 0.913493 0.792328 0.996443 0.576683 0.
                                                  //| 593017 0.901622 0.952467 0.162182 0.944444 0.794152 0.902138 0.993974 0.187
                                                  //| 485 1.0 0.962093 0.8752 0.79798 0.381196 0.89149 0.810651 0.923077 0.835896
                                                  //|  0.945107 0.833207 0.495252 0.553075 0.191574 0.993743 0.992986 0.993258 0.
                                                  //| 988478 0.967515 0.986938 0.987163 0.277179 0.91927 0.90234 0.904548 0.92525
                                                  //| 8 0.295116 0.908391 0.967089 0.995
                                                  //| Output exceeds cutoff limit.
	
	
	
 var seq	= Seq[Int]()                      //> seq  : Seq[Int] = List()
	for(i <- "일정한조건에따르면,자유롭게이것을재배포할수가있습니다. 힗러리 "){
	seq = seq :+ char_map(i.toString())

	}
	println(seq)                              //> List(301, 145, 123, 98, 222, 125, 459, 386, 192, 47, 20, 278, 618, 220, 36,
                                                  //|  22, 1, 185, 186, 77, 120, 64, 118, 82, 147, 148, 17, 62, 4790, 4791, 112, 
                                                  //| 76, 4790)
	
        val observations = seq                    //> observations  : Seq[Int] = List(301, 145, 123, 98, 222, 125, 459, 386, 192,
                                                  //|  47, 20, 278, 618, 220, 36, 22, 1, 185, 186, 77, 120, 64, 118, 82, 147, 148
                                                  //| , 17, 62, 4790, 4791, 112, 76, 4790)
        val endState = 0                          //> endState  : Int = 0
        val viterbi = new ViterbiAlgorithm(hmmmodel)
                                                  //> viterbi  : kr.pe.freesearch.jhannanum.comm.ViterbiAlgorithm = kr.pe.freesea
                                                  //| rch.jhannanum.comm.ViterbiAlgorithm@571a75a2
       
        
       // val expectedProbability: Double = matches.values.max.toDouble / attempts
        
        val delta: Double = viterbi(seq,seq.size, endState)._1
                                                  //> delta  : Double = 2.6858360372937463E-10

        val a: Seq[Int] = viterbi(seq,seq.size, 0)._2
                                                  //> a  : Seq[Int] = List(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 
                                                  //| 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0)
        println(a)                                //> List(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0
                                                  //| , 0, 0, 0, 0, 1, 0, 0, 0, 0)
        //assertEquals(expectedProbability, delta, tolerance)
}
	