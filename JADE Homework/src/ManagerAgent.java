import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class ManagerAgent extends Agent
{
    @Override
    protected void setup() {
        Object[] args = getArguments();
        String arg1 = (String)args[0]; // this returns the arg1
        String arg2 = (String)args[1]; // this returns the arg2

        System.out.println("Manager-agent " + getAID().getName() + " started with arguments " + arg1 + " " + arg2);

        



        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("restaurant");
        sd.setName(arg2);
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
        System.out.println("Seller-agent " + getAID().getName() + " terminating.");
    }
}
