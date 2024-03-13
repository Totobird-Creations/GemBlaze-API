package net.totobirdcreations.gemblaze.util.value

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class ExpirableValue<T : Any> internal constructor(val expirationTime : Duration) {
    internal constructor(expirationTime : Int) : this(expirationTime.milliseconds);

    private lateinit var value  : T;
    private          var update : Instant = Instant.fromEpochMilliseconds(0);

    internal fun put(value : T) {
        this.value  = value;
        this.update = Clock.System.now();
    }

    internal fun renew() {
        this.update = Clock.System.now();
    }

    internal fun invalidate() {
        this.update = Instant.fromEpochMilliseconds(0);
    }

    fun isInvalid() : Boolean {
        return (! this::value.isInitialized) || (Clock.System.now() - this.update).absoluteValue >= expirationTime;
    }

    fun getOrDefault(fallback : T) : T {
        return if (this.isInvalid()) {fallback} else {this.value};
    }

    fun getOrElse(fallback : () -> T) : T {
        return if (this.isInvalid()) {fallback()} else {this.value};
    }

    fun getOrNull() : T? {
        return if (this.isInvalid()) {null} else {this.value};
    }


    override fun toString() : String {
        return if (this.isInvalid()) {"null"} else {this.value.toString()};
    }

    override fun equals(other : Any?) : Boolean {
        if (this.isInvalid()) {
            return false;
        }
        if (other is ExpirableValue<*>) {
            return (! other.isInvalid()) && this.value == other.value;
        }
        return this.value == other;
    }

    override fun hashCode() : Int {
        return value.hashCode();
    }

}