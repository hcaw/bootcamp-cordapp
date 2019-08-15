package bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static final String ID = "bootcamp.TokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);

        if (!tx.getInputs().isEmpty() || tx.getOutputs().size() != 1) {
            throw new IllegalArgumentException("Incorrect input/output amounts.");
        }
        if (command.getValue() instanceof Commands.Issue) {
            // Grabbing the transaction's contents.
            final TokenState tokenStateOutput = tx.outputsOfType(TokenState.class).get(0);
            if (tokenStateOutput.getAmount() <= 0) { throw new IllegalArgumentException("Output amount not positive."); }
            // Checking the transaction's required signers.
            final List<PublicKey> requiredSigners = command.getSigners();
            if (!(requiredSigners.contains(tokenStateOutput.getIssuer().getOwningKey())))
                throw new IllegalArgumentException("Token transfer should have output's issuer as a required signer.");

        } else throw new IllegalArgumentException("Unrecognised command.");
    }


    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}