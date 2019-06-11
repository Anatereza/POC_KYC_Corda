package com.template;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public  class DocumentState implements ContractState, QueryableState {
    private final String Doc;
    private final String Client;
    private final Party Initiator;
    private final String NomDoc;
    private final int Status;
    private final String DateA;
    private final String DateE;
    private final String DateMAJ;
    private  Party Other1;
    private  Party Other2;



    /**
     * @param Doc the Doc unique id
     * @param Client the client id
     * @param Initiator the creator
     * @param NomDoc the Doc name
     * @param Status the status
     * @param DateA the timestamp
     * @param DateE the expiration date
     * @param DateMAJ the MAJ date
     */


    public DocumentState(String Doc, String Client, Party Initiator, Party other1,
                         Party other2, int Status, String NomDoc, String DateA, String DateE, String DateMAJ) {

        this.Doc = Doc;
        this.Client = Client;
        this.Initiator = Initiator;
        this.Other1 = other1;
        this.Other2 = other2;
        this.Status = Status;
        this.NomDoc = NomDoc;
        this.DateA = DateA;
        this.DateE = DateE;
        this.DateMAJ = DateMAJ;

    }

    public void setOther1(Party other1) {
        Other1 = other1;
    }
    public void setOther2(Party other2) {
        Other2 = other2;
    }
    public String getDoc() {
        return Doc;
    }
    public String getClient() {
        return Client;
    }

    public Party getInitiator() {
        return Initiator;
    }
    public int getStatus() {
        return Status;
    }
    public String getNomDoc() {
        return NomDoc;
    }

    public String getDateA() {        return DateA;    }
    public String getDateE() {        return DateE;    }
    public String getDateMAJ() {        return DateMAJ;    }



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
        return ImmutableList.of(new DocumentSchemaV1());
    }





    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof DocumentSchemaV1) {
            return new DocumentSchemaV1.PersistentDocument(
                    this.Doc,
                    this.Client,
                    this.NomDoc,
                    this.Status,
                    this.DateA);

        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}