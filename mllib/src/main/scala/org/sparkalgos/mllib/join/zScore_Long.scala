/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sparkAlgos.mllib.join

import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import scala.collection.immutable.Vector
import scala.math.BigInt

object zScore_Long {
  
  /**
   * Formats the integer to it's binary value with 0's spaced to the left of the binary string
   * i.e. * asNdigitBinary(3,4) = 0011
   * 
   * @param source : Long to be formatted to it's binary value
   * @param digits : the length of the binary string
   * @return binary value of <source> with length of the string equal to <digits>
   */

  def asNdigitBinary (source: Long, digits: Int): String = {
		val l: java.lang.Long = source.toBinaryString.toLong
		String.format ("%0" + digits + "d", l) 
  }
  

 
  
  /**
   * Computers the z-scores for each entry of the input RDD of Vector of Long,
   * sorted in ascending order
   * 
   * @param  rdd of Vector of Long
   * @return z-scores of the RDD[( <line_no> , <z-value> )]
   */
  def computeScore(rdd : RDD[(Vector[Long],Long)])	: RDD[(Long,BigInt)] = {

    val score = rdd.map(word => scoreOfDataPoint(word._1) -> word._2).
    			sortByKey(true).
    			map(word => word._2 -> word._1)
    score
  }
  
  
  /**
   * Computes the z-score of a Vector
   *  
   * @param Vector of Long
   * @return z-score of the vector      
   */
  def scoreOfDataPoint(vector : Vector[Long]) : BigInt = {
 
    var max = 0

    //compute the length of the largest binary string in the vector of integers
    for(i <- 0 to vector.length-1){
      if (vector(i).toBinaryString.length() > max ) max = vector(i).toBinaryString.length()
    }

    var str = new StringBuilder(max * vector.length )

    //map each integer within the vector to a formatted binary string of length <max>
    val bin2 = vector.map(word => asNdigitBinary(word, max))

    //create the string which is the binary string(z-value) for the input vector
    for(i <- 0 to max-1) {
      for(j <- 0 to vector.length-1) {
        
        str += bin2(j)(i)
      }
    }
    str = str.reverse
    
    //convert the binary string(z-value) to it's corresponding Integer value
    var b : BigInt = 0
    for(i <- 0 to str.length-1){
      if(str(i).equals('1')) b = b.setBit(i)
    }
    
    b
  }
  
}