package net.corda.project.mea.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import net.corda.core.contracts.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import net.corda.project.mea.flows.matchFlow;
import net.corda.project.mea.states.matchState;

import net.corda.project.mea.flows.realFlow;
import net.corda.project.mea.states.realState;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "http://www.mea.meaxpress.com")
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(RestController.class);
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }

    /** Helpers for filtering the network map cache. */
    public String toDisplayString(X500Name name){
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isMe(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    @Configuration
    class Plugin {
        @Bean
        public ObjectMapper registerModule() {
            return JacksonSupport.createNonRpcMapper();
        }
    }

    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }

    @GetMapping(value = "/me",produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami(){
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }


    //real
    @GetMapping(value = "/corda/real/all",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<realState>> getRealState() {
        return proxy.vaultQuery(realState.class).getStates();
    }
    @PostMapping(value = "/corda/real/pop",produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<List<StateAndRef<realState>>> getRealPop(HttpServletRequest request) {
        String linearId = request.getParameter("linearId");
        List<StateAndRef<realState>> real = proxy.vaultQuery(realState.class).getStates().stream().filter(
                it -> it.getState().getData().getLinearId().getId().equals(UUID.fromString(linearId))).collect(Collectors.toList());
        return ResponseEntity.ok(real);
    }
    @PostMapping (value = "/corda/real/push" , produces =  APPLICATION_JSON_VALUE , headers =  "Content-Type=application/x-www-form-urlencoded" )
    public ResponseEntity<String> getRealPush(HttpServletRequest request) throws IllegalArgumentException {
        String party = request.getParameter("partyName");
        String networkname = request.getParameter("networkname");
        String realdatetime = request.getParameter("realdatetime");
        String customertype = request.getParameter("customertype");
        String meterid = request.getParameter("meterid");
        String volume = request.getParameter("volume");

        // Get party objects for myself and the counterparty.
        CordaX500Name partyX500Name = CordaX500Name.parse(party);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);

        // Create a new IOU state using the parameters given.
        try {
            // Start the IOUIssueFlow. We block and waits for the flow to return.
            SignedTransaction result = proxy.startTrackedFlowDynamic(
                    realFlow.Initiator.class,
                    otherParty,
                    networkname,
                    realdatetime,
                    customertype,
                    meterid,
                    volume
            ).getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(""+result.getTx().getOutput(0));
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }




    //match
    @GetMapping(value = "/corda/matching/all",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<matchState>> getMatchAll() {
        // Filter by state type: IOU.
        return proxy.vaultQuery(matchState.class).getStates();
    }

    @PostMapping(value = "/corda/matching/pop",produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<List<StateAndRef<matchState>>> getMatchingPop(HttpServletRequest request) {

        String linearId = request.getParameter("linearId");
        List<StateAndRef<matchState>> matching = proxy.vaultQuery(matchState.class).getStates().stream().filter(
                it -> it.getState().getData().getLinearId().getId().equals(UUID.fromString(linearId))).collect(Collectors.toList());
        return ResponseEntity.ok(matching);

    }


    @PostMapping (value = "/corda/matching/push" , produces =  APPLICATION_JSON_VALUE , headers =  "Content-Type=application/x-www-form-urlencoded" )
    public ResponseEntity<String> getMatchingPush(HttpServletRequest request) throws IllegalArgumentException {
        String party = request.getParameter("partyName");
        String networkname = request.getParameter("networkname");
        String tradedatetime = request.getParameter("tradedatetime");
        String tradetype = request.getParameter("tradetype");
        String buyerid = request.getParameter("buyerid");
        String buyervolume = request.getParameter("buyervolume");
        String buyercost = request.getParameter("buyercost");
        String sellerid = request.getParameter("sellerid");
        String sellervolume = request.getParameter("sellervolume");
        String sellercost = request.getParameter("sellercost");
        // Get party objects for myself and the counterparty.
        CordaX500Name partyX500Name = CordaX500Name.parse(party);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);

        // Create a new IOU state using the parameters given.
        try {
            // Start the IOUIssueFlow. We block and waits for the flow to return.
            SignedTransaction result = proxy.startTrackedFlowDynamic(
                    matchFlow.Initiator.class,
                    otherParty,
                    networkname,
                    tradedatetime,
                    tradetype,
                    buyerid,
                    buyervolume,
                    buyercost,
                    sellerid,
                    sellervolume,
                    sellercost
            ).getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(""+result.getTx().getOutput(0));
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }



}