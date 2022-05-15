import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.Arrays;

public class ManagerAgent extends Agent
{
    String restaurantName = null; // name of the restaurant
    String restaurantCuisine = null; // cuisine of the restaurant
    Integer restaurantMenu = null; // menu of the restaurant
    Integer restaurantWorkingHours = null; // working hours of the restaurant
    Float restaurantPriceMultiplier = null; // price multiplier of the restaurant
    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args.length < 5 )
        {
            System.out.println("[ERRROR] [ManagerAgent] " + getAID().getName() + " Error in initial arguments");
        }

        restaurantName = (String)args[0]; // name of the restaurant
        restaurantCuisine = (String)args[1]; // cuisine of the restaurant
        restaurantMenu = Integer.parseInt((String)args[2]); // menu of the restaurant
        restaurantWorkingHours = Integer.parseInt((String)args[3]); // working hours of the restaurant
        restaurantPriceMultiplier = Float.parseFloat((String)args[4]); // price multiplier of the restaurant

        if (restaurantName == null || restaurantCuisine  == null || restaurantMenu == null || restaurantWorkingHours == null || restaurantPriceMultiplier <= 0)
        {
            System.out.println("[ERROR] [ManagerAgent] " + getAID().getName() + " Error in initial arguments");
        }

        System.out.println("[ManagerAgent] " + getAID().getName() + " started with arguments " + restaurantName + " "
                + restaurantCuisine + " " + restaurantMenu  + " " + restaurantWorkingHours + " " + restaurantPriceMultiplier);

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

//        System.out.println("ManagerAgent " + getAID().getName() + " has menu " + Restaurant.Menus.get("turkish")[0][0]);

        addBehaviour(waitForQueries);
    };

    Behaviour waitForQueries = new TickerBehaviour(this, 1000) {
        @Override
        public void onTick() {
            System.out.println("[ManagerAgent] " + getAID().getLocalName() + " is waiting for reservation request.");

            ACLMessage rcv = receive();

            if (rcv != null)
            {
                switch(rcv.getPerformative())
                {
                    case ACLMessage.QUERY_IF:
                        ReservationTemplate reservationDetails = null;

                        try {
                            reservationDetails = (ReservationTemplate)rcv.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }

                        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " received query: " + reservationDetails);

                        // check if all dishes are served
                        boolean allDishesPresent = true;
                        var menu = Arrays.stream((Restaurant.Menus.get(restaurantCuisine)[restaurantMenu])).toList(); //  list of dishes served at the restaurant

                        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " MENU:"  + menu);

                        for (String dish : reservationDetails.Dishes)
                        {
                            if (!menu.contains(dish))
                            {
                                System.out.println("[ManagerAgent] " + getAID().getLocalName() + " "  + dish + " is not served here");
                            }
                        }






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
