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
public class realSchema extends MappedSchema {
    public realSchema() {
        super(IOUSchema.class, 1, Arrays.asList(PersistentReal.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "iou.changelog-master";
    }

    @Entity
    @Table(name = "real")
    public static class PersistentReal extends PersistentState {
        @Column(name = "sender") private final String sender;
        @Column(name = "receiver") private final String receiver;
        @Column(name = "linear_id") @Type (type = "uuid-char") private final UUID linearId;
        @Column(name = "networkname") private final String networkname;
        @Column(name = "realdatetime") private final String realdatetime;
        @Column(name = "customertype") private final String customertype;
        @Column(name = "meterid") private final String meterid;
        @Column(name = "volume") private final String volume;

        public PersistentReal(
                                String sender,
                                String receiver,
                                UUID linearId,
                                String networkname,
                                String realdatetime,
                                String customertype,
                                String meterid,
                                String volume
        ) {
            this.sender = sender;
            this.receiver = receiver;
            this.linearId = linearId;
            this.networkname = networkname;
            this.realdatetime = realdatetime;
            this.customertype = customertype;
            this.meterid = meterid;
            this.volume = volume;

        }

        // Default constructor required by hibernate.
        public PersistentReal() {
            this.sender = null;
            this.receiver = null;
            this.linearId = null;
            this.networkname = null;
            this.realdatetime = null;
            this.customertype = null;
            this.meterid = null;
            this.volume = null;
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
        public String getrealdatetime() { return realdatetime; }
        public String getcustomertype() { return customertype; }
        public String getmeterid() {return meterid; }
        public String getvolume() { return volume; }

    }
}