import java.io.Serializable;
import java.util.Arrays;

// used for making a reservation at a restaurant
public class ProposalReply implements Serializable
{
    public String explanation;
    public double cost;

    public ProposalReply(String explanation, double cost) {
        this.explanation = explanation;
        this.cost = cost;
    }
}
