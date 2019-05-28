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
    @GET
    @Path("Request")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<DemandeState>> GetRequest0(@QueryParam("client") int param1, @QueryParam("doc") int param2) throws NoSuchFieldException {
        QueryCriteria generalcriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

        Field client1 = DemandeSchemaV1.PersistentDemande.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, param1);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);

        Field doc1 = DemandeSchemaV1.PersistentDemande.class.getDeclaredField("Doc");
        CriteriaExpression docIndex = Builder.equal(doc1, param2);
        QueryCriteria docCriteria = new QueryCriteria.VaultCustomQueryCriteria(docIndex);

        if(param1 == 0 && param2 == 0) { return   services.vaultQueryByCriteria(generalcriteria,DemandeState.class).getStates(); }


        else if(param2 == 0) {
            QueryCriteria criteria = generalcriteria.and(clientCriteria);
            return   services.vaultQueryByCriteria(criteria,DemandeState.class).getStates();
        }
        else if(param1 == 0) {
            QueryCriteria criteria = generalcriteria.and(docCriteria);
            return   services.vaultQueryByCriteria(criteria,DemandeState.class).getStates();
        }
        else {
            QueryCriteria criteria = generalcriteria.and(clientCriteria).and(docCriteria);
            return   services.vaultQueryByCriteria(criteria,DemandeState.class).getStates();
        }

    }
    @GET
    @Path("Subscribes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<SubscribeState>> GetSubscribe() {
        return services.vaultQuery(SubscribeState.class).getStates();
    }

    @GET
    @Path("Request1")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<DemandeState>> GetRequest() {
        return services.vaultQuery(DemandeState.class).getStates();
    }

    @GET
    @Path("Request2")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<DemandeState>> GetRequest1(@QueryParam("param1") int param1) throws NoSuchFieldException {
        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
        Field client1 = DemandeSchemaV1.PersistentDemande.class.getDeclaredField("Client");
        CriteriaExpression clientIndex = Builder.equal(client1, param1);
        QueryCriteria clientCriteria = new QueryCriteria.VaultCustomQueryCriteria(clientIndex);
        QueryCriteria criteria = generalCriteria.and(clientCriteria);
        return   services.vaultQueryByCriteria(criteria,DemandeState.class).getStates();
        /* return Response.status(OK).entity(results).build(); */
    }

    /**
     * Accessible at /api/template/templateGetEndpoint.
     */

    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response templateGetEndpoint() {
        return Response.ok("Template GET endpoint.").build();
    }

    @PUT
    @Path("sendRequest")
    public Response sendRequest(@QueryParam("doc") int doc, @QueryParam("client") int client) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(DemandeFlow.class, doc, client)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }


    @PUT
    @Path("updateRequest1")
    public Response updateRequest1(@QueryParam("doc") int doc, @QueryParam("client") int client, @QueryParam("etat") String etat) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(UpdateDemandeFlow.class, doc, client, etat)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }



    @PUT
    @Path("subscribeRequest")
    public Response subscribeRequest(@QueryParam("doc") int doc, @QueryParam("client") int client) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(SubscribeFlow.class, doc, client)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }

    @PUT
    @Path("updateSubscribeRequest")
    public Response updateSubscribeRequest(@QueryParam("doc") int doc, @QueryParam("client") int client) throws InterruptedException, ExecutionException {
        // CordaX500Name OtherX1 = CordaX500Name.parse(other1);
        //Party OtherXX1 = services.wellKnownPartyFromX500Name(OtherX1);


        final SignedTransaction signedTx = services
                .startTrackedFlowDynamic(UpdateSubscribeFlow.class, doc, client)
                .getReturnValue()
                .get();

        final String msg = String.format("Request id %s committed to ledger.\n", signedTx.getId());
        return Response.status(CREATED).entity(msg).build();


    }
}



