package com.drivekit.demoapp.manager

import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError
import java.lang.ref.WeakReference

internal object DriveKitListenerManager : DriveKitListener {

    private var listeners: MutableList<WeakReference<DriveKitListener>> = mutableListOf()

    override fun onAuthenticationError(errorType: RequestError) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.onAuthenticationError(errorType)
        }
    }

    override fun onConnected() {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.onConnected()
        }
    }

    override fun onDisconnected() {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.onDisconnected()
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
        listeners = mutableListOf()
    }
}