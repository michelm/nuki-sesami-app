package com.example.nuki_sesami_app.connections

import com.example.nuki_sesami_app.base.ObservableState

abstract class NukiSesamiConnection {
    /** Observable state, will be set to true when connected */
    var connected = ObservableState(false)
        protected set

    /** Observable state, will be set in case of (connection) errors */
    var error = ObservableState("")
        protected set

    /** List of subscribers to message events */
    private val observers = ArrayList<(String, String) -> Unit>()

    /** Closes the connection with the server */
    abstract fun close()

    /** Notifies all observers a new message for a specific topic has arrived */
    protected fun notify(topic: String, message: String) {
        observers.forEach {
            it(topic, message)
        }
    }

    /** Notifies all observers a new value for a specific topic has arrived */
    protected fun notify(topic: String, value: Int) {
        notify(topic, value.toString())
    }

    /** Used by observers so they can be notified when a message has arrived */
    fun subscribe(observer: (String, String) -> Unit) {
        observers.add(observer)
    }

    /** Publishes a message on a topic */
    abstract fun publish(topic: String, value: String)
}