/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.SchedulerJobHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

// Dependencies need to be verified and the batch jobs need to be run or else the integration tests will be failing
@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class AASchedulerJobDependencyIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SchedulerJobHelper schedulerJobHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.requestSpec.header("Fineract-Platform-TenantId", "default");
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @After
    public void changeStatus(){
        ArrayList<HashMap> allSchedulerJobsData = this.schedulerJobHelper.getAllSchedulerJobs(this.requestSpec, this.responseSpec);
        activateAllTheJobs(allSchedulerJobsData,false);
    }

    @Test
    public void testDependencyOfSchedulerJob() throws InterruptedException {
        ArrayList<HashMap> allSchedulerJobsData = this.schedulerJobHelper.getAllSchedulerJobs(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(allSchedulerJobsData);

        activateAllTheJobs(allSchedulerJobsData,true);

        boolean jobWillNotExecute = true;

        executeDepentJobs(allSchedulerJobsData, jobWillNotExecute);

        executeJobById(getJobIdFromName(allSchedulerJobsData, "Update Loan Arrears Ageing"));

        executeJobById(getJobIdFromName(allSchedulerJobsData, "Update Non Performing Assets"));

        jobWillNotExecute = false;

        executeDepentJobs(allSchedulerJobsData, jobWillNotExecute);

    }
    
    private void executeJobById(Integer jobId) throws InterruptedException {

        System.out.println("Exicuting job --->"+jobId);
       
        // Retrieving Scheduler Job by ID
        HashMap schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
        Assert.assertNotNull(schedulerJob);

        // Executing Scheduler Job
        this.schedulerJobHelper.runSchedulerJob(this.requestSpec, jobId.toString());

        // Retrieving Scheduler Job by ID
        schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
        Assert.assertNotNull(schedulerJob);

        // Waiting for Job to complete
        while ((Boolean) schedulerJob.get("currentlyRunning") == true) {
            Thread.sleep(15000);
            schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
            Assert.assertNotNull(schedulerJob);
            System.out.println("Job is Still Running");
        }
        Thread.sleep(1500);
        ArrayList<HashMap> jobHistoryData = this.schedulerJobHelper.getSchedulerJobHistory(this.requestSpec, this.responseSpec,
                jobId.toString());

        // Verifying the Status of the Recently executed Scheduler Job
        Assert.assertEquals("Verifying Last Scheduler Job Status:"+jobId, "success", jobHistoryData.get(jobHistoryData.size() - 1).get("status"));
    }

    private void activateAllTheJobs(ArrayList<HashMap> allSchedulerJobsData, final Boolean active) {
        for (HashMap scheduleDetail : allSchedulerJobsData) {
            Boolean activeState = (Boolean) scheduleDetail.get("active");
            Integer jobId = (Integer) scheduleDetail.get("jobId");
            if (active ^ activeState) {
                HashMap changes = this.schedulerJobHelper.updateSchedulerJob(this.requestSpec, this.responseSpec, jobId.toString(),
                        active.toString());
                // Verifying Scheduler Job updation
                Assert.assertEquals("Verifying Scheduler Job Updation:"+jobId, active, changes.get("active"));
                scheduleDetail.put("active", active);

            }
        }
    }

    private void executeDepentJobs(ArrayList<HashMap> allSchedulerJobsData, boolean jobWillNotExecute) throws InterruptedException {
        for (HashMap scheduleDetail : allSchedulerJobsData) {

            String jobName = (String) scheduleDetail.get("displayName");

            if (jobName.equals("Update Non Performing Assets") || jobName.equals("Recalculate Interest For Loans")
                    || jobName.equals("Apply penalty to overdue loans")
                    || jobName.equals("Add Accrual Transactions For Loans With Income Posted As Transactions")
                    || jobName.equals("Add Periodic Accrual Transactions") || jobName.equals("Add Accrual Transactions")) {
                Integer jobId = (Integer) scheduleDetail.get("jobId");
                System.out.println("Exicuting job --->"+jobId);
                // Retrieving Scheduler Job by ID
                HashMap schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
                Assert.assertNotNull(schedulerJob);

                // Executing Scheduler Job
                this.schedulerJobHelper.runSchedulerJob(this.requestSpec, jobId.toString());

                // Retrieving Scheduler Job by ID
                schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
                Assert.assertNotNull(schedulerJob);

                // Waiting for Job to complete
                while ((Boolean) schedulerJob.get("currentlyRunning") == true) {
                    Thread.sleep(15000);
                    schedulerJob = this.schedulerJobHelper.getSchedulerJobById(this.requestSpec, this.responseSpec, jobId.toString());
                    Assert.assertNotNull(schedulerJob);
                    System.out.println("Job is Still Running");
                }
                ArrayList<HashMap> jobHistoryData = this.schedulerJobHelper.getSchedulerJobHistory(this.requestSpec, this.responseSpec,
                        jobId.toString());

                if (jobWillNotExecute) {
                    Assert.assertTrue(jobHistoryData.isEmpty());
                } else {
                    Assert.assertEquals("Verifying Last Scheduler Job Status :"+jobId, "success", jobHistoryData.get(jobHistoryData.size() - 1)
                            .get("status"));
                }
            }
        }
    }

    private Integer getJobIdFromName(ArrayList<HashMap> allSchedulerJobsData, String name) {

        for (HashMap scheduleDetail : allSchedulerJobsData) {

            String jobName = (String) scheduleDetail.get("displayName");

            if (jobName.equals(name)) { return (Integer) scheduleDetail.get("jobId"); }
        }
        return null;
    }
}
