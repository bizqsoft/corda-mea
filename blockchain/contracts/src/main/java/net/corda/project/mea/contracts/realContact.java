package net.corda.project.mea.contracts;

import net.corda.project.mea.states.realState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class realContact implements Contract{
    public static final String ID = "net.corda.project.mea.contracts.realContact";

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<Commands.Create> command = requireSingleCommand(tx.getCommands(), Commands.Create.class);
        requireThat(require -> {
            // Generic constraints around the IOU transaction.
            require.using("No inputs should be consumed when issuing an IOU.",
                    tx.getInputs().isEmpty());
            require.using("Only one output states should be created.",
                    tx.getOutputs().size() == 1);
            final realState out = tx.outputsOfType(realState.class).get(0);
            require.using("The lender and the borrower cannot be the same entity.",
                    !out.getSender().equals(out.getReceiver()));
            require.using("All of the participants must be signers.",
                    command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));

            // IOU-specific constraints.
            require.using("The IOU's network must not be null", out.getnetworkname() !=null);

            return null;
        });
    }

    /**
     * This contracts only implements one command, Create.
     */
    public interface Commands extends CommandData {
        class Create implements realContact.Commands {}
    }
}
