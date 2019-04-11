import java.util.*;
public class Route {
	public List<Bus> busses;
	public int numStops;
	public int timeBetweenStops;
	public String name;
	
	
	
	public Route(String name){
		busses = new ArrayList<Bus>();
		this.name = name;
	}
}
