// Copyright 2021-2022 CNSCC311 Team 6. All rights reserved.
// This file defines the snackbar class.

/**
 * Snackbar is an android widget introduced with the Material Design library.
 * It is designed to replace Toast, which is another popup message widget.
 */
class Snackbar {

    #barElement
    #infoElement
    #btnElement
    #timeout

    /**
     * Constructor for snackbar
     *
     * @param bar The snackbar itself
     * @param info Left part of the snackbar for displaying information
     * @param btn Right part of the snackbar for do some actions
     */
    constructor(bar, info, btn) {
        this.#barElement = bar
        this.#infoElement = info
        this.#btnElement = btn
        // Initialize position
        this.#barElement.style.bottom = `-${this.#barElement.offsetHeight}px`
    }

    /**
     * Display snackbar to show message
     *
     * @param msg Message to display
     * @param btn Button text
     * @param action Button onclick event
     * @param period Period to display in millisecond with default value of 3000
     */
    show(msg, btn = "OK", action = () => {}, period = 3000) {
        clearTimeout(this.#timeout)
        this.#infoElement.innerHTML = msg
        this.#barElement.style.bottom = "0"
        this.#btnElement.innerHTML = btn
        this.#btnElement.onclick = () => {
            action()
            clearTimeout(this.#timeout)
            this.#barElement.style.bottom = `-${this.#barElement.offsetHeight}px`
        }
        this.#barElement.style.display = "inherit"
        this.#timeout = setTimeout(() => {
            this.#barElement.style.bottom = `-${this.#barElement.offsetHeight}px`
        }, period)
    }

}
