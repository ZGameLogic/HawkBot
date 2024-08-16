package com.zgamelogic.controllers;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class GeneralController {
    @PostConstruct
    public void init() {
        log.info("Well played gentlemen");
    }
}
