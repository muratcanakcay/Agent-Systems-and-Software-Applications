import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;

public class WeatherAgent extends Agent
{
    @Override
    protected void setup()
    {

//        Behaviour b = new OneShotBehaviour(this) {
        Behaviour b = new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);

                ACLMessage msg = receive(mt);

                if (msg != null)
                {
                    switch(msg.getPerformative())
                    {
                        case ACLMessage.QUERY_IF:
                            System.out.println(getAID().getName() + " received query: " + msg.getContent());

                            ACLMessage response = msg.createReply();
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent("Today it's raining.");
                            System.out.println(getAID().getName() + " is sending response message!");
                            send(response);
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
