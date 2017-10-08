package walmartCode.ticket.services;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import walmartCode.ticket.beans.Seat;
import walmartCode.ticket.beans.SeatHold;
import walmartCode.ticket.utility.Status;

public class TicketServiceTest {
	private static Logger log = Logger.getLogger(TicketServiceTest.class);
	private static final String email = "MrEmail@Email.Email";
	//Ideally we'd have spring inject this (or test against an in-memory database), but for proof of concept testing, we'll have ticketService take in a mock as a constructor parameter
	private DataAccessLayerMock mockData = new DataAccessLayerMock();
	private TicketServiceImpl ticketService = new TicketServiceImpl(mockData);
	
	@Before
	public void cleanData(){
	DataAccessLayerMock aMock = (DataAccessLayerMock) ticketService.getDal();
	aMock.cleanAndSetup();
	ticketService.setDal(aMock);
	}
	
	@Test
	public void findAndHoldSeatsTest(){
		log.info("Testing findAndHoldSeats");
		assertTrue("Data was cleaned", ticketService.numSeatsAvailable() == 12);
	    SeatHold actualRound1 = ticketService.findAndHoldSeats(5, email);
		assertTrue("Correct number of seats were reserved", ticketService.numSeatsAvailable() == 7);
		
		 SeatHold actualRound2 = ticketService.findAndHoldSeats(6, email);
		 assertTrue("Ensure an even number of seats may be reserved and that multiple holds may be made", ticketService.numSeatsAvailable() == 1);
		 
		 log.info(actualRound2.toString());
		 
		 //Wait for expiration time to pass, not robust testing as expiration timer is subject to change.
		 try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 assertTrue("Test expirations", actualRound1.getExpired());
		 
	}
	@Test(expected = IllegalArgumentException.class)
	public void findAndHoldSeatsNegativeTest(){
		ticketService.findAndHoldSeats(20, email);
		ticketService.findAndHoldSeats(-20, email);
	}
	@Test
	public void numSeatsAvailableTest(){
		log.info("Testing numSeatsAvailabletest");
		//Positive Testing
		int expected = 12;
		int actual = ticketService.numSeatsAvailable();
		assertEquals(expected, actual);
	}
	
	@Test
	public void utilSequentialSeatsStartAndLengthTest(){
		log.info("Testing utilSequentialSeatsStartAndLength");
		Seat[] posTestRow = {
			new Seat(1, Status.AVAILIBLE),
			new Seat(24, Status.AVAILIBLE),
			new Seat(2, Status.RESERVED),
			new Seat(3, Status.AVAILIBLE),
			new Seat(4, Status.AVAILIBLE),
			new Seat(5, Status.AVAILIBLE),
			new Seat(6, Status.HELD) };
	
	int[] expected = {3, 3};
	int[] actual = ticketService.utilSequentialSeatsStartAndLength(posTestRow);
	assertTrue("Positive Sequential Seats testing", Arrays.equals(expected, actual));
	}
	
	@Test
	public void reserveSeatsTest(){
		//Check holds
		log.info("Testing reserveSeatsTest");
		//Get mock 
		SeatHold myHold = ticketService.getDal().findSeatHold(123);
		String reserveID = ticketService.reserveSeats(myHold.getHoldId(), myHold.getCustomerEmail());
		assertNotNull("Ensure placeholder ID is returned", reserveID);
		
		Seat[][] seats = ticketService.getDal().getSeats();
		assertTrue("Check first seat for status update", seats[1][4].getStatus().equals(Status.RESERVED));
		assertTrue("Check second seat for status update", seats[1][5].getStatus().equals(Status.RESERVED));
		
	}
	@Test(expected = IllegalArgumentException.class)
	public void reserveSeatsNegativeTest(){
		SeatHold myHold = ticketService.getDal().findSeatHold(123);
	    ticketService.reserveSeats(myHold.getHoldId(), "UnexpectedEmail@TrashWebsite.jpg"); 
	}
}
