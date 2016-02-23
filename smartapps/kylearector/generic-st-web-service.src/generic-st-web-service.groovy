/*
 *  Generic ST Web Service
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
    name: "Generic ST Web Service",
    namespace: "KyleARector",
    author: "Kyle Rector",
    description: "SmartApp Web Service",
    category: "Convenience",
    iconUrl: "http://kylearector.com/favicon.ico",
    iconX2Url: "http://kylearector.com/favicon.ico",
    iconX3Url: "http://kylearector.com/favicon.ico",
    oauth: [displayName: "SmartApp Web Service", displayLink: ""])

// Sections for common device/functionality types
// Multiple devices supported, no explicit declaration
// Required for installation   
preferences {
	section("Sensors") {
		input "switches", "capability.switch", title:"Which Switches?", multiple: true, required: false
        input "powerMeters", "capability.powerMeter", title:"Which Power Meters?", multiple: true, required: false
        input "motionSensors", "capability.motionSensor", title:"Which Motion Sensors?", multiple:true, required: false
        input "contactSensors", "capability.contactSensor", title:"Which Contact Sensors?", multiple:true, required: false
        input "presenceSensors", "capability.presenceSensor", title:"Which Presence Sensors?", multiple:true, required: false
        input "tempSensors", "capability.temperatureMeasurement", title:"Which Temperature Sensors?", multiple:true, required: false
        input "vibeSensors", "capability.accelerationSensor", title:"Which Vibration Sensors?", multiple:true, required: false
	}
}
 
// Required for web services in the SmartApp
// Routes HTTP requests to resulting actions

// Event sets for each type of capability/sensor, one overarching collect events poll (/recentEvents)
mappings {
    path("/allEvents") {
    	action: [
        	GET: "allEvents",
        ]
    }
    path("/switches") {
    	action: [
        	GET: "listSwitches",
            POST: "updateSwitches"
        ]
    }
    path("/switchState") {
    	action: [
        	GET: "switchState"
        ]
    }
    path("/motionSensors") {
    	action: [
        	GET: "listMotionSensors"
        ]
    }
    path("/motionEvents") {
    	action: [
        	GET: "motionEvents"
        ]
    }
    path("/contactSensors") {
    	action: [
        	GET: "listContactSensors"
        ]
    }
    path("/contactEvents") {
    	action: [
        	GET: "contactEvents"
        ]
    }
    path("/presenceSensors") {
    	action: [
        	GET: "listPresenceSensors"
        ]
    }
    path("/presenceEvents") {
    	action: [
        	GET: "presenceEvents"
        ]
    }
    // May need to have a subscription that raises events if certain criteria are met. Set in preferences?
    path("/tempSensors") {
    	action: [
        	GET: "listTempSensors"
        ]
    }
    path("/tempEvents") {
    	action: [
        	GET: "tempEvents"
        ]
    }
    // May need to have a subscription that raises events if certain criteria are met. Set in preferences?
    path("/vibeSensors") {
    	action: [
        	GET: "listVibeSensors"
        ]
    }
    path("/vibeEvents") {
    	action: [
        	GET: "vibeEvents"
        ]
    }
}

// Called immediately after installation of SmartApp
// Used to subscribe to device handlers and schedule tasks
def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
   	// Possibly move state variables here so that they are only overwritten when installed, not updated
}

// Called when changes are made to user selected inputs
def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

// Set subscriptions to events here
def initialize() {
	log.debug "Initialized"
	state.lastAllPoll = now()
    state.lastMotionPoll = now()
    state.lastContactPoll = now()
    state.lastPresencePoll = now()
    state.lastTempPoll = now()
    state.lastVibePoll = now()
    log.debug "The most recent poll was at ${new Date(state.lastAllPoll)}"
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
    log.debug "The most recent poll for all sensors was at ${new Date(state.lastAllPoll)}"
    resp.sort {it.date}
    resp.reverse(true)
   	return resp
}

def updateSwitches() {
	// Get parameters of request
	def device = request.JSON?.device
    def command = request.JSON?.command
    // Validate that the device supports the sent command, otherwise return error
    if (command && device) {
    	if (device != "all") {
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

def listSwitches() {
	def resp = []
	switches.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def switchState() {
	def resp = []
    def device = params.device
    if (device) {
    	for (thing in switches) {
        	if (thing.displayName == device) {
            	resp << [name: device, value: thing.currentValue("switch")]
                break
            }
        }
    }
    else {
    	httpError(501, "No device sent")
    }
    return resp
}

def listMotionSensors() {
	def resp = []
	motionSensors.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def motionEvents() {
	def resp = []
    def deviceName     
    motionSensors.each {
    	deviceName = it.displayName
    	def baseEventList = it.events(max: 50).findAll{ it.date > new Date(state.lastMotionPoll) && (it.value == "active" || it.value == "inactive")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, date: it.date]
        }
    }
    state.lastMotionPoll = now()
    log.debug "The most recent motion poll was at ${new Date(state.lastMotionPoll)}"
    resp.sort {it.date}
    resp.reverse(true)
    return resp
}

def listContactSensors() {
	def resp = []
	contactSensors.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def contactEvents() {
	def resp = []
    def deviceName    
    contactSensors.each {
    	deviceName = it.displayName
    	def baseEventList = it.events(max: 50).findAll{ it.date > new Date(state.lastContactPoll) && (it.value == "open" || it.value == "closed")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, date: it.date]
        }
    }
    state.lastContactPoll = now()
    log.debug "The most recent contact poll was at ${new Date(state.lastContactPoll)}"
    resp.sort {it.date}
    resp.reverse(true)
    return resp
}

def listPresenceSensors() {
	def resp = []
	presenceSensors.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def presenceEvents() {
	def resp = []
    def deviceName      
    presenceSensors.each {
    	deviceName = it.displayName
    	def baseEventList = it.events(max: 50).findAll{ it.date > new Date(state.lastPresencePoll) && (it.value == "away" || it.value == "present")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, date: it.date]
        }
    }
    state.lastPresencePoll = now()
    log.debug "The most recent presence poll was at ${new Date(state.lastPresencePoll)}"
    resp.sort {it.date}
    resp.reverse(true)
    return resp
}

def listTempSensors() {
	def resp = []
	tempSensors.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def tempEvents() {
	def resp = []
    def deviceName     
    tempSensors.each {
    	deviceName = it.displayName
    	def baseEventList = it.events(max: 50).findAll{ it.date > new Date(state.lastTempPoll) && it.value.isNumber()}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, date: it.date]
        }
    }
    state.lastTempPoll = now()
    log.debug "The most recent temperature poll was at ${new Date(state.lastTempPoll)}"
    resp.sort {it.date}
    resp.reverse(true)
    return resp
}

def listVibeSensors() {
	def resp = []
	vibeSensors.each {
		resp << [name: it.displayName, id: it.id]
	}
	return resp
}

def vibeEvents() {
	def resp = []
    def deviceName     
    vibeSensors.each {
    	deviceName = it.displayName
    	def baseEventList = it.events(max: 50).findAll{ it.date > new Date(state.lastVibePoll) && (it.value == "active" || it.value == "inactive")}
        baseEventList.each {
        	resp << [name: deviceName, value: it.value, date: it.date]
        }
    }
    state.lastVibePoll = now()
    log.debug "The most recent vibration poll was at ${new Date(state.lastVibePoll)}"
    resp.sort {it.date}
    resp.reverse(true)
    return resp
}