/**
 *  Copyright 2020 Schneider Electric
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
 *  SE8350 ZigBee Thermostat
 *
 *  Author: Ettore Colicchio
 *
 *  Date: 2020-05-19
 *  Rev 0.0.27
 */

 

preferences {


}



metadata {

		definition (name: "8350 Room Controller", namespace: "SE8350", author: "Schneider Electric", ocfDeviceType: "oic.d.thermostat") {

    	capability "Actuator"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        //capability "Thermostat"
        capability "Thermostat Mode"
        capability "Thermostat Heating Setpoint"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Operating State"
		capability "Configuration"
        capability "Polling"
		capability "Refresh"
        capability "Fan Speed"

        attribute "outsideTemp", "number"
        attribute "piCoolingDemand", "number" 
        attribute "piHeatingDemand", "number" 
        attribute "standbyHeatingSetpoint", "number"
        attribute "standbyCoolingSetpoint", "number"
        attribute "unoccHeatingSetpoint", "number"
        attribute "unoccCoolingSetpoint", "number"        
        attribute "occupancyCommand", "enum", ["Local Occ, Occupied, Unoccupied"]
        attribute "effectiveOccupancy", "enum", ["Occupied, Unoccupied, Override, Standby"]
        attribute "coolSP", "enum", ["UpdateCoolingSetpoint"]
        attribute "heatSP", "enum", ["UpdateHeatingSetpoint"]
        attribute "unoccCoolSP", "enum", ["UpdateUnoccCoolingSetpoint"]
        attribute "unoccHeatSP", "enum", ["UpdateUnoccHeatingSetpoint"]        
        attribute "GFan", "number"

		command "switchMode"
        command "increaseHeatSetpoint"
        command "decreaseHeatSetpoint"
        command "increaseCoolSetpoint"
        command "decreaseCoolSetpoint"      
        command "increaseUnoccCoolSetpoint"
        command "decreaseUnoccCoolSetpoint"      
        command "increaseUnoccHeatSetpoint"
        command "decreaseUnoccHeatSetpoint"          
        command "parameterSetting"
        command "switchOccupancy"
        command "increaseEffectiveSetpoint"
        command "decreaseEffectiveSetpoint"
        command "setOccupied"
        command "setUnoccupied"

		fingerprint profileId: "0104", inClusters: "0000,0003,0201,0202,0204,0405", outClusters: "0402,0405", manufacturer: "Viconics", model: "254-143", deviceJoinName: "VT8350" //Viconics VT8350
		fingerprint profileId: "0104", inClusters: "0000,0003,0201,0202,0204,0405", outClusters: "0402,0405", manufacturer: "Schneider Electric", model: "254-145", deviceJoinName: "SE8350" //Schneider Electric SE8350	
	}

    

	// simulator metadata

	simulator { }



	tiles(scale : 2) {

		multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temp", label:'${currentValue}' ,backgroundColor:"#44b621")
                attributeState("high", label:'HIGH', backgroundColor:"#44b621")
            	attributeState("low", label:'LOW', backgroundColor:"#44b621")
            	attributeState("--", label:'--', backgroundColor:"#44b621")
			}
            tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "increaseEffectiveSetpoint")
                attributeState("VALUE_DOWN", action: "decreaseEffectiveSetpoint")
			}            
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#ffa81e")
                attributeState("cooling", backgroundColor:"#00A0DC")
			}
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
            	attributeState("heatingSetpoint", label:'${currentValue}')
            }
    		tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
    		}               
		}
        standardTile("mode", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "off", label:'', action:"switchMode",  nextState:"auto", icon:"st.thermostat.heating-cooling-off"
            state "auto", label:'', action:"switchMode", nextState:"cool", icon:"st.thermostat.auto"     
            state "cool", label:'', action:"switchMode", nextState:"heat", icon:"st.thermostat.cool"
			state "heat", label:'', action:"switchMode", nextState:"off",  icon:"st.thermostat.heat"
        }
		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "heat", label:'${currentValue}° Heat', unit:"C", backgroundColor:"#ffffff"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "cool", label:'${currentValue}° Cool', unit:"C", backgroundColor:"#ffffff"
        }    
		valueTile("unoccHeatingSetpoint", "device.unoccHeatingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "heat", label:'${currentValue}° Unocc Heat', unit:"C", backgroundColor:"#ffffff"
		}
		valueTile("unoccCoolingSetpoint", "device.unoccCoolingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "cool", label:'${currentValue}° Unocc Cool', unit:"C", backgroundColor:"#ffffff"            
		}        
        standardTile("occCommand", "device.occupancyCommand", decoration: "flat", width: 2, height: 2) {
            state "Local Occ", label:'${name}', action:"switchOccupancy", nextState:"Occupied"
            state "Occupied", label:'${name}', action:"switchOccupancy", nextState:"Unoccupied", icon:"http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png"
            state "Unoccupied", label:'${name}', action:"switchOccupancy", nextState:"Local Occ", icon:"http://cdn.device-icons.smartthings.com/Home/home3-icn@2x.png"
        }
        standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        standardTile("effOcc", "device.effectiveOccupancy", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "Occupied", label: '${name}', icon:"http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png"
            state "Unoccupied", label: '${name}', icon:"http://cdn.device-icons.smartthings.com/Home/home3-icn@2x.png"
            state "Override", label: '${name}', icon:"http://cdn.device-icons.smartthings.com/Home/home1-icn@2x.png"
            state "Standby", label: '${name}'
		}    
        standardTile("incCoolSP", "device.coolSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateCoolingSetpoint", label:'', action:"increaseCoolSetpoint", icon:"st.thermostat.thermostat-up" //icon:"http://cdn.device-icons.smartthings.com/Weather/weather14-icn@2x.png"
		}  
        standardTile("decCoolSP", "device.coolSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateCoolingSetpoint", label:'', action:"decreaseCoolSetpoint", icon:"st.thermostat.thermostat-down"// icon:"http://cdn.device-icons.smartthings.com/Weather/weather7-icn@2x.png"
		}
		standardTile("incHeatSP", "device.heatSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateHeatingSetpoint", label:'', action:"increaseHeatSetpoint", icon:"st.thermostat.thermostat-up" //icon:"http://cdn.device-icons.smartthings.com/Weather/weather14-icn@2x.png"
		}  
        standardTile("decHeatSP", "device.heatSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateHeatingSetpoint", label:'', action:"decreaseHeatSetpoint", icon:"st.thermostat.thermostat-down"//  icon:"http://cdn.device-icons.smartthings.com/Weather/weather7-icn@2x.png"
		}     
        standardTile("incUnoccCoolSP", "device.unoccCoolSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateUnoccCoolingSetpoint", label:'', action:"increaseUnoccCoolSetpoint", icon:"st.thermostat.thermostat-up" //icon:"http://cdn.device-icons.smartthings.com/Weather/weather14-icn@2x.png"
		}  
        standardTile("decUnoccCoolSP", "device.unoccCoolSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateUnoccCoolingSetpoint", label:'', action:"decreaseUnoccCoolSetpoint", icon:"st.thermostat.thermostat-down"// icon:"http://cdn.device-icons.smartthings.com/Weather/weather7-icn@2x.png"
		}
        standardTile("incUnoccHeatSP", "device.unoccHeatSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateUnoccHeatingSetpoint", label:'', action:"increaseUnoccHeatSetpoint", icon:"st.thermostat.thermostat-up" //icon:"http://cdn.device-icons.smartthings.com/Weather/weather14-icn@2x.png"
		}  
        standardTile("decUnoccHeatSP", "device.unoccHeatSP", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "UpdateUnoccHeatingSetpoint", label:'', action:"decreaseUnoccHeatSetpoint", icon:"st.thermostat.thermostat-down"// icon:"http://cdn.device-icons.smartthings.com/Weather/weather7-icn@2x.png"
		}          
        main ("thermostatMulti")
        details(["thermostatMulti", "humidity", "mode", "occCommand", "effOcc", "heatingSetpoint", "decHeatSP", "incHeatSP", "coolingSetpoint", "decCoolSP", "incCoolSP", "unoccHeatingSetpoint", "decUnoccHeatSP", "incUnoccHeatSP", "unoccCoolingSetpoint", "decUnoccCoolSP", "incUnoccCoolSP","refresh", "configure"])
    }

}



