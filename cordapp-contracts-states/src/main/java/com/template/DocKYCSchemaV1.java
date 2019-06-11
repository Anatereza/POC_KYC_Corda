package com.template;

import com.google.common.collect.ImmutableList;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * A DocumentState schema.
 */
public class DocKYCSchemaV1 extends MappedSchema {
    public DocKYCSchemaV1() {
        super(DocumentSchemaV1.class, 1, ImmutableList.of(PersistentDocKYC.class));
    }

    @Entity
    @Table(name = "document_states")
    public static class PersistentDocKYC extends PersistentState {
        @Column(name = "DocKYC")private final String DocKYC;
        @Column(name = "Date") private final String Date;
        @Column(name = "Entite") private final Party Entite;



        public PersistentDocKYC(String docKYC, String date, Party entite) {
            this.DocKYC = docKYC;
            this.Date = date;
            this.Entite  = entite;


        }

        // Default constructor required by hibernate.
        public PersistentDocKYC() {
            this.DocKYC = null;
            this.Date = null;
            this.Entite  = null;


        }

        public String getDocKYC() {
            return DocKYC;
        }

        public String getDate() {
            return Date;
        }

        public Party getEntite() {
            return Entite;
        }


    }
}
