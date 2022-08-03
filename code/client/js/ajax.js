// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines the AjaxRequest class.

/**
 * AjaxRequest class simplifies AJAX requests, and can try different hosts iteratively
 */
class AjaxRequest {

    #debug = false // When debug is true, the hosts, method and URLs will be printed on console
    #rand = true  // When rand is true, the order of hosts will be random to avoid server load unbalance

    #method
    #hosts
    #uri

    /**
     * Constructor of the class
     *
     * @param method A HTTP method (GET, POST, PUT, and DELETE)
     * @param hosts Array of hosts
     * @param uri URI of the target site
     */
    constructor(method, hosts, uri) {
        this.#method = method
        if (this.#rand) {
            // Disrupt the order of hosts
            if (hosts.length > 1) {
                let i = hosts.length
                while (i) {
                    let j = Math.floor(Math.random() * i--)
                    let temp = hosts[i]
                    hosts[i] = hosts[j]
                    hosts[j] = temp
                }
            }
            if (this.#debug) {
                console.log(hosts)
            }
        }
        this.#hosts = hosts
        this.#uri = uri
    }

    /**
     * Default handler for success is to print response text in console
     *
     * @param resp Response text that AJAX request received
     */
    #successHandler = (resp) => console.log(`Response: ${resp}`)

    /**
     * Modify default handler for success
     *
     * @param successHandler Handler for success with default in case of passing nothing
     * @returns {AjaxRequest} Chain method
     */
    bindSuccess = (successHandler = (resp) => this.#successHandler(resp)) => {
        this.#successHandler = (resp) => successHandler(resp)
        return this
    }

    /**
     * Default handler for failure is to print response status
     *
     * @param stat Status that AJAX request received, it will be 0 if request timeout
     */
    #failureHandler = (stat) => console.log(`Status: ${stat}`)

    /**
     * Modify default handler for failure
     *
     * @param failureHandler Handler for failure with default in case of passing nothing
     * @returns {AjaxRequest} Chain method
     */
    bindFailure = (failureHandler = (stat) => this.#failureHandler(stat)) => {
        this.#failureHandler = (stat) => failureHandler(stat)
        return this
    }

    /**
     * Modify default handlers for success and failure
     *
     * @param successHandler Handler for success with default in case of passing nothing
     * @param failureHandler Handler for failure with default in case of passing nothing
     * @returns {AjaxRequest} Chain method
     */
    bind = (
        successHandler = (resp) => this.#successHandler(resp),
        failureHandler = (stat) => this.#failureHandler(stat)
    ) => {
        this.#successHandler = (resp) => successHandler(resp)
        this.#failureHandler = (stat) => failureHandler(stat)
        return this
    }

    /**
     * Traverse the hosts and send AJAX requests iteratively
     *
     * @param body Request body to send, only POST method required here
     */
    request = (body) => {
        let succeed = false, i = 0, lock = false, status
        // Try to raise a request every 1 millisecond
        let interval = setInterval(() => {
            // When no request succeed and the hosts has not been traversed
            if (!succeed && this.#hosts[i] !== undefined) {
                // When there is no request is handling
                // The AJAX requests are asynchronous, so this simple lock is required
                if (!lock) {
                    if (this.#debug) {
                        console.log(`${this.#method} ${this.#hosts[i]}${this.#uri}`)
                    }
                    // Start request, lock
                    lock = true
                    // Conventional AJAX request using native JavaScript
                    let xmlhttp = XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP")
                    xmlhttp.open(this.#method, this.#hosts[i] + this.#uri, true)
                    xmlhttp.setRequestHeader("Accept", "application/json")
                    if (this.#method === POST) {
                        xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
                    }
                    xmlhttp.send(body)
                    xmlhttp.onreadystatechange = () => {
                        // When received response (or timeout)
                        if (xmlhttp.readyState === 4) {
                            // Received status 200
                            if (xmlhttp.status === 200) {
                                // The request succeed
                                this.#successHandler(xmlhttp.responseText)
                                succeed = true
                            } else {
                                // The request failed, but only record status
                                status = xmlhttp.status
                                lock = false
                                // Add one to i for traverse hosts
                                i++
                            }
                        }
                    }
                }
            } else {
                // If the request does not succeed, handle failure
                if (!succeed) {
                    this.#failureHandler(status)
                }
                // No matter it ends of success or traverse, stop the interval
                clearInterval(interval)
            }
        }, 1)
    }

}
