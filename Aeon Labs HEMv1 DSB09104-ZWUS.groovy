/*
 *  Aeon HEMv1
 *  DSB09104-ZWUS  
 *  (Used jscgs350's "My Aeon Home Energy Monitor Gen1" as base design)
 *  
 *  Version history
 *		20181024 v0.1  initial release; power monitoring/storage calculates and stores for 1hr, 2hr, 12hrs, 1 day and 7 days, prior data is also stored and accessible.
 *  
 *
 *  ToDo
 *  	Various cleanup
 *		Add data storage based on billing cycle end, change data stored to week 1-3 and remaining days for the current month and prior month.
 *		Add data package creation for data logging, store 15 data points across 5 minutes for each phase with corresponding date time.
 */
metadata {
	definition (
    	name: "Aeon Labs HEMv1 DSB09104-ZWUS", 
        namespace: "psyctto", 
        category: "", 
        author: "psyctto"
    )
	{
		capability "Energy Meter"
		capability "Power Meter"
		capability "Configuration"
		capability "Sensor"
		capability "Refresh"
		capability "Polling"
		capability "Battery"
		capability "Health Check"

		attribute "currentKWH", "string"		// Used to show current kWh since last reset
		attribute "currentWATTS", "string"		// Used to show current watts being used on the main tile
		attribute "minWATTS", "string"			// Used to store/display minimum watts used since last reset
		attribute "maxWATTS", "string"			// Used to store/display maximum watts used since last reset
        attribute "energyValue", "string"
		attribute "energyMessage", "string"
        attribute "energyMessageTimeAgo", "string"
        attribute "energyMessageSansTimeAgo", "string"
        attribute "energyDetail", "string"        
        attribute "energyDetailCurrentTitle", "string"
        attribute "energyDetailCurrent", "string"
        attribute "energyDetailPrevious", "string"
        attribute "energyDetailPreviousFinal", "string"
        attribute "energyDetailDisplayType", "string"
		attribute "kwhCosts", "string"			// Used to show energy costs since last reset
        attribute "resetkwhTimeStamp", "number"
        attribute "kWhLastReset", "number"
        attribute "energyDisp", "string"	//Not used, but logic and commands are in place
        attribute "energyOne", "string"		//Not used, but logic and commands are in place
        attribute "energyTwo", "string"     //Not used, but logic and commands are in place   
        attribute "powerDisp", "string"
        attribute "powerEvent", "string"
        attribute "powerOne", "string"
        attribute "powerTwo", "string"
        
        attribute "detail1hrkwh", "string"
        attribute "detail2hrkwh", "string"
        attribute "detail12hrkwh", "string"
        attribute "detail24hrkwh", "string"
        attribute "detail7dykwh", "string"
        attribute "detail1hrCost", "string"
        attribute "detail2hrCost", "string"
        attribute "detail12hrCost", "string"
        attribute "detail24hrCost", "string"
        attribute "detail7dyCost", "string"

		command "resetkwh"
        command "resetMinMax"
		command "resetMeter"
        command "togglePreviousEnergyDetail"

		fingerprint deviceId: "0x2101", inClusters: " 0x70,0x31,0x72,0x86,0x32,0x80,0x85,0x60"
	}

	// tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name:"currentWATTS", type: "generic", width: 6, height: 2, decoration: "flat"){
			tileAttribute ("device.currentWATTS", key: "PRIMARY_CONTROL") {
				attributeState "default", label: '${currentValue}W', icon: "https://raw.githubusercontent.com/constjs/jcdevhandlers/master/img/device-activity-tile@2x.png",
                backgroundColors:[
					[value: "0", 		color: "#153591"],
					[value: "3000", 	color: "#1e9cbb"],
					[value: "6000", 	color: "#90d2a7"],
					[value: "9000", 	color: "#44b621"],
					[value: "12000", 	color: "#f1d801"],
					[value: "15000", 	color: "#d04e00"], 
					[value: "18000", 	color: "#bc2323"]
				]
			}			
            tileAttribute ("device.powerDisp", key: "SECONDARY_CONTROL") {
				attributeState "powerDisp", label:'${currentValue}'
			}
		}
        valueTile("history", "device.history",width: 5, height: 1, decoration:"flat") {
			state "history", label:'${currentValue}', backgroundColor:"#ffffff"
		}
        valueTile("energyMessage", "device.energyMessage", width: 5, height: 1, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("currentKWH", "device.currentKWH", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("kwhCosts", "device.kwhCosts", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', backgroundColor:"#ffffff")
		}
        valueTile("energyDetail", "device.energyDetail", width: 6, height: 3, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', action: "togglePreviousEnergyDetail", backgroundColor:"#ffffff")
		}
        standardTile("resetMinMax", "device.resetMinMax", width: 1, height: 1, inactiveLabel: false, decoration: "ring") {
			state "default", label:'', action:"resetMinMax", icon:"st.secondary.refresh-icon", backgroundColor: "#00a0dc"
        	state "working", label:'', action:"resetMinMax", icon:"st.secondary.refresh-icon", backgroundColor: "#f1d801"
		}
		standardTile("resetkwh", "device.resetkwh", width: 1, height: 1, inactiveLabel: false, decoration: "ring") {
			state "default", label:'', action:"resetkwh", icon:"st.secondary.refresh-icon", backgroundColor: "#00a0dc"
            state "working", label:'', action:"resetkwh", icon:"st.secondary.refresh-icon", backgroundColor: "#f1d801"
		}
		standardTile("refresh", "device.refresh", width: 3, height: 2, inactiveLabel: false, decoration: "ring") {
			state "default", label:'Refresh', action:"refresh", icon:"st.secondary.refresh-icon", backgroundColor: "#00a0dc"
            state "working", label:'Refresh', action:"refresh", icon:"st.secondary.refresh-icon", backgroundColor: "#f1d801"
		}
		standardTile("configure", "device.configure", width: 3, height: 2, inactiveLabel: false, decoration: "ring") {
			state "configure", label:'Config', action:"configure", icon:"st.secondary.preferences-tile", backgroundColor: "#00a0dc"
            state "configure", label:'Config', action:"configure", icon:"st.secondary.preferences-tile", backgroundColor: "#f1d801"
		}
		

		main (["currentWATTS"])
		details(["currentWATTS", "history", "resetMinMax", "energyDetail", "currentKWH", "kwhCosts", "energyMessage", "resetkwh", "refresh", "configure"])
	}

	preferences {
        input "kWhCost", "string", title: "Enter your cost per kWh (default is 0.18):", defaultValue: 0.18, required: false, displayDuringSetup: true
    }
}

