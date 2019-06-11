/* Automatique
package com.template;

public class CertificateSchemaV1 {
}
*/

package com.template;

import com.google.common.collect.ImmutableList;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import sun.applet.Main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.print.DocFlavor;
import java.util.UUID;

/**
 * A CertificateState schema.
 */
public class CertificateSchemaV1 extends MappedSchema {
    public CertificateSchemaV1() {
        super(CertificateSchemaV1.class, 1, ImmutableList.of(PersistentCertificate.class));
    }

    @Entity
    @Table(name = "document_states")
    public static class PersistentCertificate extends PersistentState {
        @Column(name = "certificate") private final String Cert;
        @Column(name = "client") private final String Client;
        @Column(name = "DocKYC") private final String DocKYC;
        @Column(name = "status") private final Integer Status;
        @Column(name = "maintenance") private final Integer Maintien;


        public PersistentCertificate(String cert, String client, String DocKYC, Integer status, Integer maintien) {
            this.Client = client;
            this.DocKYC = DocKYC;
            this.Cert = cert;
            this.Status = status;
            this.Maintien = maintien;

        }

        // Default constructor required by hibernate.
        public PersistentCertificate() {
            this.Client = null;
            this.DocKYC = null;
            this.Cert = null;
            this.Status = null;
            this.Maintien = null;

        }

        public String getClient() {
            return Client;
        }

        public String getDocKYC() {
            return DocKYC;
        }

        public String getCert() {
            return Cert;
        }

        public Integer getStatus() {
            return Status;
        }

        public Integer getMaintien() {
            return Maintien;
        }


    }
}
