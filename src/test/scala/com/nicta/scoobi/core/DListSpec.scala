/**
 * Copyright 2011,2012 National ICT Australia Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nicta.scoobi
package core

import org.apache.hadoop.io.Text

import testing.mutable.NictaSimpleJobs
import Scoobi._
import org.specs2.matcher.TerminationMatchers
import impl.plan.comp.CompNodeData._
import impl.plan.comp.Optimiser
import impl.plan.DListImpl

class DListSpec extends NictaSimpleJobs with TerminationMatchers {

  tag("issue 99")
  "a DList can be created and persisted with some Text" >> { implicit sc: SC =>
    val list = DList((new Text("key1"), new Text("value1")), (new Text("key2"), new Text("value2")))
    run(list).map(_.toString).sorted must_== Seq("(key1,value1)", "(key2,value2)")
  }

  tag("issue 117")
  "A complex graph example must not throw an exception" >> { implicit sc: SC =>

    def simpleJoin[T: WireFormat, V: WireFormat](a: DList[(Int, T)], b: DList[(Int, V)]) =
      (a.map(x => (x._1, x._1)) ++ b.map(x => (x._1, x._1))).groupByKey

    val data = DList((12 -> 13), (14 -> 15), (13 -> 55))
    val (a, b, c, d, e) = (data, data, data, data, data)

    val q = simpleJoin(simpleJoin(a, b), simpleJoin(c, d))
    val res = simpleJoin(q, simpleJoin(q, e).groupByKey)

    normalise(res.run) === "Vector((12,Vector(12, 12)), (13,Vector(13, 13)), (14,Vector(14, 14)))"
  }

  tag("issue 119")
  "joining an object created from random elements and a DList must not crash" >> { implicit sc: SC =>
    val r = new scala.util.Random

    val s = (1 to 10).map(i => (i, r.nextInt(i))).
                      groupBy(_._2).
                      mapValues(r.shuffle(_))

    (DObject(s) join DList(1, 2, 3)).run must not(throwAn[Exception])
  }

  tag("issue 137")
  "DList.concat will concatenate multiple DLists." >> { implicit sc: SC =>
    val aa = DList(1 to 5)
    val bb = DList(6 to 10)

    (aa ++ bb).run.sorted must_== (1 to 10).toSeq
  }

  tag("issue 194")
  "Length of an empty list should be zero" >> { implicit sc: SC =>
     DList[Int]().length.run === 0
  }

  "DLists can be concatenated via reduce" >> {
    "without group by key" >> { implicit sc: SC =>
      Seq.fill(5)(DList(1 -> 2)).reduce(_++_).run === Seq.fill(5)(1 -> 2)
    }
    "with a group by key" >> { implicit sc: SC =>
      Seq.fill(5)(DList(1 -> 2)).reduce(_++_).groupByKey.run.toList.toString === Seq(1 -> Vector.fill(5)(2)).toString
    }
  }

}