def updated() {
	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	state.displayDisabled = ("true" == displayEvents)
	state.displayBattery = ("true" == displayBatteryLevel)
    state.displayUSB = ("true" == displayUSBPower)
//	log.debug "updated (kWhCost: ${kWhCost}, wattsLimit: ${wattsLimit}, reportType: ${reportType}, wattsChanged: ${wattsChanged}, wattsPercent: ${wattsPercent}, secondsWatts: ${secondsWatts}, secondsKwh: ${secondsKwh}, secondsBattery: ${secondsBattery}, decimalPositions: ${decimalPositions})"
	response(configure())
}

def parse(String description) {
//	log.debug "Parse received ${description}"
	def result = null
	def cmd = zwave.parse(description, [0x31: 1, 0x32: 1, 0x60: 3, 0x80: 1])
//	log.debug "Parse returned ${cmd}"
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.meterv1.MeterReport cmd) {
	def dispValue
	def newValue
    def costDisplay = "Current Cost\n\$"
    def energyDetailDisplay
    def energyMessageDisplay
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    def kwhTimeStamp = now()
    def deltakwhTimeStamp = (kwhTimeStamp - state.resetkwhTimeStamp) / 60000
    def maxWatts = 24000
    //sendEvent(name: "resetMinMax", value: "default")
	if (cmd.meterType == 33) {
		if (cmd.scale == 0) {
			newValue = cmd.scaledMeterValue
//            log.debug "newValue is ${newValue} and prevValue is ${state.energyValue}"
			if (newValue != state.energyValue) {
				state.energyValue = newValue
                dispValue = String.format("%3.3f",newValue) + "kWh"
				sendEvent(name: "currentKWH", value: "Current Energy\n" + dispValue, unit: "", displayed: false)
                
				BigDecimal costDecimal = newValue * (kWhCost as BigDecimal)				
				costDisplay += String.format("%3.2f",costDecimal)
				sendEvent(name: "kwhCosts", value: costDisplay as String, unit: "", displayed: false)
				[name: "energy", value: newValue, unit: "kWh", displayed: true]
				
                if (deltakwhTimeStamp < 60){
                	state.energyMessageTimeAgo = "${String.format("%3.0f",deltakwhTimeStamp)}mins"
                }
                if (deltakwhTimeStamp >= 60){
                	state.energyMessageTimeAgo = "${String.format("%3.2f",(deltakwhTimeStamp/60))}hrs"
                }
                if (deltakwhTimeStamp >= 3600){
                	state.energyMessageTimeAgo = "${String.format("%3.2f",(deltakwhTimeStamp/3600))}days"
                }
                energyMessageDisplay = state.energyMessageSansTimeAgo + ", " + state.energyMessageTimeAgo + " ago"
                sendEvent(name: "energyMessage", value: energyMessageDisplay, unit: "", displayed: true)
                
                state.energyDetailCurrentTitle = "Energy Used Since Last Reset\n"
                energyDetailDisplay = "--------------------------\n"
                energyDetailDisplay += "Time                 Energy                 Cost   \n"
                if (deltakwhTimeStamp > 59 && deltakwhTimeStamp < 61){
                	state.detail1hrkwh = dispValue
                    state.detail1hrCost = "\$" + String.format("%3.2f",costDecimal)                	
                }
                if (deltakwhTimeStamp > 119 && deltakwhTimeStamp < 121){
                	state.detail2hrkwh = dispValue  
                    state.detail2hrCost = "\$" + String.format("%3.2f",costDecimal)
                }
                if (deltakwhTimeStamp > 719 && deltakwhTimeStamp < 721){
                	state.detail12hrkwh = dispValue  
                    state.detail12hrCost = "\$" + String.format("%3.2f",costDecimal)
                }
                if (deltakwhTimeStamp > 1439 && deltakwhTimeStamp < 1441){
                	state.detail24hrkwh = dispValue  
                    state.detail24hrCost = "\$" + String.format("%3.2f",costDecimal)
                }
                if (deltakwhTimeStamp > 10079 && deltakwhTimeStamp < 10081){
                	state.detail7dykwh = dispValue  
                    state.detail7dyCost = "\$" + String.format("%3.2f",costDecimal)
                }
                energyDetailDisplay += "1hr            ${state.detail1hrkwh}            ${state.detail1hrCost}\n"
                energyDetailDisplay += "2hrs            ${state.detail2hrkwh}            ${state.detail2hrCost}\n"
                energyDetailDisplay += "12hrs            ${state.detail12hrkwh}            ${state.detail12hrCost}\n"
                energyDetailDisplay += "1day            ${state.detail24hrkwh}            ${state.detail24hrCost}\n"
                energyDetailDisplay += "7days            ${state.detail7dykwh}            ${state.detail7dyCost}"
                state.energyDetailPreviousFinal = "\n--------Report End--------\n${state.energyMessageTimeAgo}            ${dispValue}            \$${String.format("%3.2f",costDecimal)}"
                state.energyDetailCurrent = energyDetailDisplay
                if (state.energyDetailDisplayType == "current"){
                	sendEvent(name: "energyDetail", value: "${state.energyDetailCurrentTitle}${energyDetailDisplay}", displayed: false)
                }
			}
		}
		else if (cmd.scale==2) {
			newValue = cmd.scaledMeterValue								// Remove all rounding
			if (newValue < 0) {newValue = state.powerValue}				// Don't want to see negative numbers as a valid minimum value (something isn't right with the meter) so use the last known good meter reading
			if (newValue < maxWatts) {								// don't handle any wildly large readings due to firmware issues
				if (newValue != state.powerValue) {						// Only process a meter reading if it isn't the same as the last one
					dispValue = Math.round(cmd.scaledMeterValue)					
					if (newValue < state.powerLow) {
						def dispLowValue = dispValue+"W on "+timeString
						sendEvent(name: "minWATTS", value: dispLowValue as String, unit: "", displayed: false)
						state.powerLow = newValue
                        def historyDisp = ""
						historyDisp = "Min : ${device.currentState('minWATTS')?.value}\nMax : ${device.currentState('maxWATTS')?.value}"
                        sendEvent(name: "history", value: historyDisp, displayed: false)
					}
					if (newValue > state.powerHigh) {
						def dispHighValue = dispValue+"W on "+timeString
                        def historyDisp = ""
						sendEvent(name: "maxWATTS", value: dispHighValue as String, unit: "", displayed: false)
						state.powerHigh = newValue
					    historyDisp = "Min ${device.currentState('minWATTS')?.value}\nMax ${device.currentState('maxWATTS')?.value}"
					    sendEvent(name: "history", value: historyDisp, displayed: false)
					}
					sendEvent(name: "currentWATTS", value: dispValue as String, unit: "", displayed: false)
                    
					state.powerValue = newValue
					if (state.displayDisabled) {
						[name: "power", value: newValue, unit: "W", displayed: true]
					} else {
						[name: "power", value: newValue, unit: "W", displayed: false]
					}
				}
			}
		}
	}
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	def dispValue
	def newValue
	def formattedValue
    def maxWatts = 24000

   	if (cmd.commandClass == 50) {    
   		def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1]) // can specify command class versions here like in zwave.parse
		if (encapsulatedCommand) {
			if (cmd.sourceEndPoint == 1) {
				if (encapsulatedCommand.scale == 2 ) {
					newValue = Math.round(encapsulatedCommand.scaledMeterValue)
                    if (newValue > maxWatts) { return }
					formattedValue = newValue as String
                    state.powerOne = formattedValue
                    dispValue = "L1  ${state.powerOne}W, L2 ${state.powerTwo}W"
                    sendEvent(name: "powerDisp", value: dispValue, unit: "", displayed: false)
                    dispValue = "L1  ${state.powerOne}W"
					[name: "powerEvent", value: dispValue, unit: "W", displayed: true]
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
					formattedValue = String.format("%5.2f", newValue)
					dispValue = "L1  ${formattedValue}kWh"
				}           	
			} 
			else if (cmd.sourceEndPoint == 2) {
				if (encapsulatedCommand.scale == 2 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue)
                    if (newValue > maxWatts ) { return }
					formattedValue = newValue as String
                    state.powerTwo = formattedValue
                    dispValue = "L1  ${state.powerOne}W, L2 ${state.powerTwo}W"
                    sendEvent(name: "powerDisp", value: dispValue, unit: "", displayed: false)
                    dispValue = "L2  ${state.powerTwo}W"
					[name: "powerEvent", value: dispValue, unit: "W", displayed: true]
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
					formattedValue = String.format("%5.2f", newValue)
					dispValue = "L2  ${formattedValue}kWh"
				}       	
			}
		}
	}
}

