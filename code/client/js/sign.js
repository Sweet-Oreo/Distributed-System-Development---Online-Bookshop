// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines functions for doing sign-related actions.

/**
 * Sign in or sign up according to elementId
 *
 * @param elementId Element of sign-in or sign-up
 */
function sign(elementId) {

    // Initialize the popup-window for signing
    let background = document.getElementById("window-background")
    background.style.display = "inherit"
    let signWindow = document.getElementById(elementId)
    signWindow.style.display = "inherit"
    signWindow.style.marginTop = `${(window.innerHeight - signWindow.offsetHeight) / 2}px`

    // Set onclick event on submit button
    document.getElementById(`${elementId}-submit`).onclick = handleSign

    // Set onclick event on cancel button
    document.getElementById(`${elementId}-cancel`).onclick = closeWindow

    /**
     * According to the element ID, do sign in or sign up
     */
    function handleSign() {
        if (elementId === "sign-up") {
            handleSignUp()
        } else if (elementId === "sign-in") {
            handleSignIn()
        } else {
            snackbar.show(`THIS SHOULD NOT HAPPEN!!!`)
        }
    }

    /**
     * Close the sign window
     */
    function closeWindow() {
        signWindow.style.display = "none"
        background.style.display = "none"
    }

    // Set onkeydown events on window
    window.onkeydown = (e) => {
        if (e.key === "Escape") {
            // Close the sign window when pressed ESC
            closeWindow()
        } else if (e.key === "Enter") {
            // Submit when pressed Enter
            handleSign()
        }
    }

    /**
     * Sign up new user
     */
    function handleSignUp() {
        let tel = document.getElementById(`${elementId}-tel`).value
        let name = document.getElementById(`${elementId}-name`).value
        let passwd = document.getElementById(`${elementId}-passwd`).value
        let confirm = document.getElementById(`${elementId}-confirm`).value
        let card = document.getElementById(`${elementId}-card`).value
        let addr = document.getElementById(`${elementId}-addr`).value
        // Check inputs are not null
        if (tel === "") {
            snackbar.show("Please enter telephone")
        } else if (name === "") {
            snackbar.show("Please enter user name")
        } else if (passwd === "") {
            snackbar.show("Please enter password")
        } else if (confirm === "") {
            snackbar.show("Please confirm password")
        } else if (passwd !== confirm) {
            snackbar.show("The two passwords are different")
        } else if (card === "") {
            snackbar.show("Please enter card number")
        } else if (addr === "") {
            snackbar.show("Please enter address")
        } else {
            // Send POST AJAX request
            new AjaxRequest(POST, userHosts, `UserService/users`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the insert operation succeed
                            if (respObj.result) {
                                // Close the sign window and notice
                                closeWindow()
                                snackbar.show(`Sign up successfully`)
                            } else {
                                snackbar.show(`Failed to sign up: ${respObj.error}`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to sign up: ${info}`)
                    },
                )
                .request(`tel=${tel}&name=${name}&passwd=${md5(passwd)}&card=${card}&addr=${addr}`)
        }
    }

    /**
     * User sign in
     */
    function handleSignIn() {
        let tel = document.getElementById(`${elementId}-tel`)
        let passwd = document.getElementById(`${elementId}-passwd`)
        // Check inputs are nonempty
        if (tel.value === "") {
            snackbar.show("Please enter telephone")
        } else if (passwd.value === "") {
            snackbar.show("Please enter password")
        } else {
            // Send GET AJAX request
            new AjaxRequest(GET, userHosts, `UserService/users/${tel.value}?passwd=${md5(passwd.value)}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the result in response data is true
                            if (respObj.result) {
                                // Close the sign window, set cookie, and change the header in page
                                closeWindow()
                                // Password cached in cookie will by calculated by MD5
                                document.cookie = `BookStore={"name":"${respObj.user.name}","tel":"${tel.value}","passwd":"${md5(passwd.value)}","card":"${respObj.user.card}","addr":"${respObj.user.addr}","consumption":${respObj.user.consumption.toFixed(2)}}`
                                setSignedIn(document.getElementById("sign-operations"))
                                // Clean inputs
                                tel.value = ""
                                passwd.value = ""
                            } else {
                                snackbar.show(`Wrong telephone or password`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to sign up: ${info}`)
                    },
                )
                .request()
        }
    }

}


/**
 * User sign out
 */
function signOut() {
    // Clean and reset cookie
    cookie = null
    document.cookie = `BookStore={};-1,`
    setNotSignedIn(document.getElementById("sign-operations"))
}


/**
 * Check if suer has signed in by parsing cookie
 *
 * @param element
 */
function setSignedIn(element) {
    try {
        // Read cookie
        cookie = JSON.parse(document.cookie.split("BookStore=")[1])
        if (cookie.name !== undefined) {
            // Display user actions
            element.innerHTML = `<li class="user"><a>${cookie.name}</a><span class="user-operations">
<a onclick="getTxn()">Manage transactions</a>
<a onclick="getCart()">Manage Cart</a>
<a onclick="modify('card')">Modify Card</a>
<a onclick="modify('addr')">Modify address</a>
<a onclick="modify('passwd')">Modify password</a>
<a onclick="signOut()">Sign Out</a>
<p class="consumption" id="consumption">You have consumed $${cookie.consumption}</p>
</span></li>`
            return
        }
    } catch (ignore) {
    }
    // If the above action failed, set as not signed in
    setNotSignedIn(element)
}


/**
 * Set header for not signed in
 *
 * @param element
 */
function setNotSignedIn(element) {
    element.innerHTML = `<li><a onclick="sign('sign-up')">Sign Up</a></li><li><a onclick="sign('sign-in')">Sign In</a></li>`
}
