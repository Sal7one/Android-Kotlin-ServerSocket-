package com.sal7one.serversocket


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.net.Socket

class AppViewModel : ViewModel() {

    private var _connectionStatus = MutableStateFlow(false)
    val connectionStatus = _connectionStatus

    val messageToSocket = Channel<String>()

    fun openSocketConnection() = viewModelScope.launch(Dispatchers.IO) {
        _connectionStatus.value = !_connectionStatus.value

        while (true) {
            try {
                val socket = Socket(hostAddress, portAddress)
                val dos = DataOutputStream(socket.getOutputStream())

                if(_connectionStatus.value){
                    socket.soTimeout = 4500
                    dos.writeUTF(messageToSocket.receive())
                }
                dos.flush()
                dos.close()
                socket.close()
            } catch (e: Exception) {
                _connectionStatus.value = false
                Log.e("AppViewModel", "$e")
            }
        }
    }

    companion object {
        const val hostAddress = "192.168.8.146"
        const val portAddress = 4999
    }
}