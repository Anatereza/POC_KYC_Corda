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
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.lang.reflect.Field;
import java.text.DateFormat;
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
public class AbonTestFlow extends FlowLogic<SignedTransaction> {
    private final String cert;

    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public AbonTestFlow(String cert) {

        this.cert = cert;


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

        // We create the transaction components.
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String now = sdf.format(new Date());

////// retrieve Initiator from certificat

        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field cert1 = null;
        try {
            cert1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Cert");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression certIndex = Builder.equal(cert1, cert);
        QueryCriteria certCriteria = new QueryCriteria.VaultCustomQueryCriteria(certIndex);
        QueryCriteria criteria = generalcriteria.and(certCriteria);

        Vault.Page<CertificateState> result = getServiceHub().getVaultService().queryBy(CertificateState.class, criteria);
        StateAndRef<CertificateState> state = result.getStates().get(0);
        Party initiator = state.getState().getData().getInitiator();



///// end
        List<List<String>> notifications = new ArrayList<List<String>>();

        List<String> notification = new ArrayList<String>();
        notification.add("notification");
        notification.add("date");
        notifications.add(notification);



        //write the CSV back out to the console
        for(List<String> csv : notifications)
        {
            //dumb logic to place the commas correctly
            if(!csv.isEmpty())
            {
                System.out.print(csv.get(0));
                for(int i=1; i < csv.size(); i++)
                {
                    System.out.print("," + csv.get(i));
                }
            }
            System.out.print("\n");
        }




        AbonnementState outputState = new AbonnementState(cert, getOurIdentity(), initiator, notifications, true);


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