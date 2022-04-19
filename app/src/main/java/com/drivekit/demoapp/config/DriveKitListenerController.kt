package com.drivekit.demoapp.config

import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError
import java.lang.ref.WeakReference

object DriveKitListenerController : DriveKitListener {

    private val listeners: MutableList<WeakReference<DriveKitListener>> = mutableListOf()

    override fun onAuthenticationError(errorType: RequestError) {
        for (listener in listeners) {
            listener.get()?.onAuthenticationError(errorType)
        }
    }

    override fun onConnected() {
        for (listener in listeners) {
            listener.get()?.onConnected()
        }
    }

    override fun onDisconnected() {
        for (listener in listeners) {
            listener.get()?.onDisconnected()
        }
    }

    fun registerListener(listener: DriveKitListener) {
        listeners.add(WeakReference(listener))
    }

    fun unregisterListener(listener: DriveKitListener) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.let {
                if (it === listener) {
                    iterator.remove()
                }
            }
        }
    }

    fun reset() {
        listeners.clear()
    }
}