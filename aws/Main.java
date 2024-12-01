/*
*  2024년 클라우드 컴퓨팅 Term Project
*  작성자: 2019038074 이우창
*  작성일: 2024-11-21 ~
*/

package aws;

import java.io.File;
import java.util.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetCommandInvocationRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetCommandInvocationResult;
import com.amazonaws.services.simplesystemsmanagement.model.SendCommandRequest;
import com.amazonaws.services.simplesystemsmanagement.model.SendCommandResult;

public class Main {

    final static Regions region = Regions.US_EAST_1;
    final static String jobDirectoryPath = "jobs";

    static AmazonEC2 ec2;

    private static void init() throws Exception {

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
    }

    public static void main(String[] args) throws Exception {

        // ec2 객체 초기화
        init();

        Scanner menu = new Scanner(System.in);
        Scanner idString = new Scanner(System.in);
        int number = 0;

        while (true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1.  list instance               2.  available zones       ");
            System.out.println("  3.  start instance              4.  available regions     ");
            System.out.println("  5.  stop instance               6.  create instance       ");
            System.out.println("  7.  reboot instance             8.  list images           ");
            System.out.println("  9.  shell command               10. submit jobs           ");
            System.out.println("  11. condor_status               12. condor_q              ");
            System.out.println("                                  99. quit                  ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if (menu.hasNextInt()) {
                number = menu.nextInt();
            } else {
                System.out.println("You must enter an integer!");
                break;
            }

            String instanceId = "";
            String bucketName = "";

            switch (number) {
                case 1:
                    listInstances();
                    break;

                case 2:
                    listAvailableZones();
                    break;

                case 3:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        startInstance(instanceId.trim());
                    break;

                case 4:
                    listAvailableRegions();
                    break;

                case 5:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        stopInstance(instanceId.trim());
                    break;

                case 6:
                    System.out.print("Enter ami id: ");
                    String amiId = "";
                    if (idString.hasNext())
                        amiId = idString.nextLine();

                    if (!amiId.trim().isEmpty())
                        createInstance(amiId.trim());
                    break;

                case 7:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        rebootInstance(instanceId.trim());
                    break;

                case 8:
                    listImages();
                    break;

                case 9:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        runShellCommand(instanceId.trim());
                    break;

                case 10:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();
                    System.out.print("Enter S3 Bucket Name: ");
                    if (idString.hasNext())
                        bucketName = idString.nextLine();

                    if (!instanceId.trim().isEmpty() && !bucketName.trim().isEmpty())
                        submitJobs(instanceId, bucketName);
                    break;

                case 11:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        showCondorStatus(instanceId.trim());
                    break;

                case 12:
                    System.out.print("Enter instance id: ");
                    if (idString.hasNext())
                        instanceId = idString.nextLine();

                    if (!instanceId.trim().isEmpty())
                        showCondorQ(instanceId.trim());
                    break;

                case 99:
                    System.out.println("bye!");
                    menu.close();
                    idString.close();
                    return;
                default:
                    System.out.println("No such menu!");
            }
        }
    }

    public static void listInstances() {
        System.out.println("Listing instances...");
        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                            "[AMI] %s, " +
                            "[type] %s, " +
                            "[state] %10s, " +
                            "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public static void listAvailableZones() {
        System.out.println("Listing available zones...");

        try {
            DescribeAvailabilityZonesResult result = ec2.describeAvailabilityZones();
            Iterator<AvailabilityZone> iterator = result.getAvailabilityZones().iterator();

            AvailabilityZone zone;
            while (iterator.hasNext()) {
                zone = iterator.next();
                System.out.printf(
                    "[id] %s,  [region] %15s, [zone] %15s\n",
                    zone.getZoneId(),
                    zone.getRegionName(),
                    zone.getZoneName()
                );
            }
            System.out.println("\nYou have access to " + result.getAvailabilityZones().size() +
                    " Availability Zones.");

        } catch (AmazonServiceException ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Reponse Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }
    }

