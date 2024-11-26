/*
*  2024년 클라우드 컴퓨팅 Term Project
*  작성자: 2019038074 이우창
*  작성일: 2024-11-21 ~
*/

package aws;

import java.util.Iterator;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.opsworks.model.StartInstanceRequest;

public class Main {

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
                .withRegion(Regions.US_EAST_1)
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
            System.out.println("  1. list instance                2. available zones        ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if (menu.hasNextInt()) {
                number = menu.nextInt();
            } else {
                System.out.println("You must enter an integer!");
                break;
            }

            String instanceId = "";

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
}
