/*
 *  Slackbot Interface
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
 
/////////////////////////////////////////////////////
//                      Set Up                     //
///////////////////////////////////////////////////// 
definition(
    name: "Slackbot Interface",
    namespace: "KyleARector",
    author: "Kyle Rector",
    description: "Slackbot Interface",
    category: "Convenience",
    iconUrl: "http://kylearector.com/favicon.ico",
    iconX2Url: "http://kylearector.com/favicon.ico",
    iconX3Url: "http://kylearector.com/favicon.ico",
    oauth: [displayName: "Slackbot Interface", displayLink: ""])
    
preferences {
	section("Sensors") {
		input "switches", "capability.switch", title:"Which Switches?", multiple: true, required: false
        input "motionSensors", "capability.motionSensor", title:"Which Motion Sensors?", multiple:true, required: false
        input "contactSensors", "capability.contactSensor", title:"Which Contact Sensors?", multiple:true, required: false
        input "presenceSensors", "capability.presenceSensor", title:"Which Presence Sensors?", multiple:true, required: false
        input "tempSensors", "capability.temperatureMeasurement", title:"Which Temperature Sensors?", multiple:true, required: false
        input "vibeSensors", "capability.accelerationSensor", title:"Which Vibration Sensors?", multiple:true, required: false
	}
}

mappings {
    path("/sensors") {
    	action: [
        	GET: "allSensors"
        ]
    }
    path("/allEvents") {
    	action: [
        	GET: "allEvents"
        ]
    }
    path("/switches") {
    	action: [
            POST: "updateSwitches"
        ]
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
	log.debug "Initialized"
    state.lastAllPoll = now()
}

/////////////////////////////////////////////////////
//             Event Handlers And Methods          //
/////////////////////////////////////////////////////
def allEvents() {
	def resp = []
    def baseEventList
    def deviceName  
    switches.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && (it.value == "on" || it.value == "off")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "switch", date: it.date]
        }
    }
    motionSensors.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && (it.value == "active" || it.value == "inactive")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "motion", date: it.date]
        }
    }
    contactSensors.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && (it.value == "open" || it.value == "closed")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "contact", date: it.date]
        }
    }
    presenceSensors.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && (it.value == "away" || it.value == "present")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "presence", date: it.date]
        }
    }
    tempSensors.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && it.value.isNumber()}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "temperature", date: it.date]
        }
    }
    vibeSensors.each {
    	deviceName = it.displayName
    	baseEventList = it.events(max: 20).findAll{ it.date > new Date(state.lastAllPoll) && (it.value == "active" || it.value == "inactive")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, capability: "acceleration", date: it.date]
        }
    }
    state.lastAllPoll = now()
    //log.debug "The most recent poll for all sensors was at ${new Date(state.lastAllPoll)}"
    resp.sort {it.date}
    resp.reverse(true)
   	return resp
}

def allSensors() {
	def resp = []
    switches.each{ 
    	resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    motionSensors.each{
    	resp << [name: it.displayName, value: it.currentValue("motion")]
    }
    contactSensors.each{
    	resp << [name: it.displayName, value: it.currentValue("contact")]
    }
    return resp
}

// Updates state of switches based on POST parameters device and command
def updateSwitches() {
	// Get parameters of request
	def device = request.JSON?.device
    def command = request.JSON?.command
    // Validate that the device supports the sent command, otherwise return error
    if (command && device) {
    	if (device != "all" && device != "everything") {
            for (thing in switches) {
                if (thing.displayName == device) {
                    if (!thing.hasCommand(command)) {
                        httpError(501, "${command} is not a valid command for ${device}")
                        break
                    }
                    else {
                        thing."$command"()
                        break
                    }
                }
            }
        }
        else {
        	// Could simply perform switches.on() if no requirement for validity checking
            // Most switches should have on() and off() 
        	switches.each {
            	if (!it.hasCommand(command)) {
                	httpError(501, "${command} is not a valid command for ${it.displayName}")
                }
                else {
                	it."$command"()
                }
        	}
        }
	}
    else {
    	httpError(501, "No device or command sent")
    }
}
