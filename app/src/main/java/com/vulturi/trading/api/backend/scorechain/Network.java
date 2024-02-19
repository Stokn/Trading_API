package com.vulturi.trading.api.backend.scorechain;

public enum Network {
    ETH(Blockchain.ETHEREUM),BTC(Blockchain.BITCOIN);
    private Blockchain blockchain;

    Network( Blockchain blockchain) {
        this.blockchain = blockchain;
    }


    public Blockchain getBlockchain() {
        return blockchain;
    }
}
