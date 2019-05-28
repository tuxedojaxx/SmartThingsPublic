/**
Copyright Sinopé Technologies
1.0.5
SVN-503
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
**/

metadata {

preferences {
		input("AirFloorModeParam", "enum", title: "Control mode (Default: Floor)", description:"Control mode using the floor or ambient temperature.", options: ["Ambient", "Floor"], multiple: false, required: false)
        input("RoomSetpointMinParam", "number", title:"Maximum Setpoint (5C to 36C / 41F to 97F)", range:("5..97"), description:"Minimum setpoint temperature available. Must be lower than Maximum setpoint.", required: false)
    	input("RoomSetpointMaxParam", "number", title:"Maximum Setpoint (5C to 36C / 41F to 97F)", range:("5..97"), description:"Maximum setpoint temperature available. Must be higher than Minimum setpoint.", required: false)
		//input("UnoccupySetpointParam", "number", title: "Away setpoint (5C to 36C / 41F to 97F)", description: "Thermostat's assigned temperature when the network is in away mode.", range:("5..36"), required: false)
        input("BacklightAutoDimParam", "enum", title:"Backlight setting (Default: Sensing)", description: "On Demand or Sensing", options: ["Sensing", "On Demand"], multiple: false, required: false)
    	input("KbdLockParam", "enum", title: "Keypad lock (Default: Unlocked)", description: "Enable or disable the device's buttons.",options: ["Lock", "Unlock"], multiple: false, required: false)
    	input("TimeFormatParam", "enum", title:"Time Format (Default: 24h)", description: "Time format displayed by the device.", options:["24h", "12h AM/PM"], multiple: false, required: false)
    	input("EnableOutdorTemperatureParam", "bool", title: "enable/disable outdoor temperature", description: "Set it to true to enable outdoor temperature on the thermostat")
        input("FloorSensorTypeParam", "enum", title:"Probe type (Default: 10k)", description: "Choose probe type.", options: ["10k", "12k"], multiple: false, required: false)
    	input("FloorMaxAirTemperatureParam", "number", title:"Ambient limit (5C to 36C / 41F to 97)", range:("5..97"),
    		description: "The maximum ambient temperature limit when in floor control mode.", required: false)
        input("FloorLimitMinParam", "number", title:"Floor low limit (5C to 36C / 41F to 97F)", range:("5..97"), description: "The minimum temperature limit of the floor when in ambient control mode.", required: false)
    	input("FloorLimitMaxParam", "number", title:"Floor high limit (5C to 36C / 41F to 97F)", range:("5..97"), description: "The maximum temperature limit of the floor when in ambient control mode.", required: false)
    	input("AuxLoadParam", "number", title:"Auxiliary load value (Default: 0)", range:("0..65535"),
        	description: "Enter the power in watts of the heating element connected to the auxiliary output.", required: false)
    	input("trace", "bool", title: "Trace (Only for debugging)", description: "Set it to true to enable tracing")
		input("logFilter", "number", title: "Trace level", range: "1..5",
			description: "1= ERROR only, 2= <1+WARNING>, 3= <2+INFO>, 4= <3+DEBUG>, 5= <4+TRACE>")
}


	definition(name: "TH1300ZB Sinope Thermostat", namespace: "Sinope Technologies", author: "Sinope Technologies") {
		capability "thermostatHeatingSetpoint"
		capability "thermostatMode"
		capability "thermostatOperatingState"
		capability "thermostatSetpoint"
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Thermostat"
		capability "Configuration"
		capability "Refresh"
		capability "Sensor"

		attribute "temperatureDisplayMode", "enum", ["Deg_C", "Deg_F"]
		attribute "occupancyStatus", "enum", ["unoccupy", "occupy"]
		attribute "outdoorTemp", "string"
		attribute "heatingSetpointRangeHigh", "string"
		attribute "heatingSetpointRangeLow", "string"
		attribute "heatingSetpointRange", "VECTOR3"
		attribute "verboseTrace", "string"
		attribute "presence", "enum", ["present", "non present"]
		attribute "keypadLockStatus", "enum", ["unlock", "lock1"]
		//		attribute "keypadLockStatus" ,"enum", ["unlock", "lock1","lock2","lock3","lock4","lock5"]
        attribute "gfciStatus", "enum", ["OK", "error"]
        attribute "floorLimitStatus", "enum", ["OK", "floorLimitLowReached", "floorLimitMaxReached", "floorAirLimitLowReached", "floorAirLimitMaxReached"]
        attribute "temperature", "string"

		attribute "rangeHigh", "number"
        attribute "rangeLow", "number"

		command "heatLevelUp"
		command "heatLevelDown"
		command "setThermostatMode"
		command "switchMode"
		command "setTemperatureDisplayMode"
		command "setOccupancyStatus"
		command "setThermostatSetpoint", ["number"]
		command "setHeatingSetpointRangeHigh"
		command "setHeatingSetpointRangeLow"
		command "setHeatingSetpointRange"
		command "unoccupy"
		command "occupy"
		command "present"
		command "away"
		command "unlock"
		command "lock1"
		command "setLockStatus"
        command "updateZipCode"
		
		fingerprint endpoint: "1",
			profileId: "0104",
			inClusters: "0000,0003,0004,0005,0201,0204,0402,0B04,0B05,FF01",
			outClusters: "0003", manufacturer: "Sinope Technologies", model: "TH1300ZB"
	}


	//--------------------------------------------------------------------------------------------------------

	// simulator metadata
	simulator {}

	//--------------------------------------------------------------------------------------------------------
	tiles(scale: 2) {
		multiAttributeTile(name: "thermostatMulti", type: "thermostat", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label: '${currentValue}', unit: "dF", backgroundColor: "#269bd2")
			}
			tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "heatLevelUp")
				attributeState("VALUE_DOWN", action: "heatLevelDown")
			}
			tileAttribute("device.heatingDemand", key: "SECONDARY_CONTROL") {
				attributeState("default", label: '${currentValue}%', unit: "%")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor: "#44b621")
				attributeState("heating", backgroundColor: "#ffa81e")
				attributeState("cooling", backgroundColor: "#269bd2")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label: '${name}')
				attributeState("heat", label: '${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("default", label: '${currentValue}', unit: "dF")
			}
		}
		//-- Value Tiles -------------------------------------------------------------------------------------------

		valueTile("temperature", "device.temperature", inactiveLabel: false, width: 2, height: 2) {
			state("temperature", label:'${currentValue}', backgroundColors: getBackgroundColors() )		}

		valueTile("heatingDemand", "device.heatingDemand", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "heatingDemand", label: '${currentValue}%', unit: "%", backgroundColor: "#ffffff"
		}

		//-- Standard Tiles ----------------------------------------------------------------------------------------

		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "heat", label: '${currentValue}', backgroundColor: "#ffffff"
		}

		standardTile("heatLevelUp", "device.heatingSetpoint", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "default", label: '', action: "heatLevelUp", icon: "st.thermostat.thermostat-up"
		}

		standardTile("heatLevelDown", "device.heatingSetpoint", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "default", label: '', action: "heatLevelDown", icon: "st.thermostat.thermostat-down"
		}


		standardTile("operatingState", "device.thermostatOperatingState", width: 2, height: 2) {
			state "idle", label: '${name}', backgroundColor: "#ffffff"
			state "heating", label: '${name}', backgroundColor: "#e86d13"
			state "cooling", label: '${name}', backgroundColor: "#00A0DC"
		}
		standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
			state "off", label: '', action: "switchMode", icon: "st.thermostat.heating-cooling-off"
			state "heat", label: '', action: "switchMode", icon: "st.thermostat.heat", defaultState: true
		}

		standardTile("temperatureDisplayMode", "device.temperatureDisplayMode", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
			state "Deg_C", label: '${name}', action: "setTemperatureDisplayMode", icon: "st.alarm.temperature.normal", defaultState: true
			state "Deg_F", label: '${name}', action: "setTemperatureDisplayMode", icon: "st.alarm.temperature.normal"
		}

		standardTile("occupancyStatus", "device.occupancyStatus", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
			state "occupy", label: '${name}', action: "setOccupancyStatus", icon: "st.Home.home4", defaultState: true
			state "unoccupy", label: '${name}', action: "setOccupancyStatus", icon: "st.presence.car.car"
		}
		standardTile("lockStatus", "device.keypadLockStatus", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
			state "unlock", label: '${name}', action: "setLockStatus", icon: "st.presence.house.unlocked", defaultState: true
			state "lock1", label: '${name}', action: "setLockStatus", icon: "st.presence.house.secured"
		}

		standardTile("refresh", "device.temperature", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		standardTile("weatherTemperature", "device.outdoorTemp", inactiveLabel: false, width: 2, height: 2,
			decoration: "flat", canChangeIcon: false) {
			state "default", label: 'OutdoorTemp ${currentValue}', unit: "dF",
				icon: "st.Weather.weather2",
				backgroundColor: "#ffffff"
		}
        standardTile("gfciStatus", "device.gfciStatus", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
        	state "OK", label: "GFCI", backgroundColor: "#44b621"//green
            state "error", label: "GFCI", backgroundColor: "#bc2323"//red
        }
		standardTile("floorLimitStatus", "device.floorLimitStatus", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "OK", label: 'Limit OK', backgroundColor: "#44b621"//green
            state "floorLimitLowReached", label: 'Limit low', backgroundColor: "#153591"//blue
            state "floorLimitMaxReached", label: 'Limit high', backgroundColor: "#ff8133"//orange
            state "floorAirLimitLowReached", label: 'Limit low', backgroundColor: "#153591"//blue
            state "floorAirLimitMaxReached", label: 'Limit high', backgroundColor: "#ff8133"//orange
		}

		//-- Control Tiles -----------------------------------------------------------------------------------------
		/*controlTile("levelSliderControl", "device.heatingSetpoint", "slider", height: 2, width: 6, inactiveLabel: false, range:"(5..36)") {
             state "heatingSetpoint", action:"setLevel"
        }*/
		//-- Main & Details ----------------------------------------------------------------------------------------

		main("thermostatMulti")
		details(["thermostatMulti",
        	//"levelSliderControl",
			//			"heatLevelUp","heatingSetpoint","heatLevelDown",
            //"temperature",
			"thermostatMode",
			//			"occupancyStatus", 
			//			"lockStatus",
			//			"temperatureDisplayMode", 
			//			"configure",
            "gfciStatus",
            "floorLimitStatus",
			"refresh"
		])
	}
}

