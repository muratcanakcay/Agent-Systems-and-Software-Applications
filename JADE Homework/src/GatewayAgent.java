import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class GatewayAgent extends Agent
{
    @Override
    protected void setup() {
        Object[] args = getArguments();
        String cuisine = (String)args[0]; // cuisine of the restaurant - received from ManagerAgent during creation

        System.out.println("GatewayAgent " + getAID().getName() + " started with arguments " + cuisine);

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
    };

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
