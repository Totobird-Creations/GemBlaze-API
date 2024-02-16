package net.totobirdcreations.gemblazeapi.util


class RequestableValue<T : Any> internal constructor(
    private var value     : T?      = null,
    private val requester : () -> T
) {

    private val isKnown : Boolean
        get() {return this.value != null;};


    fun getOrNull() : T? {
        return if (this.isKnown) {this.value} else {null};
    }

    @Throws(RequestFulfillmentFailedException::class)
    fun getOrRequest() : T {
        if (! this.isKnown) {
            this.value = this.requester();
        }
        return this.value!!;
    }


    internal fun put(value : T) {
        this.value = value;
    }

    internal fun putOrNull(value : T?) {
        this.value = value;
    }

    internal fun putNull() {
        this.value = null;
    }


    override fun toString() : String {
        return this.value.toString();
    }

    override fun equals(other : Any?) : Boolean {
        if (! this.isKnown) {
            return false;
        }
        if (other is RequestableValue<*>) {
            return other.isKnown && this.value == other.value;
        }
        return this.value == other;
    }

    override fun hashCode() : Int {
        return value?.hashCode() ?: 0
    }

}


abstract class RequestFulfillmentFailedException : Exception();
class RequestImpossibleToFulfillException : RequestFulfillmentFailedException();
class RequestFailedToBeFulfilledException : RequestFulfillmentFailedException();
