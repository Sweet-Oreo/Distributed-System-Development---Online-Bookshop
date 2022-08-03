// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines some constants and global variables, and set onload and onscroll events for window.

// HTTP Methods
const GET = "GET"
const POST = "POST"
const PUT = "PUT"
const DELETE = "DELETE"

// Host Distribution Service
const HDS = ["http://192.168.43.37:8080/"]
// Request for service hosts every 1 minute
const DHSTimeout = 1000 * 60 * 3

// Available hosts for services, foregoing hosts has higher priority when sending AJAX request
let userHosts = ["http://192.168.43.218:8080/"]
let bookHosts = ["http://192.168.43.22:8080/"]
let cartHosts = ["http://192.168.43.22:8080/"]
let txnHosts = ["http://192.168.43.22:8080/"]

// Snackbar object, see also in snackbar.js
let snackbar

let page = 1
let cookie = null

// Set onload event for window
window.onload = () => {

    // Initialize snackbar
    let sBar = document.getElementById("snackbar")
    let sInfo = document.getElementById("snackbar-info")
    let sBtn = document.getElementById("snackbar-btn")
    snackbar = new Snackbar(sBar, sInfo, sBtn)

    // Initialize back-to-top button
    let backToTop = document.getElementById("back-to-top")
    backToTop.style.bottom = `-${backToTop.offsetHeight}px`

    // Check if have signed in by reading document.cookie, see details in sign.js
    setSignedIn(document.getElementById("sign-operations"))

    // Update service hosts by calling from services, if failed to update, will use default values
    updateHosts(true)
    // Update service hosts
    setInterval(updateHosts, DHSTimeout)

    /**
     * Update service hosts
     *
     * @param isFirstTime The first time it will call search() function
     */
    function updateHosts(isFirstTime = false) {
        // Send GET AJAX request
        new AjaxRequest(GET, HDS, `HostService/hosts`).bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    bookHosts = respObj.BookHosts
                    userHosts = respObj.UserHosts
                    cartHosts = respObj.CartHosts
                    txnHosts = respObj.TxnHosts
                } catch (ignore) {
                }
                if (isFirstTime) {
                    search()
                }
            },
            () => {
                if (isFirstTime) {
                    search()
                }
            }
        ).request()
    }

}


// Set onscroll event for window
window.onscroll = () => {
    let backToTop = document.getElementById("back-to-top")
    let height = document.documentElement.scrollTop || document.body.scrollTop
    // When scrolled 200 from top, show the back-to-top button, otherwise hide it
    if (height > 200) {
        backToTop.style.bottom = "3.6rem"
    } else {
        backToTop.style.bottom = -backToTop.offsetHeight + "px"
    }
}