def parse(String description) {

	return parseCalled(description)

}



def parseCalled(String description) {

	log.debug "Parse description $description"

	def map = [:]

	if (description?.startsWith("read attr -")) {

		def descMap = parseDescriptionAsMap(description)
		log.debug "Desc Map: $descMap"
		if (descMap.cluster == "0201" && descMap.attrId == "0000")
        {
			map.name = "temperature"
			map.value = getTemperature(descMap.value)
            if (descMap.value == "7ffd") {		//0x7FFD
                map.value = "low"
            }
            else if (descMap.value == "7fff") {	//0x7FFF
                map.value = "high"
            }
            else if (descMap.value == "8000") {	//0x8000
                map.value = "--"
            }          
            else if (descMap.value > "8000") {
                map.value = -(Math.round(2*(655.36 - map.value))/2)
            }
            map.unit = getTemperatureScale()
            map.isStateChange = true
		}
        else if (descMap.cluster == "0201" && descMap.attrId == "0012") {
			log.debug "HEATING SETPOINT"
			map.name = "heatingSetpoint"
			map.value = getTemperature(descMap.value)
            if (descMap.value == "8000") {		//0x8000
                map.value = "--"
            }
            map.unit = getTemperatureScale()
            map.isStateChange = true
		}
        else if (descMap.cluster == "0201" && descMap.attrId == "0014") {
			log.debug "UNOCC HEATING SETPOINT"
			map.name = "unoccHeatingSetpoint"
			map.value = getTemperature(descMap.value)
            if (descMap.value == "8000") {		//0x8000
                map.value = "--"
            }
            map.unit = getTemperatureScale()
            map.isStateChange = true
		}   
        else if (descMap.cluster == "0201" && descMap.attrId == "0013") {
			log.debug "UNOCC COOLING SETPOINT"
			map.name = "unoccCoolingSetpoint"
			map.value = getTemperature(descMap.value)
            if (descMap.value == "8000") {		//0x8000
                map.value = "--"
            }
            map.unit = getTemperatureScale()
            map.isStateChange = true
		}        
        else if (descMap.cluster == "0201" && descMap.attrId == "0002") {

			log.debug "MOTION SENSOR"

			map.name = "motion"

			map.value = descMap.value

			map.isStateChange = true

		}        

        else if (descMap.cluster == "0201" && descMap.attrId == "0011") {

			log.debug "COOLING SETPOINT"

			map.name = "coolingSetpoint"

			map.value = getTemperature(descMap.value)

            if (descMap.value == "8000") {		//0x8000

                map.value = "--"

            }
			map.unit = getTemperatureScale()
            map.isStateChange = true            

            sendEvent(name:"coolSP", value:"UpdateCoolingSetpoint", isStateChange:true)

		}    

        else if (descMap.cluster == "0201" && descMap.attrId == "0a58") {

			log.debug "GFan"

            log.debug descMap.value

			map.name = "GFan"

			map.value = descMap.value

            map.isStateChange = true


		}          

        else if (descMap.cluster == "0201" && descMap.attrId == "0687") { //tsat system mode


				log.debug "MODE"

				map.name = "thermostatMode"

				map.value = getModeMap()[descMap.value]


                map.isStateChange = true


		}        

        else if (descMap.cluster == "0201" && descMap.attrId == "07a6") {

        	log.debug "HUMIDITY TSTAT"
			map.name = "humidity"
            map.unit = "%"
			map.value = Integer.parseInt(descMap.value,16)
            map.isStateChange = true
		}    

        else if (descMap.cluster == "0201" && descMap.attrId == "0650") {

        	log.debug "GAMMA Occupancy Command"
            def modeOrder =  occCommands()
			map.name = "occupancyCommand"
           
            if (descMap.value == "00")
            {
            	map.value = modeOrder[0]
            }
            else  if (descMap.value == "01")
            {
            	map.value = modeOrder[1]
            }
            else if (descMap.value == "02")
            {
            	map.value = modeOrder[2]
            }
            else
            {
            	map.value = "Local Occ"
            }
            map.isStateChange = true
		}
        else if (descMap.cluster == "0201" && descMap.attrId == "0687") {
        	log.debug "GAMMA System Mode"
		}      
        else if (descMap.cluster == "0201" && descMap.attrId == "0c50") {
        	log.debug "GAMMA Occupancy Status"
            def modeOrder =  occStatusList()
			map.name = "effectiveOccupancy"
            if (descMap.value == "00")
            {
            	map.value = modeOrder[0]
            }
            else  if (descMap.value == "01")
            {
            	map.value = modeOrder[1]
            }
            else if (descMap.value == "02")
            {
            	map.value = modeOrder[2]
            }
            else if (descMap.value == "03")
            {
            	map.value = modeOrder[3]
            }            
            else
            {
            	map.value = "Override"
            }
            map.isStateChange = true
   
		}

        else if (descMap.cluster == "0201" && descMap.attrId == "06bf") //Mode status

        {

        	log.debug "Effective System Mode"
            def currentSetpoint
            def currentFanStatus
         	map.name = "thermostatOperatingState"
           	log.debug descMap.value
           	if (descMap.value == "00")
           	{
           		map.value = "idle"
            }
           	else if (descMap.value == "01")
            {
            	 map.value = "cooling"
                 currentSetpoint = device.currentValue("coolingSetpoint")
                 def degrees = new BigDecimal(currentSetpoint).setScale(1, BigDecimal.ROUND_HALF_UP)
                 sendEvent(name:"thermostatSetpoint", value:currentSetpoint, isStateChange: true)
            }
            else  if (descMap.value == "02")
            {
            	 map.value = "heating"
                 currentSetpoint = device.currentValue("heatingSetpoint")
                 def degrees = new BigDecimal(currentSetpoint).setScale(1, BigDecimal.ROUND_HALF_UP)
                 sendEvent(name:"thermostatSetpoint", value:currentSetpoint, isStateChange: true)                 
            }
            map.isStateChange = true
		}
        else if (descMap.cluster == "0201" && descMap.attrId == "0688") //Fan Mode

        {
			def appFanMode
        	log.debug "Fan Mode"
         	map.name = "fanSpeed"
           	log.debug descMap.value
            
            //Map thermostat value to the Fan Speed slider
            if (descMap.value == "00") //low
            {
            	appFanMode = 1
            }
            else if (descMap.value == "01") //medium
            {
            	appFanMode = 2
            }
            else if (descMap.value == "02") //high
            {
            	appFanMode = 3
            }
            else if (descMap.value == "03") //auto
            {
            	appFanMode = 4
            }
            else
            {
            	appFanMode = 4
            }
                        
          	map.value = appFanMode
            map.isStateChange = true
		}          


	}
    else if (description?.startsWith("humidity")) 
	{

    def descMap = parseDescriptionAsMap(description)

		log.debug "Desc Map: $descMap"
		log.debug "HUMIDITY"
		map.name = "humidity"
        map.unit = "%"
		//map.value = descMap.humidity
		map.value = (description - "humidity: " - "%").trim()     
        map.isStateChange = true               
    }



	def result = null

	if (map) {

    	map.isStateChange = true

		result = createEvent(map)

	}

	log.debug "Parse returned $map"

	return result

}



