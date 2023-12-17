package net.totobirdcreations.gemblazeapi.util


class Event<Callback>(triggerHandler : (Collection<Callback>) -> Callback) {

    private val callbacks : ArrayList<Callback> = arrayListOf();

    internal val trigger = triggerHandler(this.callbacks);

    fun register(callback : Callback) : Event<Callback> {
        this.callbacks.add(callback);
        return this;
    }

}