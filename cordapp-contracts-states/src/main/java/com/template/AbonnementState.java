
package com.template;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;


//  LinearState, QueryableState
public class AbonnementState implements ContractState, QueryableState {
    private final String Cert;
    private  Party Applicant;
    private Party Initiator;
    private final List<List<String>> Notifications;
    private final Boolean Status;

    /**
     * @param Cert the certiicate id
     * @param Applicant the applicant
     * @param Initiator the Initiator
     * @param Notifications the certificate notifications
     * @param Status the abonnement status
     *

     */


    public AbonnementState(String Cert, Party Applicant, Party Initiator,  List<List<String>> Notifications, Boolean Status) {

        this.Cert = Cert;
        //celui qui s'abonne
        this.Applicant = Applicant;
        //celui qui crée le certificat
        this.Initiator = Initiator;
        this.Notifications = Notifications;
        this.Status = Status;

    }


    public void setNotifications(List<String> notification) {
        Notifications.add(notification);
    }
    public String getCert() {
        return Cert;
    }
    public Party getApplicant() { return Applicant; }
    public Party getInitiator() { return Initiator; }

    public List<List<String>> getNotifications() {        return Notifications;    }
    public Boolean getStatus(){ return Status;}



    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(Applicant, Initiator);

    }


    @Override public Iterable<MappedSchema> supportedSchemas() { return ImmutableList.of(new AbonnementSchemaV1());
    }





    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof AbonnementSchemaV1) {
            return new AbonnementSchemaV1.PersistentAbonnement(
                    this.Cert,
                    this.Applicant,
                    this.Initiator,
                    this.Status);

        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}