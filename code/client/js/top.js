// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines the function for getting back to top.

/**
 * Get back to the top of the window
 */
function backToTop() {
    let scrollTimer = -1
    let height = document.documentElement.scrollTop || document.body.scrollTop
    if (scrollTimer === -1) {
        scrollTimer = setInterval(() => {
            height -= height / 5
            if (height <= 1) {
                height = 0
                clearInterval(scrollTimer)
                scrollTimer = -1
            }
            window.scrollTo(0, height)
        }, 10)
    }
}