def setLevel(value)
{
	traceEvent(settings.logFilter,"level: ${value}", settings.trace)
    if(RoomSetpointMinParam && (value < RoomSetpointMinParam))
    {
    	traceEvent(settings.logFilter,"value < RoomSetpointMinParam: ${value}", settings.trace)
    	value = RoomSetpointMinParam
    }
    else if(RoomSetpointMaxParam && (value > RoomSetpointMaxParam))
    {
    	traceEvent(settings.logFilter,"value > RoomSetpointMaxParam: ${value}", settings.trace)
    	value = RoomSetpointMaxParam
    }
    traceEvent(settings.logFilter,"setpoint: ${value}", settings.trace)
    setHeatingSetpoint(value)
}

def getBackgroundColors() {
	def results
	if (state?.scale == 'C') {
		// Celsius Color Range
		results = [
			[value: 0, color: "#153591"],
			[value: 7, color: "#1e9cbb"],
			[value: 15, color: "#90d2a7"],
			[value: 23, color: "#44b621"],
			[value: 29, color: "#f1d801"],
			[value: 35, color: "#d04e00"],
			[value: 37, color: "#bc2323"]
		]
	} else {
		results =
			// Fahrenheit Color Range
			[
				[value: 31, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 95, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			]
	}
	return results
       
}



//-- Installation ----------------------------------------------------------------------------------------


def installed() {
	traceEvent(settings.logFilter, "installed>Device is now Installed", settings.trace)
    //runEvery15Minutes(refresh_misc)
	initialize()
	//configure('installed')
}

def updated() {
	   
 	//initialize()
  	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 15000) {
		state.updatedLastRanAt = now()  
		def cmds = []

		traceEvent(settings.logFilter, "updated>Device is now updated", settings.trace)
		try {
			unschedule()
		} catch (e) {
			traceEvent(settings.logFilter, "updated>exception $e, continue processing", settings.trace, get_LOG_ERROR())
		}
        
        runEvery15Minutes(refresh_misc)

        /*
        if(UnoccupySetpointParam){
            def UnoccupySetpoint = UnoccupySetpointParam.toInteger()
            UnoccupySetpoint = UnoccupySetpoint * 100
            cmds += zigbee.writeAttribute(0x0201, 0x0014, 0x29, UnoccupySetpoint)
        }
        else{
            cmds += zigbee.writeAttribute(0x0201, 0x0014, 0x29, 1500)
        }
        */
        
        if(AirFloorModeParam == "Ambient"){//Air mode
            traceEvent(settings.logFilter,"Set to Ambient mode",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0105, 0x30, 0x0001)
        }
        else{//Floor mode
            traceEvent(settings.logFilter,"Set to Floor mode",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0105, 0x30, 0x0002)
        }
        
        if(KbdLockParam == "Lock"){
            traceEvent(settings.logFilter,"device lock",settings.trace)
            lock1()
        }
        else{
            traceEvent(settings.logFilter,"device unlock",settings.trace)
            unlock()
        }
        
        if(TimeFormatParam == "12h AM/PM"){//12h AM/PM
            traceEvent(settings.logFilter,"Set to 12h AM/PM",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0114, 0x30, 0x0001)
        }
        else{//24h
            traceEvent(settings.logFilter,"Set to 24h",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0114, 0x30, 0x0000)
        }
        
        if(BacklightAutoDimParam == "On Demand"){	//Backlight when needed
            traceEvent(settings.logFilter,"Backlight on press",settings.trace)
            cmds += zigbee.writeAttribute(0x0201, 0x0402, 0x30, 0x0000)
        }
        else{//Backlight sensing
            traceEvent(settings.logFilter,"Backlight sensing",settings.trace)
            cmds += zigbee.writeAttribute(0x0201, 0x0402, 0x30, 0x0001)
        }
        
        if(FloorSensorTypeParam == "12k"){//sensor type = 12k
            traceEvent(settings.logFilter,"Sensor type is 12k",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x010B, 0x30, 0x0001)
        }
        else{//sensor type = 10k
            traceEvent(settings.logFilter,"Sensor type is 10k",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x010B, 0x30, 0x0000)
        }
        
        if(FloorMaxAirTemperatureParam){
        	def MaxAirTemperatureValue
        	if(state?.scale == 'F')
            {
            	 MaxAirTemperatureValue = fahrenheitToCelsius(FloorMaxAirTemperatureParam).toInteger()
            }
            else//state?.scale == 'C'
            {
            	MaxAirTemperatureValue = FloorMaxAirTemperatureParam.toInteger()
            }
            MaxAirTemperatureValue = checkTemperature(MaxAirTemperatureValue)//check if the temperature is between the maximum and minimum
            MaxAirTemperatureValue =  MaxAirTemperatureValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x0108, 0x29, MaxAirTemperatureValue)
        }
        else{
            cmds += zigbee.writeAttribute(0xFF01, 0x0108, 0x29, 0x8000)
        }
        
        if(RoomSetpointMinParam){
       	 	def RoomSetpointMinValue
            if(state?.scale == 'F')
            {
            	RoomSetpointMinValue = fahrenheitToCelsius(RoomSetpointMinParam).toInteger()
            }
            else//state?.scale == 'C'
            {
            	RoomSetpointMinValue = RoomSetpointMinParam.toInteger()
            }
            RoomSetpointMinValue = checkTemperature(RoomSetpointMinValue)//check if the temperature is between the maximum and minimum
            RoomSetpointMinValue =  RoomSetpointMinValue * 100
            cmds += zigbee.writeAttribute(0x0201, 0x0015, 0x29, RoomSetpointMinValue)
        }
        else{
            cmds += zigbee.writeAttribute(0x0201, 0x0015, 0x29, 0x01F4)
        }
        
        if(RoomSetpointMaxParam){
            def RoomSetpointMaxValue
            if(state?.scale == 'F')
            {
            	RoomSetpointMaxValue = fahrenheitToCelsius(RoomSetpointMaxParam).toInteger()
            }
            else//state?.scale == 'C'
            {
            	RoomSetpointMaxValue = RoomSetpointMaxParam.toInteger()
            }
            RoomSetpointMaxValue = checkTemperature(RoomSetpointMaxValue)//check if the temperature is between the maximum and minimum
            RoomSetpointMaxValue =  RoomSetpointMaxValue * 100
            cmds += zigbee.writeAttribute(0x0201, 0x0016, 0x29, RoomSetpointMaxValue)
        }
        else{
            cmds += zigbee.writeAttribute(0x0201, 0x0016, 0x29, 0x0E10)
        }
        
        if(FloorLimitMinParam){
        	def FloorLimitMinValue
            if(state?.scale == 'F')
            {
            	FloorLimitMinValue = fahrenheitToCelsius(FloorLimitMinParam).toInteger()
            }
        	else//state?.scale == 'C'
            {
            	FloorLimitMinValue = FloorLimitMinParam.toInteger()
            }
            FloorLimitMinValue = checkTemperature(FloorLimitMinValue)//check if the temperature is between the maximum and minimum
            FloorLimitMinValue =  FloorLimitMinValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x0109, 0x29, FloorLimitMinValue)
        }
        else{
            cmds += zigbee.writeAttribute(0xFF01, 0x0109, 0x29, 0x8000)
        }
        
        if(FloorLimitMaxParam){
        	def FloorLimitMaxValue
            if(state?.scale == 'F')
            {
            	FloorLimitMaxValue = fahrenheitToCelsius(FloorLimitMaxParam).toInteger()
            }
            else//state?.scale == 'C'
            {
            	FloorLimitMaxValue = FloorLimitMaxParam.toInteger()
            }
            FloorLimitMaxValue = checkTemperature(FloorLimitMaxValue)//check if the temperature is between the maximum and minimum
            FloorLimitMaxValue =  FloorLimitMaxValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x010A, 0x29, FloorLimitMaxValue)
        }
        else{
            cmds += zigbee.writeAttribute(0xFF01, 0x010A, 0x29, 0x8000)
        }
        
        if(AuxLoadParam){
            def AuxLoadValue = AuxLoadParam.toInteger()
            cmds += zigbee.writeAttribute(0xFF01, 0x0118, 0x21, AuxLoadValue)
        }
        else{
            cmds += zigbee.writeAttribute(0xFF01, 0x0118, 0x21, 0x0000)
        }
        
        cmds += zigbee.readAttribute(0x0201, 0x0015)	// Rd thermostat Minimum heating setpoint
        cmds += zigbee.readAttribute(0x0201, 0x0016)	// Rd thermostat Maximum heating setpoint
        
		sendZigbeeCommands(cmds)
       	refresh_misc()
    }
    
}

