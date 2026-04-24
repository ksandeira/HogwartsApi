package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping("/{studentId}/upload")
    public ResponseEntity<Void> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam("avatar") MultipartFile file) {
        try {
            avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{studentId}/data")
    public ResponseEntity<byte[]> getAvatarData(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatar(studentId);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + avatar.getFilePath() + "\"")
                .body(avatar.getData());
    }

    @GetMapping
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Avatar> avatars = avatarService.getAllAvatars(page, size);
        return ResponseEntity.ok(avatars);
    }
}