def parseDescriptionAsMap(description) {

	(description - "read attr - ").split(",").inject([:]) { map, param ->

		def nameAndValue = param.split(":")

		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]

	}

}

def getModeMap() { [

	"00":"off",
    "01":"auto",
    "02":"cool",
    "03":"heat",
    "04":"heat",
	//"05":"eco"

]}



def getFanModeMap() { [

	"04":"fanOn",
	"05":"fanAuto"

]}



def poll() {

    return pollCalled()

}



def pollCalled() {

	log.debug "**pollcalled**"

    delayBetween([


                zigbee.readAttribute(0x201, 0x0000),	//Read Local Temperature

    			zigbee.readAttribute(0x201, 0x0012),	//Read Heat Setpoint

                zigbee.readAttribute(0x201, 0x0687),	//Read System Mode //formerly 001c

                zigbee.readAttribute(0x201, 0x0011),	//Read Cool Setpoint

                zigbee.readAttribute(0x201, 0x0650),	//occupancy command

                zigbee.readAttribute(0x201, 0x0A58),	//GFan Status

                zigbee.readAttribute(0x201, 0x0013),	//Read Unocc Cool Setpoint

                zigbee.readAttribute(0x201, 0x0014),	//Read Unocc Heat Setpoint

                zigbee.readAttribute(0x201, 0x0C50),    //Effective Occupancy Status

                zigbee.readAttribute(0x201, 0x0698),    //Fan Mode

                zigbee.readAttribute(0x204, 0x0000),	//Read Temperature Display Mode

                zigbee.readAttribute(0x405, 0x0000),	//Read System Mode

                zigbee.readAttribute(0x201, 0x06BF),    //Effective Mode Status
                
                zigbee.readAttribute(0x201, 0x0688),    //Fan Mode Status

                sendEvent( name: 'change', value: 0 )

        ], 200)    

             

}				



