package com.template;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * An IOUState schema.
 */
public class AbonnementSchemaV1 extends MappedSchema {
    public AbonnementSchemaV1() {
        super(AbonnementSchema.class, 1, ImmutableList.of(PersistentAbonnement.class));
    }

    @Entity
    @Table(name = "abonnement_states")
    public static class PersistentAbonnement extends PersistentState {
        @Column(name = "certificate") private final String Cert;
        @Column(name = "applicant") private final Party Applicant;
        @Column(name = "initiator") private final Party Initiator;
        @Column(name = "status") private final boolean Status;




        public PersistentAbonnement(String cert, Party applicant,Party Initiator, boolean status) {
            this.Cert = cert;
            this.Applicant = applicant;
            this.Initiator = Initiator;
            this.Status = status;
        }

        // Default constructor required by hibernate.
        public PersistentAbonnement() {
            this.Cert = null;
            this.Applicant = null;
            this.Initiator = null;
            this.Status = false;
        }

        public String getCert() {
            return Cert;
        }

        public Party getApplicant() {
            return Applicant;
        }
        public Party Initiator() {
            return Initiator;
        }


        public boolean getStatus() {
            return Status;
        }



    }
}