// Handles all Z-Wave commands we aren't interested in
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug "Unhandled event ${cmd}"
	[:]
}

def togglePreviousEnergyDetail() {
	log.debug "togglePreviousEnergyDetail()"
    
	if (state.energyDetailDisplayType == "current") {
		state.energyDetailDisplayType = "previous"
        //state.energyDetailPrevious = state.energyDetailPrevious.replaceAll('tracking...', 'no data')
        sendEvent(name: "energyDetail", value: "${state.energyDetailPrevious}", displayed: false)
        //sendSmsMessage("18454810575", state.energyDetailPrevious)
        //sendNotification("test notification - sms", [method: "phone", phone: "8454810575"])
	}
	else {
		state.energyDetailDisplayType = "current"
        sendEvent(name: "energyDetail", value: "${state.energyDetailCurrentTitle}${state.energyDetailCurrent}", displayed: false)
	}
}

def refresh() {
	//sendEvent(name: "refresh", value: "working")
	log.debug "Refreshed ${device.name}"
	state.energyValue = -1		// force tile update
	state.powerValue = -1
	delayBetween([
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
    //sendEvent(name: "refresh", value: "default")
}

def poll() {
	refresh()
}

// PING is used by Device-Watch in attempt to reach the Device
def ping() {
	refresh()
}

def resetkwh() {
	//sendEvent(name: "resetkwh", value: "working")
	//log.debug "${device.name} reset kWh/Cost values"
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    state.resetkwhTimeStamp = now()
    state.energyMessageSansTimeAgo = "Energy Data (kWh/Cost) Reset On\n" + timeString
    state.energyDetailPrevious = "Previous Energy Report (Length ${state.energyMessageTimeAgo})\n" + state.energyDetailCurrent.replaceAll('tracking...', 'no data') + state.energyDetailPreviousFinal
    state.energyDetailCurrent = "waiting..."
    state.energyDetailDisplayType = "current"
    state.detail1hrkwh = "tracking..."
    state.detail2hrkwh = "tracking..."
    state.detail12hrkwh = "tracking..."
    state.detail24hrkwh = "tracking..."
    state.detail7dykwh = "tracking..."
    state.detail1hrCost = "tracking..."
    state.detail2hrCost = "tracking..."
    state.detail12hrCost = "tracking..."
    state.detail24hrCost = "tracking..."
    state.detail7dyCost = "tracking..."
    state.energyValue = 0
    sendEvent(name: "energyMessage", value: state.energyMessageSansTimeAgo, unit: "", displayed: true)
	sendEvent(name: "currentKWH", value: "waiting...", unit: "", displayed: false)
	sendEvent(name: "kwhCosts", value: "waiting...", unit: "", displayed: false)
    sendEvent(name: "energyDetail", value: "waiting...", unit: "", displayed: false)
    
	def cmd = delayBetween( [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
	cmd
    //sendEvent(name: "resetkwh", value: "default")
}

def resetMinMax() {
	//sendEvent(name: "resetMinMax", value: "working")
	//log.debug "${device.name} reset minimum and maximum watts values"
    def historyDisp = ""
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    state.powerLow = 99999
    state.powerHigh = 0	
	sendEvent(name: "minWATTS", value: "tracking...", unit: "", displayed: false)
    sendEvent(name: "maxWATTS", value: "tracking...", unit: "", displayed: false)
    
    historyDisp = "Min ${device.currentState('minWATTS')?.value}\nMax ${device.currentState('maxWATTS')?.value}"
    sendEvent(name: "history", value: historyDisp, displayed: false)
    
	def cmd = delayBetween( [
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
	cmd    
}

def resetMeter() {
	//sendEvent(name: "resetMeter", value: "working")
	//log.debug "Resetting all home energy meter values..."
    def historyDisp = ""
	state.powerHigh = 0
	state.powerLow = 99999
    state.energyValue = 0
	sendEvent(name: "minWATTS", value: "tracking...", unit: "", displayed: false)
	sendEvent(name: "maxWATTS", value: "tracking...", unit: "", displayed: false)
	sendEvent(name: "currentKWH", value: "waiting...", unit: "", displayed: false)
	sendEvent(name: "kwhCosts", value: "waiting...", unit: "", displayed: false)
    
    historyDisp = "Power Low : ${device.currentState('minWATTS')?.value}\nPower High : ${device.currentState('maxWATTS')?.value}"
    sendEvent(name: "history", value: historyDisp, displayed: false)
    
	def cmd = delayBetween( [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
	cmd
    //sendEvent(name: "resetMeter", value: "default")
}

def configure() {
	//sendEvent(name: "configure", value: "working")
	//log.debug "${device.name} configuring..."

	def cmd = delayBetween([
        // Perform a complete factory reset. Use this all by itself and comment out all others below.
        // Once reset, comment this line out and uncomment the others to go back to normal
//    	zwave.configurationV1.configurationSet(parameterNumber: 255, size: 4, scaledConfigurationValue: 1).format()
/*        // Accumulate kWh energy when Battery Powered. By default this is disabled to assist saving battery power. (0 == disable, 1 == enable)
        zwave.configurationV1.configurationSet(parameterNumber: 12, size: 1, scaledConfigurationValue: 1).format(),
        // Send data based on a time interval (0), or based on a change in wattage (1).	 0 is default and enables parameters 111, 112, and 113. 1 enables parameters 4 and 8.
        zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: reportType).format(),
        // If parameter 3 is 1, don't send unless watts have changed by 50 <default> for the whole device.
        zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: wattsChanged).format(),
        // If parameter 3 is 1, don't send unless watts have changed by 10% <default> for the whole device.
        zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: wattsPercent).format(),
        // Defines the type of report sent for Reporting Group 1 for the whole device.	1->Battery Report, 4->Meter Report for Watt, 8->Meter Report for kWh
        zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 4).format(), //watts
        // If parameter 3 is 0, report every XX Seconds (for Watts) for Reporting Group 1 for the whole device.
        zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: secondsWatts).format(),
        // Defines the type of report sent for Reporting Group 2 for the whole device.	1->Battery Report, 4->Meter Report for Watt, 8->Meter Report for kWh
        zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 8).format(), //kWh
        // If parameter 3 is 0, report every XX seconds (for kWh) for Reporting Group 2 for the whole device.
        zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: secondsKwh).format(),
        // Defines the type of report sent for Reporting Group 3 for the whole device.	1->Battery Report, 4->Meter Report for Watt, 8->Meter Report for kWh
        zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 1).format(), //battery
        // If parameter 3 is 0, report every XX seconds (for battery) for Reporting Group 2 for the whole device.
        zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: secondsBattery).format()
 */
 		zwave.configurationV1.configurationSet(parameterNumber: 100, size: 1, scaledConfigurationValue: 0).format(),		// reset to defaults
        zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 6149).format(),   	// Total Power, L1/L2 Energy
		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: 10).format(), 		// Every 10 seconds
		zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 1572872).format(),	// Total Energy
		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: 60).format(), 		// Every 1 minute
		zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 770).format(),		// L1/L2 Power
		zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 10).format() 		// Every 10 seconds
	])

	cmd
    //sendEvent(name: "configure", value: "default")
}
