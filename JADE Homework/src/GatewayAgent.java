import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;

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
        protected ACLMessage prepareResponse(ACLMessage propose)
        {
            System.out.println("[GatewayAgent] " + getAID().getName() + " received proposal: " + propose.getContent());
            sendQueryToManager(propose.getContent());
            System.out.println("[GatewayAgent] " + getAID().getName() + " sent query to its manager");







            return null;
        }
    };

    void sendQueryToManager(String content)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
        msg.addReceiver(myManagerAgent);
        msg.setLanguage("English");
        msg.setOntology("Answer-Ontology");
        msg.setContent(content);
        System.out.println("[GatewayAgent] " + getAID().getLocalName() + " is sending query to manager: " + content);

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
