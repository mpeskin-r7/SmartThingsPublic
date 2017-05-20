/**
 *  Trigger when Present
 *
 *  Copyright 2017 Mark Peskin
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
    name: "Trigger when Present",
    namespace: "mpeskin-r7",
    author: "Mark Peskin",
    description: "A simple app that triggers an associated switch only when presence is detected. Useful for Alexa integration.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When I press this toggle") {
        input "theDevices", "capability.momentary", title: "Momentary Devices", multiple: true, required: true
    }
    section("Only when all of these people are present") {
        input "people", "capability.presenceSensor", multiple: true
    }
    section("Toggle this switch") {
        input "theSwitch", "capability.switch", required: true
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
	theDevices.each {
    	subscribe(it,"momentary.pushed",pushHandler)
    }
}

def pushHandler(evt) {
    if (everyoneIsHere()) {
    	// toggle the switch
    	if (theSwitch.currentValue('switch').contains('on')) {
			theSwitch.off()
		}
		else {
    		theSwitch.on()
    	}
	}
}

// returns true if all configured sensors are present,
// false otherwise.
private everyoneIsHere() {
    def result = true
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) {
        if (person.currentPresence != "present") {
            // someone is present, so set our our result
            // variable to false and terminate the loop.
            result = false
            break
        }
    }
    log.debug "everyoneIsHere: $result"
    return result
}
