package com.zgamelogic.monitoring

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.CompletableFuture

@Service
class MonitoringService {

    @Async
    fun isOnline(host: String, port: Int, timeoutMillis: Int = 2_000): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMillis)
                true
            }
        } catch (_: Exception) {
            false
        })
    }
}