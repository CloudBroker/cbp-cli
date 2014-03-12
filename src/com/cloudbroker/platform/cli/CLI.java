package com.cloudbroker.platform.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.cloudbroker.platform.api.core.CloudbrokerClient;
import com.cloudbroker.platform.api.data.Base;
import com.cloudbroker.platform.api.data.Executable;
import com.cloudbroker.platform.api.data.InstanceType;
import com.cloudbroker.platform.api.data.Job;
import com.cloudbroker.platform.api.data.Region;
import com.cloudbroker.platform.api.data.Resource;

public class CLI {
	private static String usageRules = "\nArguments:\n-name job name\n-executable executable name or id\n-region region name or id\n-instanceType intance type name or id\n-argumentString argument string for a job\n-nodes number of nodes\n-dataDir path to the folder with data files\n-stdin name of the datafile datatype of which should be stdin\n-previousJob Name or id of the previous job(to create a pipeline)";
	private Executable executable;
	private Region region;
	private InstanceType instanceType;
	private Job previousJob;
	private CloudbrokerClient cloudbrokerClient = null;

	@Parameter(names = "-name", description = "Name of the job to be created on CBP")
	private String jobName = "";

	@Parameter(names = "-executable", description = "Name or id of the executable")
	private String executableInput = "";

	@Parameter(names = "-region", description = "Name or id of the region")
	private String regionInput = "";

	@Parameter(names = "-instanceType", description = "Name or id of the instance type")
	private String instanceTypeInput = "";

	@Parameter(names = "-argumentString", description = "Argument string for a job")
	private String argumentString = "";

	@Parameter(names = "-nodes", description = "Number of nodes")
	private int nodes = 1;

	@Parameter(names = "-dataDir", description = "Path to the folder with data files")
	private String pathToDataDir = ".";

	@Parameter(names = "-stdin", description = "Name of the datafile datatype of which should be stdin")
	private String stdin = "";

	@Parameter(names = "-previousJob", description = "Name or id of the previous job(to create a pipeline)")
	private String previousJobInput = "";

	public CloudbrokerClient getCloudbrokerClient() {
		if (cloudbrokerClient == null) {
			Properties cliProps = loadProperties();
			cloudbrokerClient = CloudbrokerClient.getInstance(
					cliProps.getProperty("username"),
					cliProps.getProperty("password"),
					cliProps.getProperty("host"),
					Integer.valueOf(cliProps.getProperty("port")).intValue());
			cloudbrokerClient.setTimeouts(60);
		}
		return cloudbrokerClient;
	}

	public void submitJob() throws ClassNotFoundException, IOException,
			InterruptedException {
		System.out.println("Processing input parameters...");
		if (!validateParams()) {
			printMessageAndExit(usageRules);
		}
		Job job = null;
		executable = getBaseObject(Executable.class, executableInput);
		if (previousJob != null) {
			System.out.println("Check passed. Pipeline job will be created now. Pipeline job will be submitted as soon as previous job is finished");
			job = getCloudbrokerClient().createAndSubmitJob(jobName,
					pathToDataDir, false, executable, previousJob, nodes, argumentString,
					stdin);
		} else {
			region = getBaseObject(Region.class, regionInput);
			instanceType = getBaseObject(InstanceType.class, instanceTypeInput);
			if (!region.getResourceID().equals(instanceType.getResourceID())) {
				printMessageAndExit("Region and instance type specified are incompatible");
			}
			Resource resource = cloudbrokerClient.get(Resource.class, region.getResourceID());

			System.out.println("Check passed. Creating and submitting job..");
			job = cloudbrokerClient.createAndSubmitJob(jobName, pathToDataDir,
					false, executable, resource, region, instanceType, nodes,
					argumentString, stdin);
		}
		if (previousJob != null) {
			System.out.println("Job " + job.getId() + " created.");
		} else {
			System.out.println("Job " + job.getId() + " created and submitted.");
		}
		System.out.println("Waiting for a job to complete");
		if (!pollJob(job)) {
			printMessageAndExit("Job failed to complete");
		}
		System.out.println("Preparing to download results..");
		getCloudbrokerClient().downloadJobFilesTo(job,
				pathToDataDir);
		System.out.println("Files were downloaded to: " + pathToDataDir);
		System.out.println("Exiting..");
	}

	private boolean pollJob(Job job) throws ClassNotFoundException,
			IOException, InterruptedException {
		String status = "";
		int minute = 1;
		while (!status.equalsIgnoreCase("completed")) {
			Thread.sleep(60 * 1000);
			status = getCloudbrokerClient().getJobStatus(job);
			System.out.println("Minute: " + minute + ". Status: " + status);
			minute++;
		}
		System.out.println("Job completed");
		return true;

	}

	private boolean validateParams() throws ClassNotFoundException, IOException {
		boolean checkPassed = true;
		if (executableInput.isEmpty()) {
			System.out.println("Executable is missing");
			checkPassed = false;
		}
		if (previousJobInput.isEmpty()) {
			if (jobName.isEmpty()) {
				System.out.println("Job name is missing");
				checkPassed = false;
			}
			if (regionInput.isEmpty()) {
				System.out.println("Region is missing");
				checkPassed = false;
			}
			if (instanceTypeInput.isEmpty()) {
				System.out.println("InstanceType is missing");
				checkPassed = false;
			}
		} else {
			previousJob = getJobObject(previousJobInput);
			if (previousJob == null) {
				System.out.println("Previous job specified was not found");
				checkPassed = false;
			} else {
				if (previousJob.getStatus().equalsIgnoreCase("completed") || previousJob.getStatus().equalsIgnoreCase("finishing")) {
					System.out.println("Previous job should not be finishing / completed");
					checkPassed = false;
				}
			}
		}
		return checkPassed;
	}

	private <T extends Base> T getBaseObject(Class<T> baseType, String input)
			throws ClassNotFoundException, IOException {
		if (isUuid(input)) {
			return getCloudbrokerClient().get(baseType, input);
		} else {
			return getCloudbrokerClient().findByName(baseType, input);
		}
	}

	private Job getJobObject(String input)
			throws ClassNotFoundException, IOException {
		if (isUuid(input)) {
			return getCloudbrokerClient().get(Job.class, input);
		} else {
			return getCloudbrokerClient().findByName(Job.class, input);
		}
	}

	private boolean isUuid(String input) {
		return (input.length() == 36 && input.charAt(8) == '-'
				&& input.charAt(13) == '-' && input.charAt(18) == '-' && input
					.charAt(23) == '-');
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		CLI cli = new CLI();
		new JCommander(cli, args);
		cli.submitJob();
	}

	private Properties loadProperties() {
		String pathToProperties = new String("cbp.properties");
		Properties cliProps = new Properties();
		try {
			FileInputStream in = new FileInputStream(pathToProperties);
			cliProps.load(in);
			in.close();
		} catch (Exception e) {
			printMessageAndExit(e.getMessage() + "\n" + usageRules);
		}
		return cliProps;
	}
	
	private void printMessageAndExit(String message) {
		System.out.println(message);
		System.exit(-1);
	}
}
