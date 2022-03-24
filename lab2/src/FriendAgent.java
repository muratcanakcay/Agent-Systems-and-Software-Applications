import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class FriendAgent extends Agent
{
    @Override
    protected void setup()
    {

//        Behaviour b = new OneShotBehaviour(this) {
        Behaviour b = new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
                msg.addReceiver(new AID("Muratcan", AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Weather-Forecast-Ontology");
                msg.setContent("How's the weather today?");
                System.out.println(getAID().getName() + " is sending query message!");
                send(msg);

                ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                msg2.addReceiver(new AID("Muratcan", AID.ISLOCALNAME));
                msg2.setLanguage("English");
                msg2.setOntology("Weather-Forecast-Ontology");
                msg2.setContent("This souldn't display!");
                System.out.println(getAID().getName() + " is sending info message!");
                send(msg2);

                ACLMessage rcv = receive();

                if (rcv != null)
                {
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.INFORM:
                            System.out.println(getAID().getName() + " received message: " + rcv.getContent());
                            break;
                    }
                }

                block(15000);



            }
        };
        addBehaviour(b);
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
