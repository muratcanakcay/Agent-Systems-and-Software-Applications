import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class ManagerAgent extends Agent
{
    @Override
    protected void setup() {
        Object[] args = getArguments();
        String restaurantName = (String)args[0]; // name of the restaurant
        String restaurantCuisine = (String)args[1]; // cuisine of the restaurant
        String restaurantMenu = (String)args[2]; // menu of the restaurant

        System.out.println("ManagerAgent " + getAID().getName() + " started with arguments " + restaurantName + " " + restaurantCuisine + " " + restaurantMenu);

        // create the Gateway agent for this restaurant
        Object[] gatewayArgs = new Object[2];
        gatewayArgs[0] = restaurantCuisine;
        gatewayArgs[1] = getAID();

        AgentContainer ac = getContainerController();

        try {
            AgentController gateway = ac.createNewAgent(restaurantName+"Gateway", "GatewayAgent", gatewayArgs);
            gateway.start();
        }
        catch (Exception e){}

        System.out.println("ManagerAgent " + getAID().getName() + " has menu " + Menus.Cuisines.get("turkish")[0][0]);

        addBehaviour(waitForQueries);
    };

    Behaviour waitForQueries = new CyclicBehaviour(this) {
        @Override
        public void action() {
            System.out.println("[ManagerAgent] " + getAID().getLocalName() + " is waiting for reservation request.");

            ACLMessage rcv = receive();

            if (rcv != null)
            {
                switch(rcv.getPerformative())
                {
                    case ACLMessage.QUERY_IF:
                        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " received query: " + rcv.getContent());

//                        if(rcv.getOntology().equals("Play-Ontology"))
//                        {
//                            String result = play();
//                            sendResult(result);
//                        }
//                        break;
//                    case ACLMessage.INFORM:
//                        if (rcv.getOntology() == "Game-Over-Ontology") {
//                            doDelete();
//                        }
                }
            }

            block(1000);

        }
    };

    void sendResult(String answer)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Answer-Ontology");
        msg.setContent(answer);
        System.out.println(getAID().getLocalName() + " is sending answer: " + answer);

        send(msg);
    }

    @Override
    protected void takeDown()
    {
        // Close the GUI
        // myGui.dispose();
        // Printout a dismissal message
        System.out.println("ManagerAgent " + getAID().getName() + " terminating.");
    }
}