def getTemperature(value) {

	if (value != null) {

    	log.debug("value $value")

		def celsius = Integer.parseInt(value, 16) / 100

		if (getTemperatureScale() == "C") {

			return celsius

		}

        else {

			return Math.round(celsiusToFahrenheit(celsius))

		}

	}

}



def refresh() {

    poll()

}



def quickSetHeat(degrees) {

    sendEvent( name: 'change', value: 1 )

    setHeatingSetpoint(degrees, 0)

}



def setHeatingSetpoint(preciseDegrees, delay = 0) {

	if (preciseDegrees != null) {

		def temperatureScale = getTemperatureScale()

		def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)

		log.debug "setHeatingSetpoint({$degrees} ${temperatureScale})"        

        sendEvent(name: "heatingSetpoint", value: degrees, unit: temperatureScale, isStateChange: true)

        def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

        delayBetween([

        	zigbee.writeAttribute(0x201, 0x12, 0x29, hex(celsius * 100)),

        	zigbee.readAttribute(0x201, 0x12),	//Read Heat Setpoint

            zigbee.readAttribute(0x201, 0x06BF),//Mode status
            
            zigbee.readAttribute(0x201, 0x11),	//Read Cool Setpoint

            poll()


    	], 100)

	}

}





def quickSetUnoccHeat(degrees) {

    sendEvent( name: 'change', value: 1 )

    setUnoccHeatingSetpoint(degrees, 0)

}

def setUnoccHeatingSetpoint(preciseDegrees, delay = 0) {

	if (preciseDegrees != null) {

		def temperatureScale = getTemperatureScale()

		def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)

		log.debug "setUnoccHeatingSetpoint({$degrees} ${temperatureScale})"        

        sendEvent(name: "unoccHeatingSetpoint", value: degrees, unit: temperatureScale, isStateChange: true)
    

        def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

        delayBetween([

        	zigbee.writeAttribute(0x201, 0x14, 0x29, hex(celsius * 100)),

        	zigbee.readAttribute(0x201, 0x14),	//Read Heat Setpoint

            zigbee.readAttribute(0x201, 0x06BF),//Mode status

            poll()


    	], 100)

	}

}


