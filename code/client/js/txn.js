// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines functions to operate transactions.

/**
 * Generate transaction
 *
 * @param amount
 */
function addTxn(amount) {
    // Send POST AJAX request
    new AjaxRequest(POST, txnHosts, `TransactionService/txns/${cookie.tel}`)
        .bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    // When the result in response data is true
                    if (respObj.result) {
                        // Refresh cart
                        getCart(false)
                        // Update cookie and consumption
                        cookie.consumption += amount
                        document.cookie = `BookStore={"name":"${cookie.name}","tel":"${cookie.tel}","passwd":"${cookie.passwd}","card":"${cookie.card}","addr":"${cookie.addr}","consumption":${cookie.consumption.toFixed(2)}}`
                        document.getElementById("consumption").innerHTML = `You have consumed $${cookie.consumption.toFixed(2)}`
                        snackbar.show(`Generated transaction successfully`, `SHOW`, getTxn)
                    } else {
                        snackbar.show(`Failed to purchase`)
                    }
                } catch (e) {
                    snackbar.show(`Failed to parse response text: ${e}`)
                }
            },
            (stat) => {
                let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                snackbar.show(`Failed to purchase: ${info}`)
            },
        )
        .request(`passwd=${cookie.passwd}`)
}


/**
 * Get all the transactions
 */
function getTxn() {

    // Initialize elements
    let background = document.getElementById("window-background")
    let txnWindow = document.getElementById("txn-window")

    // Send GET AJAX request
    new AjaxRequest(GET, txnHosts, `TransactionService/txns/${cookie.tel}?passwd=${cookie.passwd}`)
        .bindSuccess((resp) => {
            try {
                let respObj = JSON.parse(resp)
                // When the result in response data is true
                if (respObj.result) {
                    // Check if user have transaction
                    if (respObj.transaction === undefined) {
                        // Transaction not exists, close the transaction window and show information
                        closeWindow()
                        snackbar.show(`You have no transaction for now`)
                    } else {
                        // Sort by transaction status
                        respObj.transaction.sort((txn1, txn2) => {
                            let status1 = txn1.status, status2 = txn2.status
                            if (status1 < status2) {
                                return -1
                            } else if (status1 > status2) {
                                return 1
                            } else {
                                return 0
                            }
                        })
                        // Show transaction window
                        displayTxns(respObj)
                    }
                } else {
                    snackbar.show(`Failed to get transactions`)
                }
            } catch (e) {
                snackbar.show(`Failed to parse response text: ${e}`)
            }
        })
        .bindFailure((stat) => {
            let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
            snackbar.show(`Failed to get transactions: ${info}`)
        })
        .request()

    /**
     * Show transaction window
     *
     * @param respObj Response object
     */
    function displayTxns(respObj) {

        // Initialize element and table headers
        let list = document.getElementById("txn-list")
        list.innerHTML = `<tr><th>Time</th><th>Books</th><th>Amount</th><th>Status</th><th>Operation</th></tr>`

        // Traverse the transactions to assemble the contents
        for (const txn of respObj.transaction) {
            let bookList = ``
            for (let i = 0; i < txn.books.length; i++) {
                bookList += `${txn.books[i].title} (${txn.books[i].amount})`
                if (i < txn.books.length) {
                    bookList += `<br>`
                }
            }
            let ops = txn.status === 0 ? `<span class="material-icons" onclick="cancelTxn(\`${txn.id}\`, ${txn.amount})">undo</span><span class="material-icons" onclick="ackTxn(\`${txn.id}\`)">check</span>` : `-`
            list.innerHTML += `<tr>
<td>${txn.time.split(".")[0]}</td>
<td>${bookList}</td>
<td>$${txn.amount.toFixed(2)}</td>
<td>${statusToText(txn.status)}</td>
<td>${ops}</td>
</tr>`
        }

        // Display and adjust position
        // IMPORTANT: Must display after setting innerHTML, otherwise, offset of this window will occur
        background.style.display = "inherit"
        txnWindow.style.display = "inherit"
        txnWindow.style.marginTop = `${(window.innerHeight - txnWindow.offsetHeight) / 2}px`

        // Set onclick event on cancel button for closing the transaction window
        document.getElementById("txn-cancel").onclick = closeWindow

        // Set onkeydown event
        window.onkeydown = (e) => {
            if (e.key === "Escape") {
                // When ESC id pressed, close the window
                closeWindow()
            }
        }

        /**
         * Convert status (int) to its representation text (string)
         * 0 for item on delivering, 1 for item receipt, 2 for item returned
         *
         * @param status Transaction status
         * @returns {string}
         */
        function statusToText(status) {
            switch (status) {
                case 0:
                    return "Delivering"
                case 1:
                    return "Received"
                case 2:
                    return "Returned"
                default:
                    return "Unknown status"
            }
        }

    }

    /**
     * Close the transaction window
     */
    function closeWindow() {
        txnWindow.style.display = "none"
        background.style.display = "none"
    }

}


/**
 * Cancel transaction (return items)
 *
 * @param txnId Transaction ID
 * @param amount Transaction amount of price
 */
function cancelTxn(txnId, amount) {
    // Send PUT AJAX request
    new AjaxRequest(PUT, txnHosts, `TransactionService/txns/${cookie.tel}/${txnId}?passwd=${cookie.passwd}&status=2`)
        .bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    // When the result in response data is true
                    if (respObj.result) {
                        // Refresh transactions
                        getTxn()
                        // Update consumption and cookie
                        cookie.consumption -= amount
                        document.cookie = `BookStore={"name":"${cookie.name}","tel":"${cookie.tel}","passwd":"${cookie.passwd}","card":"${cookie.card}","addr":"${cookie.addr}","consumption":${cookie.consumption.toFixed(2)}}`
                        document.getElementById("consumption").innerHTML = `You have consumed $${cookie.consumption.toFixed(2)}`
                        snackbar.show(`Item has been returned successfully`)
                    } else {
                        snackbar.show(`Failed to return item`)
                    }
                } catch (e) {
                    snackbar.show(`Failed to parse response text: ${e}`)
                }
            },
            (stat) => {
                let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                snackbar.show(`Failed to return item: ${info}`)
            }
        )
        .request()
}


/**
 * Confirm that the items are received
 *
 * @param txnId Transaction ID
 */
function ackTxn(txnId) {
    // Send PUT AJAX request
    new AjaxRequest(PUT, txnHosts, `TransactionService/txns/${cookie.tel}/${txnId}?passwd=${cookie.passwd}&status=1`)
        .bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    // When the result in response data is true
                    if (respObj.result) {
                        // Refresh transaction
                        getTxn()
                        snackbar.show(`Item has been acknowledged to be received`)
                    } else {
                        snackbar.show(`Failed to acknowledge receipt`)
                    }
                } catch (e) {
                    snackbar.show(`Failed to parse response text: ${e}`)
                }
            },
            (stat) => {
                let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                snackbar.show(`Failed to acknowledge receipt: ${info}`)
            }
        )
        .request()
}
