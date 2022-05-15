import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class ClientAgent extends Agent
{
    @Override
    protected void setup() {
        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                // Update the list of agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();

                sd.setType("restaurant");
                sd.setName("turkish");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    AID[] tempAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        tempAgents[i] = result[i].getName();
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                // Perform the request
                // myAgent.addBehaviour(new RequestPerformer());
            }
        } );
    };

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getLocalName());
        super.takeDown();
    }
}

