package net.corda.project.mea.states;
import net.corda.project.mea.contracts.realContact;
import net.corda.project.mea.schema.realSchema;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import java.util.Arrays;
import java.util.List;

/**
 * The states object recording IOU agreements between two parties.
 *
 * A states must implement [ContractState] or one of its descendants.
 */

@BelongsToContract(realContact.class)

public class realState implements LinearState, QueryableState {
    private final Party sender;
    private final Party receiver;
    private final UniqueIdentifier linearId;
    private final String networkname;
    private final String realdatetime;
    private final String customertype;
    private final String meterid;
    private final String volume;



    /**
     * @param value the value of the IOU.
     * @param lender the party issuing the IOU.
     * @param borrower the party receiving and approving the IOU.
     */
    public realState(
            Party sender,
            Party receiver,
            UniqueIdentifier linearId,
            String networkname,
            String realdatetime,
            String customertype,
            String meterid,
            String volume

    )
    {

        this.sender = sender;
        this.receiver = receiver;
        this.linearId = linearId;
        this.networkname = networkname;
        this.realdatetime = realdatetime;
        this.customertype = customertype;
        this.meterid = meterid;
        this.volume = volume;
    }


    public Party getSender() { return sender; }
    public Party getReceiver() { return receiver; }
    public String getnetworkname() { return networkname; }
    public String getrealdatetime() { return realdatetime; }
    public String getcustomertype() { return customertype; }
    public String getmeterid() { return meterid; }
    public String getvolume() { return volume; }

    @Override public UniqueIdentifier getLinearId() { return linearId; }

    @Override public List<AbstractParty> getParticipants() {
        return Arrays.asList(sender, receiver);
    }

    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof realSchema) {
            return new realSchema.PersistentReal(
                    this.sender.getName().toString(),
                    this.receiver.getName().toString(),
                    this.linearId.getId(),
                    this.networkname,
                    this.realdatetime,
                    this.customertype,
                    this.meterid,
                    this.volume
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new realSchema());
    }

    @Override
    public String toString() {

        return String.format("{id:%s}", linearId);
    }
}