/*
 *  Dash Button Home Control
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
    name: "Dash Button Home Control",
    namespace: "KyleARector",
    author: "Kyle Rector",
    description: "SmartApp Web Service for home control with Amazon Dash Buttons",
    category: "Health & Wellness",
    iconUrl: "http://kylearector.com/favicon.ico",
    iconX2Url: "http://kylearector.com/favicon.ico",
    iconX3Url: "http://kylearector.com/favicon.ico",
    oauth: [displayName: "Dash Button Home Control", displayLink: ""])

// Sections for common device/functionality types
// Multiple devices supported, no explicit declaration
// Required for installation   
preferences {
	section("Things to Control") {
		input "btn1Switches", "capability.switch", title:"Switches For Button 1", multiple: true, required: false
        input "btn2Switches", "capability.switch", title:"Switches For Button 2", multiple: true, required: false
	}
}
 
// Required for web services in the SmartApp
// Routes HTTP requests to resulting actions

// Event sets for each type of capability/sensor, one overarching collect events poll (/recentEvents)
mappings {
    path("/control") {
    	action: [
        	GET: "listSwitches",
            POST: "toggleSwitches"
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
}

/////////////////////////////////////////////////////
//             Event Handlers And Methods          //
/////////////////////////////////////////////////////


// TO DO:
// Implement default off behavior if not all devices in same state

// Updates a specific switch with a specific commmand sent 
def toggleSwitches() {
	// Get parameters of request
	def button = request.JSON?.button
    // Validate that the device supports the sent command, otherwise return error
    if (button) {
    	if (button == "1") {
        	// Get the state of the first device
            // Apply the opposite state to other devices to synchronize
        	if (btn1Switches[0].currentValue("switch") == "off") {
            	for (thing in btn1Switches) {
                	thing.on()
            	}
            }
            else {
                for (thing in btn1Switches) {
                	thing.off()
            	}
            }
        }
        else if (button == "2") {
        	// Get the state of the first device
            // Apply the opposite state to other devices to synchronize
        	if (btn2Switches[0].currentValue("switch") == "off") {
            	for (thing in btn2Switches) {
                	thing.on()
            	}
            }
            else {
                for (thing in btn2Switches) {
                	thing.off()
            	}
            }
        }
        else {
        	httpError(501, "Not a valid button number")
        }
    }
}
