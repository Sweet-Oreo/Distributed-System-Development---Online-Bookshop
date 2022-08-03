// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines the modify function for modifying user information.

/**
 * Modify user information depending on target
 *
 * @param target Information to be modified: card, address, or password
 */
function modify(target) {

    // Initialize the popup-window for modifying information
    let elementId = `modify-${target}`
    let background = document.getElementById("window-background")
    background.style.display = "inherit"
    let modifyWindow = document.getElementById(`${elementId}-window`)
    modifyWindow.style.display = "inherit"
    modifyWindow.style.marginTop = `${(window.innerHeight - modifyWindow.offsetHeight) / 2}px`

    // Display current value of the specified information
    let current = document.getElementById(`current-${target}`)
    if (current !== null) {
        if (target === "card") {
            current.innerText = `(Your current card number is ${cookie.card})`
        } else if (target === "addr") {
            current.innerText = `(Your current address is ${cookie.addr})`
        } else {
            current.innerText = ""
        }
    }

    // Set onclick event on submit button
    document.getElementById(`${elementId}-submit`).onclick = () => handleModify(target)

    // Set onclick event on cancel button
    document.getElementById(`${elementId}-cancel`).onclick = closeWindow

    // Set onkeydown event on window
    window.onkeydown = (e) => {
        if (e.key === "Escape") {
            // Close modify window when pressed ESC
            closeWindow()
        } else if (e.key === "Enter") {
            // Submit form when pressed Enter
            handleModify(target)
        }
    }

    /**
     * Submit modified information according to the target
     *
     * @param target Information to be modified: card, address, or password
     */
    function handleModify(target) {
        // According to the value of target, do different actions
        if (target === "card") {
            modifyCard()
        } else if (target === "addr") {
            modifyAddr()
        } else if (target === "passwd") {
            modifyPasswd()
        } else {
            snackbar.show(`THIS SHOULD NOT HAPPEN!!!`)
        }
    }

    /**
     * Close the modify window
     */
    function closeWindow() {
        modifyWindow.style.display = "none"
        background.style.display = "none"
    }

    /**
     * Modify card information
     */
    function modifyCard() {
        let card = document.getElementById(elementId)
        // Check input is not empty and different
        if (card.value === "") {
            snackbar.show("Please enter new card")
        } else if (card.value === cookie.card) {
            snackbar.show("The cards should be different")
        } else {
            // Send PUT AJAX request
            new AjaxRequest(PUT, userHosts, `UserService/users/${cookie.tel}?passwd=${cookie.passwd}&new_card=${card.value}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the update operation succeed
                            if (respObj.result) {
                                // Close the modify window and update cookie
                                closeWindow()
                                cookie.card = card.value
                                document.cookie = `BookStore={"name":"${cookie.name}","tel":"${cookie.tel}","passwd":"${cookie.passwd}","card":"${card.value}","addr":"${cookie.addr}","consumption":${cookie.consumption}}`
                                snackbar.show(`Your card has been modified to ${card.value}`)
                                card.value = ""
                            } else {
                                snackbar.show(`Failed to update card information`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to modify card information: ${info}`)
                    },
                )
                .request()
        }
    }

    /**
     * Modify address information
     */
    function modifyAddr() {
        let addr = document.getElementById(elementId)
        // Check input is not empty and different
        if (addr.value === "") {
            snackbar.show("Please enter new address")
        } else if (addr.value === cookie.addr) {
            snackbar.show("The addresses should be different")
        } else {
            // Send PUT AJAX request
            new AjaxRequest(PUT, userHosts, `UserService/users/${cookie.tel}?passwd=${cookie.passwd}&new_addr=${addr.value}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the update operation succeed
                            if (respObj.result) {
                                // Close the modify window and update cookie
                                closeWindow()
                                cookie.addr = addr.value
                                document.cookie = `BookStore={"name":"${cookie.name}","tel":"${cookie.tel}","passwd":"${cookie.passwd}","card":"${cookie.card}","addr":"${addr.value}","consumption":${cookie.consumption}}`
                                snackbar.show(`Your address has been modified to ${addr.value}`)
                                addr.value = ""
                            } else {
                                snackbar.show(`Failed to update address information`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to modify address information: ${info}`)
                    }
                )
                .request()
        }
    }

    /**
     * Modify password
     */
    function modifyPasswd() {
        let oldPasswd = document.getElementById(`${elementId}-old`)
        let passwd = document.getElementById(elementId)
        let confirm = document.getElementById(`${elementId}-confirm`)
        // Check input
        // Old password should be nonempty and correct
        // New password should be nonempty and different to the old one
        // Confirm password should be nonempty and the same as the new one
        if (oldPasswd.value === "") {
            snackbar.show("Please enter old password")
        } else if (md5(oldPasswd.value) !== cookie.passwd) {
            snackbar.show("The old password is wrong")
        } else if (passwd.value === "") {
            snackbar.show("Please enter password")
        } else if (md5(passwd.value) === cookie.passwd) {
            snackbar.show("The new password should be different to the old one")
        } else if (confirm.value === "") {
            snackbar.show("Please confirm password")
        } else if (passwd.value !== confirm.value) {
            snackbar.show("The two passwords are different")
        } else {
            // Send PUT AJAX request
            new AjaxRequest(PUT, userHosts, `UserService/users/${cookie.tel}?passwd=${cookie.passwd}&new_passwd=${md5(passwd.value)}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the update operation succeed
                            if (respObj.result) {
                                // Close the modify window and update cookie
                                closeWindow()
                                cookie.passwd = md5(passwd.value)
                                document.cookie = `BookStore={"name":"${cookie.name}","tel":"${cookie.tel}","passwd":"${md5(passwd.value)}","card":"${cookie.card}","addr":"${cookie.addr}","consumption":${cookie.consumption}}`
                                snackbar.show(`Your password has been modified successfully`)
                                oldPasswd.value = ""
                                passwd.value = ""
                                confirm.value = ""
                            } else {
                                snackbar.show(`Failed to modify password`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to modify password: ${info}`)
                    },
                )
                .request()
        }
    }

}