def quickSetCool(degrees) {

    sendEvent( name: 'change', value: 1 )

    setCoolingSetpoint(degrees, 0)

}



def setCoolingSetpoint(preciseDegrees, delay = 0) {

	if (preciseDegrees != null) {

		def temperatureScale = getTemperatureScale()

		def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)


		log.debug "setCoolingSetpoint({$degrees} ${temperatureScale})"
        

        sendEvent(name: "coolingSetpoint", value: degrees, unit: temperatureScale, isStateChange: true)


        def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

        delayBetween([

        	zigbee.writeAttribute(0x201, 0x11, 0x29, hex(celsius * 100)),

        	zigbee.readAttribute(0x201, 0x11),	//Read Cool Setpoint

            zigbee.readAttribute(0x201, 0x06BF),//Mode status
            
            zigbee.readAttribute(0x201, 0x12),	//Read Heat Setpoint

            poll()


    	], 100)

	}

}

def setFanSpeed(speed)
{
	log.debug "setFanSpeed ${speed}"
    
    sendEvent("name":"fanSpeed", "value":speed, isStateChange: true)   
    if (speed == 0) //off for app, but will be set to auto
    {
    	speed = 3
    }
    else if (speed >= 4) //MAX speed or higher, go to Auto in SE83
    {
    	speed = 3
    }
    else
    {
    	speed = speed - 1
    }
    
	 
    delayBetween([

        zigbee.writeAttribute(0x201, 0x0688, 0x30, speed),
        
        zigbee.readAttribute(0x201, 0x06BF),

        zigbee.readAttribute(0x201, 0x0688),	//Read back Fan Mode        

        poll()


    ], 100) 

}

def quickSetUnoccCool(degrees) {

    sendEvent( name: 'change', value: 1 )

    setUnoccCoolingSetpoint(degrees, 0)

}



def setUnoccCoolingSetpoint(preciseDegrees, delay = 0) {

	if (preciseDegrees != null) {

		def temperatureScale = getTemperatureScale()

		def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)


		log.debug "setUnoccCoolingSetpoint({$degrees} ${temperatureScale})"
        

        sendEvent(name: "unoccCoolingSetpoint", value: degrees, unit: temperatureScale, isStateChange: true)

 
        def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

        delayBetween([

        	zigbee.writeAttribute(0x201, 0x13, 0x29, hex(celsius * 100)),

        	zigbee.readAttribute(0x201, 0x13),	//Read Cool Setpoint

            zigbee.readAttribute(0x201, 0x06BF),

            poll()


    	], 100)

	}

}





def quickSetOutTemp(degrees) {

    setOutdoorTemperature(degrees, 0)

}



def setOutdoorTemperature(degrees, delay = 0) {

    setOutdoorTemperature(degrees.toDouble(), delay)

}



def setOutdoorTemperature(Double degrees, Integer delay = 0) {

    def p = (state.precision == null) ? 1 : state.precision

    Integer tempToSend

    def tempToSendInString

    

    def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)



    if (celsius < 0) {

        tempToSend = (celsius*100) + 65536

    }

    else {

    	tempToSend = (celsius*100)

    }

    tempToSendInString = zigbee.convertToHexString(tempToSend, 4)



}



def increaseEffectiveSetpoint() {

	def currentEffectiveSetpoint = device.currentState("thermostatOperatingState")?.value

    

    if (currentEffectiveSetpoint == "heating")

    {

    	increaseHeatSetpoint()

    }

    else if (currentEffectiveSetpoint == "cooling")

    {

    	increaseCoolSetpoint()

    }

}



def decreaseEffectiveSetpoint() {

	def currentEffectiveSetpoint = device.currentState("thermostatOperatingState")?.value

    

    if (currentEffectiveSetpoint == "heating")

    {

    	decreaseHeatSetpoint()

    }

    else if (currentEffectiveSetpoint == "cooling")

    {

    	decreaseCoolSetpoint()

    }



}

def increaseHeatSetpoint() {


		float currentSetpoint = device.currentValue("heatingSetpoint")

		def locationScale = getTemperatureScale()

    	float maxSetpoint

    	float step



    	if (locationScale == "C") {

        	maxSetpoint = 30;

        	step = 0.5

    	}

    	else {

        	maxSetpoint = 86

        	step = 1

    	}



        if (currentSetpoint < maxSetpoint) {

            currentSetpoint = currentSetpoint + step

            quickSetHeat(currentSetpoint)

        }



}



def increaseUnoccHeatSetpoint() {


		float currentSetpoint = device.currentValue("unoccHeatingSetpoint")

		def locationScale = getTemperatureScale()

    	float maxSetpoint

    	float step



    	if (locationScale == "C") {

        	maxSetpoint = 30;

        	step = 0.5

    	}

    	else {

        	maxSetpoint = 86

        	step = 1

    	}



        if (currentSetpoint < maxSetpoint) {

            currentSetpoint = currentSetpoint + step

            quickSetUnoccHeat(currentSetpoint)

        }


}



