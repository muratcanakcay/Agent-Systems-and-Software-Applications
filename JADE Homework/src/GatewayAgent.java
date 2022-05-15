import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ProposeResponder;

import java.io.IOException;
import java.io.Serializable;

public class GatewayAgent extends Agent
{
    AID myManagerAgent;
    @Override
    protected void setup() {
        Object[] args = getArguments();
        String cuisine = (String)args[0]; // cuisine of the restaurant - received from ManagerAgent during creation
        myManagerAgent = (AID)args[1];

        System.out.println("[GatewayAgent] " + getAID().getName() + " started with:\n       Cuisine: " + cuisine + "\n" +
                "       ManagerAgent:  " + myManagerAgent);


        // the Agent registers itself to DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("restaurant");
        sd.setName(cuisine); // cuisine of the restaurant
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(respondCfp);
    };

    Behaviour respondCfp = new ProposeResponder(this, MessageTemplate.MatchAll()) {
        //@Override
        protected ACLMessage prepareResponse(ACLMessage proposal)
        {
            ReservationTemplate reservationDetails = null;

            try {
                reservationDetails = (ReservationTemplate)proposal.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            System.out.println("[GatewayAgent] " + getAID().getName() + " received proposal: " + reservationDetails);

            try {
                sendQueryToManager(reservationDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("[GatewayAgent] " + getAID().getName() + " sent query to its manager");

            ACLMessage rcv = blockingReceive(5000);
            System.out.println("[GatewayAgent] " + getAID().getName() + " waiting for reply from its manager");

            if (rcv != null)
            {
                switch (rcv.getPerformative())
                {
                    case ACLMessage.INFORM:
                        System.out.println("[GatewayAgent] " + getAID().getLocalName() + " received query response: " + rcv.getContent());
                }
            }

            if (rcv == null || rcv.getContent().equals("0.0"))
            {
                ProposalReply replyContent = new ProposalReply("This restaurant will not be able to fullfill your reservation request.", 0.0);
                ACLMessage negativeReply = proposal.createReply();
                negativeReply.setPerformative(ACLMessage.REJECT_PROPOSAL);

                try {
                    negativeReply.setContentObject(replyContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return negativeReply;
            }
            else
            {
                ProposalReply replyContent = new ProposalReply("This restaurant can fullfill your reservation request.", Double.parseDouble(rcv.getContent()));
                ACLMessage positiveReply = proposal.createReply();
                positiveReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                try {
                    positiveReply.setContentObject(replyContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return positiveReply;
            }
        }
    };

    void sendQueryToManager(Serializable reservationDetails) throws IOException {
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
        msg.addReceiver(myManagerAgent);
        msg.setLanguage("English");
        msg.setOntology("Answer-Ontology");
        msg.setContentObject(reservationDetails);
        System.out.println("[GatewayAgent] " + getAID().getLocalName() + " is sending query to its manager: " + reservationDetails);

        send(msg);
    }

    @Override
    protected void takeDown()
    {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Close the GUI
        // myGui.dispose();
        // Printout a dismissal message
        System.out.println("GatewayAgent " + getAID().getName() + " terminating.");
    }
}
