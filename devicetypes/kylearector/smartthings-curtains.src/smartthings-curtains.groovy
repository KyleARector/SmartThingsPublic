/**
 *  SmartThings Curtains
 *
 *  Author: KyleARector
 *	Date: 2016-02-23
 *  Capabilities:
 *   Switch
 *  Custom Attributes:
 *  Custom Commands:
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
 
// NEED TO ADD MOVING STATUS UPDATE - OPEN, OPENING, CLOSE, CLOSING SENT FROM ARDUINO
 
metadata {
	definition (name: "SmartThings Curtains", author: "KyleARector") {
    		capability "Switch"
	}

	tiles(scale: 2) {
		standardTile("switch", "device.switch", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			state "on", label: 'open', action: "switch.off", icon: "st.Home.home9", backgroundColor: "#79b821"
			state "off", label: 'closed', action: "switch.on", icon: "st.Home.home9", backgroundColor: "#ffffff"
		}
        
		main "switch"
		details(["switch"])
	}
    simulator {
        status "on":  "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6E"
        status "off": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6666"
    
        // reply messages
        reply "raw 0x0 { 00 00 0a 0a 6f 6e }": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6E"
        reply "raw 0x0 { 00 00 0a 0a 6f 66 66 }": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6666"
    }
}

Map parse(String description) {

	def value = zigbee.parse(description)?.text
	def linkText = getLinkText(device)
	def descriptionText = getDescriptionText(description, linkText, value)
	def handlerName = value
	def isStateChange = value != "ping"
	def displayed = value && isStateChange

	def result = [
		value: value,
		name: value in ["on","off"] ? "switch" : (value && value != "ping" ? "greeting" : null),
		handlerName: handlerName,
		linkText: linkText,
		descriptionText: descriptionText,
		isStateChange: isStateChange,
		displayed: displayed
	]

	log.debug result.descriptionText
	result
}

def on() {
    zigbee.smartShield(text: "open").format()
}

def off() {
	zigbee.smartShield(text: "close").format()
}

def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	// TODO: handle 'greeting' attribute

}