CREATE TABLE Customer(
user_name VARCHAR(128) PRIMARY KEY,
full_name VARCHAR(256),
zipcode INT,
password VARCHAR(128));

CREATE TABLE Booking(
flight_id INT PRIMARY KEY,
passengers_count INT,
FOREIGN KEY(flight_id) REFERENCES Flights(fid));

CREATE TABLE Reservation(
user_name VARCHAR(128),
reservation_id INT,
flight_id INT,
date INT,
PRIMARY KEY(reservation_id),
UNIQUE(user_name, date),
FOREIGN KEY(user_name) REFERENCES Customer(user_name),
FOREIGN KEY(flight_id) REFERENCES Booking(flight_id));

INSERT INTO Customer
VALUES('OldBenKen', 'Ben Kenobi', 1138, 'OurOnlyHope');

INSERT INTO Customer
VALUES('Kote', 'Kvothe Kingkiller', 2007, 'EdemaRuh');

INSERT INTO Customer
VALUES('TibetanAgent', 'Dale Cooper', 98065, 'TheBlackLodge');

INSERT INTO Customer
VALUES('BattleStar', 'William Adama', 21311 , 'FrackingCylons');

INSERT INTO Customer
VALUES('BabyBlue', 'Walter White', 87105, 'SayMyName');

INSERT INTO Customer
VALUES('ParanoidAndroid', 'Rick Deckard', 1968, 'ElectricSheep');

INSERT INTO Customer
VALUES('OnePunch', 'Caped Baldy', 1008111, 'Saitama')

INSERT INTO Customer
VALUES('BubbleBlower94', 'Patrick Star', 96970, 'MyMindIsAnEnigma');