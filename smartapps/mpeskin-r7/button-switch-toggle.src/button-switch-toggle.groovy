/**
 *  Button Switch Toggle
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
    name: "Button Switch Toggle",
    namespace: "mpeskin-r7",
    author: "Mark Peskin",
    description: "App to allow a button to toggle an associated switch on and off",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When I press this button") {
        input "theDevices", "capability.button", title: "Button Devices", multiple: true, required: true
        input "theButton", type : "number", title: "Button Number", required: true, defaultValue : 1
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
    	subscribe(it,"button.pushed",buttonPushedHandler)
    }
}

def buttonPushedHandler(evt) {
    def buttonNumberMatcher = evt.data =~ /.*(\d).*/
    def buttonNumber = buttonNumberMatcher[0][1]
	log.debug "received button pushed event: $buttonNumber"
    if (buttonNumber.toInteger() == theButton.toInteger()) {
    	// toggle the switch
    	if (theSwitch.currentValue('switch').contains('on')) {
			theSwitch.off()
		}
		else {
    		theSwitch.on()
    	}
	}
}
