import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class ManagerAgent extends Agent
{
    @Override
    protected void setup() {
        Object[] args = getArguments();
        String arg1 = (String)args[0]; // name of the restaurant
        String arg2 = (String)args[1]; // cuisine of the restaurant
        String arg3 = (String)args[2]; // menu of the restaurant

        System.out.println("Manager-agent " + getAID().getName() + " started with arguments " + arg1 + " " + arg2 + " " + arg3);

        // create the gateway agent
        jade.core.Runtime runtime = jade.core.Runtime.instance();

        Object[] gatewayArgs = new Object[1];
        gatewayArgs[0] = arg2; // cuisine of the restaurant  - to be passed to gatewayAgent
        AgentContainer ac = getContainerController();
        try {
            AgentController gateway = ac.createNewAgent(arg1+"Gateway", "GatewayAgent", gatewayArgs);
            gateway.start();
        }
        catch (Exception e){}






    };

    @Override
    protected void takeDown()
    {
        // Close the GUI
        // myGui.dispose();
        // Printout a dismissal message
        System.out.println("ManagerAgent " + getAID().getName() + " terminating.");
    }
}
