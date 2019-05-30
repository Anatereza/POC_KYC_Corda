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
public class DocumentSchemaV1 extends MappedSchema {
    public DocumentSchemaV1() {
        super(DocumentSchemaV1.class, 1, ImmutableList.of(PersistentDocument.class));
    }

    @Entity
    @Table(name = "document_states")
    public static class PersistentDocument extends PersistentState {
        @Column(name = "client") private final Integer Client;
        @Column(name = "doc") private final Integer Doc;
        @Column(name = "status") private final String Status;
        @Column(name = "date_expiration") private final String DateA;


        public PersistentDocument(Integer client, Integer doc, String status, String dateE) {
            this.Client = client;
            this.Doc = doc;
            this.Status = status;
            this.DateA = dateE;

        }

        // Default constructor required by hibernate.
        public PersistentDocument() {
            this.Client = 0;
            this.Doc = null;
            this.Status = null;
            this.DateA = null;

        }

        public Integer getClient() {
            return Client;
        }

        public Integer getDoc() {
            return Doc;
        }

        public String getStatus() {
            return Status;
        }

        public String getDateE() {
            return DateA;
        }


    }
}
