// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file provides functions for handling cart related actions.

/**
 * Add 1 amount for adding to cart
 *
 * @param isbn Book ISBN for getting the element
 * @param max Maximum amount, is actually book stock
 */
function addAmount(isbn, max) {
    let amount = document.getElementById(`book-amount-${isbn}`)
    if (amount.value < 0) {
        // When the amount is less than 0, set as 1
        amount.value = 1
    } else if (amount.value < max) {
        // When the amount is in (0, max), add 1
        amount.value++
    } else if (amount.value > max) {
        // When the amount is more than max, set as max
        amount.value = max
    }
}


/**
 * Minus 1 amount for adding to cart
 *
 * @param isbn Book ISBN for getting the element
 * @param max Maximum amount, is actually book stock
 */
function minusAmount(isbn, max) {
    let amount = document.getElementById(`book-amount-${isbn}`)
    if (amount.value < 0) {
        // When the amount is less than 0, set as 1
        amount.value = 1
    } else if (amount.value > max) {
        // When the amount is more than max, set as max
        amount.value = max
    } else if (amount.value > 1) {
        // When the amount is in (1, max], minus 1
        amount.value--
    }
}


/**
 * Add book to cart
 *
 * @param isbn Book ISBN for finding amount input and build AJAX request
 * @param title Book title
 * @param stock Book stock
 */
function addToCart(isbn, title, stock) {
    // Check cookie to validate login
    if (cookie !== null) {
        let amount = document.getElementById(`book-amount-${isbn}`).value
        // The amount should be in (0, stock)
        if (amount < 1) {
            snackbar.show(`You should at least add one book to cart`)
        } else if (amount > stock) {
            snackbar.show(`This book does not have enough stock to purchase`)
        } else {
            // Send POST AJAX request
            new AjaxRequest(POST, cartHosts, `CartService/carts/${cookie.tel}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            if (respObj.result) {
                                snackbar.show(`Added <i>${title}</i> to cart successfully`, `SHOW`, getCart)
                            } else {
                                snackbar.show(`Failed to add <i>${title}</i> to cart: book already exists`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to add <i>${title}</i> to cart: ${info}`)
                    },
                )
                .request(`passwd=${cookie.passwd}&isbn=${isbn}&amount=${amount}`)
        }
    } else {
        snackbar.show(`You have not sign in, please sign in!`)
    }
}


/**
 * Get cart information of user
 */
function getCart(alert = true) {

    let amountSum = 0, priceSum = 0
    let background = document.getElementById("window-background")
    let cartWindow = document.getElementById("cart-window")

    // Send GET AJAX request
    new AjaxRequest(GET, cartHosts, `CartService/carts/${cookie.tel}?passwd=${cookie.passwd}`)
        .bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    if (respObj.result) {
                        // When the books segment does not exist in response data
                        if (respObj.books === undefined) {
                            // Close the cart window
                            closeWindow()
                            if (alert) {
                                snackbar.show(`There is nothing in your cart`)
                            }
                        } else {
                            // Otherwise, sort and show cart window
                            respObj.books.sort((book1, book2) => {
                                let title1 = book1.title, title2 = book2.title
                                if (title1 > title2) {
                                    return 1
                                } else if (title1 < title2) {
                                    return -1
                                } else {
                                    return 0
                                }
                            })
                            displayCarts(respObj)
                        }
                    } else {
                        snackbar.show(`Failed to get cart`)
                    }
                } catch (e) {
                    snackbar.show(`Failed to parse response text: ${e}`)
                }
            },
            (stat) => {
                let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                snackbar.show(`Failed to get cart: ${info}`)
            },
        )
        .request()

    /**
     * Display cart window
     *
     * @param respObj
     */
    function displayCarts(respObj) {
        let list = document.getElementById("cart-list")
        // Headers in table
        list.innerHTML = `<tr><th>Title</th><th>Price</th><th>Amount</th><th>Operations</th></tr>`
        // Traverse book list in response data
        for (const book of respObj.books) {
            // Calculate the sum of price and amount
            priceSum += book.price * book.amount
            amountSum += book.amount
            // Assemble elements in table
            list.innerHTML += `
<tr>
<td style="width: 100%"><a>${book.title}</a></td>
<td><a>$${book.price.toFixed(2)}</a></td>
<td>
<input id="cart-amount-input-${book.isbn}" style="display: none" type="number" value="${book.amount}">
<label for="cart-amount-input-${book.isbn}" hidden></label>
<a id="cart-amount-span-${book.isbn}" style="display: block">${book.amount}</a>
</td>
<td>
<span class="material-icons" id="cart-amount-submit-${book.isbn}" style="display: none">check</span>
<span class="material-icons" id="cart-amount-cancel-${book.isbn}" style="display: none">close</span>
<span class="material-icons" id="cart-amount-modify-${book.isbn}" onclick="modifyAmount(\`${book.isbn}\`, ${book.amount})">edit</span>
<span class="material-icons" onclick="deleteCart(\`${book.isbn}\`, \`${book.title}\`)">delete</span>
</td>
</tr>
        `
        }
        // List the summary in the last line
        list.innerHTML += `
<tr>
<td><b>Summary</b></td>
<td><b>$${priceSum.toFixed(2)}</b></td>
<td><b>${amountSum}</b></td>
<td><b>-</b></td>
</tr>
`
        // Display and adjust position
        // IMPORTANT: Must display after setting innerHTML, otherwise, offset of this window will occur
        background.style.display = "inherit"
        cartWindow.style.display = "inherit"
        cartWindow.style.marginTop = `${(window.innerHeight - cartWindow.offsetHeight) / 2}px`
        // Set onclick event on clean cart button for removing all the books in cart
        document.getElementById("cart-clean").onclick = () => {
            // Send DELETE AJAX request
            new AjaxRequest(DELETE, cartHosts, `CartService/carts/${cookie.tel}?passwd=${cookie.passwd}`)
                .bind(
                    (resp) => {
                        try {
                            let respObj = JSON.parse(resp)
                            // When the cart has been cleaned successfully
                            if (respObj.result) {
                                // Close cart window
                                closeWindow()
                                snackbar.show(`Cart has been cleaned successfully`)
                            } else {
                                snackbar.show(`Failed clean cart`)
                            }
                        } catch (e) {
                            snackbar.show(`Failed to parse response text: ${e}`)
                        }
                    },
                    (stat) => {
                        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                        snackbar.show(`Failed to add to cart: ${info}`)
                    }
                )
                .request()
        }
        // Set onclick event on submit button for generating transaction
        document.getElementById("cart-submit").onclick = () => addTxn(priceSum)
        // Set onclick event on cancel button for closing the cart window
        document.getElementById("cart-cancel").onclick = closeWindow
        // Set onkeydown event
        window.onkeydown = (e) => {
            if (e.key === "Escape") {
                // When ESC id pressed, close the window
                closeWindow()
            }
        }
    }

    /**
     * Close cart window
     */
    function closeWindow() {
        cartWindow.style.display = "none"
        background.style.display = "none"
    }

}


