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
package impl
package exec

import core._
import plan.comp._

trait ExecutionMode extends CompNodes {

  def checkSourceAndSinks(node: CompNode)(implicit sc: ScoobiConfiguration) {
    initAttributable(node)
    node match {
      case process: ProcessNode => process.sinks.filterNot { case b: Bridge => hasBeenFilled(b); case _ => true }.foreach(_.outputCheck)
      case load: Load           => load.source.inputCheck
      case _                    => ()
    }
    children(node).foreach(n => checkSourceAndSinks(n))
  }
}
