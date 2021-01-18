package com.marklogic.scheduler.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.scheduler.entity.Workflow;

public class WorkflowStateManager extends ResourceManager {

    Gson gson = new Gson();

    public WorkflowStateManager(DatabaseClient client, String serviceName) {
        super();

        // Initialize the Resource Manager via the Database Client
        client.init(serviceName, this);
    }

    public List<Workflow> checkWorkflowState() {
        // Build up the set of parameters for the service call
        RequestParameters params = new RequestParameters();
        // params.add("uris", uris);

        // get the initialized service object from the base class
        ResourceServices services = getServices();
        // call the service implementation on the REST Server,
        // returning a ResourceServices object
        ServiceResultIterator resultItr = services.get(params);
        // iterate over results, get content
        List<Workflow> responses = new ArrayList<>();
        StringHandle readHandle = new StringHandle();

        

        try {
            while (resultItr.hasNext()) {
                ServiceResult result = resultItr.next();
                // get the result content
                result.getContent(readHandle);

                JsonObject jsonObject = gson.fromJson(readHandle.get(), JsonObject.class);

                for (JsonElement jsonElement : (JsonArray) jsonObject.get("workflows")) {
                    if (jsonElement.isJsonObject()) {
                        JsonObject workflow = jsonElement.getAsJsonObject();
                        responses.add(gson.fromJson(workflow.toString(), Workflow.class));
                    }
                }

            }
            // release the iterator resources
            resultItr.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return responses;
    }

}