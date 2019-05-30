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
        @Column(name = "certificate") private final Integer Cert;
        @Column(name = "client") private final Integer Client;
        @Column(name = "status") private final String Status;
        @Column(name = "maintenance") private final Boolean Maintien;


        public PersistentCertificate(Integer cert, Integer client, String status, Boolean maintien) {
            this.Client = client;
            this.Cert = cert;
            this.Status = status;
            this.Maintien = maintien;

        }

        // Default constructor required by hibernate.
        public PersistentCertificate() {
            this.Client = 0;
            this.Cert = null;
            this.Status = null;
            this.Maintien = null;

        }

        public Integer getClient() {
            return Client;
        }

        public Integer getCert() {
            return Cert;
        }

        public String getStatus() {
            return Status;
        }

        public Boolean getMaintien() {
            return Maintien;
        }


    }
}