void initialize() {
	state?.scale = getTemperatureScale()
    runIn(2,refresh)
    
	def supportedThermostatModes = ['off', 'heat']
	state?.supportedThermostatModes = supportedThermostatModes
	sendEvent(name: "supportedThermostatModes", value: supportedThermostatModes, displayed: (settings.trace ?: false))
	def supportedThermostatFanModes = []
	sendEvent(name: "supportedThermostatFanModes", value: supportedThermostatFanModes, displayed: (settings.trace ?: false))
}

def ping() {
	refresh()
}

def uninstalled() {
	unschedule()
}

//-- Parsing ---------------------------------------------------------------------------------------------

// parse events into attributes
def parse(String description) {
	
    def result = []
	def scale = getTemperatureScale()
	state?.scale = scale
	traceEvent(settings.logFilter, "parse>Description :( $description )", settings.trace)
	def cluster = zigbee.parse(description)
	traceEvent(settings.logFilter, "parse>Cluster : $cluster", settings.trace)
	if (description?.startsWith("read attr -")) {
    	def descMap = zigbee.parseDescriptionAsMap(description)
        result += createCustomMap(descMap)
        if(descMap.additionalAttrs){
       		def mapAdditionnalAttrs = descMap.additionalAttrs
            mapAdditionnalAttrs.each{add ->
            	traceEvent(settings.logFilter,"parse> mapAdditionnalAttributes : ( ${add} )",settings.trace)
                add.cluster = descMap.cluster
                result += createCustomMap(add)
            }
        }
    }
	traceEvent(settings.logFilter, "Parse returned $result", settings.trace)
	return result
}

