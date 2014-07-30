package com.signalcollect.sna

import com.signalcollect.Vertex
import scala.collection.mutable.LinkedList

class Path(val sourceVertexId: Int, val targetVertexId: Int) {
  var path = scala.collection.mutable.ArrayBuffer(sourceVertexId, targetVertexId)
  var length = 2
  override def toString(): String = {
    "Path(Source Vertex: " + sourceVertexId + " Target Vertex: " + targetVertexId + " Path: " + path + " Number of Nodes on Path: " + length + ") "
  }
  def incrementSize(){
    length +=1
  }
}