package com.template;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

/*
 * CashContract, governing how CashState will evolve over time.
 *
 * See src/main/java/java_example/LandContract.java for contract example.
 *
 */

public class CertificateContract implements Contract {
    public static String CERTIFICATE_CONTRACT_ID = "com.template.CertificateContract";

    /** Set of commands defined to execute certificate contract Or the set of abstract methods, accessed through interface. */
    public interface Commands extends CommandData {
        class Certificat implements Commands {}
        class Status implements  Commands {}
        class Maintenance implements  Commands {}
        class DateProch implements  Commands {}
    }


    /**
     * Overrides the verify method in all the ledger transaction, all the state objects go under state transitions with
     * the help of the inputs/outputs/commands. It must also throw an exception if any problem occurs that should prevent
     * state transition. It takes a single object rather than an argument so that additional data can be added without
     * breaking binary compatibility with existing contract code.
     *
     * @param tx
     * @throws IllegalArgumentException
     * @NotNull LedgerTransaction
     *
     */

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        // Arbitrary data passed to the contract program of each input state.
        // We should only ever receive one command at a time, else throw an exception
        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);

        // Invoke Certificat command
        if (command.getValue() instanceof Commands.Certificat) {
            // Input checks - contract shape rules
            if (tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("Certificate contract should have no input.");
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Certificate contract should have one output.");
            if (tx.getCommands().size() != 1)
                throw new IllegalArgumentException("Certificate contract should have one command.");
            if (tx.outputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate contract output should be CertificateState");

            //Certificate specific checks - our contract validation rules
            //final CertificateState certificateStateOutput = tx.outputsOfType(CertificateState.class).get(0);

            // Instance of ContractState object to hold data from user
            //if (certificateStateOutput.getDocuments().size() < 1) throw new IllegalArgumentException("Certificate contract should have at least 1 document.");

        }

        // Invoke Status command
        else if (command.getValue() instanceof Commands.Status) {
            // Checking the shapes of the transaction - how certificate state input and output state look like.
            if (tx.getInputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update status should have one inputs.");
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update status should have one output.");
            if (tx.getCommands().size() != 1)
                throw new IllegalArgumentException("Certificate update status should have one status command.");
            if (tx.inputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update status input should be an CertificateState.");
            if (tx.outputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update status output should be an CertificateState.");

            //Vérifier que l'entité qui modifie le certificat est bien l'entité qui l'a crée

            // Grabbing the transaction's output contents.
            final CertificateState certificateStateInput = tx.inputsOfType(CertificateState.class).get(0);
            final CertificateState certificateStateOutput = tx.outputsOfType(CertificateState.class).get(0);
            if (!(certificateStateInput.getInitiator().equals(certificateStateOutput.getInitiator())))
                throw new IllegalArgumentException("Certificate state input and output should not have different initiators.");



        } else if (command.getValue() instanceof Commands.Maintenance) {
            // Checking the shapes of the transaction - how certificate state input and output state look like.
            if (tx.getInputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update maintien should have one inputs.");
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update maintien should have one output.");
            if (tx.getCommands().size() != 1)
                throw new IllegalArgumentException("Certificate update maintien should have one status command.");
            if (tx.inputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update maintien input should be an CertificateState.");
            if (tx.outputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update maintien output should be an CertificateState.");


            //Vérifier que l'entité qui modifie le certificat est bien l'entité qui l'a crée


        } else if (command.getValue() instanceof Commands.DateProch) {
            // Checking the shapes of the transaction - how certificate state input and output state look like.
            if (tx.getInputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update date prochaine certif should have one inputs.");
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Certificate update date prochaine certif should have one output.");
            if (tx.getCommands().size() != 1)
                throw new IllegalArgumentException("Certificate update date prochaine certif should have one status command.");
            if (tx.inputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update date prochaine certif input should be an CertificateState.");
            if (tx.outputsOfType(CertificateState.class).size() != 1)
                throw new IllegalArgumentException("Certificate update date prochaine certif output should be an CertificateState.");

        } else throw new IllegalArgumentException("Unrecognised command.");
    }
}

