// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines the search function for finding books by keyword and page.

/**
 * Search book by keyword and page, then display
 *
 * @param refresh Clear old search results or append to them
 */
function search(refresh) {

    // Get keyword and remove the spaces in front and end
    let keyword = document.getElementById("searchbar").value.trim()

    // Send GET AJAX request
    new AjaxRequest(GET, bookHosts, `BookService/books?keyword=${keyword}&page=${page}`)
        .bind((resp) => parseAndDisplay(resp), (stat) => alertStatus(stat))
        .request()

    /**
     * Parse response text and display results
     *
     * @param resp AJAX response text
     */
    function parseAndDisplay(resp) {
        let info = keyword === "" ? "" : ` for "${keyword}"`
        let cardList = document.getElementById("card-list")
        let cardMore = document.getElementById("card-more")
        try {
            let respObj = JSON.parse(resp)
            // When there is no book segment in response JSON text
            if (respObj.books === undefined) {
                // Hide the more-result button
                cardMore.style.display = "none"
                // When needs to clean results
                if (refresh === true) {
                    // Reset innerHTML as no result
                    cardList.innerHTML = `<div class="card-error">No result found${info}</div>`
                } else {
                    // Otherwise, tell user that all the result has been listed
                    snackbar.show(`All results${info} has been listed`)
                }
            } else {
                // When needs to clean results
                if (refresh === true) {
                    // Set innerHTML as empty
                    cardList.innerHTML = ``
                }
                // Traverse the book in response data
                for (const book of respObj.books) {
                    let authors = ``
                    // Traverse the authors to assemble the string
                    for (let i = 0; i < book.authors.length; i++) {
                        authors += `${book.authors[i]}`
                        if (i < book.authors.length - 1) {
                            authors += ", "
                        }
                    }
                    // Append a card with information
                    cardList.innerHTML += `<div class="card">
<div class="card-left">
<p class="card-title">${book.title}</p>
<p class="card-author">Written by ${authors}</p>
<p class="card-publisher">Published by ${book.publisher}</p>
<p class="card-price">$${book.price.toFixed(2)}</p>
</div>
<div class="card-right">
<div>
<a style="border-radius: 0.3rem 0 0 0.3rem" onclick="minusAmount(${book.isbn}, ${book.stock})">&minus;</a>
<label for="book-amount-${book.isbn}"></label>
<input type="number" min="1" step="1" value="1" max="${book.stock}" id="book-amount-${book.isbn}">
<a style="border-radius: 0 0.3rem 0.3rem 0" onclick="addAmount(${book.isbn}, ${book.stock})">&plus;</a>
<p>stock: ${book.stock}</p>
</div>
<div>
<button onclick="addToCart(\`${book.isbn}\`, \`${book.title}\`, ${book.stock})">ADD TO CART</button>
</div>
</div>
</div>`
                }
                // Server returns 10 books, if received less than 10 books, it will not have "next page"
                if (respObj.books.length < 10) {
                    // Only when the page is greater than 1
                    if (page > 1) {
                        // Tell user that all the results has been listed
                        snackbar.show(`All results${info} has been listed`)
                    }
                    // Anyway, hide the more-results buton
                    cardMore.style.display = "none"
                } else {
                    cardMore.style.display = "inherit"
                }
            }
        } catch (e) {
            snackbar.show(`Failed to parse response text: ${e}`)
        }
        // Make sure that footer is on the bottom of the page
        setFooter()
    }

    /**
     * Display error information
     *
     * @param stat
     */
    function alertStatus(stat) {
        let info = stat === 0 ? `request timeout` : `HTTP status ${stat}`
        document.getElementById("card-list").innerHTML = `<div class="card-error">Failed to get books: ${info}</div>`
        snackbar.show(`Failed to get books: ${info}`)
        // Make sure that footer is on the bottom of the page
        setFooter()
    }

}
