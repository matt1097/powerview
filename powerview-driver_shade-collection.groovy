/**
 * Hunter Douglas PowerView Scene Collection (device handler)
 * Copyright (c) 2017 Johnvey Hwang
 * Ported to Hubitat by matt1097 03/2020 based on Steve Borenstein's work
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


// ----------------------------------------------------------------------------
// configuration
// ----------------------------------------------------------------------------

metadata {
    definition (
        name: "Hunter Douglas PowerView Scene Collection", 
        namespace: "johnvey", 
        importUrl: "https://raw.githubusercontent.com/matt1097/powerview/master/powerview-driver_shade-collection.groovy",
        author: "Johnvey Hwang, ported by matt1097"
    ) {
        // tags
        capability "Actuator"

        // device capabilities
        capability "Momentary"
        capability "Switch"
    }

    tiles(scale: 2) {
        standardTile("on", "device.windowShade", width: 3, height: 3,
                    inactiveLabel: false, decoration: "flat") {
            state("default", label:'Activate', action:"push",
                icon:"st.switches.light.on")
        }
        main(["on"])
    }
}


// ----------------------------------------------------------------------------
// hub comm methods
// ----------------------------------------------------------------------------

/**
 * Returns a unique id for deviceNetworkId uses; prefix must coordinate with
 * the `getDeviceId()` method in powerview-manager.groovy
 */
private getDeviceId(pvId) {
    return "scenecollection;${state.hubMAC};${pvId}"
}

private sendRequest(method, path, body=null) {
    def host = "${state.hubIP}:${state.hubPort}"
    def hubAction = new hubitat.device.HubAction(
        [
            method: method,
            path: path,
            HOST: host,
            headers: [
                'HOST': host,
                'Content-Type': 'application/json'
            ],
            body: body
        ],
        getDeviceId(state.pvShadeId),
        [
            callback: sendRequestCallback
        ]
    )
    log.debug("sendRequest: ${method} ${host}${path}")
    return hubAction
}

def sendRequestCallback(response) {
    if (response.status != 200) {
        log.warn("got unexpected response: status=${response.status} body=${response.body}")
    }
}


// ----------------------------------------------------------------------------
// app lifecycle hooks
// ----------------------------------------------------------------------------

def setHubInfo() {
    def DEFAULT_HUB_PORT = 80
    state.hubMAC = parent.hubMAC
    state.hubIP = parent.hubIP
    state.hubPort = DEFAULT_HUB_PORT //parent.hubPort
    state.pvSceneCollectionId = device.name
    log.debug("called setHubInfo() - hubMAC=${state.hubMAC} hubIP=${state.hubIP} hubPort=${state.hubPort} pvSceneId=${state.pvSceneCollectionId}")
}

// parse hub response into attributes
def parse(String description) {
    log.warn("parse() not implemented! Got: '${description}'")
}

def installed() {
    log.info('CMD installed()')
    setHubInfo()
}

def updated() {
    log.info('CMD updated()')
    setHubInfo()
}

// implement the momentary method
def push() {
    log.debug("CMD push(${state.pvSceneId})")
    sendRequest("GET", "/api/scenecollections?sceneCollectionId=${state.pvSceneCollectionId}")
}

def on() {
    log.debug("CMD on()")
    return push()
}

def off() {
    log.debug("CMD off()")
    // pass
}