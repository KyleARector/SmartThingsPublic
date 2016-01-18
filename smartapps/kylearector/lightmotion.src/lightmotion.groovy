/**
 *  LightMotion
 *
 *  Copyright 2016 Kyle Rector
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "LightMotion",
    namespace: "KyleARector",
    author: "Kyle Rector",
    description: "Allows lights or switches to be activated with motion, and sets a time out period to turn them off. Also allows the selection of hours to not perform the action. ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

/////////////////////////////////////////////////////
//                      Set Up                     //
///////////////////////////////////////////////////// 

preferences {
	section("Devices") {
		input "switches", "capability.switch", title:"Which Switches?", multiple: true, required: true
        input "motionSensors", "capability.motionSensor", title:"Which Motion Sensors?", multiple:true, required: true
    }
    section("Timeout Threshold (Default is 5 Min)") {
		input "timeOutThreshold", "decimal", title: "Number of Minutes", required: false 
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(motionSensors, "motionSensor", motionDetected)
}

/////////////////////////////////////////////////////
//             Event Handlers And Methods          //
/////////////////////////////////////////////////////

//If lights on, don't keep sending on command
// If motion detected while lights are still on, unschedule previous deactivation

def motionDetected(evt) {
	
}