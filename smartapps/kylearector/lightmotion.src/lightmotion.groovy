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
		input "switches", "capability.switch", title:"Which Switches?", multiple: true, required: false
        input "motionSensors", "capability.motionSensor", title:"Which Motion Sensors?", multiple:true, required: false
    }
    section("Parameters") {
    	input "startTime", "time", title:"Start Time", required:false
        input "endTime", "time", title:"End Time", required:false
    }
    // Can add timeout threshold if necessary
    // Ecolink device motion timeout is sufficient for personal use
    /*section("Timeout Threshold (Default is 5 Min)") {
		input "timeOutThreshold", "decimal", title: "Number of Minutes", required: false 
	}*/
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
	subscribe(motionSensors, "motion", motionDetected)
}

/////////////////////////////////////////////////////
//             Event Handlers And Methods          //
/////////////////////////////////////////////////////

// Implement below if using timeout
// If lights on, don't keep sending on command
// If motion detected while lights are still on, unschedule previous deactivation

def motionDetected(evt) {
	// Only runs inside allowed hours
	if (checkTime()) {
        log.debug "${evt.name}: ${evt.value}"
        if (evt.value == "active") {
            switches.each {
                if (it.currentValue("switch") != "on")
                {
                    it.on()
                    log.debug "Turning on ${it.displayName}"
                }
            }
        }
        else {
            switches.each {
                if (it.currentValue("switch") != "off")
                {
                    it.off()
                    log.debug "Turning off ${it.displayName}"
                }
            }
        }
    }
    else {
    	log.debug "Motion detected, but off hours"
    }
}

private checkTime() {
	def result = true
    // If values not null
    // Null if user did not select optional inputs
	if (startTime && endTime) {
		def timeNow = now()
		def begin = timeToday(startTime).time
		def end = timeToday(endTime).time
		result = begin < end ? timeNow >= begin && timeNow <= end : timeNow <= end || timeNow >= begin      
	}
	return result
}