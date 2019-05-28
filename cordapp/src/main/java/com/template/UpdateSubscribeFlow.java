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
import java.util.List;

import static com.template.TemplateContract.TEMPLATE_CONTRACT_ID;

/**
 * Define your flow here.
 */
@InitiatingFlow
@StartableByRPC
public class UpdateSubscribeFlow extends FlowLogic<SignedTransaction> {
    private final Integer doc;
    private final Integer client;


    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public UpdateSubscribeFlow(Integer doc, Integer client) {

        this.doc = doc;
        this.client = client;


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

        // update testing ***********

        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field client1 = null;
        try {
            client1 = SubscribeSchemaV1.PersistentSubscribe.class.getDeclaredField("Client");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);

        Field doc1 = null;
        try {
            doc1 = SubscribeSchemaV1.PersistentSubscribe.class.getDeclaredField("Doc");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression docIndex = Builder.equal(doc1, doc);
        QueryCriteria docCriteria = new QueryCriteria.VaultCustomQueryCriteria(docIndex);







        QueryCriteria criteria = generalcriteria.and(clientCriteria).and(docCriteria);


        // *****
        Vault.Page<SubscribeState> result = getServiceHub().getVaultService().queryBy(SubscribeState.class, criteria);
        StateAndRef<SubscribeState> inputState = result.getStates().get(0);

        StateRef ourStateRef = new StateRef(inputState.getRef().getTxhash(),0);
        StateAndRef ourStateAndRef = getServiceHub().toStateAndRef(ourStateRef);

        // test 2 inputs


             Party applicant = inputState.getState().getData().getApplicant();
        Party initiator = inputState.getState().getData().getInitiator();

        SubscribeState outputState = new SubscribeState(doc, client, initiator, applicant, now, true);

        // sendmail1
        String name1 = initiator.getName().getOrganisation();
        System.out.println("here");
        System.out.println(name1);
        System.out.println(other1.getName().getOrganisation());
        String param11 = "pocblockchain.natixisassurance@gmail.com";
        if(initiator.equals(other1)){
            param11 = "pocblockchain.caisseepargne@gmail.com";
        }
        else if(initiator.equals(other3)){
            param11 = "pocblockchain.bpceassurance@gmail.com";
        }
        String param12 = doc.toString();
        String param13 = client.toString();
        String param14 = '"'+applicant.getName().getOrganisation()+'"';
        String param15 = now;

        try
        {
            Runtime.getRuntime().exec("cmd run cmd.exe /C \"py ../../../sendMail.py " + param11 +" "+ param12 +" "+ param13 +" " +param14 +" "+ param15+"\"");

        }
        catch (Exception e)
        {
            System.out.println("HEY Buddy ! U r Doing Something Wrong ");
            e.printStackTrace();
        }


        //SECOND UPDATE
        List<StateAndRef<SubscribeState>> inputStates = result.getStates();
        SubscribeState outputState2 = null;
        StateRef ourStateRef2;
        StateAndRef ourStateAndRef2 = null;

        System.out.println("HEY Buddy !  ");
        System.out.println(inputStates.size());


        if(inputStates.size() == 2){
            ourStateRef2 = new StateRef(inputStates.get(1).getRef().getTxhash(),0);
            ourStateAndRef2 = getServiceHub().toStateAndRef(ourStateRef2);
            Party applicant2 = inputStates.get(1).getState().getData().getApplicant();
            Party initiator2 = inputStates.get(1).getState().getData().getInitiator();

            outputState2 = new SubscribeState(doc, client, initiator2, applicant2, now, true);
            String name2 = initiator2.getName().getOrganisation();
            System.out.println(name2);
            String param21 = "pocblockchain.natixisassurance@gmail.com";

            if(initiator2.equals(other1)){
                param21 = "pocblockchain.caisseepargne@gmail.com";
            }
            else if(initiator2.equals(other3)){
                param21 = "pocblockchain.bpceassurance@gmail.com";
            }
            /*
            if(name1 == "Caisse Epargne"){
                param21 = "pocblockchain.caisseepargne@gmail.com";
            }
            else if(name1 == "BPCE Assurance"){
                param21 = "pocblockchain.bpceassurance@gmail.com";
            }
            */
            String param24 = '"'+applicant2.getName().getOrganisation()+'"';


            try
            {
                Runtime.getRuntime().exec("cmd run cmd.exe /C \"py ../../../sendMail.py " + param21 +" "+ param12 +" "+ param13 +" " +param24 +" "+ param15+"\"");

            }
            catch (Exception e)
            {
                System.out.println("HEY Buddy ! U r Doing Something Wrong ");
                e.printStackTrace();
            }
        }

// END of update testing



        CommandData cmdType = new TemplateContract.Commands.Action();
        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey());

        // We create a transaction builder and add the components.

        final TransactionBuilder txBuilder = new TransactionBuilder(notary);

        txBuilder.addInputState(ourStateAndRef);
        txBuilder.addOutputState(outputState, TEMPLATE_CONTRACT_ID);

        if(inputStates.size() == 2){
            txBuilder.addInputState(ourStateAndRef2);
            txBuilder.addOutputState(outputState2, TEMPLATE_CONTRACT_ID);
        }
        txBuilder.addCommand(cmd);


        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));




        return null;
    }
}