//--------------------------------------------------------------------------------------------------------

def createCustomMap(descMap){
	def result = null
    def map = [: ]
		if (descMap.cluster == "0201" && descMap.attrId == "0000") {
			map.name = "temperature"
			map.value = getTemperatureValue(descMap.value, true)
            if(map.value > 158)
            {
            	map.value = "Sensor Error"
            }
            else
            {
            	if(state?.scale == "C")
                {
                    //map.value = Double.toString(map.value)
                    map.value = String.format( "%.1f", map.value )
                }
                else//scale == "F"
                {
                	map.value = String.format( "%d", map.value )
                }
            }
			traceEvent(settings.logFilter, "parse>ACTUAL TEMP:  ${map.value}", settings.trace)
		} else if (descMap.cluster == "0201" && descMap.attrId == "0008") {
			map.name = "heatingDemand"
			map.value = getHeatingDemand(descMap.value)
			def operatingState = (map.value.toInteger() < 10) ? "idle" : "heating"
			def isChange = isStateChange(device, "thermostatOperatingState", operatingState)
			def isDisplayed = isChange
			sendEvent(name: "thermostatOperatingState", value: operatingState, displayed: isDisplayed)
			traceEvent(settings.logFilter, "parse>HEATING DEMAND: ${map.value}, operatingState=$operatingState", settings.trace)

		} else if (descMap.cluster == "0201" && descMap.attrId == "0012") {
			def currentStatus = device.currentState("occupancyStatus")?.value
			if (currentStatus == "occupy") {
				map.name = "heatingSetpoint"
				map.value = getTemperatureValue(descMap.value, true)
				def isChange = isStateChange(device, "thermostatSetpoint", map.value.toString())
				def isDisplayed = isChange
				sendEvent(name: "thermostatSetpoint", value: map.value, unit: scale, displayed: isDisplayed)
				traceEvent(settings.logFilter, "parse>OCCUPY HEATING SETPOINT:${map.value} ", settings.trace)
			}
		} else if (descMap.cluster == "0201" && descMap.attrId == "0014") {
			def currentStatus = device.currentState("occupancyStatus")?.value
			if (currentStatus == "unoccupy") {
				map.name = "heatingSetpoint"
				map.value = getTemperatureValue(descMap.value, true)
				def isChange = isStateChange(device, "thermostatSetpoint", map.value.toString())
				def isDisplayed = isChange
				sendEvent(name: "thermostatSetpoint", value: map.value, unit: scale, displayed: isDisplayed)
				traceEvent(settings.logFilter, "parse>UNOCCUPY HEATING SETPOINT: ${map.value}", settings.trace)
			}
		} else if (descMap.cluster == "0201" && descMap.attrId == "0015") {
			map.name = "heatingSetpointRangeLow"
			map.value = getTemperatureValue(descMap.value, true)
			traceEvent(settings.logFilter, "parse>LOW HeatingSetpoint: ${map.value}", settings.trace)
		} else if (descMap.cluster == "0201" && descMap.attrId == "0016") {
			map.name = "heatingSetpointRangeHigh"
			map.value = getTemperatureValue(descMap.value, true)
			traceEvent(settings.logFilter, "parse>HIGH HeatingSetpoint: ${map.value}", settings.trace)
		} else if (descMap.cluster == "0201" && descMap.attrId == "001c") {
			map.name = "thermostatMode"
			map.value = getModeMap()[descMap.value]
			traceEvent(settings.logFilter, "MODE: ${map.value}", settings.trace)
		} else if (descMap.cluster == "0201" && descMap.attrId == "0002") {
			map.name = "occupancyStatus"
			map.value = getOccupancyMap()[descMap.value]
			traceEvent(settings.logFilter, "parse>OCCUPANCY: ${map.value}", settings.trace)
			if (map.value == "occupy") {
				def isChange = isStateChange(device, "presence", "present")
				def isDisplayed = isChange
				sendEvent(name: "presence", value: "present", displayed: isDisplayed)
			} else {
				def isChange = isStateChange(device, "presence", "non present")
				def isDisplayed = isChange
				sendEvent(name: "presence", value: "non present", displayed: isDisplayed)
			}
		} else if (descMap.cluster == "0201" && descMap.attrId == "0400") {
			map.name = "occupancyStatus"
			map.value = getOccupancyMap()[descMap.value]
			traceEvent(settings.logFilter, "parse>MF-OCCUPANCY", settings.trace)
			if (map.value == "occupy") {
				def isChange = isStateChange(device, "presence", "present")
				def isDisplayed = isChange
				sendEvent(name: "presence", value: "present", displayed: isDisplayed)
			} else {
				def isChange = isStateChange(device, "presence", "non present")
				def isDisplayed = isChange
				sendEvent(name: "presence", value: "non present", displayed: isDisplayed)
			}
		} else if (descMap.cluster == "0204" && descMap.attrId == "0000") {
			map.name = "temperatureDisplayMode"
			map.value = getTemperatureDisplayModeMap()[descMap.value]
			traceEvent(settings.logFilter, "parse>DISPLAY MODE: ${map.value}", settings.trace)
		} else if (descMap.cluster == "0204" && descMap.attrId == "0001") {
			map.name = "keypadLockStatus"
			map.value = getLockMap()[descMap.value]
			traceEvent(settings.logFilter, "parse>KEYPAD LOCK STATUS: ${map.value}", settings.trace)
		} else if (descMap.cluster == "FF01" && descMap.attrId == "010c") {
			map.name = "floorLimitStatus"
			if(descMap.value.toInteger() == 0){
            	map.value = "OK"
            }else if(descMap.value.toInteger() == 1){
            	map.value = "floorLimitLowReached"
            }else if(descMap.value.toInteger() == 2){
            	map.value = "floorLimitMaxReached"
            }else if(descMap.value.toInteger() == 3){
            	map.value = "floorAirLimitMaxReached"
            }else{
            	map.value = "floorAirLimitMaxReached"
            }
			traceEvent(settings.logFilter, "parse>floorLimitStatus: ${map.value}", settings.trace)
		} else if (descMap.cluster == "FF01" && descMap.attrId == "0115") {
			map.name = "gfciStatus"
			if(descMap.value.toInteger() == 0){
            	map.value = "OK"
            }else if(descMap.value.toInteger() == 1){
            	map.value = "error"
            }
			traceEvent(settings.logFilter, "parse>gfciStatus: ${map.value}", settings.trace)
		}

	if (map) {
		def isChange = isStateChange(device, map.name, map.value.toString())
		map.displayed = isChange
		if ((map.name.toLowerCase().contains("temp")) || (map.name.toLowerCase().contains("setpoint"))) {
			map.scale = scale
		}
		result = createEvent(map)
	}
    return result
}

//-- Temperature -----------------------------------------------------------------------------------------

