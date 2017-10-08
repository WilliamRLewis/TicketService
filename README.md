# TicketService
Service Layer for a basic TicketService
Assumptions:
I assume a backend layer capable of the following:
 1. I can retrieve a Seat[][] representing seating,
 2. I can save a seat object 
 3. I can find a hold given a hold id
 4. Upon reserving a seat I can get a generated reservationID
 
Instructions for command line run:
navigate to /ticket/ (directory containing the pom)
run the following commands
  1. mvn clean
  2. mvn package
  3.mvn test
