package com.template;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
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

import java.security.PublicKey;
import java.security.Timestamp;
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
public class CertificateTestFlow extends FlowLogic<SignedTransaction> {
    private final Integer client;
    private final String profil;
    private final List<String> documents;
    private final String description;
    private final String dateProchaineCert;


    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();


    public CertificateTestFlow(Integer client, String profil, List<String> documents, String description, String dateProchaineCert) {

        this.client = client;
        this.profil = profil;
        this.documents = documents;
        this.description = description;
        this.dateProchaineCert = dateProchaineCert;
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

        final SimpleDateFormat sdf2 = new SimpleDateFormat("ddmmyyyyhhmmss");
        String time = sdf2.format(new Date());

        //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp());
        //        //String test = "test0";
        String clientid = String.valueOf(client);
        String idcert = clientid + time;


        CertificateState certificateOutputState = new CertificateState(idcert, client, 1, 1,  getOurIdentity(), profil, documents, description, now, dateProchaineCert, other2, other3);


        if(getOurIdentity().equals(other2)){
            certificateOutputState.setOther1(other1);
            certificateOutputState.setOther2(other3);
        }

        else if(getOurIdentity().equals(other3)){
            certificateOutputState.setOther1(other1);
            certificateOutputState.setOther2(other2);
        }


        //test
        /*
        QueryCriteria.VaultQueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field client1 = null;
        try {
            client1 = DocumentSchemaV1.PersistentDocument.class.getDeclaredField("client");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);


        Field doc1 = null;
        try {
            doc1 = DocumentSchemaV1.PersistentDocument.class.getDeclaredField("nom_doc");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //critères sur les documents : boucler sur la liste
        QueryCriteria criteria = generalcriteria.and(clientCriteria);

        CriteriaExpression docIndex;
        QueryCriteria docCriteria;
        for (int i=0; i < documents.size(); i++) {
            docIndex = Builder.equal(doc1, documents.get(i));
            docCriteria = new QueryCriteria.VaultCustomQueryCriteria(docIndex);
            criteria.and(docCriteria);

        }

        Vault.Page<DocumentState> result = getServiceHub().getVaultService().queryBy(DocumentState.class, criteria);

        int statusDoc;
        StateAndRef<DocumentState> inputDocState;
        for (int i=0; i <documents.size(); i++) {
            statusDoc = result.getStates().get(i).getState().getData().getStatus();
            if (statusDoc != 1)
                throw new IllegalArgumentException("Tous les documents du certificat doivent etre valide");
        }
        */





        // We create a transaction builder and add the components.
        final TransactionBuilder txBuilder = new TransactionBuilder(notary);


        txBuilder.addOutputState(certificateOutputState, CertificateContract.CERTIFICATE_CONTRACT_ID);

        // We add the InitiateSell command to the transaction.
        // Note that we also specific who is required to sign the transaction.
        CertificateContract.Commands.Certificat commandData = new CertificateContract.Commands.Certificat();
        List<PublicKey> requiredSigners = ImmutableList.of(certificateOutputState.getInitiator().getOwningKey());
        txBuilder.addCommand(commandData, requiredSigners);

        // STEP.4.5. We check that the transaction builder we've created meets the
        // contracts of the input and output states.
        txBuilder.verify(getServiceHub());


        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        subFlow(new FinalityFlow(signedTx));


        return null;
    }
}