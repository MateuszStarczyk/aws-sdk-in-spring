package org.pwr.zrcaw_z4.controllers;

import org.pwr.zrcaw_z4.BucketsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pwr.zrcaw_z4.dtos.BucketDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.pwr.zrcaw_z4.controllers.AddressesMapping.BUCKETS;

@RestController
@RequestMapping(BUCKETS)
public class BucketsController {

    private final BucketsService bucketsService;

    public BucketsController(BucketsService bucketsService) {
        this.bucketsService = bucketsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getMyBuckets() {
        List<?> buckets = bucketsService.getAllBucketsNames().stream().map(BucketDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(buckets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilesForBucket(@PathVariable String id) {
        List<?> files = bucketsService.getAllFiles(id);
        return ResponseEntity.ok(files);
    }

    @PostMapping("")
    public ResponseEntity<?> addBucket(@RequestBody BucketDTO request) {
        if (bucketsService.addBucket(request.getName()))
            return ResponseEntity.ok("Bucket successfully created");
        else
            return ResponseEntity.ok("Bucket creation error");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBucket(@PathVariable String id) {
        if (bucketsService.deleteBucket(id))
            return ResponseEntity.ok("Bucket successfully deleted");
        else
            return ResponseEntity.ok("Bucket deletion error");
    }
}
