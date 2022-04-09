package net.corda.project.mea.states;

import net.corda.project.mea.contracts.matchContract;
import net.corda.project.mea.schema.matchSchema;
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
@BelongsToContract(matchContract.class)
public class matchState implements LinearState, QueryableState {
    private final Party sender;
    private final Party receiver;
    private final UniqueIdentifier linearId;
    private final String networkname;
    private final String tradedatetime;
    private final String tradetype;
    private final String buyerid;
    private final String buyervolume;
    private final String buyercost;
    private final String sellerid;
    private final String sellervolume;
    private final String sellercost;

    /**
     * @param value the value of the IOU.
     * @param lender the party issuing the IOU.
     * @param borrower the party receiving and approving the IOU.
     */
    public matchState(
                    Party sender,
                    Party receiver,
                    UniqueIdentifier linearId,
                    String networkname,
                    String tradedatetime,
                    String tradetype,
                    String buyerid,
                    String buyervolume,
                    String buyercost,
                    String sellerid,
                    String sellervolume,
                    String sellercost
    )
    {

        this.sender = sender;
        this.receiver = receiver;
        this.linearId = linearId;
        this.networkname = networkname;
        this.tradedatetime = tradedatetime;
        this.tradetype = tradetype;
        this.buyerid = buyerid;
        this.buyervolume = buyervolume;
        this.buyercost = buyercost;
        this.sellerid = sellerid;
        this.sellervolume = sellervolume;
        this.sellercost = sellercost;
    }


    public Party getSender() { return sender; }
    public Party getReceiver() { return receiver; }
    public String getnetworkname() { return networkname; }
    public String gettradedatetime() { return tradedatetime; }
    public String gettradetype() { return tradetype; }
    public String getbuyerid() { return buyerid; }
    public String getbuyervolume() { return buyervolume; }
    public String getbuyercost() { return buyercost; }
    public String getsellerid() { return sellerid; }
    public String getsellervolume() { return sellervolume; }
    public String getsellercost() { return sellercost; }

    @Override public UniqueIdentifier getLinearId() { return linearId; }

    @Override public List<AbstractParty> getParticipants() {
        return Arrays.asList(sender, receiver);
    }

    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof matchSchema) {
            return new matchSchema.PersistentMatch(
                    this.sender.getName().toString(),
                    this.receiver.getName().toString(),
                    this.linearId.getId(),
                   this.networkname,
                    this.tradedatetime,
                    this.tradetype,
                    this.buyerid,
                    this.buyervolume,
                    this.buyercost,
                    this.sellerid,
                    this.sellervolume,
                    this.sellercost
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new matchSchema());
    }

    @Override
    public String toString() {

        return String.format("{id:%s}", linearId);
    }
}