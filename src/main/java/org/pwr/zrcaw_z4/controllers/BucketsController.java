package org.pwr.zrcaw_z4.controllers;

import org.pwr.zrcaw_z4.dtos.Bucket;
import org.pwr.zrcaw_z4.services.BucketsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Controller
public class BucketsController {

    private final BucketsService bucketsService;

    public BucketsController(BucketsService bucketsService) {
        this.bucketsService = bucketsService;
    }

    @GetMapping("/buckets")
    public String getMyBuckets(Model model) {
        model.addAttribute("buckets", bucketsService.getBuckets());
        model.addAttribute("bucket", new Bucket());
        return "buckets/main";
    }

    @GetMapping("/buckets/{id}/files")
    public String getFilesForBucket(@PathVariable String id, Model model) {
        model.addAttribute("bucket_name", id);
        model.addAttribute("files", bucketsService.getAllFiles(id));
        return "/buckets/bucketFiles";
    }

    @PostMapping("/buckets/{id}/files")
    public String uploadFile(@PathVariable String id, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/buckets/" + id + "/files";
        }
        try {
            bucketsService.putFile(file.getBytes(), id, file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "File upload error!");
        }
        return "redirect:/buckets/" + id + "/files";
    }

    @GetMapping("/buckets/{id}/files/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id, @PathVariable String filename) {
        Resource file = new ByteArrayResource(bucketsService.getObjectBytes(id, filename));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    @GetMapping("/buckets/{id}/files/{filename}/delete")
    public String deleteFile(@PathVariable String id, @PathVariable String filename, RedirectAttributes redirectAttributes) {
        try {
            bucketsService.deleteFile(id, filename);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "File deletion error");
        }
        return "redirect:/buckets/" + id + "/files";
    }

    @PostMapping("/buckets")
    public String createBucket(@ModelAttribute("bucket") Bucket bucket, RedirectAttributes redirectAttributes) {
        try {
            bucketsService.saveBucket(bucket);
        }  catch (BucketAlreadyExistsException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "The requested bucket name is not available. The bucket namespace is shared by all users of the system.");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Bucket name should be between 3 and 63 characters long!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "The requested bucket name is not available. The bucket namespace is shared by all users of the system.");
        }
        return "redirect:/buckets";
    }

    @GetMapping("/buckets/{id}/delete")
    public String deleteBucket(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bucketsService.deleteBucket(id);
        } catch (S3Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", " The bucket you tried to delete is not empty!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Bucket deletion error!");
        }
        return "redirect:/buckets";
    }
}
