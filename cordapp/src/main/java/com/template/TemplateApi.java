/*package com.template;

public class TestAPIAna {
}
*/


package com.template;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.ws.rs.core.Response.Status.CREATED;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import static sun.security.timestamp.TSResponse.BAD_REQUEST;


// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class TemplateApi {
    private final CordaRPCOps services;
    private final CordaX500Name myLegalName;


    public TemplateApi(CordaRPCOps services) {
        this.services = services;
        this.myLegalName = services.nodeInfo().getLegalIdentities().get(0).getName();

    }

    /**
     * Accessible at /api/template/Request.:
     */

    //API créer certificat
    @PUT
    @Path("createCertificate")
    public Response createCertificate(@QueryParam("client") String client, @QueryParam("profil") String profil, @QueryParam("documents") List<String> documents, @QueryParam("description") String descrip, @QueryParam("dateProchaineCertif") String dateProchCert) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(CertificateFlow.class, client, profil, documents, descrip, dateProchCert)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    /// TEST API consulter mes certificats et certificats autres
    @GET
    @Path("getALLCertificats")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CertificateState>> GetALLCertificats(@QueryParam("client") String client, @QueryParam("status") int status, @QueryParam("maintien") int maintien, @QueryParam("mine") Boolean mine) throws NoSuchFieldException {
        QueryCriteria generalCriteria1 = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        Field client1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);


        Field status1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Status");
        CriteriaExpression statusIndex = Builder.equal(status1, status);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);

        Field maintien1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Maintien");
        CriteriaExpression maintienIndex = Builder.equal(maintien1, maintien);
        QueryCriteria maintienCriteria = new QueryCriteria.VaultCustomQueryCriteria(maintienIndex);

        //CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        Party me = services.wellKnownPartyFromX500Name(myLegalName);

        Field initiator1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Initiator");
        if (mine){
            CriteriaExpression initiatorIndex = Builder.equal(initiator1, me);
            QueryCriteria initiatorCriteria = new QueryCriteria.VaultCustomQueryCriteria(initiatorIndex);
            QueryCriteria generalCriteria = generalCriteria1.and(initiatorCriteria);

            if(status == 0 && maintien == 0 && client == null) { return   services.vaultQueryByCriteria(generalCriteria,CertificateState.class).getStates(); }

            else if(status == 0 && maintien == 0 ) { return   services.vaultQueryByCriteria(generalCriteria.and(clientCriteria),CertificateState.class).getStates(); }

            else if(maintien == 0) {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
            }
            else if(status == 0) {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
            }

            else {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria).and(statusCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();        }

        } else {
            CriteriaExpression initiatorIndex = Builder.notEqual(initiator1, me);
            QueryCriteria initiatorCriteria = new QueryCriteria.VaultCustomQueryCriteria(initiatorIndex);
            QueryCriteria generalCriteria = generalCriteria1.and(initiatorCriteria);

            if(status == 0 && maintien == 0 && client == null) { return   services.vaultQueryByCriteria(generalCriteria,CertificateState.class).getStates(); }

            else if(status == 0 && maintien == 0 ) { return   services.vaultQueryByCriteria(generalCriteria.and(clientCriteria),CertificateState.class).getStates(); }

            else if(maintien == 0) {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
            }
            else if(status == 0) {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
            }

            else {
                QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria).and(statusCriteria);
                return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();        }
        }


    }


    /// TEST API consulter mes certificats
    @GET
    @Path("getMesCertificats")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CertificateState>> GetMesCertificats(@QueryParam("client") String client, @QueryParam("status") int status, @QueryParam("maintien") int maintien) throws NoSuchFieldException {
        QueryCriteria generalCriteria1 = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        //CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        Party me = services.wellKnownPartyFromX500Name(myLegalName);


        Field initiator1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Initiator");
        CriteriaExpression initiatorIndex = Builder.equal(initiator1, me);
        QueryCriteria initiatorCriteria = new QueryCriteria.VaultCustomQueryCriteria(initiatorIndex);
        QueryCriteria generalCriteria = generalCriteria1.and(initiatorCriteria);

        Field client1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);


        Field status1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Status");
        CriteriaExpression statusIndex = Builder.equal(status1, status);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);

        Field maintien1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Maintien");
        CriteriaExpression maintienIndex = Builder.equal(maintien1, maintien);
        QueryCriteria maintienCriteria = new QueryCriteria.VaultCustomQueryCriteria(maintienIndex);

        //QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria).and(maintienCriteria);

        if(status == 0 && maintien == 0 && client == null) { return   services.vaultQueryByCriteria(generalCriteria,CertificateState.class).getStates(); }

        else if(status == 0 && maintien == 0 ) { return   services.vaultQueryByCriteria(generalCriteria.and(clientCriteria),CertificateState.class).getStates(); }

        else if(maintien == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else if(status == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }

        else {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();        }


    }

    /// TEST API consulter AUTRES certificats
    @GET
    @Path("getAutresCertificats")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CertificateState>> GetAutresCertificats(@QueryParam("client") String client, @QueryParam("status") int status, @QueryParam("maintien") int maintien) throws NoSuchFieldException {
        QueryCriteria generalCriteria1 = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        //CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        Party me = services.wellKnownPartyFromX500Name(myLegalName);


        Field initiator1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Initiator");
        //CriteriaExpression initiatorIndex = Builder.equal(initiator1, me);
        CriteriaExpression initiatorIndex = Builder.notEqual(initiator1, me);
        QueryCriteria initiatorCriteria = new QueryCriteria.VaultCustomQueryCriteria(initiatorIndex);
        QueryCriteria generalCriteria = generalCriteria1.and(initiatorCriteria);

        Field client1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);


        Field status1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Status");
        CriteriaExpression statusIndex = Builder.equal(status1, status);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);

        Field maintien1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Maintien");
        CriteriaExpression maintienIndex = Builder.equal(maintien1, maintien);
        QueryCriteria maintienCriteria = new QueryCriteria.VaultCustomQueryCriteria(maintienIndex);

        //QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria).and(maintienCriteria);


        if(status == 0 && maintien == 0 && client == null) { return   services.vaultQueryByCriteria(generalCriteria,CertificateState.class).getStates(); }

        else if(status == 0 && maintien == 0 ) { return   services.vaultQueryByCriteria(generalCriteria.and(clientCriteria),CertificateState.class).getStates(); }

        else if(maintien == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else if(status == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }

        else {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();        }


    }

    /// API consulter certificat
    @GET
    @Path("getCertificat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CertificateState>> GetCertificat(@QueryParam("client") String client, @QueryParam("status") int status, @QueryParam("maintien") int maintien) throws NoSuchFieldException {
        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        Field client1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);


        Field status1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Status");
        CriteriaExpression statusIndex = Builder.equal(status1, status);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);

        Field maintien1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("Maintien");
        CriteriaExpression maintienIndex = Builder.equal(maintien1, maintien);
        QueryCriteria maintienCriteria = new QueryCriteria.VaultCustomQueryCriteria(maintienIndex);

        //QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria).and(maintienCriteria);


        if(status == 0 && maintien == 0 && client == null) { return   services.vaultQueryByCriteria(generalCriteria,CertificateState.class).getStates(); }

        else if(status == 0 && maintien == 0 ) { return   services.vaultQueryByCriteria(generalCriteria.and(clientCriteria),CertificateState.class).getStates(); }

        else if(maintien == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else if(status == 0) {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }

        else {
            QueryCriteria criteria = generalCriteria.and(clientCriteria).and(maintienCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();        }

        //return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();

    }



    /// API consulter docs sans client
    @GET
        @Path("docs")
        @Produces(MediaType.APPLICATION_JSON)
        public List<StateAndRef<DocumentState>> GetFolder0() throws NoSuchFieldException {
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            return   services.vaultQueryByCriteria(generalCriteria,DocumentState.class).getStates();
    }

    /// API consulter docs en fonction du client
    @GET
    @Path("dossier")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<DocumentState>> GetFolder(@QueryParam("client") Integer client) throws NoSuchFieldException {
        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field client1 = DocumentSchemaV1.PersistentDocument.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);
        QueryCriteria criteria = generalCriteria.and(clientCriteria);
        return   services.vaultQueryByCriteria(criteria,DocumentState.class).getStates();

    }

    // api créer document + upload de doc
    @PUT
    @Path("uploadDoc")
    public Response uploadDoc(@QueryParam("nomdoc") String nomdoc, @QueryParam("client") String client, @QueryParam("status") int status, @QueryParam("expire") String expire, @QueryParam("path") String filePath) throws InterruptedException, ExecutionException, IOException {

        InputStream is = new FileInputStream(filePath);

        SecureHash attachmentHashValue =  services.uploadAttachment(is);
        System.out.println("File hash"+ attachmentHashValue);
        /** End attachment */

        String doc = attachmentHashValue.toString();

        //DocumentFlow(Integer doc, Integer client, int status, String nomdoc, String expire)
        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DocumentFlow.class, doc, client, status, nomdoc, expire, attachmentHashValue)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }


    // api dowload doc sur le node et en local dans Dowloads
    @PUT
    @Path("downDoc")
    public Response downDoc(@QueryParam("filepath") String filePath, @QueryParam("nomdoc") String nomdoc) throws InterruptedException, ExecutionException, IOException {
        SecureHash h = SecureHash.parse(filePath);

        System.out.println("File hash" + h);

        InputStream it = services.openAttachment(h);

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(it);
            fout = new FileOutputStream(nomdoc);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }

        //copy du doc en local
        try
        {
            String[] command = new String[5];
            command[0] = "cmd";
            command[1] = "/c";
            command[2] = "copy";
            command[3] = nomdoc;
            command[4] = "C:\\Users\\anatereza.mascarenha\\Downloads";
            Runtime.getRuntime().exec (command);


        }
        catch (Exception e)
        {
            System.out.println("HEY Buddy ! U r Doing Something Wrong ");
            e.printStackTrace();
        }

        /** End attachment */



        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DocDownFlow.class, h)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();

    }


    // api créer document KYC
    @PUT
    @Path("uploadDocKYC")
    public Response uploadDocKYC(@QueryParam("path") String filePath) throws InterruptedException, ExecutionException, IOException {

        InputStream is = new FileInputStream(filePath);

        SecureHash attachmentHashValue =  services.uploadAttachment(is);
        System.out.println("File hash"+ attachmentHashValue);
        /** End attachment */

        String doc = attachmentHashValue.toString();

        //DocumentFlow(Integer doc, Integer client, int status, String nomdoc, String expire)
        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DocKYCFlow.class, doc, attachmentHashValue)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }


    //update certificat APIs
    @PUT
    @Path("updateStatusCertificate")
    public Response updateStatusCertificate(@QueryParam("cert") String cert, @QueryParam("status") Integer status) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(StatusCertificateFlow.class, cert, status)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @PUT
    @Path("updateProfilCertificate")
    public Response updateProfilCertificate(@QueryParam("cert") String cert, @QueryParam("profil") String profil) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(ProfilCertificateFlow.class, cert, profil)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @PUT
    @Path("updateDateNextCertificate")
    public Response updateDateNextCertificate(@QueryParam("cert") String cert, @QueryParam("date") String dateProchaineCert) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DateNextCertificateFlow.class, cert, dateProchaineCert)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @PUT
    @Path("updateMaintenanceCertificate")
    public Response updateMaintenanceCertificate(@QueryParam("cert") String cert, @QueryParam("main") Integer maintenance) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(MaintenanceCertificateFlow.class, cert, maintenance)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @PUT
    @Path("updateCertificate")
    public Response updateCertificate(@QueryParam("cert") String cert, @QueryParam("status") int status, @QueryParam("profil") String profil, @QueryParam("date") String dateProchaineCert, @QueryParam("main") int maintenance) throws InterruptedException, ExecutionException {

        if (!(status == 0)) {
            Integer intStatus = new Integer(status);

            final SignedTransaction signedTx = services
                    .startTrackedFlowDynamic(StatusCertificateFlow.class, cert, intStatus)
                    .getReturnValue()
                    .get();

            final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        }
        else if (!(profil == null)){

            final SignedTransaction signedTx = services
                    .startTrackedFlowDynamic(ProfilCertificateFlow.class, cert, profil)
                    .getReturnValue()
                    .get();

            final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();
        }

        else if (!(dateProchaineCert == null)){

            final SignedTransaction signedTx = services
                    .startTrackedFlowDynamic(DateNextCertificateFlow.class, cert, dateProchaineCert)
                    .getReturnValue()
                    .get();

            final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();
        }

        else if (!(maintenance == 0)){
            Integer intMaintenance = new Integer(maintenance);
            final SignedTransaction signedTx = services
                    .startTrackedFlowDynamic(MaintenanceCertificateFlow.class, cert, intMaintenance)
                    .getReturnValue()
                    .get();

            final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();
        }

        else {

            final String msg = "Request not committed to ledger.";
            return Response.status(CREATED).entity(msg).build();

        }
    }






}