def getTemperatureValue(value, doRounding = false) {
	def scale = state?.scale
	if (value != null) {
		double celsius = (Integer.parseInt(value, 16) / 100).toDouble()
		if (scale == "C") {
			if (doRounding) {
				def tempValueString = String.format('%2.1f', celsius)
				//if (tempValueString.matches(".*([.,][456])")) {
                if (tempValueString.matches(".*([.,][25-74])")) {
					tempValueString = String.format('%2d.5', celsius.intValue())
					traceEvent(settings.logFilter, "getTemperatureValue>value of $tempValueString which ends with 456=> rounded to .5", settings.trace)
				//} else if (tempValueString.matches(".*([.,][789])")) {
                } else if (tempValueString.matches(".*([.,][75-99])")) {
					traceEvent(settings.logFilter, "getTemperatureValue>value of$tempValueString which ends with 789=> rounded to next .0", settings.trace)
					celsius = celsius.intValue() + 1
					tempValueString = String.format('%2d.0', celsius.intValue())
				} else {
					traceEvent(settings.logFilter, "getTemperatureValue>value of $tempValueString which ends with 0123=> rounded to previous .0", settings.trace)
					tempValueString = String.format('%2d.0', celsius.intValue())
				}
				return tempValueString.toDouble().round(1)
			} 
            else {
				return celsius.round(1)
			}

		} else {
			return Math.round(celsiusToFahrenheit(celsius))
		}
	}
}

//-- Heating Demand --------------------------------------------------------------------------------------

def getHeatingDemand(value) {
	if (value != null) {
		def demand = Integer.parseInt(value, 16)
		return demand.toString()
	}
}

//-- Heating Setpoint ------------------------------------------------------------------------------------

def heatLevelUp() {
	def scale = state?.scale
	def heatingSetpointRangeHigh
	double nextLevel

	try {
		heatingSetpointRangeHigh = device.latestValue("heatingSetpointRangeHigh")
	} catch (any) {
		traceEvent(settings.logFilter, "heatLevelUp>not able to get heatingSetpointRangeLow ($heatingSetpointRangeLow),using default value",
			settings.trace, get_LOG_WARN())
	}
	heatingSetpointRangeHigh = (heatingSetpointRangeHigh) ?: (scale == 'C') ? 10.0 : 50

	if (scale == 'C') {
		nextLevel = device.currentValue("heatingSetpoint").toDouble()
		nextLevel = (nextLevel + 0.5).round(1)
		if (nextLevel > heatingSetpointRangeHigh.toDouble().round(1)) {
			nextLevel = heatingSetpointRangeHigh.toDouble().round(1)
		}
		setHeatingSetpoint(nextLevel)
	} else {
		nextLevel = device.currentValue("heatingSetpoint")
		nextLevel = (nextLevel + 1)
		if (nextLevel > heatingSetpointRangeHigh.toDouble()) {
			nextLevel = heatingSetpointRangeHigh.toDouble()
		}
		setHeatingSetpoint(nextLevel.intValue())
	}

}

def heatLevelDown() {
	def scale = state?.scale
	def heatingSetpointRangeLow
	double nextLevel

	try {
		heatingSetpointRangeLow = device.latestValue("heatingSetpointRangeLow")
	} catch (any) {
		traceEvent(settings.logFilter, "heatLevelDown>not able to get heatingSetpointRangeLow ($heatingSetpointRangeLow),using default value",
			settings.trace, get_LOG_WARN())
	}
	heatingSetpointRangeLow = (heatingSetpointRangeLow) ?: (scale == 'C') ? 10.0 : 50
	if (scale == 'C') {
		nextLevel = device.currentValue("heatingSetpoint").toDouble()
		nextLevel = (nextLevel - 0.5).round(1)
		if (nextLevel < heatingSetpointRangeLow.toDouble().round(1)) {
			nextLevel = heatingSetpointRangeLow.toDouble().round(1)
		}
		setHeatingSetpoint(nextLevel)
	} else {
		nextLevel = device.currentValue("heatingSetpoint")
		nextLevel = (nextLevel - 1)
		if (nextLevel < heatingSetpointRangeLow.toDouble()) {
			nextLevel = heatingSetpointRangeLow.toDouble()
		}
		setHeatingSetpoint(nextLevel.intValue())
	}


}
void setThermostatSetpoint(temp) {
	setHeatingSetpoint(temp)
}

def setHeatingSetpoint(degrees) {
	def scale = state?.scale
	def degreesDouble = degrees as Double
	String tempValueString
	if (scale == "C") {
		tempValueString = String.format('%2.1f', degreesDouble)
	} else {
		tempValueString = String.format('%2d', degreesDouble.intValue())
	}
	sendEvent("name": "heatingSetpoint", "value": tempValueString, displayed: true)
	sendEvent("name": "thermostatSetpoint", "value": tempValueString, displayed: true)
	traceEvent(settings.logFilter, "setHeatingSetpoint> new setPoint: $tempValueString", settings.trace)
	def celsius = (scale == "C") ? degreesDouble : (fahrenheitToCelsius(degreesDouble) as Double).round(1)
	def cmds = []
	cmds += zigbee.writeAttribute(0x201, 0x12, 0x29, hex(celsius * 100))
	sendZigbeeCommands(cmds)
}


def setCoolingSetpoint(temp) {
	traceEvent(settings.logFilter, "coolingSetpoint is not supported by this thermostat", settings.trace, get_LOG_WARN())
}

void setHeatingSetpointRange(rangeArray = []) {

	if ((!rangeArray) || (rangeArray.size() != 2)) {
		traceEvent(settings.logFilter, "setHeatingSetpointRange>cannot change the thermostat Range, value ($rangeArray) is null or invalid", settings.trace, get_LOG_WARN(), true)
		return
	}
	def temp_min = rangeArray[0]
	def temp_max = rangeArray[1]
	setHeatingSetpointRangeLow(temp_min)
	setHeatingSetpointRangeHigh(temp_max)
}


def setHeatingSetpointRangeLow(degrees) {
	def scale = state?.scale
	def degreesDouble = degrees as Double
	traceEvent(settings.logFilter, "setHeatingSetpointRangeLow> new Low: $degreesDouble", settings.trace)
	def celsius = (scale == "C") ? degreesDouble : (fahrenheitToCelsius(degreesDouble) as Double).round(1)
	def cmds = []

	cmds += zigbee.writeAttribute(0x201, 0x15, 0x29, hex(celsius * 100))
	cmds += zigbee.readAttribute(0x0201, 0x0012)
	cmds += zigbee.readAttribute(0x0201, 0x0015)
	sendZigbeeCommands(cmds)

	float temp = degrees.toFloat().round(1)
	sendEvent(name: 'heatingSetpointRangeLow', value: temp, isStateChange: true, unit: scale)
	def setpointRangeHigh = device.currentValue('heatingSetpointRangeHigh')
	def heatingSetpointRange = [temp, setpointRangeHigh]
	sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, isStateChange: true, displayed: (settings.trace ?: false))
}

