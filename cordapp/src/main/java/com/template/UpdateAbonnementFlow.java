/*
package com.template;

public class UpdateAbonnementFlow {
}

*/

package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;

/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class UpdateAbonnementFlow extends FlowLogic<SignedTransaction> {
    private final String cert;
    private final Boolean status;




    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public UpdateAbonnementFlow(String cert, Boolean status) {
        this.cert = cert;
        this.status = status;

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

        // update testing ***********

        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field certificate1 = null;
        try {
            certificate1 = AbonnementSchemaV1.PersistentAbonnement.class.getDeclaredField("certificate");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression certificateIndex = Builder.equal(certificate1, cert);
        QueryCriteria certificateCriteria = new QueryCriteria.VaultCustomQueryCriteria(certificateIndex);

        Party monabonnement = getOurIdentity();
        Field identite = null;
        try {
            identite = AbonnementSchemaV1.PersistentAbonnement.class.getDeclaredField("applicant");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression idIndex = Builder.equal(identite, monabonnement);
        QueryCriteria applicantCriteria = new QueryCriteria.VaultCustomQueryCriteria(idIndex);


        QueryCriteria criteria = generalcriteria.and(certificateCriteria).and(applicantCriteria);


        // *****
        Vault.Page<AbonnementState> result = getServiceHub().getVaultService().queryBy(AbonnementState.class, criteria);
        StateAndRef<AbonnementState> inputState = result.getStates().get(0);

        StateRef ourStateRef = new StateRef(inputState.getRef().getTxhash(),0);
        StateAndRef ourStateAndRef = getServiceHub().toStateAndRef(ourStateRef);

        // get output values
        List<List<String>> notifications = inputState.getState().getData().getNotifications();

        Party initiator = inputState.getState().getData().getInitiator();

        AbonnementState outputState = new AbonnementState(cert, getOurIdentity(),initiator, notifications, status);

        // END of update testing

        CommandData cmdType = new TemplateContract.Commands.Action();
        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey());

        // We create a transaction builder and add the components.

        final TransactionBuilder txBuilder = new TransactionBuilder(notary);

        txBuilder.addInputState(ourStateAndRef);
        txBuilder.addOutputState(outputState, TEMPLATE_CONTRACT_ID);

        txBuilder.addCommand(cmd);


        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));




        return null;
    }
}

