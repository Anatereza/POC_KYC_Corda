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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;

/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class NotificationFlow extends FlowLogic<SignedTransaction> {
    private final String cert;
    private final String msg;




    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public NotificationFlow(String cert, String msg) {
        this.cert = cert;
        this.msg = msg;

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

        //TEST
        // We create the transaction components.
        //final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        //String now = sdf.format(new Date());
        //List<String> notification = Arrays.asList(now,notif);

        final SimpleDateFormat sdf2 = new SimpleDateFormat("ddmmyyyyhhmmss");
        String time = sdf2.format(new Date());


        List<String> notification = new ArrayList<String>();
        notification.add(time);
        notification.add(msg);

        List<List<String>> notifications = new ArrayList<List<String>>();
        notifications.add(notification);

        // update testing ***********

        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field certificate1 = null;
        try {
            certificate1 = AbonnementSchemaV1.PersistentAbonnement.class.getDeclaredField("Cert");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression certificateIndex = Builder.equal(certificate1, cert);
        QueryCriteria certificateCriteria = new QueryCriteria.VaultCustomQueryCriteria(certificateIndex);


        QueryCriteria criteria = generalcriteria.and(certificateCriteria);

        // *****
        Vault.Page<AbonnementState> result = getServiceHub().getVaultService().queryBy(AbonnementState.class, criteria);

        StateAndRef<AbonnementState> inputState;
        StateRef ourStateRef;
        StateAndRef ourStateAndRef;

        Party initiator;
        Party applicant;
        AbonnementState outputState;

        // We create a transaction builder and add the components.

        final TransactionBuilder txBuilder = new TransactionBuilder(notary);

        for (int i=0 ;  i < result.getStates().size(); i++) {
            inputState = result.getStates().get(i);
            ourStateRef = new StateRef(inputState.getRef().getTxhash(),0);
            ourStateAndRef = getServiceHub().toStateAndRef(ourStateRef);

            initiator = inputState.getState().getData().getInitiator();
            applicant = inputState.getState().getData().getApplicant();
            outputState = new AbonnementState(cert,applicant ,initiator, notifications);

            txBuilder.addInputState(ourStateAndRef);
            txBuilder.addOutputState(outputState, TEMPLATE_CONTRACT_ID);

        }

        CommandData cmdType = new TemplateContract.Commands.Action();
        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey());


        txBuilder.addCommand(cmd);

        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));


        return null;
    }
}

