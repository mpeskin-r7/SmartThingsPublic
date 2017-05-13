/**
 *  Conditional Entry Lighting
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
    name: "Conditional Entry Lighting",
    namespace: "mpeskin-r7",
    author: "Mark Peskin",
    description: "Turns on lighting only when certain specified conditions are met.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Trigger when opened:") {
		input "triggers", "capability.contactSensor", title: "Sensors", required: true, multiple: true
        input "nightOnly", type: "bool", title: "Only run at night?", defaultValue: true, required: true
        mode name: "modeMultiple", title: "Choose run modes", required: false
	}
    section("Switches to trigger:") {
    	input "switches", "capability.switch", title: "Switches", required: true, multiple: true
        input "finalMode", "mode", title: "Set Mode when Triggered", required: false
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
	triggers.each {
    	subscribe(it,"contact.open",triggerHandler)
    }
}

def triggerHandler(evt) {
	log.debug "triggerHandler called: $evt"
    if (nightOnly) {
    	def sunData = getSunriseAndSunset()
    	if (timeOfDayIsBetween(sunData.sunrise, sunData.sunset, new Date(), location.timeZone)) {
        	log.debug "it's daytime, aborting execution"
            return
        }
    }
    switches.each { theSwitch ->
    	theSwitch.on()
    }
    if (finalMode != null) {
    	log.debug "setting mode to: $finalMode"
    	location.setMode(finalMode)
    }
}