def setHeatingSetpointRangeHigh(degrees) {
	def scale = state?.scale
	def degreesDouble = degrees as Double
	traceEvent(settings.logFilter, "setHeatingSetpointRangeHigh> new High: $degreesDouble", settings.trace)
	def celsius = (scale == "C") ? degreesDouble : (fahrenheitToCelsius(degreesDouble) as Double).round(1)
	def cmds = []

	cmds += zigbee.writeAttribute(0x201, 0x16, 0x29, hex(celsius * 100))
	cmds += zigbee.readAttribute(0x0201, 0x0012)
	cmds += zigbee.readAttribute(0x0201, 0x0016)

	sendZigbeeCommands(cmds)

	float temp = degrees.toFloat().round(1)
	sendEvent(name: 'heatingSetpointRangeHigh', value: temp, isStateChange: true, unit: scale)
	def setpointRangeLow = device.currentValue('heatingSetpointRangeLow')
	def heatingSetpointRange = [setpointRangeLow, temp]
	sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, isStateChange: true, displayed: (settings.trace ?: false))
}


//-- Thermostat and Fan Modes -------------------------------------------------------------------------------------
void off() {
	setThermostatMode('off')
}
void auto() {
	setThermostatMode('auto')
}
void heat() {
	setThermostatMode('heat')
}
void emergencyHeat() {
	setThermostatMode('heat')
}

void cool() {
	setThermostatMode('cool')
}
void fanOn() {
	setThermostatFanMode('on')
}
void fanAuto() {
	setThermostatFanMode('auto')
}
void fanOff() {
	setThermostatFanMode('off')
}
def fanCirculate() {
	setThermostatFanMode('circulate')
}

def modes() {
	["mode_off", "mode_heat"]
}

def getModeMap() {
	[
		"00": "off",
		"04": "heat"
	]
}

def getSupportedThermostatModes() {

	if (!state?.supportedThermostatModes) {
		state?.supportedThermostatModes = (device.currentValue("supportedThermostatModes")) ?
			device.currentValue("supportedThermostatModes").toString().minus('[').minus(']').tokenize(',') : ['off', 'heat']
	}

	return state?.supportedThermostatModes
}


def setThermostatFanMode(mode) {

	traceEvent(settings.logFilter, "setThermostatFanMode to $mode is not supported by this thermostat", settings.trace, get_LOG_WARN())
}
def switchMode() {
	traceEvent(settings.logFilter, "swithMode>begin", settings.trace)
	def currentMode = "mode_" + device.currentState("thermostatMode")?.value
	def modeOrder = modes()
	def index = modeOrder.indexOf(currentMode)
	def next = index >= 0 && index < modeOrder.size() - 1 ? modeOrder[index + 1] : modeOrder[0]
	traceEvent(settings.logFilter, "swithMode>switching thermostatMode to $next", settings.trace)
	"$next" ()
}


def setThermostatMode(mode) {
	traceEvent(settings.logFilter, "setThermostatMode>switching thermostatMode", settings.trace)
	mode = mode?.toLowerCase()
	def supportedThermostatModes = getSupportedThermostatModes()

	if (mode in supportedThermostatModes.toString()) {
		"mode_$mode" ()
	} else {
		traceEvent(settings.logFilter, "setThermostatMode to $mode is not supported by this thermostat", settings.trace, get_LOG_WARN())
	}
}

def mode_off() {
	traceEvent(settings.logFilter, "off>begin", settings.trace)
	sendEvent("name": "thermostatMode", "value": "off")
	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x001C, 0x30, 0)
	cmds += zigbee.readAttribute(0x0201, 0x0008)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "off>end", settings.trace)
}

def mode_heat() {
	traceEvent(settings.logFilter, "heat>begin", settings.trace)
	sendEvent("name": "thermostatMode", "value": "heat")
	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x001C, 0x30, 4)
	cmds += zigbee.readAttribute(0x0201, 0x0008)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "heat>end", settings.trace)
}

//-- Temperature Display Mode ----------------------------------------------------------------------------

def temperatureDisplayModes() {
	["Deg_C", "Deg_F"]
}

def getTemperatureDisplayModeMap() {
	[
		"00": "Deg_C",
		"01": "Deg_F"
	]
}

def setTemperatureDisplayMode() {
	traceEvent(settings.logFilter, "setTemperatureDisplayMode>begin", settings.trace)
	def currentMode = device.currentState("temperatureDisplayMode")?.value
	def modeOrder = temperatureDisplayModes()
	def index = modeOrder.indexOf(currentMode)
	def next = index == 0 ? modeOrder[1] : modeOrder[0]
	traceEvent(settings.logFilter, "setTemperatureDisplayMode>switching to $next", settings.trace)
	"$next" ()
}

def Deg_C() {
	traceEvent(settings.logFilter, "Deg_C>begin", settings.trace)
	def currentStatus = device.currentState("occupancyStatus")?.value
	sendEvent("name": "temperatureDisplayMode", "value": "Deg_C")
	def cmds = []
	if (currentStatus == "occupy") {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 0)
		cmds += zigbee.readAttribute(0x0201, 0x0000)
		cmds += zigbee.readAttribute(0x0201, 0x0012)
	} else {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 0)
		cmds += zigbee.readAttribute(0x0201, 0x0000)
		cmds += zigbee.readAttribute(0x0201, 0x0014)
	}
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "Deg_C>end", settings.trace)
}

def Deg_F() {
	traceEvent(settings.logFilter, "Deg_F>begin", settings.trace)
	def currentStatus = device.currentState("occupancyStatus")?.value
	sendEvent("name": "temperatureDisplayMode", "value": "Deg_F")
	def cmds = []
	if (currentStatus == "occupy") {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 1)
		cmds += zigbee.readAttribute(0x0201, 0x0000)
		cmds += zigbee.readAttribute(0x0201, 0x0012)
	} else {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 1)
		cmds += zigbee.readAttribute(0x0201, 0x0000)
		cmds += zigbee.readAttribute(0x0201, 0x0014)
	}
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "Deg_F>end", settings.trace)
}

//-- Occupancy -------------------------------------------------------------------------------------------

def occupancySts() {
	["unoccupy", "occupy"]
}

def getOccupancyMap() {
	[
		"00": "unoccupy",
		"01": "occupy"
	]
}

def setOccupancyStatus() {
	traceEvent(settings.logFilter, "setOccupancyStatus>switching occupancy status", settings.trace)
	def currentStatus = device.currentState("occupancyStatus")?.value
	traceEvent(settings.logFilter, "setOccupancyStatus>Occupancy :$currentStatus", settings.trace)
	def statusOrder = occupancySts()
	def index = statusOrder.indexOf(currentStatus)
	traceEvent(settings.logFilter, "setOccupancyStatus>Index = $index", settings.trace)
	def next = (index >= 0 && index < (statusOrder.size() - 1)) ? statusOrder[index + 1] : statusOrder[0]
	traceEvent(settings.logFilter, "setOccupancyStatus>switching occupancy from $currentStatus to $next", settings.trace)
	"$next" ()
}
void away() {
	unoccupy()
}