def decreaseHeatSetpoint() {



        float currentSetpoint = device.currentValue("heatingSetpoint")

        def locationScale = getTemperatureScale()

        float minSetpoint

        float step



        if (locationScale == "C") {

            minSetpoint = 5;

            step = 0.5

        }

        else {

            minSetpoint = 41

            step = 1

        }



    	if (currentSetpoint > minSetpoint) {

        	currentSetpoint = currentSetpoint - step

        	quickSetHeat(currentSetpoint)

    	}


}



def decreaseUnoccHeatSetpoint() {



        float currentSetpoint = device.currentValue("unoccHeatingSetpoint")

        def locationScale = getTemperatureScale()

        float minSetpoint

        float step



        if (locationScale == "C") {

            minSetpoint = 5;

            step = 0.5

        }

        else {

            minSetpoint = 41

            step = 1

        }



    	if (currentSetpoint > minSetpoint) {

        	currentSetpoint = currentSetpoint - step

        	quickSetUnoccHeat(currentSetpoint)

    	}


}





def increaseCoolSetpoint() {



		float currentSetpoint = device.currentValue("coolingSetpoint")

		def locationScale = getTemperatureScale()

    	float maxSetpoint

    	float step



    	if (locationScale == "C") {

        	maxSetpoint = 30;

        	step = 0.5

    	}

    	else {

        	maxSetpoint = 86

        	step = 1

    	}



        if (currentSetpoint < maxSetpoint) {

            currentSetpoint = currentSetpoint + step

            quickSetCool(currentSetpoint)

        }


}



def increaseUnoccCoolSetpoint() {



		float currentSetpoint = device.currentValue("unoccCoolingSetpoint")

		def locationScale = getTemperatureScale()

    	float maxSetpoint

    	float step



    	if (locationScale == "C") {

        	maxSetpoint = 30;

        	step = 0.5

    	}

    	else {

        	maxSetpoint = 86

        	step = 1

    	}



        if (currentSetpoint < maxSetpoint) {

            currentSetpoint = currentSetpoint + step

            quickSetUnoccCool(currentSetpoint)

        }


}



def decreaseCoolSetpoint() {



        float currentSetpoint = device.currentValue("coolingSetpoint")

        def locationScale = getTemperatureScale()

        float minSetpoint

        float step



        if (locationScale == "C") {

            minSetpoint = 12;

            step = 0.5

        }

        else {

            minSetpoint = 53

            step = 1

        }



    	if (currentSetpoint > minSetpoint) {

        	currentSetpoint = currentSetpoint - step

        	quickSetCool(currentSetpoint)

    	}


}





def decreaseUnoccCoolSetpoint() {



        float currentSetpoint = device.currentValue("unoccCoolingSetpoint")

        def locationScale = getTemperatureScale()

        float minSetpoint

        float step



        if (locationScale == "C") {

            minSetpoint = 12;

            step = 0.5

        }

        else {

            minSetpoint = 53

            step = 1

        }



    	if (currentSetpoint > minSetpoint) {

        	currentSetpoint = currentSetpoint - step

        	quickSetUnoccCool(currentSetpoint)

    	}


}



def occStatusList() {

	["Occupied","Unoccupied","Override", "Standby"]

}



def occCommands() {

	["Local Occ", "Occupied","Unoccupied"]

}



def fanModeOrder()

{

	["on", "auto", "circulate"]	

}

def switchOccupancy() {

	log.debug "SWITCH Occupancy CALLED"

    def currentMode = device.currentState("occupancyCommand")?.value

    def lastTriedMode = state.lastTriedMode ?: currentMode ?: "Local Occ"

    def modeOrder = occCommands()

    def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }

	def nextMode = next(currentMode)

    def modeNumber;

    Integer setpointModeNumber;

    def modeToSendInString;



    if (nextMode == "Local Occ") {

    	modeNumber = 00

        setpointModeNumber = 00

        sendEvent("name":"occupancyCommand", "value":"Local Occ", isStateChange: true)

    }

    else if (nextMode == "Occupied") {

    	modeNumber = 01

        setpointModeNumber = 01

        sendEvent("name":"occupancyCommand", "value":"Occupied", isStateChange: true)

    }

    else if (nextMode == "Unoccupied") {

    	modeNumber = 02

        setpointModeNumber = 02

        sendEvent("name":"occupancyCommand", "value":"Unoccupied", isStateChange: true)

    }    



    log.debug "next mode is {$modeNumber}"


    state.lastTriedMode = nextMode

	modeToSendInString = zigbee.convertToHexString(setpointModeNumber, 2)

    delayBetween([


            zigbee.writeAttribute(0x201, 0x0650, 0x30, modeNumber),	//Write Lock Mode

            zigbee.readAttribute(0x201, 0x06BF),

            zigbee.readAttribute(0x201, 0x0C50),


            poll()

    ], 1000)

}



def modes() {

	["off", "auto", "cool", "heat"]

}


