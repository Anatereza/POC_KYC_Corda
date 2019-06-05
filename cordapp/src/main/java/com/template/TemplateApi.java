/*package com.template;

public class TestAPIAna {
}
*/


package com.template;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    @PUT
    @Path("sendCertificate")
    public Response sendCertificate(@QueryParam("client") Integer client, @QueryParam("profil") String profil, @QueryParam("documents") List<String> documents, @QueryParam("description") String descrip, @QueryParam("dateProchaineCertif") String dateProchCert) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(CertificateFlow.class, client, profil, documents, descrip, dateProchCert)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @GET
    @Path("RequestCertificate")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CertificateState>> GetRequestCertificate(@QueryParam("client") int param1, @QueryParam("statut") int param2, @QueryParam("maintien") int param3)  throws NoSuchFieldException {
        //Consulter certificat : client, avec option statut, option maintien
        QueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        Field client1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("client");
        CriteriaExpression clientIndex = Builder.equal(client1, param1);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);

        Field status1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("status");
        CriteriaExpression statusIndex = Builder.equal(status1, param2);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusIndex);

        Field maintien1 = CertificateSchemaV1.PersistentCertificate.class.getDeclaredField("maintenance");
        CriteriaExpression maintienIndex = Builder.equal(maintien1, param3);
        QueryCriteria maintienCriteria = new QueryCriteria.VaultCustomQueryCriteria(maintienIndex);

        if(param1 == 0 && param2 == 0 && param3 == 0) { return   services.vaultQueryByCriteria(generalcriteria,CertificateState.class).getStates(); }

        else if(param3 == 0 && param2 == 0 && param1 !=0) {
            QueryCriteria criteria = generalcriteria.and(clientCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else if(param3 == 0 && param2 !=0 && param1 !=0) {
            QueryCriteria criteria = generalcriteria.and(clientCriteria).and(statusCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }

        else if(param2 == 0 && param3 !=0 && param1 !=0) {
            QueryCriteria criteria = generalcriteria.and(clientCriteria).and(maintienCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else if(param2 != 0 && param3 !=0 && param1 !=0) {
            QueryCriteria criteria = generalcriteria.and(clientCriteria).and(statusCriteria).and(maintienCriteria);
            return   services.vaultQueryByCriteria(criteria,CertificateState.class).getStates();
        }
        else {
            throw new IllegalArgumentException("Unrecognised entries");

        }

    }

    /// API consulter docs
    @GET
    @Path("dossier")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<DocumentState>> GetFolder(@QueryParam("client") Integer client) throws NoSuchFieldException {
        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
        Field client1 = DocumentSchemaV1.PersistentDocument.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, client);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);
        QueryCriteria criteria = generalCriteria.and(clientCriteria);
        return   services.vaultQueryByCriteria(criteria,DocumentState.class).getStates();

    }
////////////

    // api créer document
    @PUT
    @Path("CreateDoc")
    public Response CreateDoc(@QueryParam("doc") Integer doc, @QueryParam("client") Integer client, @QueryParam("status") int status, @QueryParam("nomdoc") String nomdoc, @QueryParam("expire") String expire) throws InterruptedException, ExecutionException {
        //DocumentFlow(Integer doc, Integer client, int status, String nomdoc, String expire)
        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DocumentFlow.class, doc, client, status, nomdoc, expire)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

}






