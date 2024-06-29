package com.example.nuki_sesami_app

class ObservableState<ValueType>(state: ValueType) {
    private var _state: ValueType = state
    var value: ValueType
        get() = _state
        set(value) {
            val changed = (_state != value)
            _state = value
            if (changed)
                notify(value)
        }

    private val observers = ArrayList<(ValueType) -> Unit>()

    fun subscribe(observer: (ValueType) -> Unit) {
        observers.add(observer)
    }

    private fun notify(value: ValueType) {
        observers.forEach {
            it.invoke(value) // notifies the observer of the changed value
        }
    }
}
