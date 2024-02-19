package com.vulturi.trading.api;

import com.vulturi.trading.api.backend.scorechain.ScoreChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ScoreChainServiceTest {
    @Autowired
    private ScoreChainService scoreChainService;


}
