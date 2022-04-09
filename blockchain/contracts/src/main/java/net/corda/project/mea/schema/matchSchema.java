package net.corda.project.mea.schema;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.UUID;
//4.6 changes
import org.hibernate.annotations.Type;
import javax.annotation.Nullable;

/**
 * An IOUState schema.
 */
public class matchSchema extends MappedSchema {
    public matchSchema() {
        super(IOUSchema.class, 1, Arrays.asList(PersistentMatch.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "iou.changelog-master";
    }

    @Entity
    @Table(name = "matching")
    public static class PersistentMatch extends PersistentState {
        @Column(name = "sender") private final String sender;
        @Column(name = "receiver") private final String receiver;
        @Column(name = "linear_id") @Type (type = "uuid-char") private final UUID linearId;
        @Column(name = "networkname") private final String networkname;
        @Column(name = "tradedatetime") private final String tradedatetime;
        @Column(name = "tradetype") private final String tradetype;
        @Column(name = "buyerid") private final String buyerid;
        @Column(name = "buyervolume") private final String buyervolume;
        @Column(name = "buyercost") private final String buyercost;
        @Column(name = "sellerid") private final String sellerid;
        @Column(name = "sellervolume") private final String sellervolume;
        @Column(name = "sellercost") private final String sellercost;

        public PersistentMatch(String sender,
                             String receiver,
                             UUID linearId,
                             String networkname,
                             String tradedatetime,
                             String tradetype,
                             String buyerid,
                             String buyervolume,
                             String buyercost,
                             String sellerid,
                             String sellervolume,
                             String sellercost
                             ) {
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

        // Default constructor required by hibernate.
        public PersistentMatch() {
            this.sender = null;
            this.receiver = null;
            this.linearId = null;
            this.networkname = null;
            this.tradedatetime = null;
            this.tradetype = null;
            this.buyerid = null;
            this.buyervolume = null;
            this.buyercost = null;
            this.sellerid = null;
            this.sellervolume = null;
            this.sellercost = null;
        }

        public String getSender() {
            return sender;
        }
        public String getReceiver() {
            return receiver;
        }
        public UUID getId() {
            return linearId;
        }
        public String getnetworkname() {
            return networkname;
        }
        public String gettradedatetime() { return tradedatetime; }
        public String gettradetype() { return tradetype; }
        public String getbuyerid() {return buyerid; }
        public String getbuyervolume() { return buyervolume; }
        public String getbuyercost() { return buyercost; }
        public String getsellerid() { return sellerid; }
        public String getsellervolume() { return sellervolume; }
        public String getsellercost() { return sellercost; }

    }
}