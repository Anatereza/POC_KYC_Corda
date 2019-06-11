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
public  class DocKYCState implements ContractState, QueryableState {
    private final String DocKYC;
    private final String Date;
    private final Party Entite;
    private  Party Other1;
    private  Party Other2;



    /**
     * @param DocKYC the DocKYC unique id
     * @param Date the timestamp
     * @param Entite creator
          */


    public DocKYCState(String DocKYC, String Date, Party Entite, Party other1,
                         Party other2) {

        this.DocKYC = DocKYC;
        this.Date = Date;
        this.Entite = Entite;
        this.Other1 = other1;
        this.Other2 = other2;

    }

    public void setOther1(Party other1) {
        Other1 = other1;
    }
    public void setOther2(Party other2) {
        Other2 = other2;
    }
    public String getDocKYC() {
        return DocKYC;
    }

    public Party getEntite() {
        return Entite;
    }
    public String getSDate() {
        return Date;
    }

    public Party getOther1() {
        return Other1;
    }
    public Party getOther2() {
        return Other2;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(Entite, Other1, Other2);

    }


    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new DocKYCSchemaV1());
    }





    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof DocKYCSchemaV1) {
            return new DocKYCSchemaV1.PersistentDocKYC(
                    this.DocKYC,
                    this.Date,
                    this.Entite);


        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}