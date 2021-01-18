package com.marklogic.scheduler.service;

import com.google.gson.Gson;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;

public class ChangeWorkflowStatusManager extends ResourceManager {

    Gson gson = new Gson();

    public ChangeWorkflowStatusManager(DatabaseClient client, String serviceName) {
        super();

        // Initialize the Resource Manager via the Database Client
        client.init(serviceName, this);
    }

    public void changeWorkflowStatus(String workflowId, String action, Integer newWorkflowStep) {
        // Build up the set of parameters for the service call
        RequestParameters params = new RequestParameters();
        params.add("workflowId", workflowId);
        params.add("action", action);
        if (action == "waiting") {
            params.add("newWorkflowStep", String.valueOf(newWorkflowStep));
        }

        // get the initialized service object from the base class
        ResourceServices services = getServices();

        // call the service implementation on the REST Server,
        // returning a ResourceServices object

        ServiceResultIterator resultItr = services.post(params, new StringHandle("").withFormat(Format.JSON));
        // iterate over results, get content

        StringHandle readHandle = new StringHandle();
        while (resultItr.hasNext()) {
            ServiceResult result = resultItr.next();
            // get the result content
            result.getContent(readHandle);
        }
        // release the iterator resources
        resultItr.close();

    }

}