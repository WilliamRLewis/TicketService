package walmartCode.ticket.beans;

import walmartCode.ticket.utility.Status;

public class Seat {
	private int seatId;
	private Status status;
	
	
	public Seat(int seatId, Status status) {
		super();
		this.seatId = seatId;
		this.status = status;
	}
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

}
