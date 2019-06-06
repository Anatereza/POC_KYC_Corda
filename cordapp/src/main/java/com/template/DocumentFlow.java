/*automatique

package com.template;

public class DocumentFlow {
}

 */

package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.flows.*;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;
/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class DocumentFlow extends FlowLogic<SignedTransaction> {
    private final Integer doc;
    private final String client;
    private final int status;
    private final String nomdoc;
    private final String expire;


    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public DocumentFlow(Integer doc, String client, int status, String nomdoc, String expire) {

        this.doc = doc;
        this.client = client;
        this.status = status;
        this.nomdoc = nomdoc;
        this.expire = expire;
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
        CordaX500Name OtherX1 = CordaX500Name.parse("O=Caisse Epargne,L=Paris,C=FR");
        CordaX500Name OtherX2 = CordaX500Name.parse("O=Natixis Assurance,L=Paris,C=FR");
        CordaX500Name OtherX3 = CordaX500Name.parse("O=BPCE Assurance,L=Paris,C=FR");

        Party other1 = getServiceHub().getNetworkMapCache().getPeerByLegalName(OtherX1);
        Party other2 = getServiceHub().getNetworkMapCache().getPeerByLegalName(OtherX2);
        Party other3 = getServiceHub().getNetworkMapCache().getPeerByLegalName(OtherX3);


        // We create the transaction components.
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String now = sdf.format(new Date());

        //id unique du document = nom du doc + timestamp
        //String idnomdoc = nomdoc + Integer.parseInt(now);

        DocumentState outputState = new DocumentState(doc, client, getOurIdentity(), other2, other3, status, nomdoc, now, expire, null);


        if(getOurIdentity().equals(other2)){
            outputState.setOther1(other1);
            outputState.setOther2(other3);
        }

        else if(getOurIdentity().equals(other3)){
            outputState.setOther1(other1);
            outputState.setOther2(other2);
        }

        CommandData cmdType = new TemplateContract.Commands.Action();
        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey());

        // We create a transaction builder and add the components.

        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, TEMPLATE_CONTRACT_ID)
                .addCommand(cmd);

        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));


        return null;
    }
}