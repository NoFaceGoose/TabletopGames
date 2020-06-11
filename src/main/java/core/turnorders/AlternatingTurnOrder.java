package core.turnorders;

import core.AbstractGameState;

public class AlternatingTurnOrder extends TurnOrder {
    protected int direction;

    public AlternatingTurnOrder(int nPlayers){
        super(nPlayers);
        direction = 1;
    }

    @Override
    protected TurnOrder _copy() {
        AlternatingTurnOrder to = new AlternatingTurnOrder(nPlayers);
        to.direction = direction;
        return to;
    }

    @Override
    protected void _reset() {
        direction = 1;
    }

    @Override
    public int nextPlayer(AbstractGameState gameState) {
        return (nPlayers + turnOwner + direction) % nPlayers;
    }

    public void reverse(){
        direction *= -1;
    }
}