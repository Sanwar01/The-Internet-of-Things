use anwars;
DROP TABLE IF EXISTS sensorUsage;
CREATE TABLE sensorUsage (
UserID varchar(50), 
SensorName varchar(50),
SensorValue varchar(20),
TimeInserted timestamp
);