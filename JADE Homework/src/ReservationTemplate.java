import java.io.Serializable;
import java.util.Arrays;

// used for making a reservation at a restaurant
public class ReservationTemplate implements Serializable
{
    public String cuisine;
    public String[] Dishes;
    public int noOfPeople;
    public int time;

    public ReservationTemplate(String cuisine, String[] dishes, int noOfPeople, int time) {
        this.cuisine = cuisine;
        Dishes = dishes;
        this.noOfPeople = noOfPeople;
        this.time = time;
    }

    @Override
    public String toString() {
        return "ReservationInfo: " +
                "Cuisine ="+ cuisine +
                ", Dishes =" + Arrays.toString(Dishes) +
                ", Number of People=" + noOfPeople +
                ", Time=" + time +
                '}';
    }
}
