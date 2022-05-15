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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientAgent extends Agent
{
    public List<AID> gatewayAgents = new ArrayList<AID>();
    public String desiredCuisine;


    @Override
    protected void setup() {
        Object[] args = getArguments();
        desiredCuisine = (String)args[0]; // cuisine of the restaurant - received from ManagerAgent during creation

        System.out.println("ClientAgent " + getAID().getName() + " desiredCuisine: " + desiredCuisine);

        addBehaviour(queryGateways);
    };

    Behaviour queryGateways = new WakerBehaviour(this, 5000) { // wait 5 seconds for restaurant agents to initialize

        public void onWake() {
            // Update the list of agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();

            sd.setType("restaurant");
            sd.setName(desiredCuisine);
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

            System.out.println("Client received info about GatewayAgents:");
            for (AID gatewayAgent : gatewayAgents) {
                System.out.println("      " + gatewayAgent);
            }

            if (gatewayAgents.size() == 0)
            {
                System.out.println("[ClientAgent] Unfortunately no restaurants are available with the desired cuisine : \"" + desiredCuisine + "\"");
                System.out.println("[ClientAgent] Stopping...");
                myAgent.doDelete();
            }

            // Perform the request
            myAgent.addBehaviour(sendCfp);
        }
    };

    Behaviour sendCfp = new OneShotBehaviour(this) {
        @Override
        public void action() {

            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (AID gatewayAgent : gatewayAgents) {
                cfp.addReceiver(gatewayAgent);
            }
            //cfp.setContent("kebap"); // sending reservation proposal for a specific dish, number of people and specified time
            try {
                cfp.setContentObject(new ReservationTemplate(new String[]{"kebap"}, 1, 1715));
            } catch (IOException e) {
                e.printStackTrace();
            }
            myAgent.send(cfp);
            System.out.println("[ClientAgent] sent proposals");



//            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//            msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
//            msg.setLanguage("English");
//            msg.setOntology("Ready-to-Play-Ontology");
//            msg.setContent(getAID().getLocalName()+ " is ready to play!");
//            System.out.println(getAID().getLocalName() + " is sending 'I'm ready to play' message!");
//
//            send(msg);
        }
    };



    @Override
    protected void takeDown()
    {
        System.out.println("[ClientAgent] " + getAID().getLocalName() + " stopped.");
        super.takeDown();
    }
}