    public static void startInstance(String instanceId) {
        System.out.printf("Starting instance %s...%n", instanceId);

        final AmazonEC2 ec2Temp = AmazonEC2ClientBuilder.defaultClient();

        // 유효성 검사
        DryRunSupportedRequest<StartInstancesRequest> dryRequest =
            () -> {
                StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceId);
                return request.getDryRunRequest();
            };

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);
        ec2Temp.startInstances(request);

        System.out.printf("Successfully started instance %s", instanceId);
    }

    public static void listAvailableRegions() {
        System.out.println("Listing available regions...");

        final AmazonEC2 ec2Temp = AmazonEC2ClientBuilder.defaultClient();

        DescribeRegionsResult result = ec2Temp.describeRegions();
        for (Region region : result.getRegions()) {
            System.out.printf(
                "[region] %15s, " +
                "[endpoint] %s\n",
                region.getRegionName(),
                region.getEndpoint());
        }
    }

    public static void stopInstance(String instanceId) {
        final AmazonEC2 ec2Temp = AmazonEC2ClientBuilder.defaultClient();

        // 유효성 검사
        DryRunSupportedRequest<StopInstancesRequest> dryRequest =
                () -> {
                    StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
                    return request.getDryRunRequest();
                };

        try {
            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
            ec2Temp.stopInstances(request);
            System.out.printf("Successfully stop instance %s\n", instanceId);
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }
    
    public static void createInstance(String amiId) {
        final AmazonEC2 ec2Temp = AmazonEC2ClientBuilder.defaultClient();

        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult runResult = ec2Temp.runInstances(runRequest);

        String reservationId = runResult.getReservation().getInstances().get(0).getInstanceId();

        System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                reservationId, amiId
        );
    }

    public static void rebootInstance(String instanceId) {
        System.out.println("Rebooting instance...");

        final AmazonEC2 ec2Temp = AmazonEC2ClientBuilder.defaultClient();

        try {
            RebootInstancesRequest request = new RebootInstancesRequest()
                    .withInstanceIds(instanceId);

            RebootInstancesResult result = ec2Temp.rebootInstances(request);

            System.out.printf("Successfully rebooted instance %s", instanceId);
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }

    public static void listImages() {
        System.out.println("Listing images...");

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

        request.setRequestCredentialsProvider(credentialsProvider);

        DescribeImagesResult results = ec2.describeImages(request);

        for (Image images : results.getImages()){
            System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n",
                    images.getImageId(), images.getName(), images.getOwnerId());
        }
    }

    // EC2 인스턴스에서 셸 명령어를 실행하기 위한 메서드
    public static void runShellCommand(String instanceId) throws InterruptedException {
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(region)
                .build();

        String command;
        Scanner input = new Scanner(System.in);

        String workingDirectory = "/home/ec2-user";

        while (true) {
            System.out.printf("[ec2-user@%s %s]$ ", instanceId, displayWorkingDirectory(workingDirectory));
            command = input.nextLine();
            if (command.trim().isEmpty() || command.trim().equals("exit"))
                break;

            if (command.startsWith("cd ")) {
                // change directory 로컬로 처리
                workingDirectory = changeDirectory(workingDirectory, command.substring(3).trim());
                continue;
            }

            SendCommandRequest sendCommandRequest = new SendCommandRequest()
                    .withInstanceIds(instanceId)
                    .withDocumentName("AWS-RunShellScript")
                    .withParameters(Collections.singletonMap(
                            "commands",
                            List.of("cd "+workingDirectory, command))
                    );

            sendShellCommandToInstance(instanceId, ssmClient, sendCommandRequest);
        }
    }

    // 로컬에서 working directory를 변경하기 처리하기 위한 메서드
    private static String changeDirectory(String workingDirectory, String target) {
        if (target.equals("~") || target.isEmpty()) { // 홈 디렉터리
            return "/home/ec2-user";
        }
        else if (target.equals("..") && !workingDirectory.equals("/")) { // 상위 디렉터리
            String wd = workingDirectory.substring(0, workingDirectory.lastIndexOf('/'));
            if (wd.isEmpty())
                wd = "/";
            return wd;
        }
        else if (target.startsWith("/")) { // 절대 경로
            return target;
        }
        else { // 상대 경로
            return workingDirectory + "/" + target;
        }
    }
    
    // 로컬에서 working directory 출력시 홈 디렉터리를 ~로 출력하기 위한 메서드
    private static String displayWorkingDirectory(String workingDirectory) {
        return (workingDirectory.equals("/home/ec2-user") ? "~" : workingDirectory);
    }

    // condor_status를 출력하기 위한 메서드
    public static void showCondorStatus(String instanceId) throws InterruptedException {
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(region)
                .build();

        SendCommandRequest sendCommandRequest = new SendCommandRequest()
                .withInstanceIds(instanceId)
                .withDocumentName("AWS-RunShellScript")
                .withParameters(Collections.singletonMap(
                        "commands",
                        Collections.singletonList("condor_status"))
                );

        sendShellCommandToInstance(instanceId, ssmClient, sendCommandRequest);
    }

    // condor_status를 출력하기 위한 메서드
    public static void showCondorQ(String instanceId) throws InterruptedException {
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(region)
                .build();

        SendCommandRequest sendCommandRequest = new SendCommandRequest()
                .withInstanceIds(instanceId)
                .withDocumentName("AWS-RunShellScript")
                .withParameters(Collections.singletonMap(
                        "commands",
                        Collections.singletonList("condor_q"))
                );

        sendShellCommandToInstance(instanceId, ssmClient, sendCommandRequest);
    }

    // AWS SDK를 이용해 EC2 인스턴스로 명령어를 전송하고 결과를 출력하는 메서드
    private static void sendShellCommandToInstance(String instanceId, AWSSimpleSystemsManagement ssmClient,
                                                   SendCommandRequest sendCommandRequest) throws InterruptedException {
        SendCommandResult result = ssmClient.sendCommand(sendCommandRequest);
        String commandId = result.getCommand().getCommandId();

        GetCommandInvocationRequest invocationRequest = new GetCommandInvocationRequest()
                .withInstanceId(instanceId)
                .withCommandId(commandId);

        GetCommandInvocationResult invocationResult = null;
        while (true) {
            invocationResult = ssmClient.getCommandInvocation(invocationRequest);
            String status = invocationResult.getStatus();

            if ("Success".equals(status) || "Failed".equals(status) || "Cancelled".equals(status)) {
                break;
            }
            Thread.sleep(2000);
        }

        // System.out.println("[Command Status]: " + invocationResult.getStatus());
        System.out.println(invocationResult.getStandardOutputContent());

        String serror = invocationResult.getStandardErrorContent();
        if (!serror.isEmpty())
            System.out.println("\n[Standard Error]:\n" + serror);
    }

    public static void submitJobs(String instanceId, String bucketName) throws InterruptedException {

        File jobDirectory = new File(jobDirectoryPath);
        if (!jobDirectory.exists() || !jobDirectory.isDirectory()) {
            System.out.println("[Error]: job directory path is not valid.");
            return;
        }

        File[] subDirectories = jobDirectory.listFiles();
        if (subDirectories == null || subDirectories.length == 0) {
            System.out.println("[Error]: job directory is empty.");
            return;
        }

        // 업로드할 수 있는 디렉토리 목록 출력
        System.out.println("\n[JOBS]:\n");
        for (int i = 0; i < subDirectories.length; i++) {
            // .jds 파일 찾기
            File[] jdsFile = subDirectories[i].listFiles((dir, name) -> name.endsWith(".jds"));
            if (jdsFile == null || jdsFile.length == 0)
                continue;

            System.out.printf("[%02d] %s\n", i+1, jdsFile[0].getName());
        }

        // 업로드할 디렉토리 번호 입력 받음
        int jobNumber;
        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter number to submit: ");

        try {
            jobNumber = input.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("[Error]: input is not NUMBER!");
            return;
        }

        // 업로드할 디렉토리의 파일 목록
        File[] filesToUpload = subDirectories[jobNumber - 1].listFiles();
        if (filesToUpload == null) {
            System.out.println("[Error]: error while file listing.");
            return;
        }
        System.out.println();


        String jdsName = null;
        List<List<String>> downloadUrls = new ArrayList<>();

        try {
            for (File file : filesToUpload) {
                if (jdsName == null && file.getName().endsWith(".jds"))
                    jdsName = file.getName();

                String url = S3FileUpload.uploadJobFiles(
                    region,
                    "s3-woochang-626635446593",
                    file.getName(),
                    file.getPath()
                );

                System.out.println("File uploaded successfully: " + file.getName());
                //System.out.println("downloadUrl = " + url + "\n");
                downloadUrls.add(List.of(file.getName(), url));
            }

        } catch (Exception e) {
            System.out.println("[Error]: Error occurred while uploading job files! (T.T)");
        }


        // EC2 인스턴스에서 다운로드하도록 명령 전송
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(region)
                .build();

        // 명령어 리스트
        List<String> commands = new ArrayList<>(List.of(
                "cd /home/ec2-user/program",
                "DIR_NAME=\"Job" + String.format("%02d", jobNumber) +"_$(date +%Y%m%d_%H%M%S)\"",
                "sudo -u ec2-user mkdir $DIR_NAME; cd $DIR_NAME"
        ));

        for (List<String> url : downloadUrls) {
            commands.add(String.format("sudo -u ec2-user wget -O %s '%s'", url.get(0), url.get(1)));
        }
        commands.add("chmod a+x *.sh");
        commands.add("[ -f setup.sh ] && ./setup.sh");
        commands.add("sudo -u ec2-user condor_submit " + jdsName);

        SendCommandRequest sendCommandRequest = new SendCommandRequest()
                .withInstanceIds(instanceId)
                .withDocumentName("AWS-RunShellScript")
                .withParameters(Collections.singletonMap("commands", commands));

        sendShellCommandToInstance(instanceId, ssmClient, sendCommandRequest);

        System.out.println("\nJob submitted successfully!!! (^-^)\n");
    }
}
