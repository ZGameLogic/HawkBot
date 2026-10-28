package com.zgamelogic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Ben Shabowski
 */
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args);
}