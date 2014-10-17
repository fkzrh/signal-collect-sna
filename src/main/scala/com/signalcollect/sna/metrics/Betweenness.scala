/*
 *  @author Flavio Keller
 *
 *  Copyright 2014 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.signalcollect.sna.metrics

import java.math.MathContext

import scala.BigDecimal
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable.ArrayBuffer

import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.PathCollector
import com.signalcollect.sna.PathCollectorVertex

object Betweenness {
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val pathCollectorExecRes = PathCollector.run(graph)
    val shortestPathList = PathCollector.allShortestPathsAsList(pathCollectorExecRes.vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])
    val betweennessMap = getBetweennessForAll(pathCollectorExecRes.vertexArray, shortestPathList)
    new ExecutionResult(new ComputationResults(calcAvg(betweennessMap), betweennessMap), pathCollectorExecRes.vertexArray, pathCollectorExecRes.stats)
  }

  def getBetweennessForAll(vertices: ArrayBuffer[Vertex[Any, _, Any, Any]], shortestPathList: List[Path]): java.util.Map[String, Object] = {
    var betweennessMap = new java.util.TreeMap[String, Object]
    for (betweennessVertex <- vertices) {
      val pathsThroughVertex = shortestPathList.filter(path => path.sourceVertexId != betweennessVertex.id && path.targetVertexId != betweennessVertex.id && path.path.contains(betweennessVertex.id))
      val betweenness = BigDecimal(pathsThroughVertex.size.toDouble / shortestPathList.size.toDouble).round(new MathContext(3)).toDouble
      betweennessMap.put(betweennessVertex.id.toString, betweenness.asInstanceOf[Object])
    }
    betweennessMap
  }

  def calcAvg(betweennessMap: java.util.Map[String, Object]): Double = {
    val betweennessValues = betweennessMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(betweennessValues.foldLeft(0.0)(_ + _) / betweennessValues.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}