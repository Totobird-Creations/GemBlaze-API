package net.totobirdcreations.gemblaze.util.value


class RequestableValue<T : Any> internal constructor(
    private var value     : T?,
    private val requester : () -> T?
) {

    internal constructor(requester : () -> T?) : this(null, requester);

    private val isKnown : Boolean
        get() {return this.value != null;};


    fun getOrNull() : T? {
        return this.value;
    }

    @Throws(RequestFulfillmentFailedException::class)
    fun getOrRequest() : T? {
        if (this.value == null) {
            val requested = this.requester()
            if (requested != null) {
                this.value = requested;
            }
            return requested;
        }
        return this.value;
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