def switchMode() {

	log.debug "SWITCH MODE CALLED"

    def currentMode = device.currentState("thermostatMode")?.value

    def lastTriedMode = state.lastTriedMode ?: currentMode ?: "heat"

    def modeOrder = modes()

    def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }

	def nextMode = next(currentMode)

    def modeNumber;

    Integer setpointModeNumber;

    def modeToSendInString;



    if (nextMode == "heat") {

    	modeNumber = 03

        setpointModeNumber = 03

        sendEvent("name":"thermostatMode", "value":"heat", isStateChange: true)

    }

    else if (nextMode == "off") {

    	modeNumber = 00

        setpointModeNumber = 00

        sendEvent("name":"thermostatMode", "value":"off", isStateChange: true)

    }

    else if (nextMode == "cool") {

    	modeNumber = 02

        setpointModeNumber = 02

        sendEvent("name":"thermostatMode", "value":"cool", isStateChange: true)

    }    

    else {

    	modeNumber = 01

        setpointModeNumber = 01

        sendEvent("name":"thermostatMode", "value":"auto", isStateChange: true)

    }

    

    log.debug "next mode is {$modeNumber}"

    state.lastTriedMode = nextMode

	modeToSendInString = zigbee.convertToHexString(setpointModeNumber, 2)

    delayBetween([


            zigbee.writeAttribute(0x201, 0x0687, 0x30, modeNumber),	//Write Lock Mode

            zigbee.readAttribute(0x201, 0x06BF),//Mode status

            poll()

    ], 1000)

}



def setThermostatMode(String mode) {

	log.debug "switching thermostatMode"
	def currentMode = device.currentState("thermostatMode")?.value
	def modeOrder = modes()
	//def index = modeOrder.indexOf(currentMode)
	def next = "auto"
	//def next = index >= 0 && index < modeOrder.size() - 1 ? modeOrder[index + 1] : modeOrder[0]
    if (mode == "off")
    {
    	next = "off"
    }
    else if (mode == "auto")
    {
    	next = "auto"
    }
    else if (mode == "cool")
    {
    	next = "cool"
    }
    else if (mode == "heat") 
    {
    	next = "heat"
    }
	//def next = modeOrder.indexOf(mode)
	log.debug "switching mode from $currentMode to $next"

	"$next"()

}



def setThermostatFanMode(String fanM) {

	log.debug "Switching fan mode"
	def currentFanMode = device.currentState("fanModeGamma")?.value
	log.debug "switching fan from current mode: $currentFanMode"
	def returnCommand
	log.debug fanM
	switch (fanM) {
		case "on":
			fanOnGamma()
			break
		case "auto":
			fanAutoGamma()
			break
        case "circulate":
        	fanSmartGamma()
        break
	}
}


def off() {

	def modeNumber
    modeNumber = 0
	log.debug "off"
	sendEvent("name":"thermostatMode", "value":"off", isStateChange: true)

	delayBetween([

		zigbee.writeAttribute(0x201, 0x0687, 0x30, modeNumber),	//Write System Mode

		poll()
        
    ], 1000)     

}



def cool() {

	def modeNumber
    modeNumber = 2
	log.debug "cool"
	sendEvent("name":"thermostatMode", "value":"cool", isStateChange: true)
    
    delayBetween([

        zigbee.writeAttribute(0x201, 0x0687, 0x30, modeNumber),	//Write System Mode

		poll()
        
    ], 1000)   

}



def heat() {

	def modeNumber
    modeNumber = 3
	log.debug "heat"
	sendEvent("name":"thermostatMode", "value":"heat", isStateChange: true)
   
    delayBetween([

        zigbee.writeAttribute(0x201, 0x0687, 0x30, modeNumber),	//Write System Mode

		poll()
        
    ], 1000)       

}



def eco() {

	Integer setpointModeNumber;

    def modeToSendInString;

	

	log.debug "eco"

	setpointModeNumber = 05

	modeToSendInString = zigbee.convertToHexString(setpointModeNumber, 2)

	sendEvent("name":"thermostatMode", "value":"eco", isStateChange: true)

	"st wattr 0x${device.deviceNetworkId} 0x19 0x201 0x1C 0x30 {04}"


}



def emergencyHeat() {

	log.debug "emergencyHeat"

	sendEvent("name":"thermostatMode", "value":"emergency heat", isStateChange: true)

	"st wattr 0x${device.deviceNetworkId} 0x19 0x201 0x1C 0x30 {05}"

}



def setCustomThermostatMode(mode) {

   setThermostatMode(mode)

}



def on() {

	fanOn()

}



def fanOn() {

	log.debug "fanOn"

	sendEvent("name":"thermostatFanMode", "value":"fanOn")

	"st wattr 0x${device.deviceNetworkId} 0x19 0x202 0 0x30 {04}"

}



