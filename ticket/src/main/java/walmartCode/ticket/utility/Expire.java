package walmartCode.ticket.utility;

import java.util.List;
import java.util.TimerTask;

import walmartCode.ticket.beans.Seat;
import walmartCode.ticket.beans.SeatHold;
import walmartCode.ticket.dal.DataAccessLayer;

/**
 * Handles expiration of seats on hold by setting their status to available.
 * @author William
 *
 */
public class Expire extends TimerTask {
	private SeatHold hold;
	private DataAccessLayer dal = new DataAccessLayer();
	
	public Expire(SeatHold hold){
		this.hold = hold;
	}
	@Override
	public void run() {
		for(Seat s: this.hold.getSeats()){
			s.setStatus(Status.AVAILIBLE);
			dal.save(s);
		}
		this.hold.setExpired(true);
	}

}
