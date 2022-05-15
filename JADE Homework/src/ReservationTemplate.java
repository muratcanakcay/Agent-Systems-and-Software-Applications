import java.io.Serializable;
import java.util.Arrays;

public class ReservationTemplate implements Serializable
{
    public String[] Dishes;
    public int noOfPeople;
    public int time;

    public ReservationTemplate(String[] dishes, int noOfPeople, int time) {
        Dishes = dishes;
        this.noOfPeople = noOfPeople;
        this.time = time;
    }

    @Override
    public String toString() {
        return "ReservationInfo: " +
                "Dishes =" + Arrays.toString(Dishes) +
                ", Number of People=" + noOfPeople +
                ", Time=" + time +
                '}';
    }
}
