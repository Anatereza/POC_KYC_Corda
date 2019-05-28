
package com.template;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

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
    private final String Notification;
    private final Boolean Status;

    /**
     * @param Cert the certiicate id
     * @param Applicant the applicant
     * @param Notification the certificate notification
     * @param Status the abonnement status
     *

     */


    public AbonnementState(int Cert, Party Applicant, String Notification, Boolean Status) {

        this.Cert = Cert;
        this.Applicant = Applicant;
        this.Notification = Notification;
        this.Status = Status;

    }

    public int getCert() {
        return Cert;
    }
    public Party getApplicant() { return Applicant; }
    public String getNotification() {        return Notification;    }
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