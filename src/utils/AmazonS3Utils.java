package utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import test.DriverFactory;

public class AmazonS3Utils {

  public boolean downloadFromS3(String remotePath, String downloadPath, boolean isFolder) throws FileNotFoundException, IOException {
    AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(DriverFactory.environment.get("accessKey"), DriverFactory.environment.get("secretKey")));
    Region ap_south_1 = Region.getRegion(Regions.AP_SOUTH_1);
    s3.setRegion(ap_south_1);
    String bucketName = DriverFactory.environment.get("s3BucketName").trim();
    String key = remotePath;
    System.out.println("===========================================");
    System.out.println("Getting Started with Amazon S3");
    System.out.println("===========================================\n");
    System.out.println("Downloading APP - " + downloadPath + " from S3");

    try {
      if (!(new File(downloadPath)).exists()) {
        (new File(downloadPath.trim().substring(0, downloadPath.trim().lastIndexOf(OSValidator.delimiter)))).mkdirs();
      }

      if (isFolder) {
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
        MultipleFileDownload xfer = xfer_mgr.downloadDirectory(bucketName, key, new File(downloadPath));
        xfer.waitForCompletion();
      } else {
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        S3ObjectInputStream objectData = object.getObjectContent();
        IOUtils.copy(objectData, new FileOutputStream(downloadPath));
      }

      System.out.println("Downloading completed");
      return true;
    } catch (Exception var10) {
      var10.printStackTrace();
      return false;
    }
  }

  public void uploadToS3(String remotePath, String uploadPath, String contentType) throws FileNotFoundException, IOException {
    AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(DriverFactory.environment.get("accessKey"), DriverFactory.environment.get("secretKey")));
    Region ap_south_1 = Region.getRegion(Regions.AP_SOUTH_1);
    s3.setRegion(ap_south_1);
    String bucketName = DriverFactory.environment.get("s3BucketName").trim();
    String key = remotePath;
    System.out.println("===========================================");
    System.out.println("Getting Started with Amazon S3");
    System.out.println("===========================================\n");
    System.out.println("Uploading file - " + uploadPath + " to S3");

    try {
      File initialFile = new File(uploadPath);
      Long contentLength = (long)IOUtils.toByteArray(new FileInputStream(initialFile)).length;
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(contentType);
      metadata.setContentLength(contentLength);
      InputStream inputstream = new FileInputStream(initialFile);
      PutObjectRequest request = new PutObjectRequest(bucketName, key, inputstream, metadata);
      s3.putObject(request);
      System.out.println("Uploading completed");
    } catch (Exception var13) {
      var13.printStackTrace();
    }
  }

  public void uploadFolderToS3(String remotePath, String uploadPath) throws FileNotFoundException, IOException {
    AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(DriverFactory.environment.get("accessKey"), DriverFactory.environment.get("secretKey")));
    Region ap_south_1 = Region.getRegion(Regions.AP_SOUTH_1);
    s3.setRegion(ap_south_1);
    String bucketName = DriverFactory.environment.get("s3BucketName").trim();
    String key = remotePath;
    System.out.println("===========================================");
    System.out.println("Getting Started with Amazon S3");
    System.out.println("===========================================\n");
    System.out.println("Uploading file - " + uploadPath + " to S3");

    try {
      TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
      ObjectMetadataProvider metadataProvider = new ObjectMetadataProvider() {
        public void provideObjectMetadata(File file, ObjectMetadata metadata) {
          try {
            Long contentLength;
            if (file.getName().endsWith(".json")) {
              contentLength = (long)IOUtils.toByteArray(new FileInputStream(file)).length;
              metadata.setContentType("application/json; charset=utf-8");
              metadata.setContentLength(contentLength);
            } else if (file.getName().endsWith(".html")) {
              contentLength = (long)IOUtils.toByteArray(new FileInputStream(file)).length;
              metadata.setContentType("text/html; charset=utf-8");
              metadata.setContentLength(contentLength);
            } else if (file.getName().endsWith(".txt")) {
              contentLength = (long)IOUtils.toByteArray(new FileInputStream(file)).length;
              metadata.setContentType("text/plain; charset=utf-8");
              metadata.setContentLength(contentLength);
            } else if (file.getName().endsWith(".csv")) {
              contentLength = (long)IOUtils.toByteArray(new FileInputStream(file)).length;
              metadata.setContentType("text/csv; charset=utf-8");
              metadata.setContentLength(contentLength);
            }
          } catch (IOException var4) {
            var4.printStackTrace();
          }

        }
      };
      MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucketName, key, new File(uploadPath), true, metadataProvider);
      xfer.waitForCompletion();
      System.out.println("Uploading completed");
    } catch (Exception var10) {
      var10.printStackTrace();
    }

  }
}
