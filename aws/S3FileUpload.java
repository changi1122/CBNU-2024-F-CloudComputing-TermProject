package aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.util.Date;
import java.net.URL;

public class S3FileUpload {
    
    // S3 버킷으로 작업명세서와 관련 파일을 업로드하는 메서드
    public static String uploadJobFiles(Regions region, String bucketName,
                                        String keyName, String filePath) throws Exception {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new ProfileCredentialsProvider())
                .build();

        // 파일 업로드 요청
        File file = new File(filePath);
        s3Client.putObject(new PutObjectRequest(bucketName, "jobs/" + keyName, file));

        System.out.println("File upload completed: " + keyName);

        return generateDownloadUrl(s3Client, bucketName, "jobs/" + keyName);
    }

    // S3 버킷에 업로드된 파일에 대해 다운로드 링크를 생성하는 메서드
    private static String generateDownloadUrl(AmazonS3 s3Client, String bucketName, String keyName) {

        // URL 유효시간 설정
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10;  // 10분
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyName)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL downloadUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return downloadUrl.toString();
    }
}
