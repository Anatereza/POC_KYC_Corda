/*Automatique
package com.template;

public class CertificateState {
}
*/

package com.template;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;


//  LinearState, QueryableState
public  class CertificateState implements ContractState, QueryableState {
    private final Integer Cert;
    private final Integer Client;
    // trancodage statut : 0=null, 1=valide, 2=expiré, 3 = revoqué
    private final Integer Status;
    // trancodage maintien : 0=null, 1=maintenu, 2=non maintenu
    private final Integer Maintien;
    private final Party Initiator;
    private final String Profil;
    private final List<String> Documents;
    private final String Description;
    private final String DateCreation;
    private final String DateProchaineCert;
    private Party Other1;
    private Party Other2;

    /**
     * @param Cert the Cert id
     * @param Client the client id
     * @param Status the status
     * @param Maintien the maintenance
     * @param Initiator the creator
     * @param Profil the client profil
     * @param Documents the documents of the certificate
     * @param Description the certificate description
     * @param DateCreation the timestamp
     * @param DateProchaineCert the expiration date
     * */


    public CertificateState(Integer Cert, Integer Client, Integer Status, Integer Maintien, Party Initiator, String Profil, List<String> Documents, String Description, String DateCreation, String DateProchaineCert, Party other1,
                            Party other2) {

        this.Cert = Cert;
        this.Client = Client;
        this.Status = Status;
        this.Maintien = Maintien;
        this.Initiator = Initiator;
        this.Profil = Profil;
        this.Documents = Documents;
        this.Description = Description;
        this.DateCreation = DateCreation;
        this.DateProchaineCert = DateProchaineCert;
        this.Other1 = other1;
        this.Other2 = other2;


    }


    public void setOther1(Party other1) { Other1 = other1; }
    public void setOther2(Party other2) {
        Other2 = other2;
    }

    public Integer getCert() {
        return Cert;
    }
    public Integer getClient() {
        return Client;
    }

    public Party getInitiator() {
        return Initiator;
    }
    public Integer getStatus() {
        return Status;
    }
    public Integer getMaintien() {
        return Maintien;
    }
    public String getProfil() {
        return Profil;
    }
    public List<String> getDocuments() {        return Documents;    }
    public String getDescription() {
        return Description;
    }
    public String getDateCreation() {        return DateCreation;    }
    public String getDateProchaineCert() {        return DateProchaineCert;    }



    public Party getOther1() {
        return Other1;
    }
    public Party getOther2() {
        return Other2;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(Initiator, Other1, Other2);

    }


    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new CertificateSchemaV1());
    }





    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof CertificateSchemaV1) {
            return new CertificateSchemaV1.PersistentCertificate(
                    this.Cert,
                    this.Client,
                    this.Status,
                    this.Maintien);

        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}



