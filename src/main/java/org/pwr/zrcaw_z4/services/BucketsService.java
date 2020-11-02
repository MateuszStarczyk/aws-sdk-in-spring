package org.pwr.zrcaw_z4.services;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.pwr.zrcaw_z4.models.BucketItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Component
public class BucketsService {

    S3Client s3;

    private S3Client getClient() {
        // Create the S3Client object
        Region region = Region.US_EAST_1;

        return S3Client.builder()
                .region(region)
                .build();
    }

    public byte[] getObjectBytes(String bucketName, String keyName) {

        s3 = getClient();

        try {
            // create a GetObjectRequest instance
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            // get the byte[] from this AWS S3 object
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public List<BucketItem> getAllFiles(String bucketName) {

        s3 = getClient();
        long sizeLg;
        Instant DateIn;
        BucketItem myItem;


        List bucketItems = new ArrayList<BucketItem>();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
                S3Object myValue = (S3Object) iterVals.next();
                myItem = new BucketItem();
                myItem.setKey(myValue.key());
                myItem.setOwner(myValue.owner().displayName());
                sizeLg = myValue.size() / 1024;
                myItem.setSize(String.valueOf(sizeLg));
                DateIn = myValue.lastModified();
                myItem.setDate(String.valueOf(DateIn));

                // Push the items to the list
                bucketItems.add(myItem);
            }

            return bucketItems;
        } catch (NoSuchBucketException noSuchBucketException) {
            return null;
        }
    }

    public String putFile(byte[] data, String bucketName, String objectKey) {

        s3 = getClient();

        try {
            //Put a file into the bucket
            PutObjectResponse response = s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                    RequestBody.fromBytes(data));

            return response.eTag();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public List<String> getAllBucketsNames() {

        s3 = getClient();

        return s3.listBuckets().buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    public Boolean addBucket(String name) {
        s3 = getClient();
        try {
            s3.createBucket(CreateBucketRequest.builder().bucket(name).build());
            return true;
        } catch (SdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean deleteBucket(String name) {
        s3 = getClient();
        try {
            s3.deleteBucket(DeleteBucketRequest.builder().bucket(name).build());
            return true;
        } catch (SdkException e) {
            e.printStackTrace();
        }
        return false;
    }
}