void unoccupy() {
	traceEvent(settings.logFilter, "unoccupy>Set unoccupy", settings.trace)
	sendEvent(name: "occupancyStatus", value: "unoccupy", displayed: true)
	sendEvent(name: "presence", value: "non present", displayed: true)
	state?.previousOccupyTemp = device.currentValue("heatingSetpoint")
	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x0400, 0x18, 0x00, [mfgCode: 0x119C]) //data type was 0x30
	cmds += zigbee.readAttribute(0x0201, 0x0002)
	cmds += zigbee.readAttribute(0x0201, 0x0014)
	sendZigbeeCommands(cmds)
}

void present() {
	occupy()
}
void occupy() {
	def scale = state?.scale
	traceEvent(settings.logFilter, "occupy>Set occupy", settings.trace)
	sendEvent(name: "occupancyStatus", value: "occupy", displayed: true)
	sendEvent(name: "presence", value: "present", displayed: true)
	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x0400, 0x18, 0x01, [mfgCode: 0x119C])
	cmds += zigbee.readAttribute(0x0201, 0x0002)
	cmds += zigbee.readAttribute(0x0201, 0x0012)
	sendZigbeeCommands(cmds)
	/*def temp = (state?.previousOccupyTemp) ? state ?.previousOccupyTemp : (scale == 'C') ? 20.0 : 70
	sendEvent(name: "thermostatSetpoint", value: temp, unit: scale, displayed: true)
	sendEvent(name: "heatingSetpoint", value: temp, unit: scale, displayed: true)*/
}

//-- Keypad Lock -----------------------------------------------------------------------------------------

def keypadLockLevel() {
	["unlock", "lock1"] //only those level are used for the moment
	//["Unlock","Lock1","Lock2","Lock3","Lock4","Lock5"]
}

def getLockMap() {
	[
		"00": "unlock",
		"01": "lock1",
		"02": "lock2",
		"03": "lock3",
		"04": "lock4",
		"05": "lock5"
	]
}

def setLockStatus() {
	traceEvent(settings.logFilter, "setLockStatus> begin", settings.trace)
	def currentLockLevel = device.currentValue("keypadLockStatus")
	traceEvent(settings.logFilter, "setLockStatus>KeypadLock : $currentLockLevel", settings.trace)
	def lockOrder = keypadLockLevel()
	def index = lockOrder.indexOf(currentLockLevel)
	traceEvent(settings.logFilter, "index = $index", settings.trace)
	def next = (index >= 0 && index < (lockOrder.size() - 1)) ? lockOrder[index + 1] : lockOrder[0]
	traceEvent(settings.logFilter, "change keypad lock level from $currentLockLevel to $next", settings.trace)
	"$next" ()
	traceEvent(settings.logFilter, "setLockStatus> end", settings.trace)
}

def unlock() {
	traceEvent(settings.logFilter, "unlock>begin", settings.trace)
	sendEvent("name": "keypadLockStatus", "value": "Unlock", displayed: true)
	def cmds = []
	cmds += zigbee.writeAttribute(0x0204, 0x0001, 0x30, 0x00)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "unlock>end", settings.trace)
}

def lock1() {
	traceEvent(settings.logFilter, "lock1>begin", settings.trace)
	sendEvent("name": "keypadLockStatus", "value": "lock1", displayed: true)
	def cmds = []
	cmds += zigbee.writeAttribute(0x0204, 0x0001, 0x30, 0x01)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "lock1>end", settings.trace)
}



def configure(event = 'appTouch') {
	//state?.scale = getTemperatureScale()
}


def refresh() {
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 20000) {			// Check if last update > 20 sec
		state.updatedLastRanAt = now() 
                
        occupy()
        
        state?.scale = getTemperatureScale()
        traceEvent(settings.logFilter, "refresh>scale=${state.scale}", settings.trace)
        def cmds = []

        cmds += zigbee.readAttribute(0x0204, 0x0000)	// Rd thermostat display mode
        if (state?.scale == 'C') {
            cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 0)	// Wr °C on thermostat display

        } else {
            cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 1)	// Wr °F on thermostat display 
        }
        cmds += zigbee.readAttribute(0x0201, 0x0000)	// Rd thermostat Local temperature
        cmds += zigbee.readAttribute(0x0201, 0x0012)	// Rd thermostat Occupied heating setpoint
        cmds += zigbee.readAttribute(0x0201, 0x0008)	// Rd thermostat PI heating demand
        cmds += zigbee.readAttribute(0x0201, 0x001C)	// Rd thermostat System Mode
        //cmds += zigbee.readAttribute(0x0201, 0x0002)	// Rd thermostat Occupency
        cmds += zigbee.readAttribute(0x0204, 0x0001) 	// Rd thermostat Keypad lockout
        //cmds += zigbee.readAttribute(0x0201, 0x0014)	// Rd thermostat Unoccupied heating setpoint
        cmds += zigbee.readAttribute(0x0201, 0x0015)	// Rd thermostat Minimum heating setpoint
        cmds += zigbee.readAttribute(0x0201, 0x0016)	// Rd thermostat Maximum heating setpoint
        cmds += zigbee.readAttribute(0xFF01, 0x0105)	// Rd thermostat Control mode

        cmds += zigbee.configureReporting(0x0201, 0x0000, 0x29, 19, 300, 25) 	// local temperature
        cmds += zigbee.configureReporting(0x0201, 0x0008, 0x0020, 11, 301, 10) 	// heating demand
        cmds += zigbee.configureReporting(0x0201, 0x0012, 0x0029, 8, 302, 40) 	// occupied heating setpoint
        cmds += zigbee.configureReporting(0xFF01, 0x0115, 0x30, 10, 3600, 1) 	// report gfci status each hours
        cmds += zigbee.configureReporting(0xFF01, 0x010C, 0x30, 10, 3600, 1) 	// floor limit status each hours

        sendZigbeeCommands(cmds)
        refresh_misc()
    }
}

