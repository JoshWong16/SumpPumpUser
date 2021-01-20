# SumpPumpUser

## Description 
SumpPumpUser is a client applicatoin to let users view the light data from their sump pump control panel.
- Allows users view current status of LED lights
- Users can view the history of their sump pump running time
  - Lets users compare running time of pump 1 and pump 2 to evaluate if one pump is worn down compared to the other
- Users can reset the tracking of their sump pump runnning time

## Implementation
- When users login it grants them access to their specific data in DynamoDB
- Using the Amazon DynamoDB Document API with the AWS Mobile SDK, the user's data is read and displayed 

