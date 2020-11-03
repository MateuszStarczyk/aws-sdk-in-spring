package org.pwr.aws.sdk.spring.services;

import org.pwr.aws.sdk.spring.dtos.Bucket;
import org.pwr.aws.sdk.spring.models.BucketFile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Component
public class BucketsService {

    private S3Client getClient() {
        Region region = Region.US_EAST_1;

        return S3Client.builder()
                .region(region)
                .build();
    }

    public byte[] getObjectBytes(String bucketName, String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = getClient().getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public List<BucketFile> getAllFiles(String bucketName) {
        long sizeLg;
        Instant DateIn;
        BucketFile myItem;


        List bucketItems = new ArrayList<BucketFile>();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = getClient().listObjects(listObjects);
            List<S3Object> objects = res.contents();

            for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
                S3Object myValue = (S3Object) iterVals.next();
                myItem = new BucketFile();
                myItem.setKey(myValue.key());
                myItem.setOwner(myValue.owner().displayName());
                sizeLg = myValue.size() / 1024;
                myItem.setSize(String.valueOf(sizeLg));
                DateIn = myValue.lastModified();
                myItem.setDate(String.valueOf(DateIn));

                bucketItems.add(myItem);
            }

            return bucketItems;
        } catch (NoSuchBucketException noSuchBucketException) {
            return null;
        }
    }

    public void putFile(byte[] data, String bucketName, String objectKey) {
        getClient().putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build(),
                RequestBody.fromBytes(data));
    }

    public void deleteFile(String bucketName, String filename) {
        getClient().deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(filename).build());
    }

    public List<org.pwr.aws.sdk.spring.dtos.Bucket> getBuckets() {
        return getClient().listBuckets().buckets().stream().map(b -> new org.pwr.aws.sdk.spring.dtos.Bucket(b.name())).collect(Collectors.toList());
    }

    public void saveBucket(Bucket bucket) {
        getClient().createBucket(CreateBucketRequest.builder().bucket(bucket.getName()).build());
    }

    public void deleteBucket(String name) {
        getClient().deleteBucket(DeleteBucketRequest.builder().bucket(name).build());
    }
}