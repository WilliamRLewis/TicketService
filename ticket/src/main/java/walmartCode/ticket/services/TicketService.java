package walmartCode.ticket.services;

import walmartCode.ticket.beans.SeatHold;

public interface TicketService {

	int numSeatsAvailable();
	
	SeatHold findAndHoldSeats(int numSeats, String customerEmail);
	
	String reserveSeats(int seatHoldId, String customerEmail);
}
