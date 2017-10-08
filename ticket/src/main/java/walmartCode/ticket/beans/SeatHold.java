package walmartCode.ticket.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;

import walmartCode.ticket.services.TicketServiceImpl;
import walmartCode.ticket.utility.Expire;
import walmartCode.ticket.utility.Status;

public class SeatHold {
	private int holdId;
	private List<Seat> seats;
	private String customerEmail;
	private Timer expiration;
	private Boolean expired;
	private long timerLength = 1000L;
	private static Logger log = Logger.getLogger(SeatHold.class);

	public SeatHold(String customerEmail) {
		super();
		this.customerEmail = customerEmail;
		this.seats = new ArrayList<Seat>();
		this.expired = false;
		this.expiration = new Timer();
	}
	
	public void setTimer(){
		Expire myTimer = new Expire(this);
		expiration.schedule(myTimer, timerLength);
		
	}
	public void setTimer(long customTimer){
		Expire myTimer = new Expire(this);
		expiration.schedule(myTimer, customTimer);
	}
	
	public void addSeat(Seat newSeat){
		this.seats.add(newSeat);
	}
	public int getHoldId() {
		return holdId;
	}
	public void setHoldId(int holdId) {
		this.holdId = holdId;
	}
	public List<Seat> getSeats() {
		return seats;
	}
	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}
	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}


	@Override
	public String toString() {
		String s = "SeatHold [holdId=" + holdId + ", seats=";
		for(Seat mySeat : this.seats){
			s = s + mySeat.getSeatId() + " ";
		}
	    s = s +", customerEmail=" + customerEmail;
	    return s;
	}

}
