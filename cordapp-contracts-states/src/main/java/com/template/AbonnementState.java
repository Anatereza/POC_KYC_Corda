
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
    private final int Cert;
    private  Party Applicant;
    private final ArrayList<ArrayList<String>> Notifications;
    private final Boolean Status;

    /**
     * @param Cert the certiicate id
     * @param Applicant the applicant
     * @param Notifications the certificate notifications
     * @param Status the abonnement status
     *

     */


    public AbonnementState(int Cert, Party Applicant,  ArrayList<ArrayList<String>> Notifications, Boolean Status) {

        this.Cert = Cert;
        this.Applicant = Applicant;
        this.Notifications = Notifications;
        this.Status = Status;

    }


    public void setNotifications(ArrayList<String> notification) {
        Notifications.add(notification);
    }
    public int getCert() {
        return Cert;
    }
    public Party getApplicant() { return Applicant; }
    public  ArrayList<ArrayList<String>> getNotifications() {        return Notifications;    }
    public Boolean getStatus(){ return Status;}



    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(Applicant);

    }


    @Override public Iterable<MappedSchema> supportedSchemas() { return ImmutableList.of(new AbonnementSchemaV1());
    }





    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof AbonnementSchemaV1) {
            return new AbonnementSchemaV1.PersistentAbonnement(
                    this.Cert,
                    this.Applicant,
                    this.Status);

        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}