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
import java.util.Date;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;

/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class UpdateDemandeFlow extends FlowLogic<SignedTransaction> {
    private final Integer doc;
    private final Integer client;
    private final String etat;


    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public UpdateDemandeFlow(Integer doc, Integer client, String etat) {

        this.doc = doc;
        this.client = client;
        this.etat = etat;


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





        DemandeState outputState = new DemandeState(doc, client, getOurIdentity(), other2, other3, true, now, etat);

        // update testing ***********
        if(getOurIdentity().equals(other2)){
            outputState.setOther1(other1);
            outputState.setOther2(other3);
        }

        else   if(getOurIdentity().equals(other3)){
            outputState.setOther1(other1);
            outputState.setOther2(other2);
        }

        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field client1 = null;
        try {
            client1 = DemandeSchemaV1.PersistentDemande.class.getDeclaredField("Client");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);

        Field doc1 = null;
        try {
            doc1 = DemandeSchemaV1.PersistentDemande.class.getDeclaredField("Doc");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression docIndex = Builder.equal(doc1, doc);
        QueryCriteria docCriteria = new QueryCriteria.VaultCustomQueryCriteria(docIndex);

        QueryCriteria criteria = generalcriteria.and(clientCriteria).and(docCriteria);
        // test 2

        // *****
        Vault.Page<DemandeState> result = getServiceHub().getVaultService().queryBy(DemandeState.class, criteria);
        StateAndRef<DemandeState> inputState = result.getStates().get(0);

        StateRef ourStateRef = new StateRef(inputState.getRef().getTxhash(),0);
        StateAndRef ourStateAndRef = getServiceHub().toStateAndRef(ourStateRef);








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