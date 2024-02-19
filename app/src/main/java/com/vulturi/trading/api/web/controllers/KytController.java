package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.services.kyt.KytService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/kyt")
public class KytController {

    @Autowired
    private KytService kytService;



}
