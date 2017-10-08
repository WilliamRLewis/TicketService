package walmartCode.ticket.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import walmartCode.ticket.beans.Seat;
import walmartCode.ticket.beans.SeatHold;
import walmartCode.ticket.dal.DataAccessLayer;
import walmartCode.ticket.utility.Status;

public class TicketServiceImpl implements TicketService {
	private static Logger log = Logger.getLogger(TicketServiceImpl.class);
	/**Assumptions of DAL: 1. I can retrieve a Seat[][] representing seating,
	*                    2. I can save a seat object 
	*                   3. I can find a hold given a hold id,
	*/
	private  DataAccessLayer dal;
	private static final long EXPIRATION_TIME = 10L;

	/**
	 * @return int value of number of available seats, see enum status class for possible states of a seat.
	 */
	public int numSeatsAvailable() {	
		
		Seat[][] mySeats = dal.getSeats();
		int count = (int) Arrays.stream(mySeats).flatMap(x -> Arrays.stream(x))
			.filter(s -> (s.getStatus() == Status.AVAILIBLE))
			.count();
		log.info("Available Seats: " + count);
		return count;
	}
	/**
	 * Creates a hold on seats for customer, prioritizes seating by frontmost seats, can swap out utility method to gather seatList according to different priorities.
	 * @param int numSeats, number of seats a user requests, should be a positive integer.
	 * @param String customerEmail, customer's email
	 * @return @see SeatHold.java
	 */
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		log.info("Attempting to reserve: " + numSeats + " For customer using the email: " + customerEmail);
		if(numSeatsAvailable() < numSeats){
			//TODO: Throw a suitable error and info to the user that there are not enough seats
			//Testing placeholder
			throw new IllegalArgumentException();
		}
		if(numSeats <= 0){
			//TODO; Throw invalid input error, possibly send info to user, note this could be done on API level as a try-catch?
			throw new IllegalArgumentException();
		}
		Seat[][] mySeats = dal.getSeats();
		List<Seat> seatingList = new ArrayList<Seat>();
		//utilReserveIndependant can be used if being closer to the front is of greater importance than maintaining grouping
		seatingList = utilReserveIndependent(seatingList, mySeats, numSeats);
		SeatHold newHold = new SeatHold(customerEmail);
		for(Seat s : seatingList){
			s.setStatus(Status.HELD);
			dal.save(s);
			newHold.addSeat(s);
		}
		newHold.setTimer(EXPIRATION_TIME);
		//Send user info about expiration date
		return newHold;
	}
	/**
	 * Refactor notes: checking on expiration at time of reservation is not ideal, client should be informed as soon
	 * as expiration occurs, not at the time of attempting to reserveSeats, alternatively send client information about how long tickets will be held at time of creating hold
	 */
	public String reserveSeats(int seatHoldId, String customerEmail) {
	
		SeatHold clientHold = dal.findSeatHold(seatHoldId);
		//Verify with customerEmail
		if(!(clientHold.getCustomerEmail().equals(customerEmail)))
		{
			//TODO: Throw exception on email mismatch and inform user.
			//Placeholder exception for testing
			throw new IllegalArgumentException();
			//return "Email address mismatch with given seatHold";
		}
		List<Seat> mySeats = clientHold.getSeats();
		if(!clientHold.getExpired())
		{
		for(Seat s : mySeats){
			s.setStatus(Status.RESERVED);
			//Depending on backend customerEmail should be saved to the appropriate obj, or perhaps SeatHold functionality is extended to track a single email's seats?s
			dal.save(s);
			}
		}
		else{
			//inform customer of expired seats
		}
		//Assume backend wants customerEmail info for reserved seats, could be stored in seat info, or by a venue class etc.
		//I also assume some sort of randomly generated string would be returned on saving that info to pass to the client, so we only give a string if the data successfully stores.
		return dal.reserve(seatHoldId, customerEmail);

	}
	/**
	 * Utility function to fill seats from front of theater to back according to number of Seats required by customer, does not care about grouping seats.
	 * @param hold, seatHold to add individual seats as they are found to
	 * @param seats, collection of seat objects to work with
	 * @param numSeats, seats needed to be filled
	 * @return
	 */
	private List<Seat> utilReserveIndependent(List<Seat> mySeats, Seat[][] seats, int numSeats){
		while(numSeats > 0){
			for(int i = 0; i < seats.length; i  ++){
				for(Seat s: seats[i]){
					if(s.getStatus() == Status.AVAILIBLE){
						s.setStatus(Status.HELD);
						mySeats.add(s);
						numSeats--;
						if(numSeats == 0){
							return mySeats;
						}
					}
				}
			}
		}
			return mySeats;
	}
	/**
	 * This function attempts to preserve a group of individuals to as few splits as possible, dividing the group in half whenever there are not enough seats to fit them all sequentially.
	 * @param selection List containing currently selected seats
	 * @param seatingChart chart of current seating statuses
	 * @param numSeats number of seats you wish to fill
	 * @return List of selected seats to put on hold
	 */
	private List<Seat> utilFindOptimalGrouping(List<Seat> selection, Seat[][] seatingChart, int numSeats){
		if(numSeats <= 0)
		{
			return selection;
		}
		else if(numSeats == 1){
			return utilReserveIndependent(selection, seatingChart, 1);
		}
		for(int i = 0; i < seatingChart.length; i ++){
			int[] current = utilSequentialSeatsStartAndLength(seatingChart[i]);
			if(current[1] >= numSeats){
				for(int j = current[0]; j <= numSeats; j++){
						Seat aSeat = seatingChart[i][j];
						aSeat.setStatus(Status.HELD);
						selection.add(aSeat);
						numSeats--;
				}
			 }
			}
		if(numSeats > 0){
			if(!(numSeats%2 == 0)){
			selection = utilFindOptimalGrouping(selection, seatingChart, numSeats/2 + numSeats%2);
			}
			selection = utilFindOptimalGrouping(selection, seatingChart, numSeats/2);
		}
		return selection;			
	}
	
	/**
	 * Utility method for finding the largest number of sequentially open seats in a row.
	 * @param Representing a row of seats from a given venue, returns the highest number of sequential seats in that row.
	 * @return an array with two values, the first holding the index at which the sequential seats starts, and the second the length of the sequence of seats
	 */
	public int[] utilSequentialSeatsStartAndLength(Seat[] row){
		int currentStreak = 0;
		int previousMax = 0;
		int start = 0;
		for(int i = 0; i < row.length; i++){
			if(! (row[i].getStatus() == Status.AVAILIBLE ))
			{
				currentStreak = 0;
			}
			else{
				if(currentStreak == 0){
					start = i;
				}
				currentStreak++;
				if( currentStreak > previousMax)
				{
					previousMax = currentStreak;
				}
			}
		}
		int values[] = {start, previousMax};
		return values;
	}
	//For testing, sorry about the mess
	public DataAccessLayer getDal() {
		return dal;
	}
	public void setDal(DataAccessLayer dal) {
		this.dal = dal;
	}
	public TicketServiceImpl() {
		super();
	}
	public TicketServiceImpl(DataAccessLayer dal) {
		super();
		this.dal = dal;
	}
}