/**
 * Modify book amount in cart
 *
 * @param isbn Book ISBN for finding amount input
 * @param amount Book current amount
 */
function modifyAmount(isbn, amount) {

    // Initialize elements
    let span = document.getElementById(`cart-amount-span-${isbn}`)
    let input = document.getElementById(`cart-amount-input-${isbn}`)
    let submit = document.getElementById(`cart-amount-submit-${isbn}`)
    let cancel = document.getElementById(`cart-amount-cancel-${isbn}`)
    let modify = document.getElementById(`cart-amount-modify-${isbn}`)

    // Display some of the elements and hide others of them
    span.style.display = "none"
    input.style.display = "block"
    submit.style.display = "inline"
    cancel.style.display = "inline"
    modify.style.display = "none"

    // Set default value as current amount, then focus and select all
    input.value = amount
    input.focus(input.select())

    // Set onclick event on the cancel button
    cancel.onclick = close

    /**
     * Cancel modification, display some of the elements and hide others of them
     */
    function close() {
        span.style.display = "block"
        input.style.display = "none"
        submit.style.display = "none"
        cancel.style.display = "none"
        modify.style.display = "inline"
    }

    // Set onclick event on submit button
    submit.onclick = () => {
        // Check input value is nonempty and greater than 0
        if (input.value === "") {
            snackbar.show(`Please enter the amount to modify`)
        } else if (input.value < 1) {
            snackbar.show(`The new amount should be greater than 0`)
        } else {
            // Send PUT AJAX request
            new AjaxRequest(PUT, cartHosts, `CartService/carts/${cookie.tel}/${isbn}?passwd=${cookie.passwd}&amount=${input.value}`)
                .bindSuccess((resp) => {
                    try {
                        let respObj = JSON.parse(resp)
                        // When the result in response data is true
                        if (respObj.result) {
                            // Refresh cart and show information
                            getCart()
                            snackbar.show(`Amount has been modified successfully`)
                        } else {
                            // Cancel modification and show information
                            close()
                            snackbar.show(`Failed to modify amount`)
                        }
                    } catch (e) {
                        snackbar.show(`Failed to parse response text: ${e}`)
                    }
                })
                .bindFailure((stat) => {
                    let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                    snackbar.show(`Failed to modify amount: ${info}`)
                })
                .request()
        }
    }

}

/**
 * Delete specific book from cart
 *
 * @param isbn Book ISBN for sending AJAX request
 * @param title Book title
 */
function deleteCart(isbn, title) {
    // Send PUT AJAX request
    // In fact, it should use DELETE method, but it has not be implemented in Servlet:
    // new AjaxRequest(DELETE, cartHosts, `CartService/carts/${cookie.tel}/${isbn}?passwd=${cookie.passwd}`)
    new AjaxRequest(PUT, cartHosts, `CartService/carts/${cookie.tel}/${isbn}?passwd=${cookie.passwd}&amount=0`)
        .bind(
            (resp) => {
                try {
                    let respObj = JSON.parse(resp)
                    // When deletion succeed
                    if (respObj.result) {
                        // Call getCart() to refresh the cart window
                        getCart()
                        snackbar.show(`Book <i>${title}</i> has been removed from cart successfully`)
                    } else {
                        snackbar.show(`Failed to remove book <i>${title}</i> from cart`)
                    }
                } catch (e) {
                    snackbar.show(`Failed to parse response text: ${e}`)
                }
            },
            (stat) => {
                let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
                snackbar.show(`Failed to remove book <i>${title}</i> from cart: ${info}`)
            }
        )
        .request()
}
