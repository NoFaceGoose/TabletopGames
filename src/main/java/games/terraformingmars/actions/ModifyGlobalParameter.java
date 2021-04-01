package games.terraformingmars.actions;

import core.AbstractGameState;
import core.components.Counter;
import games.terraformingmars.TMGameState;
import games.terraformingmars.TMTypes;
import games.terraformingmars.rules.requirements.CounterRequirement;

import java.util.Objects;

public class ModifyGlobalParameter extends TMModifyCounter {
    TMTypes.GlobalParameter param;

    public ModifyGlobalParameter(TMTypes.GlobalParameter param, int change, boolean free) {
        super(-1, change, free);
        this.param = param;
        requirements.add(new CounterRequirement(param.name(), -1, true));
    }

    public ModifyGlobalParameter(TMTypes.ActionType actionType, TMTypes.Resource costResource, int cost, TMTypes.GlobalParameter param, int change, boolean free) {
        super(actionType, -1, change, free);
        this.param = param;
        requirements.add(new CounterRequirement(param.name(), -1, true));
        setActionCost(costResource, cost, -1);
    }

    @Override
    public boolean execute(AbstractGameState gameState) {
        // When global parameters change, TR is increased
        TMGameState gs = (TMGameState) gameState;
        int player = this.player;
        if (player == -1) player = gs.getCurrentPlayer();
        Counter c = gs.getGlobalParameters().get(param);
        if (counterID == -1) counterID = c.getComponentID();
        if (change > 0 && !c.isMaximum() || change < 0 && !c.isMinimum()) {
            gs.getPlayerResources()[player].get(TMTypes.Resource.TR).increment(1);
            gs.getPlayerResourceIncreaseGen()[player].put(TMTypes.Resource.TR, true);
        }
        return super.execute(gs);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Modify global parameter " + param + " by " + change;
    }

    @Override
    public String toString() {
        return "Modify global parameter " + param + " by " + change;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModifyGlobalParameter)) return false;
        if (!super.equals(o)) return false;
        ModifyGlobalParameter that = (ModifyGlobalParameter) o;
        return param == that.param;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), param);
    }

}
