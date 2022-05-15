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
    Double restaurantPriceMultiplier = null; // price multiplier of the restaurant
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
        restaurantPriceMultiplier = Double.parseDouble((String)args[4]); // price multiplier of the restaurant

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

                        assert reservationDetails != null;

                        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " received query: " + reservationDetails);

                        // check if all requested dishes are served
                        boolean allDishesPresent = checkDishes(restaurantCuisine, restaurantMenu, reservationDetails.Dishes);
                        // check if restaurant is open at requested time
                        boolean restaurantOpen = checkRestaurantWorking(restaurantWorkingHours, reservationDetails.time);
                        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " reservationTime is within WORKINGHOURS: "  + restaurantOpen);

                        if (!allDishesPresent || !restaurantOpen)  // reply negative with cost=0.0
                        {
                           ACLMessage reply = rcv.createReply();
                           reply.setPerformative(ACLMessage.INFORM);
                           reply.setContent("0.0");
                           send(reply);
                        }
                        else // reply positive with the cost of the dishes
                        {
                            Double cost = calculateCost(reservationDetails.Dishes, restaurantPriceMultiplier);
                            ACLMessage reply = rcv.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(String.valueOf(cost));
                            send(reply);
                        }
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

    Double calculateCost(String[] dishes, Double restaurantPriceMultiplier)
    {
        Double sum = 0.0;
        for(String dish : dishes)
        {
            sum += Restaurant.Prices.get(dish);
        }

        sum *= restaurantPriceMultiplier;

        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " calculated cost is: "  + sum);

        return sum;
    }

    boolean checkRestaurantWorking(Integer restaurantWorkingHours, Integer requestedTime)
    {
        var openingHour = Restaurant.WorkingHours[restaurantWorkingHours][0];
        var closingHour = Restaurant.WorkingHours[restaurantWorkingHours][1];

        System.out.println("[ManagerAgent] " + getAID().getLocalName() + "Checking if " + requestedTime + " is within WORKINGHOURS: "  + openingHour + "-" + closingHour);
        if (closingHour > openingHour)
        {
            return (requestedTime>=openingHour && requestedTime<=closingHour);
        }
        else
        {
            return ((requestedTime>=openingHour && requestedTime<=2359) || (requestedTime>=0 && requestedTime<=closingHour));
        }
    }

    boolean checkDishes(String restaurantCuisine, Integer restaurantMenu, String[] requestedDishes)
    {
        var menu = Arrays.stream((Restaurant.Menus.get(restaurantCuisine)[restaurantMenu])).toList(); //  list of dishes served at the restaurant

        System.out.println("[ManagerAgent] " + getAID().getLocalName() + " MENU:"  + menu);
        for (String dish : requestedDishes)
        {
            if (!menu.contains(dish))
            {
                System.out.println("[ManagerAgent] " + getAID().getLocalName() + " "  + dish + " is not served here");
                return false;
            }
        }
        return true;
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
