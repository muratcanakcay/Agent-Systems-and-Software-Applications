import jade.core.Agent;
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

        // create the gateway agent
        Object[] gatewayArgs = new Object[1];
        gatewayArgs[0] = restaurantCuisine;
        AgentContainer ac = getContainerController();

        try {
            AgentController gateway = ac.createNewAgent(restaurantName+"Gateway", "GatewayAgent", gatewayArgs);
            gateway.start();
        }
        catch (Exception e){}

        System.out.println("ManagerAgent " + getAID().getName() + " has menu " + Menus.Cuisines.get("turkish")[0][0]);



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
