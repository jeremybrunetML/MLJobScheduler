const DataHub = require("/data-hub/5/datahub.sjs");
const datahub = new DataHub();

function post (context, params, input) {

    /*let params = {
        workflowId: "8eedca26-48c2-44ca-bc62-99a7068a035b",
        action: "done",
        newWorkflowStep: 1
    }*/

    let action = params.action || ""
    let workflow = fn.head(xdmp.invokeFunction(() => cts.doc(`/Workflow/${params.workflowId}`).toObject(),
        { "database": xdmp.database(datahub.config.FINALDATABASE) }));


    workflow.workflowStatus = action;
    if (params.newWorkflowStep != null) {
        let newStep = Number(params.newWorkflowStep);
        let currentStep = workflow.workflowStep

        if (!workflow.currentVersionSteps) { workflow.currentVersionSteps = {} }
        if (!workflow.currentVersionSteps[newStep]) { workflow.currentVersionSteps[newStep] = {} }

        if (newStep != currentStep) {
            workflow.workflowStep = newStep;
            workflow.currentVersionSteps[newStep].timestamp = fn.currentDateTime()
        }
    }

    xdmp.invokeFunction(() => xdmp.nodeReplace(cts.doc(`/Workflow/${params.workflowId}`), workflow),
        { "database": xdmp.database(datahub.config.FINALDATABASE), "update": "true" });

}

exports.GET = function (context, params) {
    let workflows = cts.search(cts.andQuery([cts.collectionQuery("Workflow")])).toArray()
    return { status : "OK", workflows : workflows}
}

exports.POST = post;