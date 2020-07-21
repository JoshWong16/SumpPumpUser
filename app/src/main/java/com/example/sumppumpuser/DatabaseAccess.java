package com.example.sumppumpuser;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Context;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Purpose of class is to initialize connection to DynamoDB
 * Defines CRUD methods
 */
public class DatabaseAccess {

    private String TAG = "SumpPumpDB";

    private final Regions COGNITO_ITENTITY_POOL_REGION = Regions.US_WEST_2;
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;

    /*
    This class is a singleton - storage for the current instance
    */
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context, HashMap<String, String> logins){
        this.context = context;

        //Create a new credentials provider
        credentialsProvider =  new CognitoCachingCredentialsProvider(context, AppSettings.COGNITO_IDENTITY_POOL_ID, COGNITO_ITENTITY_POOL_REGION);
        credentialsProvider.setLogins(logins);
        //Create a connection to the DynamoDB service
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        //Must set dbClient region here or else it defaults to us_east_1
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        //Create a table reference
        dbTable = Table.loadTable(dbClient, AppSettings.DYNAMODB_TABLE_NAME);
    }

    /**
     * Creates a singleton DatabaseAccess object
     * Singleton pattern - retrieve an instance of the DatabaseAccess
     * Ensures we always use the same instance of the DatabaseAccess class
     * Object is synchronized so that only one thread can run the instance at a time
     */
    public static synchronized DatabaseAccess getInstance(Context context, HashMap<String, String> logins){
        if (instance == null) {
            instance = new DatabaseAccess(context, logins);
        }
        return instance;
    }

    /**
     * method called to update a given lightID's status
     * @param lightID
     */
    public boolean updateLightStatus(String lightID, boolean lightStatus){
        Log.i(TAG, "in updateLightStatus");

        Document retrievedDoc = dbTable.getItem(new Primitive(lightID));

        if (retrievedDoc != null){

            //updates or switches the current light status
            boolean newStatus = lightStatus;
            retrievedDoc.put("LightStatus", newStatus);

            //creates a document object with the updated result
            Document updateResult = dbTable.updateItem(retrievedDoc, new Primitive(lightID),
                    new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));

            try{
                Log.i(TAG, "updateResult: " + Document.toJson(updateResult));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "updateResult json error: " + e.getLocalizedMessage());
            }
            return true;
        }else{
            return false;
        }

    }


    /**
     * Get light status of all lightID's
     * @param
     * @return List of document objects containing each LightID's lightstatus
     */
    public List<Document> getAllLightStatus(){
        //initialize scanconfig object
        ScanOperationConfig scanOperationConfig = new ScanOperationConfig();
        //create array of attributes to retrieve from each item
        List<String> attributeList = new ArrayList<>();
        attributeList.add("LightStatus");

        //Access table and return scan results
        scanOperationConfig.withAttributesToGet(attributeList);
        Search searchResult = dbTable.scan(scanOperationConfig);
        return searchResult.getAllResults();
    }

    /**
     * Get light status of specified light
     * @param: LightStatus#
     * @return: state of the light
     */
    public String getLightStatus(String lightID, Document user){
        String lightStatus = user.get(lightID).asString();
        return lightStatus;
    }

    /**
     * Get User item from database
     * @param: sub of user (from IDtoken)
     * @return: User item form DynamoDB as document object
     */
    public Document getUserItem(String sub){
        Document retrievedDoc = dbTable.getItem(new Primitive(sub));
        if(retrievedDoc != null){
            return retrievedDoc;
        }
        else{
            Log.e(AppSettings.tag, "error retrieving userItem from Dynamo");
            return null;
        }
    }

    /**
     * retrieves the String set of pump times from dynamoDB
     * @param subject user subject
     * @param pumpName name of pump
     * @return List of pump times
     */
    public List<String> getPumpTimeSet(String subject, String pumpName){
        Document retrievedDoc = dbTable.getItem(new Primitive(subject));
        if(retrievedDoc != null){
            //get timeSet from DynamoDB
            DynamoDBEntry timeSet = retrievedDoc.get(pumpName);
            //convert set to list and return
            List<String> timeList = timeSet.convertToAttributeValue().getSS();

            return timeList;
        }
        else{
            Log.e(AppSettings.tag, "error retrieving userItem from Dynamo");
            return null;
        }
    }

    /**
     * Resets String set of pump times in DynamoDB
     * @param subject user subject
     * @param pumpName name of pump to reset
     * @return whether it was successful or not
     */
    public Boolean resetPumpTimes(String subject, String pumpName){
        Document retrievedDoc = dbTable.getItem(new Primitive(subject));
        if(retrievedDoc != null){
            //create new String set
            Set<String> replacementSet = new HashSet<>();
            replacementSet.add("0"); //DynamoDB doesn't like empty sets

            //update set in DynamoDB
            retrievedDoc.put(pumpName, replacementSet);
            Document updateResult = dbTable.updateItem(retrievedDoc, new Primitive(subject), new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));

            try{
                Log.d(AppSettings.tag, "Update Result: " + Document.toJson(updateResult));
            }catch (Exception e){
                e.printStackTrace();
                Log.i(AppSettings.tag, "updatePumpTime json error: " + e.getLocalizedMessage());
            }
            return true;
        }

        else{
            Log.e(AppSettings.tag, "error retrieving userItem from Dynamo");
            return false;
        }
    }

    /**
     * createUser: when new user registers, this function creates a new row item in dynamodb for new user
     * @param idToken user's idToken
     * @param username
     * @param phone user's phone number
     */
    public void createUser(String idToken, String username, String phone){
        //get user subject from idToken
        JWT jwt = new JWT(idToken);
        String subject = jwt.getSubject();

        //create new user document object
        //add attributes
        Document user = new Document();
        user.put("UserId", subject);
        user.put("phone", "1" + phone);
        user.put("username", username);
        user.put("LightStatus1", "false");
        user.put("LightStatus2", "false");
        user.put("LightStatus3", "false");
        user.put("LightStatus4", "false");
        user.put("LightStatus5", "false");
        user.put("LightStatus6", "false");
        Set<String> pumpTimes1 = new HashSet<>();
        pumpTimes1.add("0");
        Set<String> pumpTimes2 = new HashSet<>();
        pumpTimes2.add("0");
        user.put("PumpTimes1", pumpTimes1);
        user.put("PumpTimes2", pumpTimes2);

        //add new user item to dynamoDB table
        dbTable.putItem(user);

    }

}