void refresh_misc() {

    def weather = get_weather()
	traceEvent(settings.logFilter,"refresh_misc>begin, settings.EnableOutdorTemperatureParam=${settings.EnableOutdorTemperatureParam}, weather=$weather", settings.trace)
	def cmds=[]

	if (weather) {
		double tempValue
        int outdoorTemp = weather.toInteger()
        if(state?.scale == 'F')
        {//the value sent to the thermostat must be in C
        //the thermostat make the conversion to F
        	outdoorTemp = fahrenheitToCelsius(outdoorTemp).toDouble().round()
        }
		String outdoorTempString
		def isChange = isStateChange(device, name, outdoorTempString)
		def isDisplayed = isChange
		sendEvent( name: "outdoorTemp", value: outdoorTempString, unit: state?.scale, displayed: isDisplayed)
		int outdoorTempValue
		int outdoorTempToSend  
		
        if(settings.EnableOutdorTemperatureParam)
        {
        	cmds += zigbee.writeAttribute(0xFF01, 0x0011, 0x21, 10800)//set the outdoor temperature timeout to 3 hours
            if (outdoorTemp < 0) {
                outdoorTempValue = -outdoorTemp*100 - 65536
                outdoorTempValue = -outdoorTempValue
                outdoorTempToSend = zigbee.convertHexToInt(swapEndianHex(hex(outdoorTempValue)))
                cmds += zigbee.writeAttribute(0xFF01, 0x0010, 0x29, outdoorTempToSend, [mfgCode: 0x119C])
            } else {
                outdoorTempValue = outdoorTemp*100
                int tempa = outdoorTempValue.intdiv(256)
                int tempb = (outdoorTempValue % 256) * 256
                outdoorTempToSend = tempa + tempb
                cmds += zigbee.writeAttribute(0xFF01, 0x0010, 0x29, outdoorTempToSend, [mfgCode: 0x119C])
            }
        }
        else
        {//delete outdoorTemp
        	//the outdoor temperature cannot be directly erased from the thermostat.
            //to erase it rapidly, the external temperature timeout must be set to the minimal value (30sec)
        	cmds += zigbee.writeAttribute(0xFF01, 0x0011, 0x21, 30)//set the outdoor temperature timeout to 30sec
        }
		
        def mytimezone = location.getTimeZone()
        long dstSavings = 0
        if(mytimezone.useDaylightTime() && mytimezone.inDaylightTime(new Date())) {
          dstSavings = mytimezone.getDSTSavings()
		}
        //To refresh the time
		long secFrom2000 = (((now().toBigInteger() + mytimezone.rawOffset + dstSavings ) / 1000) - (10957 * 24 * 3600)).toLong() //number of second from 2000-01-01 00:00:00h
		long secIndian = zigbee.convertHexToInt(swapEndianHex(hex(secFrom2000).toString())) //switch endianess
		traceEvent(settings.logFilter, "refreshTime>myTime = ${secFrom2000}  reversed = ${secIndian}", settings.trace)
		cmds += zigbee.writeAttribute(0xFF01, 0x0020, 0x23, secIndian, [mfgCode: 0x119C])
        cmds += zigbee.readAttribute(0x0201, 0x001C)

		sendZigbeeCommands(cmds)
	}
	//refreshTime()    
	traceEvent(settings.logFilter,"refresh_misc> about to  refresh other misc variables, scale=${state.scale}", settings.trace)	
	def heatingSetpointRangeHigh= (device.currentValue("heatingSetpointRangeHigh")) ?: (scale=='C')?30:99
	def heatingSetpointRangeLow= (device.currentValue("heatingSetpointRangeLow")) ?: (scale=='C')?10:50

	def low = heatingSetpointRangeLow.toFloat().round(1)
	def high = heatingSetpointRangeHigh.toFloat().round(1)
	def heatingSetpointRange= [low,high]
	def isChanged= isStateChange(device, "heatingSetpointRange", heatingSetpointRange?.toString())    
	sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, isStateChange: isChanged, displayed: (settings.trace?:false))
	traceEvent(settings.logFilter,"refresh_misc>end", settings.trace)
 
}
 

//-- Private functions -----------------------------------------------------------------------------------
void sendZigbeeCommands(cmds, delay = 1000) {
	cmds.removeAll { it.startsWith("delay") }
	// convert each command into a HubAction
	cmds = cmds.collect { new physicalgraph.device.HubAction(it) }
	sendHubCommand(cmds, delay)
}


private def get_weather() {
	def mymap = getTwcConditions()
    traceEvent(settings.logFilter,"get_weather> $mymap",settings.trace)	
    def weather = mymap.temperature
    traceEvent(settings.logFilter,"get_weather> $weather",settings.trace)	
	return weather
}


private hex(value) {

	String hex=new BigInteger(Math.round(value).toString()).toString(16)
	traceEvent(settings.logFilter,"hex>value=$value, hex=$hex",settings.trace)	
	return hex
}

def refreshTime() {
	//IS NOT USED
	def mytimezone = location.getTimeZone()
	long secFrom2000 = (((now().toBigInteger() + mytimezone.rawOffset + mytimezone.dstSavings ) / 1000) - (10957 * 24 * 3600)).toLong() //number of second from 2000-01-01 00:00:00h
	long secIndian = zigbee.convertHexToInt(swapEndianHex(hex(secFrom2000).toString())) //switch endianess
	traceEvent(settings.logFilter, "refreshTime>myTime = ${secFrom2000}  reversed = ${secIndian}", settings.trace)
	def cmds = []
	cmds += zigbee.writeAttribute(0xFF01, 0x0020, 0x23, secIndian, [mfgCode: 0x119C])
	sendZigbeeCommands(cmds)
    //IS NOT USED
}

private String swapEndianHex(String hex) {
	reverseArray(hex.decodeHex()).encodeHex()
}

private byte[] reverseArray(byte[] array) {
	int i = 0;
	int j = array.length - 1;
	byte tmp;

	while (j > i) {
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}

	return array
}

private def checkTemperature(def number)
{
	def rtnNumber = number
	/*if(state?.scale == 'F')
    {
    	if(number < 41)
        {
        	rtnNumber = 41
        }
        else if(number > 97)
    	{
        	rtnNumber = 97
        }
    }
    else//state?.scale == 'C'
    {*/
    	if(number < 5)
        {
        	rtnNumber = 5
        }
        else if(number > 36)
    	{
        	rtnNumber = 36
        }
    //}
    return rtnNumber
}

private int get_LOG_ERROR() {
	return 1
}
private int get_LOG_WARN() {
	return 2
}
private int get_LOG_INFO() {
	return 3
}
private int get_LOG_DEBUG() {
	return 4
}
private int get_LOG_TRACE() {
	return 5
}

def traceEvent(logFilter, message, displayEvent = false, traceLevel = 4, sendMessage = true) {
	int LOG_ERROR = get_LOG_ERROR()
	int LOG_WARN = get_LOG_WARN()
	int LOG_INFO = get_LOG_INFO()
	int LOG_DEBUG = get_LOG_DEBUG()
	int LOG_TRACE = get_LOG_TRACE()
	int filterLevel = (logFilter) ? logFilter.toInteger() : get_LOG_WARN()
    
	if ((displayEvent) || (sendMessage)) {
		def results = [
			name: "verboseTrace",
			value: message,
			displayed: ((displayEvent) ?: false)
		]

		if ((displayEvent) && (filterLevel >= traceLevel)) {
			switch (traceLevel) {
				case LOG_ERROR:
					log.error "${message}"
					break
				case LOG_WARN:
					log.warn "${message}"
					break
				case LOG_INFO:
					log.info "${message}"
					break
				case LOG_TRACE:
					log.trace "${message}"
					break
				case LOG_DEBUG:
				default:
					log.debug "${message}"
					break
			} /* end switch*/
			if (sendMessage) sendEvent(results)
		} /* end if displayEvent*/
	}
}