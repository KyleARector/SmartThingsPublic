/**
 *  DoorMinder
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
    name: "DoorMinder",
    namespace: "KyleARector",
    author: "Kyle Rector",
    description: "Delivers gently reminders when a door or window has been left open",
    category: "Convenience",
    iconUrl: "http://kylearector.com/favicon.ico",
    iconX2Url: "http://kylearector.com/favicon.ico",
    iconX3Url: "http://kylearector.com/favicon.ico"
)

// Sections for common device/functionality types
// Multiple devices supported, no explicit declaration
// Required for installation   
preferences {
	section("Sensors") {
        input "contactSensors", "capability.contactSensor", title:"Which Door or Window Sensors?", multiple:true, required: false
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

def allEvents() {

}