def auto() {

	def modeNumber

    modeNumber = 1	

	log.debug "auto"

    sendEvent("name":"thermostatMode", "value":"auto", isStateChange: true)

    delayBetween([

        zigbee.writeAttribute(0x201, 0x0687, 0x30, modeNumber),	//Write System Mode,

		poll()
        
    ], 1000)    	



}



def fanOnGamma() {

	def modeNumber;

	log.debug "fanOn"

	sendEvent("name":"thermostatFanMode", "value":"On")

    modeNumber = 4



        delayBetween([

        	zigbee.writeAttribute(0x201, 0x688, 0x30, modeNumber ),

        	zigbee.readAttribute(0x201, 0x688),	//Read Heat Setpoint


    	], 1000)    

}



def fanAutoGamma() {

	def modeNumber;

	log.debug "fanAuto"

    

	sendEvent("name":"thermostatFanMode", "value":"Auto")


    modeNumber = 3


        delayBetween([

        	zigbee.writeAttribute(0x201, 0x688, 0x30, modeNumber ),

        	zigbee.readAttribute(0x201, 0x688),	//Read Heat Setpoint

    	], 1000)    

}

def fanSmartGamma() {

	def modeNumber;

	sendEvent("name":"thermostatFanMode", "value":"Smart")


    modeNumber = 2


        delayBetween([

        	zigbee.writeAttribute(0x201, 0x688, 0x30, modeNumber ),

        	zigbee.readAttribute(0x201, 0x688),	//Read Heat Setpoint

    	], 1000) 

}







def configure() {

	log.debug "binding to Thermostat cluster"

    //initialize()

	delayBetween([
    

        "zdo bind 0x${device.deviceNetworkId} 1 0xA 0x201 {${device.zigbeeId}} {}", 

        "zdo bind 0x${device.deviceNetworkId} 1 0xA 0x405 {${device.zigbeeId}} {}",

        //Cluster ID (0x0201 = Thermostat Cluster), Attribute ID, Data Type, Payload (Min report, Max report, On change trigger)

		//zigbee.configureReporting(0x0201, 0x0000, 0x29, 10, 60, 50), 	//Attribute ID 0x0000 = local temperature, Data Type: S16BIT

        zigbee.readAttribute(0x201, 0x0000),	//Read Local Temperature

    	zigbee.readAttribute(0x201, 0x0012),	//Read Heat Setpoint

        zigbee.readAttribute(0x201, 0x0687),	//Read System Mode formerly 1c

        zigbee.readAttribute(0x201, 0x0011),	//read cooling setpoint

        zigbee.readAttribute(0x201, 0x0013),	//read unocc cooling setpoint

        zigbee.readAttribute(0x201, 0x0014),	//read unocc heating setpoint

        zigbee.readAttribute(0x201, 0x0650),	//occupancy command

        zigbee.readAttribute(0x201, 0x0687),    //Gamma System Mode

        zigbee.readAttribute(0x201, 0x0C50),    //Gamma Occupancy Status       

        zigbee.readAttribute(0x204, 0x0000),	//Read Temperature Display Mode

        zigbee.readAttribute(0x405, 0x0000),	

        zigbee.readAttribute(0x201, 0x06BF),	//Mode status
        
         zigbee.readAttribute(0x201, 0x0688),	//Fan mode

	], 200)

}



def updated() {

    response(parameterSetting())

}



def parameterSetting() {

    def lockmode = null

    def valid_lock = 0



    log.info "lock : $settings.lock"

    if (settings.lock == "Yes") {

        lockmode = 0x01

        valid_lock = 1

    }

    else if (settings.lock == "No") {

        lockmode = 0x00

        valid_lock = 1

    }

    

    if (valid_lock == 1)

    {

    	log.info "lock valid"

        delayBetween([

            zigbee.writeAttribute(0x204, 0x01, 0x30, lockmode),	//Write Lock Mode

            poll(),

        ], 200)

    }

    else {

    	log.info "nothing valid"

    }

}



private hex(value) {

	new BigInteger(Math.round(value).toString()).toString(16)

}



private getEndpointId() {

    new BigInteger(device.endpointId, 16).toString()

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

def initialize() {

	runEvery5Minutes(pollCalled)    

}



def setOccupied() {

    def modeNumber;

    Integer setpointModeNumber;

    def modeToSendInString;

    modeNumber = 01

    setpointModeNumber = 01

    sendEvent("name":"occupancyCommand", "value":"Occupied", isStateChange: true)    

    log.debug "next mode is {$modeNumber}"

	modeToSendInString = zigbee.convertToHexString(setpointModeNumber, 2)

    zigbee.writeAttribute(0x201, 0x0650, 0x30, modeNumber) //Write Occupancy Command


}



def setUnoccupied() {


    def modeNumber;  

    modeNumber = 02

    sendEvent("name":"occupancyCommand", "value":"Unoccupied", isStateChange: true)    

    log.debug "next mode is {$modeNumber}"

    zigbee.writeAttribute(0x201, 0x0650, 0x30, modeNumber)	//Write Occupancy Command

}