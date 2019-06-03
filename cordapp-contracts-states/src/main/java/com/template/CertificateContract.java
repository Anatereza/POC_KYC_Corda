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

    public interface Commands extends CommandData {
        class Certificat implements Commands {}
    }

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        if (command.getValue() instanceof Commands.Certificat) {
            // Input checks - contract shape rules
            if (tx.getInputStates().size() != 0) throw new IllegalArgumentException("Certificate contract should have no input.");
            if (tx.getOutputStates().size() != 1) throw new IllegalArgumentException("Certificate contract should have one output.");
            if (tx.getCommands().size() != 1) throw new IllegalArgumentException("Certificate contract should have one command.");
            if (tx.outputsOfType(CertificateState.class).size() != 1) throw new IllegalArgumentException("Certificate contract output should be CertificateState");

            // Certificate specific checks - our contract validation rules
            final CertificateState certificateStateOutput = tx.outputsOfType(CertificateState.class).get(0);

            // Instance of ContractState object to hold data from user
            if (certificateStateOutput.getDocuments().size() < 1) throw new IllegalArgumentException("Certificate contract should have at least 1 document.");


        } else
            throw new IllegalArgumentException("Unrecognised command!");
    }
}

