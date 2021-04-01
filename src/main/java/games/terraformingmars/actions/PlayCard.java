package games.terraformingmars.actions;

import core.AbstractGameState;
import games.terraformingmars.TMGameParameters;
import games.terraformingmars.TMGameState;
import games.terraformingmars.TMTypes;
import games.terraformingmars.components.TMCard;
import games.terraformingmars.rules.requirements.PlayableActionRequirement;


public class PlayCard extends TMAction {

    public PlayCard(int player, TMCard card, boolean free) {
        super(TMTypes.ActionType.PlayCard, player, free);
        this.setActionCost(TMTypes.Resource.MegaCredit, card.cost, card.getComponentID());

        this.requirements.addAll(card.requirements);
        for (TMAction aa : card.immediateEffects) {
            // All immediate effects must also be playable in order for this card to be playable
            this.requirements.add(new PlayableActionRequirement(aa));
        }
    }

    @Override
    public boolean execute(AbstractGameState gameState) {
        TMGameState gs = (TMGameState) gameState;
        TMGameParameters gp = (TMGameParameters) gameState.getGameParameters();
        int player = this.player;
        if (player == -1) player = gs.getCurrentPlayer();
        TMCard card = (TMCard) gs.getComponentById(getCardID());
        playCard(gs, player, card);
        return super.execute(gs);
    }

    private void playCard(TMGameState gs, int player, TMCard card) {
        // Second: remove from hand, resolve on-play effects and add tags etc. to cards played lists
        gs.getPlayerHands()[player].remove(card);

        // Add info to played cards stats
        if (card.cardType != TMTypes.CardType.Event) {  // Event tags don't count for regular tag counts
            for (TMTypes.Tag t : card.tags) {
                gs.getPlayerCardsPlayedTags()[player].get(t).increment(1);
            }
        }
        gs.getPlayerCardsPlayedTypes()[player].get(card.cardType).increment(1);
        if (card.shouldSaveCard()) {
            gs.getPlayerComplicatedPointCards()[player].add(card);
        } else {
            gs.getPlayedCards()[player].add(card);
            if (card.nPoints != 0) {
                gs.getPlayerCardPoints()[player].increment((int) card.nPoints);
            }
        }

        // Add actions
        for (TMAction a: card.actions) {
            a.player = player;
            gs.getPlayerExtraActions()[player].add(a);
        }

        // Add discountEffects to player's discounts
        gs.addDiscountEffects(card.discountEffects);
        gs.addResourceMappings(card.resourceMappings, false);
        // Add persisting effects
        gs.addPersistingEffects(card.persistingEffects);

        // Force an update of components before executing the effects, they might need something just added
        gs.getAllComponents();

        // Execute on-play effects
        for (TMAction aa: card.immediateEffects) {
            aa.player = player;
            aa.execute(gs);
        }
    }

    @Override
    public PlayCard copy() {
        return this;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        TMCard card = (TMCard) gameState.getComponentById(getCardID());
        return "Play card " + card.getComponentName();
    }

    @Override
    public String toString() {
        return "Play card id " + getCardID();
    }
}
