// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file sets the position of the footer according to the size of result list.

/**
 * Make sure footer is on the bottom of the page by detecting the height of window, main, and footer
 */
function setFooter() {
    let main = document.getElementById("main")
    let footer = document.getElementById("footer")
    if (main.offsetHeight + footer.scrollHeight > window.innerHeight) {
        footer.style.position = "relative"
    } else {
        footer.style.position = "absolute"
    }
}
