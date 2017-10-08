package walmartCode.ticket.services;

import java.util.List;

import walmartCode.ticket.beans.Seat;
import walmartCode.ticket.beans.SeatHold;
import walmartCode.ticket.dal.DataAccessLayer;
import walmartCode.ticket.utility.Status;

public class DataAccessLayerMock extends DataAccessLayer{
	Seat[][] venue = new Seat[3][6];
	public void cleanAndSetup(){
		Seat[] TestRow1 = {
				new Seat(1, Status.AVAILIBLE),
				new Seat(2, Status.AVAILIBLE),
				new Seat(3, Status.RESERVED),
				new Seat(4, Status.AVAILIBLE),
				new Seat(5, Status.AVAILIBLE),
				new Seat(6, Status.RESERVED) };
		Seat[] TestRow2 = {
				new Seat(10, Status.AVAILIBLE),
				new Seat(11, Status.AVAILIBLE),
				new Seat(12, Status.RESERVED),
				new Seat(13, Status.AVAILIBLE),
				new Seat(14, Status.HELD),
				new Seat(15, Status.HELD) };
		Seat[] TestRow3 = {
				new Seat(20, Status.AVAILIBLE),
				new Seat(21, Status.AVAILIBLE),
				new Seat(22, Status.AVAILIBLE),
				new Seat(23, Status.AVAILIBLE),
				new Seat(24, Status.AVAILIBLE),
				new Seat(25, Status.RESERVED) };
		
		
		this.venue[0] = TestRow1;
		this.venue[1] = TestRow2;
		this.venue[2] = TestRow3;
	}
	public Seat[][] getSeats() {
		// TODO Auto-generated method stub
		return venue;
	}

	public Seat save(Seat s) {
		this.helperSave(s);
		return s;
		
	}

	public SeatHold findSeatHold(int seatHoldId) {
		SeatHold mockHold = new SeatHold("Email@Email.Email");
		mockHold.addSeat(new Seat(14, Status.HELD));
		mockHold.addSeat(new Seat(15, Status.HELD));
		return mockHold;
	}

	public String reserve(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		String myString = "String that represents ID generation here";
		return myString;
		
	}
	public void helperSave(Seat s){
	for(int i = 0; i < venue.length; i ++){
		for(int j = 0; j < venue[i].length; j++){
			if(s.getSeatId() == venue[i][j].getSeatId()){
				venue[i][j] = s;
			}
		}
	}
	}

}

