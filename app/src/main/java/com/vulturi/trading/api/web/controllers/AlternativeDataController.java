package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.backend.bitscor.ESGScore;
import com.vulturi.trading.api.services.esg.ESGDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/v1/esg")
public class AlternativeDataController {

    @Autowired
    private ESGDataService esgDataService;

    @CrossOrigin(origins = {"*"})
    @GetMapping("/last/esg-score")
    public ResponseEntity<Collection<ESGScore>> getAll() {
        log.info("Requesting all available esg scores from BitSCOR");
        return ResponseEntity.ok(esgDataService.getAll());
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping("/last/esg-score/{coin}")
    public ResponseEntity<ESGScore> getAll(@PathVariable("coin") String coin) {
        log.info("Requesting esg score for {}", coin);
        return ResponseEntity.ok(esgDataService.get(coin));
    }

}
