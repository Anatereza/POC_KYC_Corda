/*automatique

package com.template;

public class DocumentFlow {
}

 */

package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.jar.JarInputStream;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;
/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class DocDownFLow extends FlowLogic<SignedTransaction> {
    private final SecureHash docHash;


    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public DocDownFLow(SecureHash docHash) {
        this.docHash = docHash;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We retrieve the notary and nodes identity from the network map.
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


        CommandData cmdType = new TemplateContract.Commands.Action();
        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey());

        // We create a transaction builder and add the components.

        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addCommand(cmd).addAttachment(docHash);

        //txBuilder.addAttachment(docHash);

        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));


        return null;
    }
}


