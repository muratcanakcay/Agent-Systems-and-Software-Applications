import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ProposeInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ClientAgent extends Agent
{
    // YOU CAN SET THE RESERVATION DETAILS HERE:
//    public ReservationTemplate reservationToMake = new ReservationTemplate("turkish", new String[]{"kebap", "fasulye"}, 2, 1715);
    public ReservationTemplate reservationToMake = null;
    public String desiredCuisine;
    public Integer noOfPeople;
    public Integer  desiredTime;
    public String[] desiredDishes = null;

    public List<AID> gatewayAgents = new ArrayList<AID>();
    public HashMap<String, Double> positiveResponses= new HashMap<String, Double>();


    @Override
    protected void setup() {
        Object[] args = getArguments();
        desiredCuisine = (String)args[0];
        noOfPeople = Integer.parseInt((String)args[1]);
        desiredTime = Integer.parseInt((String)args[2]);

        desiredDishes = new String[args.length-3];

        for (int i = 3; i < args.length; i++)
        {
            desiredDishes[i-3] = (String)args[i];
        }

        reservationToMake = new ReservationTemplate(desiredCuisine, desiredDishes, noOfPeople, desiredTime);

        System.out.println("[ClientAgent] " + getAID().getName() + " desiredCuisine: " + reservationToMake.cuisine);

        addBehaviour(queryGateways);
    };

    Behaviour queryGateways = new WakerBehaviour(this, 5000) { // wait 5 seconds for restaurant agents to initialize

        public void onWake() {
            // Update the list of agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();

            sd.setType("restaurant");
            sd.setName(reservationToMake.cuisine);
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                for (DFAgentDescription dfAgentDescription : result) {
                    gatewayAgents.add(dfAgentDescription.getName());
                }
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }

            System.out.println("[ClientAgent] received info about GatewayAgents:");
            for (AID gatewayAgent : gatewayAgents) {
                System.out.println("      " + gatewayAgent);
            }

            if (gatewayAgents.size() == 0)
            {
                System.out.println("[ClientAgent] Unfortunately no restaurants are available with the desired cuisine : \"" + reservationToMake.cuisine + "\"");
                System.out.println("[ClientAgent] Stopping...");
                myAgent.doDelete();
            }

            // Perform the Call for Proposal to the Gateway Agents
            sendCFP();
        }
    };

    public void sendCFP()
    {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

        for (AID gatewayAgent : gatewayAgents) {
            cfp.addReceiver(gatewayAgent);
        }

        try {
            cfp.setContentObject(reservationToMake);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addBehaviour(new ProposeInitiator(this, cfp)
        {
            protected void handleAcceptProposal(ACLMessage accept_proposal)
            {
                ProposalReply reply = null;

                try {
                    reply = (ProposalReply) accept_proposal.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                System.out.println("[ClientAgent] Received positive reply from " + accept_proposal.getSender().getName() + " : " +
                    reply.explanation + " " + "Cost will be: " + reply.cost);

                positiveResponses.put(accept_proposal.getSender().getName(), reply.cost);
            };

            protected void handleRejectProposal(ACLMessage reject_proposal)
            {
                ProposalReply reply = null;

                try {
                    reply = (ProposalReply) reject_proposal.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                System.out.println("[ClientAgent] Received negative reply from " + reject_proposal.getSender().getName() + " : " +
                        reply.explanation + " " + "Cost will be: " + reply.cost);
            };

            protected void handleAllResponses(java.util.Vector responses)
            {
                removeBehaviour(this);
                makeDecision();
            }
        });
    }

    public void makeDecision()
    {
        System.out.println("[ClientAgent] Number of positive responses :" + positiveResponses.size());



        if (positiveResponses.size() == 1)
        {
            Object firstKey = positiveResponses.keySet().toArray()[0];
            Object valueForFirstKey = positiveResponses.get(firstKey);
            System.out.println("[ClientAgent] There was only 1 positive response so I choose it:");
            System.out.println("[ClientAgent] " + firstKey + ", and the cost is " + valueForFirstKey);
        }
        else
        {
            Object[] keys = positiveResponses.keySet().toArray();
            System.out.println("[ClientAgent] There were " + positiveResponses.size() + " positive response so I choose randomly:");

            Random rng = new Random();
            int choice = rng.nextInt(positiveResponses.size());
            Object chosenKey = positiveResponses.keySet().toArray()[choice];
            Object valueForChoice = positiveResponses.get(chosenKey);

            System.out.println("[ClientAgent] " + chosenKey + ", and the cost is " + valueForChoice);
        }
    }

    @Override
    protected void takeDown()
    {
        System.out.println("[ClientAgent] " + getAID().getLocalName() + " stopped.");
        super.takeDown();
    }
}

