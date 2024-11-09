package ee.taltech.iti03022024backend.controller;


import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CommentController {
    private final CommentService service;

    @PostMapping("/camping_routes/comments/{campingRouteId}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto dto, @PathVariable long campingRouteId) {
        return service.createComment(dto, campingRouteId);
    }

    @GetMapping("/public/camping_routes/comments/{campingRouteId}")
    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(@PathVariable long campingRouteId) {
        return service.getCommentsByCampingRoute(campingRouteId);
    }

    @GetMapping("/public/camping_routes/comments/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable long userId) {
        return service.getCommentsByUserId(userId);
    }
}
